package absyn;

public class TypeExp extends Exp {
  public final static int INT = 0;
  public final static int BOOL = 1;
  public final static int VOID = 2;
  
  public int type;
  public boolean isArray;
  
  public TypeExp(int row, int col, int type, boolean isArray) {
    this.row = row;
    this.col = col;
    this.type = type;
    this.isArray = isArray;
  }
  
  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}