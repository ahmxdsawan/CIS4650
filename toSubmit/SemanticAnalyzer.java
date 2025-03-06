import absyn.*;
import java.util.*;

public class SemanticAnalyzer implements AbsynVisitor {

    // Variables
    private boolean hasErrors;
    private SymbolTable symbolTable;
    final static int SPACES = 4;
    private int indentLevel;
    private String currentFunction;
    private int currentFunctionReturnType;

    // Type constants
    public final static int INT_TYPE = 0;
    public final static int VOID_TYPE = 1;
    public final static int BOOL_TYPE = 2;
    public final static int ERROR_TYPE = -1;
    
    // 
    // CONSTRUCTOR
    // 
    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.hasErrors = false;
        this.indentLevel = 0;
        this.currentFunction = null;
    }

    private void printIndented(String message) {
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("  ");
        }
        System.out.println(message);
    }

    // 
    // ERROR HANDLING
    // 
    public boolean hasErrors() {
        return hasErrors;
    }

    // Method to report semantic errors
    private void reportError(int line, int column, String message) {
        System.err.println("Error at line " + line + ", column " + column + ": " + message);
        hasErrors = true;
    }

    // 
    // TYPE UTILITIES
    // 
    public static String getTypeName(int type) {
        switch (type) {
            case INT_TYPE: return "int";
            case VOID_TYPE: return "void";
            case BOOL_TYPE: return "bool";
            case ERROR_TYPE: return "error";
            default: return "unknown";
        }
    }
    
    private int getExpType(Exp exp) {
        if (exp instanceof IntExp) {
            return INT_TYPE;
        } else if (exp instanceof BoolExp) {
            return BOOL_TYPE;
        } else if (exp instanceof OpExp) {
            return INT_TYPE; // Assuming operations result in int
        } else if (exp instanceof Var) {
            // Fix for Var - get the Var object correctly
            Var variable = ((Var) exp); // Use the correct field name for your Var class
            
            if (variable instanceof SimpleVar) {
                Entry symbol = symbolTable.lookup(((SimpleVar) variable).name);
                if (symbol != null) {
                    return symbol.type;
                }
            } else if (variable instanceof IndexVar) {
                Entry symbol = symbolTable.lookup(((IndexVar) variable).name);
                if (symbol != null) {
                    return symbol.type;
                }
            }
        } else if (exp instanceof CallExp) {
            CallExp callExp = (CallExp) exp;
            Entry symbol = symbolTable.lookup(callExp.func);
            if (symbol != null && symbol.isFunction) {
                return symbol.type;
            }
        }
        return ERROR_TYPE;
    }
    
    private boolean isExpArray(Exp exp) {
        if (exp instanceof Var) {
            // Fix for Var - get the Var object correctly
            Var variable = ((Var) exp); // Use the correct field name for your Var class
            
            if (variable instanceof SimpleVar) {
                Entry symbol = symbolTable.lookup(((SimpleVar) variable).name);
                return symbol != null && symbol.isArray;
            }
        }
        return false;
    }

    // 
    // VISIT METHODS
    // 
    @Override
    public void visit(ExpList expList, int level) {
        while (expList != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        }
    }

    @Override
    public void visit(AssignExp exp, int level) {
        // Visit variable (left side)
        exp.lhs.accept(this, level);
        
        // Visit expression (right side)
        exp.rhs.accept(this, level);
        
        // Get types
        int lhsType = ERROR_TYPE;
        int rhsType = ERROR_TYPE;
        boolean lhsIsArray = false;
        
        // Check if lhs is a variable
        if (exp.lhs instanceof SimpleVar) {
            SimpleVar var = (SimpleVar) exp.lhs;
            Entry symbol = symbolTable.lookup(var.name);
            if (symbol != null) {
                lhsType = symbol.type;
                lhsIsArray = symbol.isArray;
            }
        } else if (exp.lhs instanceof IndexVar) {
            IndexVar var = (IndexVar) exp.lhs;
            Entry symbol = symbolTable.lookup(var.name);
            if (symbol != null && symbol.isArray) {
                lhsType = symbol.type;
                lhsIsArray = false; // We're accessing an element, not the array itself
            }
        }
        
        // Get rhs type
        if (exp.rhs instanceof Var) {
            Var var = ((Var) exp.rhs); // Use the correct field name
            if (var instanceof SimpleVar) {
                Entry symbol = symbolTable.lookup(((SimpleVar) var).name);
                if (symbol != null) {
                    rhsType = symbol.type;
                    if (symbol.isArray) {
                        rhsType = ERROR_TYPE; // Can't assign array to variable
                        reportError(exp.row, exp.col, "Cannot assign array to variable");
                    }
                }
            } else if (var instanceof IndexVar) {
                IndexVar indexVar = (IndexVar) var;
                Entry symbol = symbolTable.lookup(indexVar.name);
                if (symbol != null && symbol.isArray) {
                    rhsType = symbol.type;
                }
            }
        } else if (exp.rhs instanceof IntExp) {
            rhsType = INT_TYPE;
        } else if (exp.rhs instanceof BoolExp) {
            rhsType = BOOL_TYPE;
        } else if (exp.rhs instanceof OpExp) {
            rhsType = INT_TYPE; // Assuming operations result in int
        } else if (exp.rhs instanceof CallExp) {
            CallExp callExp = (CallExp) exp.rhs;
            Entry funcSymbol = symbolTable.lookup(callExp.func);
            if (funcSymbol != null && funcSymbol.isFunction) {
                rhsType = funcSymbol.type;
            }
        }
        
        // Type checking
        if (lhsType == VOID_TYPE) {
            reportError(exp.row, exp.col, "Cannot assign to void type");
        } else if (lhsIsArray) {
            reportError(exp.row, exp.col, "Cannot assign to array, only to array elements");
        } else if (lhsType != rhsType && lhsType != ERROR_TYPE && rhsType != ERROR_TYPE) {
            reportError(exp.row, exp.col, "Type mismatch in assignment: " + 
                       getTypeName(rhsType) + " cannot be assigned to " + getTypeName(lhsType));
        }
    }

    @Override
    public void visit(IfExp exp, int level) {
        // Visit test expression
        exp.test.accept(this, level);
        
        // Check if test expression is boolean/int
        if (exp.test instanceof OpExp) {
            // Valid for condition
        } else if (exp.test instanceof IntExp) {
            // Valid for condition (non-zero is true)
        } else if (exp.test instanceof BoolExp) {
            // Valid for condition
        } else if (exp.test instanceof Var) {
            // Check variable type
            Var var = ((Var) exp.test); // Use the correct field name
            if (var instanceof SimpleVar) {
                Entry symbol = symbolTable.lookup(((SimpleVar) var).name);
                if (symbol != null && symbol.type != INT_TYPE && symbol.type != BOOL_TYPE) {
                    reportError(exp.row, exp.col, "Condition must be of type int or bool");
                }
            }
        } else {
            reportError(exp.row, exp.col, "Invalid type for if condition");
        }
        
        // Visit then branch
        exp.thenpart.accept(this, level);
        
        // Visit else branch if it exists
        if (exp.elsepart != null) {
            exp.elsepart.accept(this, level);
        }
    }

    @Override
    public void visit(IntExp exp, int level) {
        // Nothing to do, int literals are always valid
    }

    @Override
    public void visit(OpExp exp, int level) {
        // Visit left and right operands
        exp.left.accept(this, level);
        exp.right.accept(this, level);
        
        // Check operand types
        int leftType = getExpType(exp.left);
        int rightType = getExpType(exp.right);
        
        // Check for array operands
        boolean leftIsArray = isExpArray(exp.left);
        boolean rightIsArray = isExpArray(exp.right);
        
        if (leftIsArray || rightIsArray) {
            reportError(exp.row, exp.col, "Cannot use arrays in operations");
            return;
        }
        
        // Check for void operands
        if (leftType == VOID_TYPE || rightType == VOID_TYPE) {
            reportError(exp.row, exp.col, "Cannot use void type in operations");
            return;
        }
        
        // For arithmetic operations
        if (exp.op == OpExp.PLUS || exp.op == OpExp.MINUS || 
            exp.op == OpExp.TIMES || exp.op == OpExp.OVER) {
            if (leftType != INT_TYPE || rightType != INT_TYPE) {
                reportError(exp.row, exp.col, "Arithmetic operations require integer operands");
            }
        }
        
        // For comparison operations
        if (exp.op == OpExp.LT || exp.op == OpExp.LTE || 
            exp.op == OpExp.GT || exp.op == OpExp.GTE || 
            exp.op == OpExp.EQ || exp.op == OpExp.NEQ) {
            if (leftType != rightType && leftType != ERROR_TYPE && rightType != ERROR_TYPE) {
                reportError(exp.row, exp.col, "Comparison requires operands of the same type");
            }
        }
    }

    @Override
    public void visit(TypeExp exp, int level) {
        // Type expressions are handled in VarDeclExp/FunctionDec
    }

    @Override
    public void visit(VarDeclExp exp, int level) {
        String name = exp.name;
        int type = exp.type.type;
        boolean isArray = exp.size > 0;
        int arraySize = exp.size;
        
        // Check for void type
        if (type == VOID_TYPE) {
            reportError(exp.row, exp.col, "Variable '" + name + "' cannot have type void");
            return;
        }
        
        // Check for array with size <= 0
        if (isArray && arraySize <= 0) {
            reportError(exp.row, exp.col, "Array '" + name + "' must have a positive size");
            return;
        }
        
        // Check if variable is already defined in current scope
        if (symbolTable.lookupCurrentScope(name) != null) {
            reportError(exp.row, exp.col, "Variable '" + name + "' is already defined in this scope");
            return;
        }
        
        // Add variable to symbol table
        Entry symbol = new Entry(name, type, exp.row, exp.col, isArray, arraySize);
        symbolTable.insert(symbol);
    }

    @Override
    public void visit(FunctionDec exp, int level) {
        String name = exp.name;
        int returnType = exp.result.type;
        
        // Check if function is already defined
        if (symbolTable.lookupCurrentScope(name) != null) {
            reportError(exp.row, exp.col, "Function '" + name + "' is already defined");
            return;
        }
        
        // Create parameter list
        ArrayList<Entry> parameters = new ArrayList<>();
        VarDecList params = exp.params;
        while (params != null) {
            VarDeclExp param = params.head;
            String paramName = param.name;
            int paramType = param.type.type;
            boolean isArray = param.size > 0;
            
            // Check for void parameters
            if (paramType == VOID_TYPE && !isArray) {
                reportError(param.row, param.col, "Parameter '" + paramName + "' cannot have type void");
                params = params.tail;
                continue;
            }
            
            // Check for duplicate parameter names
            boolean isDuplicate = false;
            for (Entry s : parameters) {
                if (s.name.equals(paramName)) {
                    reportError(param.row, param.col, "Duplicate parameter name '" + paramName + "'");
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                Entry paramSymbol = new Entry(paramName, paramType, param.row, param.col, isArray, 0);
                parameters.add(paramSymbol);
            }
            
            params = params.tail;
        }
        
        // Create function symbol and add to symbol table
        Entry funcSymbol = new Entry(name, returnType, exp.row, exp.col, parameters);
        symbolTable.insert(funcSymbol);
        
        // Save current function context
        String previousFunction = currentFunction;
        int previousReturnType = currentFunctionReturnType;
        currentFunction = name;
        currentFunctionReturnType = returnType;
        
        // Enter function scope
        symbolTable.enterScope();
        printIndented("Entering function '" + name + "' scope");
        indentLevel++;
        
        // Add parameters to function scope
        for (Entry param : parameters) {
            symbolTable.insert(param);
        }
        
        // Visit function body
        if (exp.body != null) {
            exp.body.accept(this, level);
            visitFunctionBody(exp.body, level);  // Custom method for function bodies
        }
        
        // Exit function scope
        indentLevel--;
        System.out.println("Function '" + name + "' Symbol Table:");
        symbolTable.printCurrentScope();
        printIndented("Leaving function '" + name + "' scope");
        symbolTable.exitScope();
        
        // Restore previous function context
        currentFunction = previousFunction;
        currentFunctionReturnType = previousReturnType;
    }

    @Override
    public void visit(CompoundExp exp, int level) {
        // Always create a new scope for compound statements
        // since we handle function bodies separately
        symbolTable.enterScope();
        printIndented("Entering new block scope");
        indentLevel++;
        
        // Visit all local declarations
        VarDecList decls = exp.decs;
        while (decls != null) {
            decls.head.accept(this, level);
            decls = decls.tail;
        }
        
        // Visit all statements
        ExpList stmts = exp.exps;
        while (stmts != null) {
            stmts.head.accept(this, level);
            stmts = stmts.tail;
        }
        
        // Exit scope
        indentLevel--;
        System.out.println("Block Symbol Table:");
        symbolTable.printCurrentScope();
        printIndented("Leaving block scope");
        symbolTable.exitScope();
    }

    @Override
    public void visit(CallExp exp, int level) {
        String funcName = exp.func;
        Entry funcSymbol = symbolTable.lookup(funcName);
        
        // Check if function exists
        if (funcSymbol == null) {
            reportError(exp.row, exp.col, "Undefined function '" + funcName + "'");
            return;
        }
        
        // Check if it's actually a function
        if (!funcSymbol.isFunction) {
            reportError(exp.row, exp.col, "'" + funcName + "' is not a function");
            return;
        }
        
        // Check argument count
        ArrayList<Entry> params = funcSymbol.parameters;
        int paramCount = params != null ? params.size() : 0;
        
        int argCount = 0;
        ExpList args = exp.args;
        while (args != null) {
            argCount++;
            args = args.tail;
        }
        
        if (paramCount != argCount) {
            reportError(exp.row, exp.col, "Function '" + funcName + "' expects " + 
                       paramCount + " arguments, but got " + argCount);
            return;
        }
        
        // Type check arguments
        args = exp.args;
        for (int i = 0; args != null && i < paramCount; i++) {
            Exp arg = args.head;
            arg.accept(this, level);  // Type check the argument
            
            Entry param = params.get(i);
            int paramType = param.type;
            boolean paramIsArray = param.isArray;
            
            int argType = getExpType(arg);
            boolean argIsArray = isExpArray(arg);
            
            // Check type compatibility
            if (paramIsArray != argIsArray) {
                reportError(arg instanceof NilExp ? exp.row : ((Absyn)arg).row, 
                           arg instanceof NilExp ? exp.col : ((Absyn)arg).col, 
                           "Argument " + (i + 1) + " type mismatch: expected " + 
                           (paramIsArray ? "array" : "non-array") + ", got " + 
                           (argIsArray ? "array" : "non-array"));
            } else if (paramType != argType && argType != ERROR_TYPE) {
                reportError(arg instanceof NilExp ? exp.row : ((Absyn)arg).row, 
                           arg instanceof NilExp ? exp.col : ((Absyn)arg).col, 
                           "Argument " + (i + 1) + " type mismatch: expected " + 
                           getTypeName(paramType) + ", got " + getTypeName(argType));
            }
            
            args = args.tail;
        }
    }

    @Override
    public void visit(WhileExp exp, int level) {
        // Visit condition
        exp.test.accept(this, level);
        
        // Check if condition is boolean/int
        if (exp.test instanceof OpExp) {
            // Valid for condition
        } else if (exp.test instanceof IntExp) {
            // Valid for condition (non-zero is true)
        } else if (exp.test instanceof BoolExp) {
            // Valid for condition
        } else if (exp.test instanceof Var) {
            // Check variable type
            Var var = ((Var) exp.test);
            if (var instanceof SimpleVar) {
                Entry symbol = symbolTable.lookup(((SimpleVar) var).name);
                if (symbol != null && symbol.type != INT_TYPE && symbol.type != BOOL_TYPE) {
                    reportError(exp.row, exp.col, "Condition must be of type int or bool");
                }
            }
        } else {
            reportError(exp.row, exp.col, "Invalid type for while condition");
        }
        
        // Visit body
        exp.body.accept(this, level);
    }

    @Override
    public void visit(ReturnExp exp, int level) {
        // Check if return is inside a function
        if (currentFunction == null) {
            reportError(exp.row, exp.col, "Return statement must be inside a function");
            return;
        }
        
        // Check return type matches function return type
        if (exp.exp == null) {
            // Return with no expression
            if (currentFunctionReturnType != VOID_TYPE) {
                reportError(exp.row, exp.col, "Function '" + currentFunction + 
                           "' must return a value of type " + getTypeName(currentFunctionReturnType));
            }
        } else {
            // Return with expression
            exp.exp.accept(this, level);
            int actualType = getExpType(exp.exp);
            
            if (currentFunctionReturnType == VOID_TYPE) {
                reportError(exp.row, exp.col, "Void function '" + currentFunction + 
                           "' cannot return a value");
            } else if (actualType != currentFunctionReturnType && actualType != ERROR_TYPE) {
                reportError(exp.row, exp.col, "Return type mismatch: expected " + 
                           getTypeName(currentFunctionReturnType) + ", got " + getTypeName(actualType));
            }
        }
    }

    @Override
    public void visit(VarDecList list, int level) {
        while (list != null) {
            list.head.accept(this, level);
            list = list.tail;
        }
    }

    @Override
    public void visit(IndexVar var, int level) {
        // Visit index expression
        var.index.accept(this, level);
        
        // Check if array exists
        Entry symbol = symbolTable.lookup(var.name);
        if (symbol == null) {
            reportError(var.row, var.col, "Undefined array '" + var.name + "'");
            return;
        }
        
        // Check if it's actually an array
        if (!symbol.isArray) {
            reportError(var.row, var.col, "'" + var.name + "' is not an array");
            return;
        }
        
        // Check if index is an integer
        int indexType = getExpType(var.index);
        if (indexType != INT_TYPE) {
            reportError(var.index instanceof NilExp ? var.row : ((Absyn)var.index).row,
                       var.index instanceof NilExp ? var.col : ((Absyn)var.index).col,
                       "Array index must be an integer");
        }
    }

    @Override
    public void visit(SimpleVar var, int level) {
        // Check if variable exists
        Entry symbol = symbolTable.lookup(var.name);
        if (symbol == null) {
            reportError(var.row, var.col, "Undefined variable '" + var.name + "'");
        }
    }

    @Override
    public void visit(NilExp exp, int level) {
        // Nothing to do, null expressions are checked elsewhere
    }

    @Override
    public void visit(BoolExp exp, int level) {
        // Nothing to do, boolean literals are always valid
    }

    private void visitFunctionBody(CompoundExp exp, int level) {
        // No need to create a new scope here since we already did in the function visit
        
        // Visit all local declarations
        VarDecList decls = exp.decs;
        while (decls != null) {
            decls.head.accept(this, level);
            decls = decls.tail;
        }
        
        // Visit all statements
        ExpList stmts = exp.exps;
        while (stmts != null) {
            stmts.head.accept(this, level);
            stmts = stmts.tail;
        }
    }
}