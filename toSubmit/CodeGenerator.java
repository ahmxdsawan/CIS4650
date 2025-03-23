import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import absyn.*;

public class CodeGenerator implements AbsynVisitor {

    // Register constants for clarity
    public static final int PC = 7;   // Program Counter
    public static final int GP = 6;   // Global Pointer
    public static final int FP = 5;   // Frame Pointer
    public static final int AC = 0;   // Accumulator
    public static final int AC1 = 1;  // Secondary accumulator

    // Offsets for stack frame items (adjust these values as needed)
    public static final int retFO = -1;   // Offset for return address
    public static final int ofpFO = -2;   // Offset for old frame pointer
    public static final int paramFO = -3; // Offset for parameters (for output, etc.)

    // Instance variables for code generation
    int mainEntry = -1;       // Entry address for main (to be set when main is encountered)
    int globalOffset = 0;     // Next available location in global data memory
    int emitLoc = 0;          // Current instruction memory location
    int highEmitLoc = 0;      // Highest instruction location generated so far
    PrintWriter code;         // Output writer for the assembly code

    // Additional instance variables for I/O and frame management
    int inputEntry;           // Entry address for the input routine
    int outputEntry;          // Entry address for the output routine
    int frameOffset = 0;      // Used to allocate storage in the current stack frame

