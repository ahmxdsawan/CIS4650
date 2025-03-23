package absyn;

public class VarDeclExp extends Exp {
  public TypeExp type;
  public String name;
  public int size;

  public int offset;    
  public int nestLevel; 

  public VarDeclExp(int row, int col, TypeExp type, String name, int size) {
    this.row = row;
    this.col = col;
    this.type = type;
    this.name = name;
    this.size = size;

    // Initialize new fields; adjust default values as needed.
    this.offset = 0;
    this.nestLevel = 0;
  }
  
  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}