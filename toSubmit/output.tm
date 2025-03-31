* Standard prelude:
  0:    LD 6, 0(0)	load gp with maxaddress
  1:   LDA 5, 0(6)	copy to gp to fp
  2:    ST 0, 0(0)	clear location 0
* Jump around i/o routines here
* code for input routine
  4:    ST 0, -1(5)	store return
  5:    IN 0, 0, 0	input
  6:    LD 7, -1(5)	return to caller
* code for output routine
  7:    ST 0, -1(5)	store return
  8:    LD 0, -2(5)	load output value
  9:   OUT 0, 0, 0	output
 10:    LD 7, -1(5)	return to caller
  3:   LDA 7, 7(7)	jump around i/o code
* End of standard prelude.
* processing function: main
* jump around function body here
 12:    ST 0, -1(5)	store return
* -> compound statement
* processing local var: x
* processing local var: fac
* -> assign
* -> id
* looking up id: x
 13:   LDA 0, -2(5)	load id address
* <- id
 14:    ST 0, -4(5)	store lhs address
* -> call of function: input
 15:    ST 5, -5(5)	push ofp
 16:   LDA 5, -5(5)	push frame
 17:   LDA 0, 1(7)	load ac with ret ptr
 18:   LDA 7, -15(7)	jump to fun loc
 19:    LD 5, 0(5)	pop frame
 20:    LD 1, -4(5)	load lhs address
 21:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: fac
 22:   LDA 0, -3(5)	load id address
* <- id
 23:    ST 0, -5(5)	store lhs address
* -> constant
 24:   LDC 0, 1(0)	load const
* <- constant
 25:    LD 1, -5(5)	load lhs address
 26:    ST 0, 0(1)	assign: store value
* <- assign
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id: x
 27:    LD 0, -2(5)	load id value
* <- id
 28:    ST 0, -6(5)	op: push left
* -> constant
 29:   LDC 0, 1(0)	load const
* <- constant
 30:    ST 0, -7(5)	op: push right
 31:    LD 0, -6(5)	op: load left
 32:    LD 1, -7(5)	op: load right
 33:   SUB 0, 0, 1	op compare left-right
 34:   JGT 0, 2(7)	br if true
 35:   LDC 0, 0(0)	false case
 36:   LDA 7, 1(7)	unconditional jmp
 37:   LDC 0, 1(0)	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* -> assign
* -> id
* looking up id: fac
 39:   LDA 0, -3(5)	load id address
* <- id
 40:    ST 0, -6(5)	store lhs address
* -> op
* -> id
* looking up id: fac
 41:    LD 0, -3(5)	load id value
* <- id
 42:    ST 0, -7(5)	op: push left
* -> id
* looking up id: x
 43:    LD 0, -2(5)	load id value
* <- id
 44:    ST 0, -8(5)	op: push right
 45:    LD 0, -7(5)	op: load left
 46:    LD 1, -8(5)	op: load right
 47:   MUL 0, 0, 1	op *
* <- op
 48:    LD 1, -6(5)	load lhs address
 49:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: x
 50:   LDA 0, -2(5)	load id address
* <- id
 51:    ST 0, -7(5)	store lhs address
* -> op
* -> id
* looking up id: x
 52:    LD 0, -2(5)	load id value
* <- id
 53:    ST 0, -8(5)	op: push left
* -> constant
 54:   LDC 0, 1(0)	load const
* <- constant
 55:    ST 0, -9(5)	op: push right
 56:    LD 0, -8(5)	op: load left
 57:    LD 1, -9(5)	op: load right
 58:   SUB 0, 0, 1	op -
* <- op
 59:    LD 1, -7(5)	load lhs address
 60:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
 61:   LDA 7, -35(7)	while: absolute jmp to test
 38:   JEQ 0, 23(7)	while: jmp to end
* <- while
* -> call of function: output
* -> id
* looking up id: fac
 62:    LD 0, -3(5)	load id value
* <- id
 63:    ST 0, -7(5)	save output value
 64:    ST 5, -8(5)	push ofp
 65:   LDA 5, -8(5)	push frame
 66:    LD 1, 0(5)	load old frame pointer
 67:    LD 0, -7(1)	load saved output value
 68:    ST 0, -2(5)	store arg for output
 69:   LDA 0, 1(7)	load ac with ret ptr
 70:   LDA 7, -64(7)	jump to output routine
 71:    LD 5, 0(5)	pop frame
* <- compound statement
 72:    LD 7, -1(5)	return to caller
 11:   LDA 7, 61(7)	jump around fn body
* <- fundecl
 73:    ST 5, 0(5)	push ofp
 74:   LDA 5, 0(5)	push frame
 75:   LDA 0, 1(7)	load ac with ret ptr
 76:   LDA 7, -65(7)	jump to main loc
 77:    LD 5, 0(5)	pop frame
* End of execution.
 78:  HALT 0, 0, 0	
