package absyn;

public class BoolExp extends Exp {
  public boolean bool;
  
  public BoolExp(int row, int col, boolean bool) {
    this.row = row;
    this.col = col;
    this.bool = bool;
  }
  
  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}