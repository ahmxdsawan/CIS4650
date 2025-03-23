package absyn;

public class IndexVar extends Var {
  public String name;
  public Exp index;

  public int nestLevel;
  public int offset;
  
  public IndexVar(int row, int col, String name, Exp index) {
    this.row = row;
    this.col = col;
    this.name = name;
    this.index = index;

    this.nestLevel = 0;
    this.offset = 0;
  }
  
  public void accept(AbsynVisitor visitor, int level, boolean isAddr) {
    visitor.visit(this, level, isAddr);
  }
}