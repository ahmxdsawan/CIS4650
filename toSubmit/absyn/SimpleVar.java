package absyn;

public class SimpleVar extends Var {
  public String name;

  public int nestLevel;
  public int offset;
  
  public SimpleVar(int row, int col, String name) {
    this.row = row;
    this.col = col;
    this.name = name;

    this.nestLevel = 0;
    this.offset = 0;
  }
  
  public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
    visitor.visit(this, level, isAddr);
  }
}