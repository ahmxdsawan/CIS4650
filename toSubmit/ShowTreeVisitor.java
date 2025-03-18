/*
  Created by: Nathan Brommersma, Ahmad Sawan, Jacob McKenna
  File Name: ShowTreeVisitor.java
*/

import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent( int level ) {
    for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
  }

  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
      expList.head.accept( this, level );
      expList = expList.tail;
    } 
  }

  public void visit( AssignExp exp, int level ) {
    indent( level );
    System.out.println( "AssignExp:" );
    level++;
    exp.lhs.accept( this, level );
    exp.rhs.accept( this, level );
  }

  public void visit( IfExp exp, int level ) {
    indent( level );
    System.out.println( "IfExp:" );
    level++;
    exp.test.accept( this, level );
    exp.thenpart.accept( this, level );
    if (exp.elsepart != null )
       exp.elsepart.accept( this, level );
  }

  public void visit( IntExp exp, int level ) {
    indent( level );
    System.out.println( "IntExp: " + exp.value ); 
  }

  public void visit( OpExp exp, int level ) {
    indent( level );
    System.out.print( "OpExp:" ); 
    switch( exp.op ) {
      case OpExp.PLUS:
        System.out.println( " + " );
        break;
      case OpExp.MINUS:
        System.out.println( " - " );
        break;
      case OpExp.TIMES:
        System.out.println( " * " );
        break;
      case OpExp.OVER:
        System.out.println( " / " );
        break;
      case OpExp.EQ:
        System.out.println( " = " );
        break;
      case OpExp.LT:
        System.out.println( " < " );
        break;
      case OpExp.GT:
        System.out.println( " > " );
        break;
      case OpExp.UMINUS:
        System.out.println( " - " );
        break;
      case OpExp.LTE:
        System.out.println(" <= ");
        break;
      case OpExp.GTE:
        System.out.println(" >= ");
        break;
      case OpExp.NEQ:
        System.out.println(" != ");
        break;
      case OpExp.OR:
        System.out.println(" || ");
        break;
      case OpExp.AND:
        System.out.println(" && ");
        break;
      case OpExp.NOT:
        System.out.println(" ~ ");
        break;
      default:
        System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    if (exp.left != null)
       exp.left.accept( this, level );
    if (exp.right != null)
       exp.right.accept( this, level );
  }

  public void visit(TypeExp exp, int level) {
    indent(level);
    String type = "";
    switch(exp.type) {
      case TypeExp.INT:
        type = "int";
        break;
      case TypeExp.BOOL:
        type = "bool";
        break;
      case TypeExp.VOID:
        type = "void";
        break;
    }
    System.out.println("TypeExp: " + type + (exp.isArray ? "[]" : ""));
  }
  
  public void visit(VarDeclExp exp, int level) {
    indent(level);
    System.out.println("VarDeclExp: " + exp.name);
    level++;
    exp.type.accept(this, level);
    if (exp.size >= 0) {
      indent(level);
      System.out.println("Array size: " + exp.size);
    }
  }
  
  public void visit(FunctionDec exp, int level) {
    indent(level);
    System.out.println("FunctionDec: " + exp.name);
    level++;
    exp.result.accept(this, level);
    
    indent(level);
    System.out.println("Parameters:");
    if (exp.params != null)
      exp.params.accept(this, level+1);
    else {
      indent(level+1);
      System.out.println("void");
    }
    
    if (exp.body != null) {
      indent(level);
      System.out.println("Body:");
      exp.body.accept(this, level+1);
    }
  }
  
  public void visit(CompoundExp exp, int level) {
    indent(level);
    System.out.println("CompoundExp:");
    level++;
    
    indent(level);
    System.out.println("Declarations:");
    if (exp.decs != null)
      exp.decs.accept(this, level+1);
    
    indent(level);
    System.out.println("Expressions:");
    if (exp.exps != null)
      exp.exps.accept(this, level+1);
  }
  
  public void visit(CallExp exp, int level) {
    indent(level);
    System.out.println("CallExp: " + exp.func);
    level++;
    
    indent(level);
    System.out.println("Arguments:");
    if (exp.args != null)
      exp.args.accept(this, level+1);
  }
  
  public void visit(WhileExp exp, int level) {
    indent(level);
    System.out.println("WhileExp:");
    level++;
    
    indent(level);
    System.out.println("Test:");
    exp.test.accept(this, level+1);
    
    indent(level);
    System.out.println("Body:");
    exp.body.accept(this, level+1);
  }
  
  public void visit(ReturnExp exp, int level) {
    indent(level);
    System.out.println("ReturnExp:");
    if (exp.exp != null)
      exp.exp.accept(this, level+1);
  }
  
  public void visit(VarDecList list, int level) {
    while (list != null) {
      list.head.accept(this, level);
      list = list.tail;
    }
  }
  
  public void visit(IndexVar var, int level) {
    indent(level);
    System.out.println("IndexVar: " + var.name);
    level++;
    
    indent(level);
    System.out.println("Index:");
    var.index.accept(this, level+1);
  }
  
  public void visit(SimpleVar var, int level) {
    indent(level);
    System.out.println("SimpleVar: " + var.name);
  }
  
  public void visit(NilExp exp, int level) {
    indent(level);
    System.out.println("NilExp");
  }

  public void visit(BoolExp exp, int level) {
    indent(level);
    System.out.println("BoolExp: " + exp.bool);
  }

}
