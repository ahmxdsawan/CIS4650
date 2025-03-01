package absyn;

public class VarDecList extends Absyn {
  public VarDeclExp head;
  public VarDecList tail;
  
  public VarDecList(VarDeclExp head, VarDecList tail) {
    this.head = head;
    this.tail = tail;
  }
  
  public void accept(AbsynVisitor visitor, int level) {
    visitor.visit(this, level);
  }
}