* Prelude
  0:    LD 6, 0(0)	Load gp with max address
  1:   LDA 5, 0(6)	Set fp = gp
  2:    ST 0, 0(0)	Clear value at location 0
* Code for input routine
  4:    ST 0, -1(5)	Store return address
  5:    IN 0, 0, 0	Input integer
  6:    LD 7, -1(5)	Return to caller
* Code for output routine
  7:    ST 0, -1(5)	Store return address
  8:    LD 0, -3(5)	Load output value
  9:   OUT 0, 0, 0	Output integer
 10:    LD 7, -1(5)	Return to caller
  3:   LDA 7, 7(7)	Jump around I/O routines
* FunctionDec: main
* Begin compound expression
* End compound expression
* Finale
 11:    ST 5, -2(5)	Push old frame pointer
 12:   LDA 5, 0(5)	Push new frame
 13:   LDA 0, 1(7)	Load return address
 14:   LDA 7, -4(7)	Jump to main
 15:    LD 5, -2(5)	Pop frame
 16:  HALT 0, 0, 0	Stop execution
