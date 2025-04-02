/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: CodeGenerator.java
*/

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import absyn.*;
import java.util.HashMap;

public class CodeGenerator implements AbsynVisitor {

    // Register constants for clarity
    public static final int PC = 7;
    public static final int GP = 6;
    public static final int FP = 5;
    public static final int AC = 0;
    public static final int AC1 = 1;

    // Offsets for stack frame items
    public static final int retFO = -1;   // Offset for return address
    public static final int ofpFO = 0;    // Offset for old frame pointer
    public static final int initFO = -2;  // Initial frame offset for parameters and locals

    // Instance variables for code generation
    private int mainEntry = -1;       // Entry address for main function
    private int globalOffset = 0;     // Next available location in global data memory
    private int emitLoc = 0;          // Current instruction memory location
    private int highEmitLoc = 0;      // Highest instruction location generated so far
    private PrintWriter code;         // Output writer for the assembly code

    // Additional instance variables for I/O
    private int inputEntry;
    private int outputEntry;
    
    // Track the current function for return statements
    private SymbolInfo currentFunction = null;
    
    // Track variable locations and functions in hashmap
    private HashMap<String, VariableInfo> varOffsets = new HashMap<>();
    private HashMap<String, FunctionDec> functionTable = new HashMap<>();

    // Class to store variable information
    private class VariableInfo {
        int offset;
        boolean isGlobal;
        
        public VariableInfo(int offset, boolean isGlobal) {
            this.offset = offset;
            this.isGlobal = isGlobal;
        }
    }