    // Constructor: initialize the output file (e.g., "output.tm")
    public CodeGenerator(String outputFile) {
        try {
            code = new PrintWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------------------------------------------------
       Emit routines to generate TM assembly instructions
       --------------------------------------------------- */

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

    /* ---------------------------------------------------
       Visitor Methods for Code Generation
       --------------------------------------------------- */

    // Assignment expression: generate code for LHS (address) and RHS (value)
    @Override
    public void visit(AssignExp exp, int level, boolean isAddr) {
        emitComment("Begin assignment");
        // Compute address for LHS (isAddr=true)
        exp.lhs.accept(this, level, true);
        // Compute value for RHS (isAddr=false)
        exp.rhs.accept(this, level, false);
        // Emit a store instruction (this is a placeholder; you would use temporary fields in a complete implementation)
        emitRM("ST", AC, 0, 0, "Store RHS value into LHS address");
        emitComment("End assignment");
    }

    // Binary operator expression (e.g., addition)
    @Override
    public void visit(OpExp exp, int level, boolean isAddr) {
        // Evaluate left and right operands to their values
        exp.left.accept(this, level, false);
        exp.right.accept(this, level, false);
        emitComment("OpExp: performing addition (example)");
        // Example: add contents of registers AC and AC1 (in a complete version, you'd manage temporary registers carefully)
        emitRO("ADD", AC, AC, AC1, "Reg0 = Reg0 + Reg1");
    }

    // Integer constant expression
    @Override
    public void visit(IntExp exp, int level, boolean isAddr) {
        emitComment("IntExp: loading constant " + exp.value);
        emitRM("LDC", AC, Integer.parseInt(exp.value), 0, "Load constant " + exp.value);
    }

    // Simple variable: load its address or value depending on isAddr
    @Override
    public void visit(SimpleVar var, int level, boolean isAddr) {
        if (isAddr) {
            emitComment("SimpleVar: computing address of " + var.name);
            // Use nestLevel and offset fields (assumed to be set during semantic analysis)
            int base = (var.nestLevel == 0 ? GP : FP);
            emitRM("LDA", AC, var.offset, base, "Load address of variable " + var.name);
        } else {
            emitComment("SimpleVar: loading value of " + var.name);
            int base = (var.nestLevel == 0 ? GP : FP);
            emitRM("LD", AC, var.offset, base, "Load value of variable " + var.name);
        }
    }

    // Expression list: visit each element in turn
    @Override
    public void visit(ExpList exp, int level, boolean isAddr) {
        while (exp != null) {
            exp.head.accept(this, level, isAddr);
            exp = exp.tail;
        }
    }

    // VarDecList: visit each declaration in the list
    @Override
    public void visit(VarDecList list, int level, boolean isAddr) {
        emitComment("Begin VarDecList");
        while (list != null) {
            list.head.accept(this, level, isAddr);
            list = list.tail;
        }
        emitComment("End VarDecList");
    }

    // Function declaration: record function address and generate code for its body
    @Override
    public void visit(FunctionDec exp, int level, boolean isAddr) {
        emitComment("FunctionDec: " + exp.name);
        exp.funaddr = emitLoc; // Record the start address of the function
        if (exp.name.equals("main")) {
            mainEntry = exp.funaddr; // Set mainEntry if this is the main function
        }
        // In a complete implementation, handle parameters and frame setup here.
        if (exp.body != null) {
            exp.body.accept(this, level + 1, false);
        }
    }

    // Compound expression: process declarations and statements in the block
    @Override
    public void visit(CompoundExp exp, int level, boolean isAddr) {
        emitComment("Begin compound expression");
        if (exp.decs != null)
            exp.decs.accept(this, level + 1, false);
        if (exp.exps != null)
            exp.exps.accept(this, level + 1, false);
        emitComment("End compound expression");
    }

    // Stub implementations for the remaining visitor methods:
    @Override
public void visit(IfExp exp, int level, boolean isAddr) {
    emitComment("Begin if statement");
    
    // Generate code for the test condition
    exp.test.accept(this, level, false);
    
    // Save current emit location for backpatching
    int savedLoc = emitSkip(1);
    
    // Generate code for the then part
    if (exp.thenpart != null) {
        exp.thenpart.accept(this, level, false);
    }
    
    // Save current emit location for backpatching (for else part)
    int savedLoc2 = emitSkip(1);
    
    // Backpatch the jump around the then part
    int currentLoc = emitLoc;
    emitBackup(savedLoc);
    emitRM_Abs("JEQ", AC, currentLoc, "Skip then part if test is false");
    emitRestore();
    
    // Generate code for the else part (if it exists)
    if (exp.elsepart != null) {
        exp.elsepart.accept(this, level, false);
    }
    
    // Backpatch the jump around the else part
    currentLoc = emitLoc;
    emitBackup(savedLoc2);
    emitRM_Abs("LDA", PC, currentLoc, "Jump around else part");
    emitRestore();
    
    emitComment("End if statement");
}
    @Override
    public void visit(TypeExp exp, int level, boolean isAddr) {
        // No code generation needed for type expressions
    }
    @Override
    public void visit(VarDeclExp exp, int level, boolean isAddr) {
        emitComment("VarDeclExp: " + exp.name);
        // In a complete implementation, determine exp.nestLevel and exp.offset here.
    }
    @Override
    public void visit(CallExp exp, int level, boolean isAddr) {
        emitComment("Begin call to " + exp.func);
        
        // Special handling for input/output functions
        if (exp.func.equals("input")) {
            emitRM_Abs("LDA", PC, inputEntry, "Call input function");
            return;
        } else if (exp.func.equals("output")) {
            // Generate code for the argument
            if (exp.args != null) {
                exp.args.accept(this, level, false);
            }
            emitRM("ST", AC, frameOffset--, FP, "Store argument");
            emitRM_Abs("LDA", PC, outputEntry, "Call output function");
            return;
        }
        
        // Compute and store arguments in reverse order
        int argCount = 0;
        int tempOffset = frameOffset;
        
        // First pass to compute argument count
        ExpList args = exp.args;
        while (args != null) {
            argCount++;
            args = args.tail;
        }
        
        // Second pass to generate code for arguments in reverse order
        args = exp.args;
        while (args != null) {
            args.head.accept(this, level, false);
            emitRM("ST", AC, tempOffset--, FP, "Store argument");
            args = args.tail;
        }
        
        // Setup for the call
        emitRM("ST", FP, tempOffset--, FP, "Store old frame pointer");
        emitRM("LDA", FP, tempOffset, FP, "Update frame pointer");
        emitRM("LDA", AC, 1, PC, "Save return address");
        
        // Jump to function
        FunctionDec funcDec = (FunctionDec) exp.dtype;
        emitRM_Abs("LDA", PC, funcDec.funaddr, "Jump to function " + exp.func);
        
        // Restore frame pointer
        emitRM("LD", FP, 0, FP, "Restore frame pointer");
        
        emitComment("End call to " + exp.func);
    }
    @Override
    public void visit(WhileExp exp, int level, boolean isAddr) {
        emitComment("Begin while statement");
        
        // Save the location of the test
        int savedLoc1 = emitLoc;
        
        // Generate code for the test condition
        exp.test.accept(this, level, false);
        
        // Save current emit location for backpatching
        int savedLoc2 = emitSkip(1);
        
        // Generate code for the body
        if (exp.body != null) {
            exp.body.accept(this, level, false);
        }
        
        // Generate code to jump back to the test
        emitRM_Abs("LDA", PC, savedLoc1, "Jump back to while test");
        
        // Backpatch the jump around the body
        int currentLoc = emitLoc;
        emitBackup(savedLoc2);
        emitRM_Abs("JEQ", AC, currentLoc, "Skip while body if test is false");
        emitRestore();
        
        emitComment("End while statement");
    }
    @Override
    public void visit(ReturnExp exp, int level, boolean isAddr) {
        emitComment("Begin return statement");
        
        // Generate code for the return expression (if any)
        if (exp.exp != null) {
            exp.exp.accept(this, level, false);
        }
        
        // Return to caller
        emitRM("LD", PC, retFO, FP, "Return to caller");
        
        emitComment("End return statement");
    }
    @Override
    public void visit(IndexVar var, int level, boolean isAddr) {
        emitComment("Begin array indexing for " + var.name);
        
        // Load base address of the array
        int base = (var.nestLevel == 0 ? GP : FP);
        emitRM("LDA", AC, var.offset, base, "Load base address of array " + var.name);
        
        // Save the base address temporarily
        emitRM("ST", AC, frameOffset--, FP, "Save base address");
        
        // Compute the index
        var.index.accept(this, level, false);
        
        // Load the base address back
        emitRM("LD", AC1, ++frameOffset, FP, "Restore base address");
        
        // Check for index out of bounds (below 0)
        emitRM("JLT", AC, 2, PC, "Jump if index < 0");
        
        // Load the array size
        emitRM("LD", AC1, -1, AC1, "Load array size");
        
        // Check for index out of bounds (above size)
        emitRM("SUB", AC1, AC1, AC, "Compute size - index");
        emitRM("JLE", AC1, 2, PC, "Jump if index >= size");
        
        // Compute the indexed address (base address + index + 1)
        emitRM("LD", AC1, frameOffset, FP, "Reload base address");
        emitRM("LDA", AC, 1, AC, "Add 1 to index (to skip size)");
        emitRM("ADD", AC, AC1, AC, "Compute address = base + index + 1");
        
        if (!isAddr) {
            // Load the value at the computed address
            emitRM("LD", AC, 0, AC, "Load value from array");
        }
        
        emitComment("End array indexing for " + var.name);
    }
    @Override
    public void visit(BoolExp exp, int level, boolean isAddr) {
        emitComment("BoolExp not implemented in this skeleton");
    }
    @Override
    public void visit(NilExp exp, int level, boolean isAddr) {
        emitComment("NilExp: no code generated");
    }

    /* ---------------------------------------------------
       Method to trigger code generation from an AST
       --------------------------------------------------- */
    public void generate(Absyn ast) {
    // Initialize global variables
    mainEntry = -1;
    globalOffset = 0;
    
    // Emit prelude instructions
    emitComment("Prelude");
    emitRM("LD", GP, 0, 0, "Load gp with max address");
    emitRM("LDA", FP, 0, GP, "Set fp = gp");
    emitRM("ST", AC, 0, AC, "Clear value at location 0");
    
    // Save location to jump around I/O routines
    int savedLoc = emitSkip(1);
    
    // Generate I/O routines
    emitComment("Code for input routine");
    inputEntry = emitLoc;
    emitRM("ST", AC, retFO, FP, "Store return address");
    emitRO("IN", AC, 0, 0, "Input integer");
    emitRM("LD", PC, retFO, FP, "Return to caller");
    
    emitComment("Code for output routine");
    outputEntry = emitLoc;
    emitRM("ST", AC, retFO, FP, "Store return address");
    emitRM("LD", AC, paramFO, FP, "Load output value");
    emitRO("OUT", AC, 0, 0, "Output integer");
    emitRM("LD", PC, retFO, FP, "Return to caller");
    
    // Backpatch jump around I/O routines
    int currentLoc = emitLoc;
    emitBackup(savedLoc);
    emitRM_Abs("LDA", PC, currentLoc, "Jump around I/O routines");
    emitRestore();
    
    // Generate code for the AST; pass false to indicate we want the computed value
    ast.accept(this, 0, false);
    
    // Check if main function was found
    if (mainEntry == -1) {
        System.err.println("Error: main function not found");
        return;
    }
    
    // Emit finale instructions
    emitComment("Finale");
    emitRM("ST", FP, globalOffset+ofpFO, FP, "Push old frame pointer");
    emitRM("LDA", FP, globalOffset, FP, "Push new frame");
    emitRM("LDA", AC, 1, PC, "Load return address");
    emitRM_Abs("LDA", PC, mainEntry, "Jump to main");
    emitRM("LD", FP, ofpFO, FP, "Pop frame");
    emitRO("HALT", 0, 0, 0, "Stop execution");
    
    // Close the output file
    code.close();
    }
}
