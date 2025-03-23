/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: SemanticAnalyzer.java
*/

import absyn.*;
import java.util.List;
import java.util.ArrayList;

public class SemanticAnalyzer implements AbsynVisitor {
    private SymbolTable symTable;
    private boolean showScopeChanges;
    private int scopeLevel;
    private SymbolInfo currentFunction;
    private boolean returnFound;

    public SemanticAnalyzer() {
        this(false);
    }
    
    // Constructor with option to show scope changes
    public SemanticAnalyzer(boolean showScopeChanges) {
        symTable = new SymbolTable();
        this.showScopeChanges = showScopeChanges;
        this.scopeLevel = 0;
        this.returnFound = false;

        if (showScopeChanges) {
        System.out.println("Entering global scope:");
        }
        
        symTable.addSymbol("input", new SymbolInfo("input", TypeExp.INT, (List<SymbolInfo>)null));
        symTable.addSymbol("output", new SymbolInfo("output", TypeExp.VOID, (List<SymbolInfo>)null));
    }

    public SymbolTable getSymbolTable() {
        return symTable;
    }

    // Visit a list of declarations.
    public void printGlobalScope() {
        if (showScopeChanges) {
            System.out.println(getIndent(scopeLevel) + "Symbol Table Values:");
            for (SymbolInfo info : symTable.getCurrentScopeSymbols()) {
                System.out.println(getIndent(scopeLevel + 1) + info);
            }
            System.out.println("Leaving global scope.");
        }
    }

