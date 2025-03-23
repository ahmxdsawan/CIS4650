package absyn;

public interface AbsynVisitor {

  public void visit( ExpList exp, int level, boolean isAddr );
  public void visit( AssignExp exp, int level, boolean isAddr);
  public void visit( IfExp exp, int level, boolean isAddr );
  public void visit( IntExp exp, int level,boolean isAddr );
  public void visit( OpExp exp, int level, boolean isAddr );

  // New methods
  public void visit(TypeExp exp, int level, boolean isAddr);
  public void visit(VarDeclExp exp, int level, boolean isAddr);
  public void visit(FunctionDec exp, int level, boolean isAddr);
  public void visit(CompoundExp exp, int level, boolean isAddr);
  public void visit(CallExp exp, int level, boolean isAddr);
  public void visit(WhileExp exp, int level, boolean isAddr);
  public void visit(ReturnExp exp, int level, boolean isAddr);
  public void visit(VarDecList list, int level, boolean isAddr);
  public void visit(IndexVar var, int level, boolean isAddr);
  public void visit(SimpleVar var, int level, boolean isAddr);
  public void visit(NilExp exp, int level, boolean isAddr);
  public void visit(BoolExp exp, int level, boolean isAddr);
}
