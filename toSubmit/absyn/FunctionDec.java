package absyn;

public class FunctionDec extends Exp {
  public TypeExp result;
  public String name;
  public VarDecList params;
  public CompoundExp body;

  public int funaddr;
  
  public FunctionDec(int row, int col, TypeExp result, String name, VarDecList params, CompoundExp body) {
    this.row = row;
    this.col = col;
    this.result = result;
    this.name = name;
    this.params = params;
    this.body = body;

    this.funaddr = -1;
  }
  
  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}