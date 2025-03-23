* Prelude
  0:    LD 6, 0(0)	Load gp with max address
  1:   LDA 5, 0(6)	Set fp = gp
  2:    ST 0, 0(0)	Clear value at location 0
* FunctionDec: main
* Begin compound expression
* Begin VarDecList
* VarDeclExp: x
* End VarDecList
* Begin assignment
* SimpleVar: computing address of x
  3:   LDA 0, 0(6)	Load address of variable x
* IntExp: loading constant 3
  4:   LDC 0, 3(0)	Load constant 3
  5:    ST 0, 0(0)	Store RHS value into LHS address
* End assignment
* End compound expression
* Finale
  6:  HALT 0, 0, 0	Stop execution
