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
    
    // Keep track of the current function we're analyzing for return type checking
    private SymbolInfo currentFunction;
    private boolean returnFound;

    public SemanticAnalyzer() {
        this(false);
    }
    
    public SemanticAnalyzer(boolean showScopeChanges) {
        symTable = new SymbolTable();
        this.showScopeChanges = showScopeChanges;
        this.scopeLevel = 0;
        this.returnFound = false;

        if (showScopeChanges) {
        System.out.println("Entering global scope:");
        }
        
        // Insert predefined functions into the global scope with null parameters list
        symTable.addSymbol("input", new SymbolInfo("input", TypeExp.INT, (List<SymbolInfo>)null));
        symTable.addSymbol("output", new SymbolInfo("output", TypeExp.VOID, (List<SymbolInfo>)null));
    }

    public SymbolTable getSymbolTable() {
        return symTable;
    }

     // Print the current (global) scope at the end.
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

    // Visit an expression list.
    public void visit(ExpList expList, int level) {
        while(expList != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        }
    }

    // Assignment: visit both sides and check types.
    public void visit(AssignExp exp, int level) {
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
        
        // Type checking for assignment
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

    public void visit(IfExp exp, int level) {
        exp.test.accept(this, level);
        
        // Check that the test expression is int or bool
        int testType = getExpressionType(exp.test);
        if (testType != TypeExp.INT && testType != TypeExp.BOOL) {
            System.err.println("Error: Test condition in if statement must be int or bool at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
        
        exp.thenpart.accept(this, level);
        if(exp.elsepart != null) {
            exp.elsepart.accept(this, level);
        }
    }

    public void visit(IntExp exp, int level) {
        // Nothing to do for an integer literal.
    }

    public void visit(OpExp exp, int level) {
        if(exp.left != null)
            exp.left.accept(this, level);
        if(exp.right != null)
            exp.right.accept(this, level);
        
        // Type checking for operations
        switch (exp.op) {
            case OpExp.PLUS:
            case OpExp.MINUS:
            case OpExp.TIMES:
            case OpExp.OVER:
                // Both operands must be integers
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
                // Both operands must be of the same type (int or bool)
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
                // Both operands must be boolean
                if (exp.left != null && exp.right != null) {
                    int leftType = getExpressionType(exp.left);
                    int rightType = getExpressionType(exp.right);
                    
                    if (leftType != TypeExp.BOOL || rightType != TypeExp.BOOL) {
                        System.err.println("Error: Logical operations require boolean operands at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
                
            case OpExp.NOT:
                // Operand must be boolean
                if (exp.right != null) {
                    int rightType = getExpressionType(exp.right);
                    
                    if (rightType != TypeExp.BOOL) {
                        System.err.println("Error: Logical NOT operation requires a boolean operand at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
                
            case OpExp.UMINUS:
                // Operand must be integer
                if (exp.right != null) {
                    int rightType = getExpressionType(exp.right);
                    
                    if (rightType != TypeExp.INT) {
                        System.err.println("Error: Unary minus operation requires an integer operand at line " + (exp.row + 1) + ", column " + (exp.col + 1));
                    }
                }
                break;
        }
    }

    public void visit(TypeExp exp, int level) {
        // Nothing to do.
    }

    // For a variable declaration, insert the variable into the symbol table.
    public void visit(VarDeclExp exp, int level) {
        exp.type.accept(this, level);
        
        // Check if array size is valid
        if (exp.size == 0) {
            System.err.println("Error: Array size cannot be 0 at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }

        // if (exp.init != null){
        //     exp.init.accept(this, level);
            
        //     // Type checking for initialization
        //     int varType = exp.type.type;
        //     int initType = getExpressionType(exp.init);
            
        //     if (varType != TypeExp.VOID && initType != TypeExp.VOID) {
        //         if (varType != initType) {
        //             System.err.println("Error: Type mismatch in variable initialization at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        //         }
        //     } else {
        //         System.err.println("Error: Cannot assign to/from void type at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        //     }
        // }
        
        // For parameters, we need to check the type directly
        boolean isArray = exp.size != -1 || exp.type.isArray;
        
        boolean added = symTable.addSymbol(exp.name, new SymbolInfo(exp.name, exp.type.type, isArray));
        if (!added) {
            System.err.println("Error: Redeclaration of variable '" + exp.name + "' at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
    }

    // For a function declaration:
    public void visit(FunctionDec exp, int level) {
        // Collect parameter information
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
        
        // Add function to symbol table with parameter info
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
        
        // Save current function for return type checking
        SymbolInfo previousFunction = currentFunction;
        currentFunction = symTable.lookup(exp.name);
        
        // Reset return tracking for each function
        returnFound = false;
        
        // Process parameters - add them to the function's scope
        if(exp.params != null) {
            exp.params.accept(this, level+1);
        }
        
        // Visit the function body
        if(exp.body != null) {
            exp.body.accept(this, level + 1);
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

    // For a compound expression, create a new scope.
    public void visit(CompoundExp exp, int level) {
        scopeLevel++;
        if (showScopeChanges) {
            System.out.println(getIndent(scopeLevel-1) + "Entering new block scope");
        }
        symTable.enterScope();
        
        if(exp.decs != null)
            exp.decs.accept(this, level+1);
        if(exp.exps != null)
            exp.exps.accept(this, level+1);
        
        // Show symbol table before leaving the scope
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

    public void visit(CallExp exp, int level) {
        // Lookup the function in the symbol table
        SymbolInfo funcInfo = symTable.lookup(exp.func);
        if (funcInfo == null) {
            System.err.println("Error: Undefined function '" + exp.func + "' at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            if (exp.args != null) {
                exp.args.accept(this, level); // Still check argument expressions
            }
            return;
        }
        
        // Process all arguments
        if (exp.args != null) {
            exp.args.accept(this, level);
        }
        
        // Check argument counts and types
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

    public void visit(WhileExp exp, int level) {
        exp.test.accept(this, level);
        
        // Check that the test expression is int or bool
        int testType = getExpressionType(exp.test);
        if (testType != TypeExp.INT && testType != TypeExp.BOOL) {
            System.err.println("Error: Test condition in while statement must be int or bool at line " + (exp.row + 1) + ", column " + (exp.col + 1));
        }
        
        exp.body.accept(this, level);
    }

    public void visit(ReturnExp exp, int level) {
        if (exp.exp != null) {
            exp.exp.accept(this, level);
            
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
            
            // Mark that we found a return statement
            returnFound = true;
        } else {
            // Empty return statement
            if (currentFunction != null && currentFunction.type != TypeExp.VOID) {
                System.err.println("Error: Non-void function '" + currentFunction.name + 
                                 "' must return a value at line " + (exp.row + 1) + ", column " + (exp.col + 1));
            }
        }
    }

    public void visit(VarDecList list, int level) {
        while(list != null) {
            list.head.accept(this, level);
            list = list.tail;
        }
    }

    // For an array variable access.
    public void visit(IndexVar var, int level) {
        SymbolInfo info = symTable.lookup(var.name);
        if(info == null) {
            System.err.println("Error: Undefined array '" + var.name + "' at line " + (var.row + 1) + ", column " + (var.col + 1));
        } else if(!info.isArray) {
            System.err.println("Error: Variable '" + var.name + "' is not an array at line " + (var.row + 1) + ", column " + (var.col + 1));
        }
        
        var.index.accept(this, level);
        
        // Check that the index is an integer
        int indexType = getExpressionType(var.index);
        if (indexType != TypeExp.INT) {
            System.err.println("Error: Array index must be an integer at line " + (var.row + 1) + ", column " + (var.col + 1));
        }
    }

    // For a simple variable usage.
    public void visit(SimpleVar var, int level) {
        SymbolInfo info = symTable.lookup(var.name);
        if(info == null) {
            System.err.println("Error: Undefined variable '" + var.name + "' at line " + (var.row + 1) + ", column " + (var.col + 1));
        }
    }

    public void visit(NilExp exp, int level) {
        // Nothing to do.
    }

    public void visit(BoolExp exp, int level) {
        // Nothing to do.
    }
    
    // Helper methods
    private int countArguments(ExpList args) {
        int count = 0;
        ExpList current = args;
        while (current != null) {
            count++;
            current = current.tail;
        }
        return count;
    }
    
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