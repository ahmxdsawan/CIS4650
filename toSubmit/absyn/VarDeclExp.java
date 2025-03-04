package absyn;

public class VarDeclExp extends Exp {
  public TypeExp type;
  public String name;
  public int size;
  
  public VarDeclExp(int row, int col, TypeExp type, String name, int size) {
    this.row = row;
    this.col = col;
    this.type = type;
    this.name = name;
    this.size = size;
  }
  
  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}