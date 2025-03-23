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

    // Instance variables for code generation
    int mainEntry = -1;       // Entry address for main (to be set when main is encountered)
    int globalOffset = 0;     // Next available location in global data memory
    int emitLoc = 0;          // Current instruction memory location
    int highEmitLoc = 0;      // Highest instruction location generated so far
    PrintWriter code;         // Output writer for the assembly code

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
        emitComment("IfExp not implemented in this skeleton");
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
        emitComment("CallExp: function call " + exp.func);
        // Set up arguments and generate call instructions here.
    }
    @Override
    public void visit(WhileExp exp, int level, boolean isAddr) {
        emitComment("WhileExp not implemented in this skeleton");
    }
    @Override
    public void visit(ReturnExp exp, int level, boolean isAddr) {
        emitComment("ReturnExp not implemented in this skeleton");
    }
    @Override
    public void visit(IndexVar var, int level, boolean isAddr) {
        emitComment("IndexVar not implemented in this skeleton");
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
        // Emit prelude instructions (initialization)
        emitComment("Prelude");
        emitRM("LD", GP, 0, 0, "Load gp with max address");
        emitRM("LDA", FP, 0, GP, "Set fp = gp");
        emitRM("ST", AC, 0, AC, "Clear value at location 0");

        // Generate code for the AST; pass false to indicate we want the computed value
        ast.accept(this, 0, false);

        // Emit finale instructions (halt execution)
        emitComment("Finale");
        emitRO("HALT", AC, 0, 0, "Stop execution");

        // Close the output file
        code.close();
    }
}