    // Constructor: initialize the output file
    public CodeGenerator(String outputFile) {
        try {
            code = new PrintWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            System.err.println("Error creating code output file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Emit routines to generate TM assembly instructions

    // Emit a comment line
    void emitComment(String comment) {
        code.println("* " + comment);
    }

    // Emit a Register-Only (RO) instruction
    void emitRO(String op, int r, int s, int t, String comment) {
        code.printf("%3d: %5s %d, %d, %d\t%s\n", emitLoc, op, r, s, t, comment);
        emitLoc++;
        if (emitLoc > highEmitLoc) highEmitLoc = emitLoc;
    }

    // Emit a Register-Memory (RM) instruction
    void emitRM(String op, int r, int d, int s, String comment) {
        code.printf("%3d: %5s %d, %d(%d)\t%s\n", emitLoc, op, r, d, s, comment);
        emitLoc++;
        if (emitLoc > highEmitLoc) highEmitLoc = emitLoc;
    }

    // Emit an RM instruction with an absolute address (useful for jumps)
    void emitRM_Abs(String op, int r, int a, String comment) {
        int relativeAddr = a - (emitLoc + 1);
        code.printf("%3d: %5s %d, %d(%d)\t%s\n", emitLoc, op, r, relativeAddr, PC, comment);
        emitLoc++;
        if (emitLoc > highEmitLoc) highEmitLoc = emitLoc;
    }

    // Skip a number of instructions (for backpatching)
    int emitSkip(int distance) {
        int i = emitLoc;
        emitLoc += distance;
        if (emitLoc > highEmitLoc) highEmitLoc = emitLoc;
        return i;
    }

    // Backup to a previously skipped location for backpatching
    void emitBackup(int loc) {
        if (loc > highEmitLoc) {
            emitComment("BUG in emitBackup");
        }
        emitLoc = loc;
    }

    // Restore the emit location to the highest emitted location so far
    void emitRestore() {
        emitLoc = highEmitLoc;
    }

    // Visitor Methods for Code Generation

    @Override
    public void visit(AssignExp exp, int offset, boolean isAddr) {
        emitComment("-> assign");
        
        // Get the address of the LHS variable
        exp.lhs.accept(this, offset, true);  // Note: isAddr=true to get the address
        
        // Save LHS address
        emitRM("ST", AC, offset, FP, "store lhs address");
        
        // Evaluate the RHS expression
        exp.rhs.accept(this, offset-1, false);
        
        // Load the LHS address into AC1
        emitRM("LD", AC1, offset, FP, "load lhs address");
        
        // Store the RHS value at the LHS address
        emitRM("ST", AC, 0, AC1, "assign: store value");
        
        emitComment("<- assign");
    }

    // Binary operator expression
    @Override
    public void visit(OpExp exp, int offset, boolean isAddr) {
        emitComment("-> op");
        
        switch (exp.op) {
            case OpExp.PLUS:
            case OpExp.MINUS:
            case OpExp.TIMES:
            case OpExp.OVER:
                // Generate code for left operand
                exp.left.accept(this, offset, false);
                emitRM("ST", AC, offset, FP, "op: push left");
                
                // Generate code for right operand
                exp.right.accept(this, offset-1, false);
                
                // Load left operand into AC1 (not register 0)
                emitRM("LD", AC1, offset, FP, "op: load left");
                
                // Perform operation with operand order matching reference
                if (exp.op == OpExp.PLUS) {
                    emitRO("ADD", AC, AC1, AC, "op +");
                } else if (exp.op == OpExp.MINUS) {
                    emitRO("SUB", AC, AC1, AC, "op -");
                } else if (exp.op == OpExp.TIMES) {
                    emitRO("MUL", AC, AC1, AC, "op *");
                } else if (exp.op == OpExp.OVER) {
                    emitRO("DIV", AC, AC1, AC, "op /");
                }
                break;
                
            case OpExp.LT:
            case OpExp.GT:
            case OpExp.LTE:
            case OpExp.GTE:
            case OpExp.EQ:
            case OpExp.NEQ:
                // Generate code for left operand
                exp.left.accept(this, offset, false);
                emitRM("ST", AC, offset, FP, "op: push left");
                
                // Generate code for right operand
                exp.right.accept(this, offset-1, false);
                
                // Load left operand into AC1 (not register 0)
                emitRM("LD", AC1, offset, FP, "op: load left");
                
                // Comparison with operand order matching reference
                if (exp.op == OpExp.LT) {
                    emitRO("SUB", AC, AC1, AC, "op <");
                    emitRM("JLT", AC, 2, PC, "br if true");
                } else if (exp.op == OpExp.GT) {
                    emitRO("SUB", AC, AC1, AC, "op >");
                    emitRM("JGT", AC, 2, PC, "br if true");
                } else if (exp.op == OpExp.LTE) {
                    emitRO("SUB", AC, AC1, AC, "op <=");
                    emitRM("JLE", AC, 2, PC, "br if true");
                } else if (exp.op == OpExp.GTE) {
                    emitRO("SUB", AC, AC1, AC, "op >=");
                    emitRM("JGE", AC, 2, PC, "br if true");
                } else if (exp.op == OpExp.EQ) {
                    emitRO("SUB", AC, AC1, AC, "op ==");
                    emitRM("JEQ", AC, 2, PC, "br if true");
                } else if (exp.op == OpExp.NEQ) {
                    emitRO("SUB", AC, AC1, AC, "op !=");
                    emitRM("JNE", AC, 2, PC, "br if true");
                }
                
                // False case
                emitRM("LDC", AC, 0, 0, "false case");
                emitRM("LDA", PC, 1, PC, "unconditional jmp");
                
                // True case
                emitRM("LDC", AC, 1, 0, "true case");
                break;
        }
        
        emitComment("<- op");
    }
    
    // Integer constant expression
    @Override
    public void visit(IntExp exp, int offset, boolean isAddr) {
        emitComment("-> constant");
        emitRM("LDC", AC, Integer.parseInt(exp.value), 0, "load const");
        emitComment("<- constant");
    }

    // Boolean constant expression
    @Override
    public void visit(BoolExp exp, int offset, boolean isAddr) {
        emitComment("-> constant");
        emitRM("LDC", AC, exp.bool ? 1 : 0, 0, "load const");
        emitComment("<- constant");
    }

    // Simple variable: load its address or value depending on isAddr
    @Override
    public void visit(SimpleVar var, int offset, boolean isAddr) {
        emitComment("-> id");
        emitComment("looking up id: " + var.name);
        
        // Look up variable info
        VariableInfo varInfo = varOffsets.get(var.name);
        if (varInfo != null) {
            int base = varInfo.isGlobal ? GP : FP;
            
            if (isAddr) {
                emitRM("LDA", AC, varInfo.offset, base, "load id address");
            } else {
                emitRM("LD", AC, varInfo.offset, base, "load id value");
            }
        } else {
            // Use default approach if not found
            if (isAddr) {
                emitRM("LDA", AC, -99, FP, "ERROR: variable not found");
            } else {
                emitRM("LDC", AC, 0, 0, "ERROR: variable not found");
            }
            System.err.println("WARNING: Variable " + var.name + " not found in offset table");
        }
        
        emitComment("<- id");
    }

    // Function declaration: generate code for function body
    @Override
    public void visit(FunctionDec exp, int offset, boolean isAddr) {
        emitComment("processing function: " + exp.name);
        
        // Save previous function context and set current function
        SymbolInfo prevFunction = currentFunction;
        currentFunction = new SymbolInfo(exp.name, exp.result.type, false);
        
        // Skip over function body initially
        emitComment("jump around function body here");
        int jumpAroundLoc = emitSkip(1);
        
        exp.funaddr = emitLoc;
        
        // Add function to the function table
        functionTable.put(exp.name, exp);
        
        // Check if this is the main function
        if (exp.name.equals("main")) {
            mainEntry = exp.funaddr;
        }
        
        // Store return address at function entry
        emitRM("ST", AC, retFO, FP, "store return");
        
        // Process parameters to set their offsets
        int paramOffset = initFO;
        VarDecList params = exp.params;
        
        while (params != null) {
            VarDeclExp param = params.head;
            
            // Store parameter in variable table with isGlobal = false
            varOffsets.put(param.name, new VariableInfo(paramOffset, false));
            
            paramOffset--;
            params = params.tail;
        }
        
        // Process function body
        if (exp.body != null) {
            // Start the frame offset after parameters
            int frameOffset = paramOffset;
            exp.body.accept(this, frameOffset, false);
        }
        
        // Return to caller
        emitRM("LD", PC, retFO, FP, "return to caller");
        
        // Backpatch the jump around function body
        int currentLoc = emitLoc;
        emitBackup(jumpAroundLoc);
        emitRM_Abs("LDA", PC, currentLoc, "jump around fn body");
        emitRestore();
        
        // Restore previous function context
        currentFunction = prevFunction;
        
        emitComment("<- fundecl");
    }

    // Compound expression: process declarations and statements
    @Override
    public void visit(CompoundExp exp, int offset, boolean isAddr) {
        emitComment("-> compound statement");
        
        // Process local declarations
        int localOffset = offset;
        
        if (exp.decs != null) {
            VarDecList decs = exp.decs;
            
            while (decs != null) {
                VarDeclExp dec = decs.head;
                
                // Add local variable to table with isGlobal = false
                varOffsets.put(dec.name, new VariableInfo(localOffset, false));
                
                emitComment("processing local var: " + dec.name);
                
                // Update offset for next variable
                if (dec.size > 0) {
                    localOffset = localOffset - dec.size - 1;
                } else {
                    localOffset = localOffset - 1;
                }
                
                decs = decs.tail;
            }
        }
        
        // Process expressions/statements
        if (exp.exps != null) {
            exp.exps.accept(this, localOffset, false);
        }
        
        emitComment("<- compound statement");
    }

    // Function call: generate code to evaluate and pass arguments
    @Override
    public void visit(CallExp exp, int offset, boolean isAddr) {
        emitComment("-> call of function: " + exp.func);
        
        // Special handling for input/output functions
        if (exp.func.equals("input")) {
            emitRM("ST", FP, offset, FP, "push ofp");
            emitRM("LDA", FP, offset, FP, "push frame");
            emitRM("LDA", AC, 1, PC, "load ac with ret ptr");
            emitRM_Abs("LDA", PC, inputEntry, "jump to fun loc");
            emitRM("LD", FP, 0, FP, "pop frame");
            return;
        } else if (exp.func.equals("output")) {
            if (exp.args != null) {
                // Evaluate the argument
                exp.args.head.accept(this, offset, false);
                
                // Store value for use after frame change
                emitRM("ST", AC, offset, FP, "save output value");
                
                // Setup the new frame
                emitRM("ST", FP, offset-1, FP, "push ofp");
                emitRM("LDA", FP, offset-1, FP, "push frame");
                
                // Reload the value using the old FP value which is now at ofpFO in new frame
                emitRM("LD", AC1, ofpFO, FP, "load old frame pointer");
                emitRM("LD", AC, offset, AC1, "load saved output value");
                
                // Store it at the parameter offset expected by output routine
                emitRM("ST", AC, initFO, FP, "store arg for output");
                
                // Complete the call
                emitRM("LDA", AC, 1, PC, "load ac with ret ptr");
                emitRM_Abs("LDA", PC, outputEntry, "jump to output routine");
                emitRM("LD", FP, 0, FP, "pop frame");
            }
            return;
        }
        
        // Evaluate all arguments first
        int numArgs = 0;
        if (exp.args != null) {
            int argOffset = offset;
            ExpList args = exp.args;
            
            while (args != null) {
                // Evaluate argument expression
                args.head.accept(this, argOffset, false);
                
                // Store evaluated argument
                emitRM("ST", AC, argOffset, FP, "store argument value");
                
                args = args.tail;
                argOffset--;
                numArgs++;
            }
        }
                
        // Setup new frame pointer
        emitRM("ST", FP, offset-numArgs, FP, "push ofp");
        emitRM("LDA", FP, offset-numArgs, FP, "push frame");
        
        // Copy arguments to their parameter positions
        if (exp.args != null) {
            for (int i = 0; i < numArgs; i++) {
                // Load argument from caller's frame
                emitRM("LD", AC1, ofpFO, FP, "load old frame pointer");
                emitRM("LD", AC, offset-i, AC1, "load argument");
                
                // Store in callee's parameter position
                emitRM("ST", AC, initFO-i, FP, "store parameter");
            }
        }
        
        // Setup return address exactly like input/output functions
        emitRM("LDA", AC, 1, PC, "load ac with ret ptr");
        
        // Jump to function
        FunctionDec funcDec = functionTable.get(exp.func);
        if (funcDec != null) {
            emitRM_Abs("LDA", PC, funcDec.funaddr, "jump to function " + exp.func);
        } else {
            emitComment("Error: Cannot find function address for " + exp.func);
        }
        
        // Pop frame after return
        emitRM("LD", FP, 0, FP, "pop frame");
        
        emitComment("<- call");
    }

    // If statement: generate code for test and branches
    @Override
    public void visit(IfExp exp, int level, boolean isAddr) {
        emitComment("-> if");
        
        // Generate code for the test condition
        exp.test.accept(this, level, false);
        
        // If condition is false (0), jump to else part or to the end
        // The comparison operations return 1 for true and 0 for false
        int skipThenLoc = emitSkip(1);
        emitComment("if: jump to else belongs here");
        
        // Generate code for the then part
        if (exp.thenpart != null) {
            exp.thenpart.accept(this, level, false);
        }
        
        // Jump around the else part (if it exists)
        int skipElseLoc = 0;
        if (exp.elsepart != null) {
            skipElseLoc = emitSkip(1);
            emitComment("if: jump to end belongs here");
        }
        
        // Backpatch the conditional jump
        int currentLoc = emitLoc;
        emitBackup(skipThenLoc);
        emitRM_Abs("JEQ", AC, currentLoc, "if: jmp to else if test is false");
        emitRestore();
        
        // Generate code for the else part (if it exists)
        if (exp.elsepart != null) {
            exp.elsepart.accept(this, level, false);
            
            // Backpatch the jump around the else part
            currentLoc = emitLoc;
            emitBackup(skipElseLoc);
            emitRM_Abs("LDA", PC, currentLoc, "if: jmp to end");
            emitRestore();
        }
        
        emitComment("<- if");
    }

    // While statement: generate code for loop
    @Override
    public void visit(WhileExp exp, int offset, boolean isAddr) {
        emitComment("-> while");
        
        // Save the location of the test
        int testLoc = emitLoc;
        emitComment("while: jump after body comes back here");
        
        // Generate code for the test condition
        exp.test.accept(this, offset, false);
        
        // If condition is false, jump around the body
        int savedLoc = emitSkip(1);
        emitComment("while: jump to end belongs here");
        
        // Generate code for the loop body
        if (exp.body != null) {
            exp.body.accept(this, offset, false);
        }
        
        // Jump back to test
        emitRM_Abs("LDA", PC, testLoc, "while: absolute jmp to test");
        
        // Backpatch the jump around the body
        int currentLoc = emitLoc;
        emitBackup(savedLoc);
        emitRM_Abs("JEQ", AC, currentLoc, "while: jmp to end");
        emitRestore();
        
        emitComment("<- while");
    }

    @Override
    public void visit(ReturnExp exp, int offset, boolean isAddr) {
        emitComment("-> return");
        
        // Generate code for the return expression - result will be in AC
        if (exp.exp != null) {
            exp.exp.accept(this, offset, false);
        } else {
            // Void return, just load 0
            emitRM("LDC", AC, 0, 0, "return: default value");
        }
        
        // Return to caller - AC now contains the return value
        emitRM("LD", PC, retFO, FP, "return to caller");
        
        emitComment("<- return");
    }

    // Variable declaration: allocate space for variables
    @Override
    public void visit(VarDeclExp exp, int offset, boolean isAddr) {
        if (currentFunction == null) {
            // Global variable
            globalOffset--;
            
            // Add variable to the table with isGlobal = true
            varOffsets.put(exp.name, new VariableInfo(globalOffset, true));
            
            emitComment("processing var: " + exp.name);
            emitComment("Global var " + exp.name + " at offset " + globalOffset);
            
            if (exp.size > 0) {
                emitRM("LDC", AC, exp.size, 0, "load array size");
                emitRM("ST", AC, globalOffset, GP, "store array size");
                globalOffset -= (exp.size + 1);
            }
        } else {
            // Local variable
            // Record offset in variables table with isGlobal = false
            varOffsets.put(exp.name, new VariableInfo(offset, false));
            
            emitComment("processing local var: " + exp.name);
            
            if (exp.size > 0) {
                emitRM("LDC", AC, exp.size, 0, "load array size");
                emitRM("ST", AC, offset, FP, "store array size");
            }
        }
    }

    // Empty statement (NilExp)
    @Override
    public void visit(NilExp exp, int offset, boolean isAddr) {
        // No code generation needed
    }

    // Type expression
    @Override
    public void visit(TypeExp exp, int offset, boolean isAddr) {
        // No code generation needed
    }

    // Visit a list of declarations.
    @Override
    public void visit(VarDecList list, int offset, boolean isAddr) {
        VarDecList current = list;
        while(current != null) {
            current.head.accept(this, offset, isAddr);
            current = current.tail;
            offset--;
        }
    }

    // Visit a list of expressions.
    @Override
    public void visit(ExpList expList, int offset, boolean isAddr) {
        ExpList current = expList;
        while(current != null) {
            current.head.accept(this, offset, isAddr);
            current = current.tail;
            offset--;
        }
    }

    // Indexed variable
    @Override
    public void visit(IndexVar var, int offset, boolean isAddr) {
        emitComment("-> subs");
        
        // Get the base address of the array using variable info
        VariableInfo varInfo = varOffsets.get(var.name);
        if (varInfo != null) {
            int base = varInfo.isGlobal ? GP : FP;
            emitRM("LDA", AC, varInfo.offset, base, "load array base address");
        } else {
            emitRM("LDC", AC, 0, 0, "ERROR: array not found");
            System.err.println("WARNING: Array " + var.name + " not found in offset table");
        }
        
        // Save base address
        emitRM("ST", AC, offset, FP, "save array base address");
        
        // Compute index
        var.index.accept(this, offset-1, false);
        
        // Load base address into AC1
        emitRM("LD", AC1, offset, FP, "load array base address");
        
        // Compute final address
        emitRO("ADD", AC, AC1, AC, "compute element address");
        
        if (!isAddr) {
            // Load value at computed address
            emitRM("LD", AC, 0, AC, "load array element");
        }
        
        emitComment("<- subs");
    }

    // Method for code generation
    public void generate(Absyn ast) {
        // Initialize global variables
        mainEntry = -1;
        globalOffset = 0;
        currentFunction = null;
        varOffsets.clear();
        functionTable.clear();
                
        // Generate prelude
        emitComment("Prelude:");
        emitRM("LD", GP, 0, 0, "load gp with maxaddress");
        emitRM("LDA", FP, 0, GP, "copy to gp to fp");
        emitRM("ST", AC, 0, 0, "clear location 0");
        
        // Save location to jump around I/O routines
        emitComment("Jump around i/o routines here");
        int savedLoc = emitSkip(1);
        
        // Generate I/O routines
        emitComment("input routine");
        inputEntry = emitLoc;
        emitRM("ST", AC, retFO, FP, "store return");
        emitRO("IN", AC, 0, 0, "input");
        emitRM("LD", PC, retFO, FP, "return to caller");
        
        emitComment("output routine");
        outputEntry = emitLoc;
        emitRM("ST", AC, retFO, FP, "store return");
        emitRM("LD", AC, initFO, FP, "load output value");
        emitRO("OUT", AC, 0, 0, "output");
        emitRM("LD", PC, retFO, FP, "return to caller");
        
        // Backpatch jump around I/O routines
        int currentLoc = emitLoc;
        emitBackup(savedLoc);
        emitRM_Abs("LDA", PC, currentLoc, "jump around i/o code");
        emitRestore();
        
        emitComment("End of prelude.");
        
        // Generate code for the AST
        ast.accept(this, initFO, false);
        
        // Check if main function was found
        if (mainEntry == -1) {
            System.err.println("Error: main function not found");
            return;
        }
        
        // finale - setup and call to main
        emitRM("ST", FP, globalOffset+ofpFO, FP, "push ofp");
        emitRM("LDA", FP, globalOffset, FP, "push frame");
        emitRM("LDA", AC, 1, PC, "load ac with ret ptr");
        emitRM_Abs("LDA", PC, mainEntry, "jump to main loc");
        emitRM("LD", FP, 0, FP, "pop frame");

        emitRO("HALT", 0, 0, 0, "");
        
        // Close the output file
        code.close();
    }
}