    private String getIndent(int level) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < level; i++) {
            s.append("  ");
        }
        return s.toString();
    }

    // Visit a list of declarations.
    public void visit(ExpList expList, int level, boolean isAddr) {
        while(expList != null) {
            expList.head.accept(this, level, isAddr);
            expList = expList.tail;
        }
    }

    // Visit an assignment expression.
    public void visit(AssignExp exp, int level, boolean isAddr) {
        exp.lhs.accept(this, level, isAddr);
        exp.rhs.accept(this, level, isAddr);
        
        int lhsType = getExpressionType(exp.lhs);
        int rhsType = getExpressionType(exp.rhs);
        
        if (lhsType != TypeExp.VOID && rhsType != TypeExp.VOID) {
            if (lhsType != rhsType) {
                System.err.println("Error: Type mismatch in assignment at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            }
        } else {
            System.err.println("Error: Cannot assign to/from void type at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
    }

    // Visit a binary operation expression.
    public void visit(IfExp exp, int level, boolean isAddr) {
        exp.test.accept(this, level, isAddr);
        
        int testType = getExpressionType(exp.test);
        if (testType != TypeExp.INT && testType != TypeExp.BOOL) {
            System.err.println("Error: Test condition in if statement must be int or bool at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
        
        exp.thenpart.accept(this, level, isAddr);
        if(exp.elsepart != null) {
            exp.elsepart.accept(this, level, isAddr);
        }
    }

    public void visit(IntExp exp, int level, boolean isAddr) {
        
    }

    // Visit a binary operation expression.
    public void visit(OpExp exp, int level, boolean isAddr) {
        if(exp.left != null)
            exp.left.accept(this, level, isAddr);
        if(exp.right != null)
            exp.right.accept(this, level, isAddr);
        
        switch (exp.op) {
            case OpExp.PLUS:
            case OpExp.MINUS:
            case OpExp.TIMES:
            case OpExp.OVER:
                if (exp.left != null && exp.right != null) {
                    int leftType = getExpressionType(exp.left);
                    int rightType = getExpressionType(exp.right);
                    
                    if (leftType != TypeExp.INT || rightType != TypeExp.INT) {
                        System.err.println("Error: Type mismatch in operand at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
                
            case OpExp.LT:
            case OpExp.GT:
            case OpExp.LTE:
            case OpExp.GTE:
            case OpExp.EQ:
            case OpExp.NEQ:
                if (exp.left != null && exp.right != null) {
                    int leftType = getExpressionType(exp.left);
                    int rightType = getExpressionType(exp.right);
                    
                    if (leftType != rightType) {
                        System.err.println("Error: Comparison operations require operands of the same type at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
                
            case OpExp.AND:
            case OpExp.OR:
                if (exp.left != null && exp.right != null) {
                    int leftType = getExpressionType(exp.left);
                    int rightType = getExpressionType(exp.right);
                    
                    if (leftType != TypeExp.BOOL || rightType != TypeExp.BOOL) {
                        System.err.println("Error: Logical operations require boolean operands at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
                
            case OpExp.NOT:
                if (exp.right != null) {
                    int rightType = getExpressionType(exp.right);
                    
                    if (rightType != TypeExp.BOOL) {
                        System.err.println("Error: Logical NOT operation requires a boolean operand at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
                
            case OpExp.UMINUS:
                if (exp.right != null) {
                    int rightType = getExpressionType(exp.right);
                    
                    if (rightType != TypeExp.INT) {
                        System.err.println("Error: Unary minus operation requires an integer operand at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
        }
    }

    public void visit(TypeExp exp, int level, boolean isAddr) {
        
    }

    // For a variable declaration:
    public void visit(VarDeclExp exp, int level, boolean isAddr) {
        exp.type.accept(this, level, isAddr);
        
        if (exp.size == 0) {
            System.err.println("Error: Array size cannot be 0 at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }

        boolean isArray = exp.size != -1 || exp.type.isArray;
        
        boolean added = symTable.addSymbol(exp.name, new SymbolInfo(exp.name, exp.type.type, isArray));
        if (!added) {
            System.err.println("Error: Redeclaration of variable '" + exp.name + "' at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
    }

    // For a function declaration:
    public void visit(FunctionDec exp, int level, boolean isAddr) {
        List<SymbolInfo> paramList = new ArrayList<>();
        
        VarDecList params = exp.params;
        while (params != null) {
            VarDeclExp param = params.head;
            paramList.add(new SymbolInfo(
                param.name, 
                param.type.type, 
                param.type.isArray || param.size != -1
            ));
            params = params.tail;
        }
        
        boolean added = symTable.addSymbol(exp.name, new SymbolInfo(exp.name, exp.result.type, paramList));
        if (!added) {
            System.err.println("Error: Redeclaration of function '" + exp.name + "' at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
        
        if (showScopeChanges) {
            System.out.println(getIndent(scopeLevel) + "Function: " + exp.name);
            System.out.println(getIndent(scopeLevel) + "Entering function scope:");
        }
        symTable.enterScope();
        scopeLevel++;
        
        SymbolInfo previousFunction = currentFunction;
        currentFunction = symTable.lookup(exp.name);
        
        returnFound = false;
        
        if(exp.params != null) {
            exp.params.accept(this, level+1, isAddr);
        }
        
        // Visit the function body
        if(exp.body != null) {
            exp.body.accept(this, level + 1, isAddr);
            if (showScopeChanges) {
                System.out.println(getIndent(scopeLevel) + "Symbol Table Values:");
                for (SymbolInfo info : symTable.getCurrentScopeSymbols()) {
                    System.out.println(getIndent(scopeLevel + 1) + info);
                }
            }
            if (exp.result.type != TypeExp.VOID && !returnFound) {
                System.err.println("Error: Non-void function '" + exp.name 
                    + "' may not return a value in all paths at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            }
        }
        
        // Show symbol table before leaving the scope
        symTable.exitScope();
        scopeLevel--;
        if (showScopeChanges) {
            System.out.println(getIndent(scopeLevel) + "Leaving function scope");
        }
        currentFunction = previousFunction;
    }

    // For a compound expression:
    public void visit(CompoundExp exp, int level, boolean isAddr) {
        scopeLevel++;
        if (showScopeChanges) {
            System.out.println(getIndent(scopeLevel-1) + "Entering new block scope");
        }
        symTable.enterScope();
        
        if(exp.decs != null)
            exp.decs.accept(this, level+1, isAddr);
        if(exp.exps != null)
            exp.exps.accept(this, level+1, isAddr);
        
        if (showScopeChanges) {
            System.out.println(getIndent(scopeLevel-1) + "Symbol table at exit from block:");
            for (SymbolInfo info : symTable.getCurrentScopeSymbols()) {
                System.out.println(getIndent(scopeLevel) + info);
            }
            System.out.println(getIndent(scopeLevel-1) + "Leaving block scope");
        }
        
        symTable.exitScope();
        scopeLevel--;
    }

    // For a call expression:
    public void visit(CallExp exp, int level, boolean isAddr) {
        SymbolInfo funcInfo = symTable.lookup(exp.func);
        if (funcInfo == null) {
            System.err.println("Error: Undefined function '" + exp.func + "' at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            if (exp.args != null) {
                exp.args.accept(this, level, isAddr); // Still check argument expressions
            }
            return;
        }
        
        if (exp.args != null) {
            exp.args.accept(this, level, isAddr);
        }
        
        if (funcInfo.parameters != null) {
            int expectedArgCount = funcInfo.parameters.size();
            int actualArgCount = countArguments(exp.args);
            
            if (expectedArgCount != actualArgCount) {
                System.err.println("Error: Function '" + exp.func + "' expects " + expectedArgCount + 
                                  " arguments, but got " + actualArgCount + 
                                  " at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            } else if (actualArgCount > 0) {
                // Check argument types if count matches
                ExpList argList = exp.args;
                int argIndex = 0;
                
                while (argList != null && argIndex < funcInfo.parameters.size()) {
                    int argType = getExpressionType(argList.head);
                    int expectedType = funcInfo.parameters.get(argIndex).type;
                    
                    if (argType != expectedType && argType != TypeExp.VOID && expectedType != TypeExp.VOID) {
                        System.err.println("Error: Argument " + (argIndex+1) + " of function '" + exp.func + 
                                         "' has wrong type. Expected " + typeToString(expectedType) + 
                                         " but got " + typeToString(argType) + 
                                         " at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                    
                    argList = argList.tail;
                    argIndex++;
                }
            }
        }
        
        // Special handling for built-in functions
        if (exp.func.equals("output")) {
            ExpList args = exp.args;
            while (args != null) {
                int argType = getExpressionType(args.head);
                if (argType != TypeExp.INT) {
                    System.err.println("Error: output function requires integer arguments at line " + 
                                      (exp.row + 1) + ", column " + (exp.col + 1));
                }
                args = args.tail;
            }
        }
    }

    // For a list of expressions.
    public void visit(WhileExp exp, int level, boolean isAddr) {
        exp.test.accept(this, level, isAddr);
        
        int testType = getExpressionType(exp.test);
        if (testType != TypeExp.INT && testType != TypeExp.BOOL) {
            System.err.println("Error: Test condition in while statement must be int or bool at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
        
        exp.body.accept(this, level, isAddr);
    }

    // For a list of return expressions.
    public void visit(ReturnExp exp, int level, boolean isAddr) {
        if (exp.exp != null) {
            exp.exp.accept(this, level, isAddr);
            
            // Check return type matches function declaration
            if (currentFunction != null) {
                int returnType = getExpressionType(exp.exp);
                if (returnType != currentFunction.type) {
                    System.err.println("Error: Return type mismatch in function '" + currentFunction.name + 
                                     "'. Expected " + typeToString(currentFunction.type) + 
                                     " but got " + typeToString(returnType) + 
                                     " at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                }
            }
            
            returnFound = true;
        } else {
            // Empty return statement
            if (currentFunction != null && currentFunction.type != TypeExp.VOID) {
                System.err.println("Error: Non-void function '" + currentFunction.name + 
                                 "' must return a value at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            }
        }
    }

    // For a list of VarDecList.
    public void visit(VarDecList list, int level, boolean isAddr) {
        while(list != null) {
            list.head.accept(this, level, isAddr);
            list = list.tail;
        }
    }

    // For an array variable access.
    public void visit(IndexVar var, int level, boolean isAddr) {
        SymbolInfo info = symTable.lookup(var.name);
        if(info == null) {
            System.err.println("Error: Undefined array '" + var.name + "' at line " + (var.row + 1) + ", column " + (var.col + 1));
        } else if(!info.isArray) {
            System.err.println("Error: Variable '" + var.name + "' is not an array at line " + (var.row + 1) + ", column " + (var.col + 1));
        }
        
        var.index.accept(this, level, isAddr);
        
        int indexType = getExpressionType(var.index);
        if (indexType != TypeExp.INT) {
            System.err.println("Error: Array index must be an integer at line " + (var.row + 1) + ", column " + (var.col + 1));
        }
    }

    // For a simple variable usage.
    public void visit(SimpleVar var, int level, boolean isAddr) {
        SymbolInfo info = symTable.lookup(var.name);
        if(info == null) {
            System.err.println("Error: Undefined variable '" + var.name + "' at line " + (var.row + 1) + ", column " + (var.col + 1));
        }
    }

    public void visit(NilExp exp, int level, boolean isAddr) {
        
    }

    public void visit(BoolExp exp, int level, boolean isAddr) {
        
    }
    
    // Helper method to count the number of arguments in a list
    private int countArguments(ExpList args) {
        int count = 0;
        ExpList current = args;
        while (current != null) {
            count++;
            current = current.tail;
        }
        return count;
    }
    
    // Helper method to convert a type integer to a string
    private String typeToString(int type) {
        switch (type) {
            case TypeExp.INT: return "int";
            case TypeExp.BOOL: return "bool";
            case TypeExp.VOID: return "void";
            default: return "unknown";
        }
    }
    
    // Helper method to determine the type of an expression
    private int getExpressionType(Exp exp) {
        if (exp instanceof IntExp) {
            return TypeExp.INT;
        } else if (exp instanceof BoolExp) {
            return TypeExp.BOOL;
        } else if (exp instanceof SimpleVar) {
            SimpleVar var = (SimpleVar) exp;
            SymbolInfo info = symTable.lookup(var.name);
            if (info != null) {
                return info.type;
            }
        } else if (exp instanceof IndexVar) {
            IndexVar var = (IndexVar) exp;
            SymbolInfo info = symTable.lookup(var.name);
            if (info != null) {
                return info.type;
            }
        } else if (exp instanceof CallExp) {
            CallExp call = (CallExp) exp;
            SymbolInfo info = symTable.lookup(call.func);
            if (info != null) {
                return info.type;
            }
        } else if (exp instanceof OpExp) {
            OpExp op = (OpExp) exp;
            switch (op.op) {
                case OpExp.PLUS:
                case OpExp.MINUS:
                case OpExp.TIMES:
                case OpExp.OVER:
                case OpExp.UMINUS:
                    return TypeExp.INT;
                case OpExp.LT:
                case OpExp.GT:
                case OpExp.LTE:
                case OpExp.GTE:
                case OpExp.EQ:
                case OpExp.NEQ:
                case OpExp.AND:
                case OpExp.OR:
                case OpExp.NOT:
                    return TypeExp.BOOL;
            }
        }
        
        return TypeExp.INT;
    }
}