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
* processing local var: test
* -> assign
* -> id
* looking up id: x
 13:   LDA 0, -2(5)	load id address
* <- id
 14:    ST 0, -5(5)	store lhs address
* -> call of function: input
 15:    ST 5, -6(5)	push ofp
 16:   LDA 5, -6(5)	push frame
 17:   LDA 0, 1(7)	load ac with ret ptr
 18:   LDA 7, -15(7)	jump to fun loc
 19:    LD 5, 0(5)	pop frame
 20:    LD 1, -5(5)	load lhs address
 21:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: fac
 22:   LDA 0, -3(5)	load id address
* <- id
 23:    ST 0, -6(5)	store lhs address
* -> constant
 24:   LDC 0, 1(0)	load const
* <- constant
 25:    LD 1, -6(5)	load lhs address
 26:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: test
 27:   LDA 0, -4(5)	load id address
* <- id
 28:    ST 0, -7(5)	store lhs address
* -> op
* -> id
* looking up id: x
 29:    LD 0, -2(5)	load id value
* <- id
 30:    ST 0, -8(5)	op: push left
* -> constant
 31:   LDC 0, 1(0)	load const
* <- constant
 32:    LD 1, -8(5)	op: load left
 33:   SUB 0, 1, 0	op >
 34:   JGT 0, 2(7)	br if true
 35:   LDC 0, 0(0)	false case
 36:   LDA 7, 1(7)	unconditional jmp
 37:   LDC 0, 1(0)	true case
* <- op
 38:    LD 1, -7(5)	load lhs address
 39:    ST 0, 0(1)	assign: store value
* <- assign
* -> while
* while: jump after body comes back here
* -> id
* looking up id: test
 40:    LD 0, -4(5)	load id value
* <- id
* while: jump to end belongs here
* -> compound statement
* -> assign
* -> id
* looking up id: fac
 42:   LDA 0, -3(5)	load id address
* <- id
 43:    ST 0, -8(5)	store lhs address
* -> op
* -> id
* looking up id: fac
 44:    LD 0, -3(5)	load id value
* <- id
 45:    ST 0, -9(5)	op: push left
* -> id
* looking up id: x
 46:    LD 0, -2(5)	load id value
* <- id
 47:    LD 1, -9(5)	op: load left
 48:   MUL 0, 1, 0	op *
* <- op
 49:    LD 1, -8(5)	load lhs address
 50:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: x
 51:   LDA 0, -2(5)	load id address
* <- id
 52:    ST 0, -9(5)	store lhs address
* -> op
* -> id
* looking up id: x
 53:    LD 0, -2(5)	load id value
* <- id
 54:    ST 0, -10(5)	op: push left
* -> constant
 55:   LDC 0, 1(0)	load const
* <- constant
 56:    LD 1, -10(5)	op: load left
 57:   SUB 0, 1, 0	op -
* <- op
 58:    LD 1, -9(5)	load lhs address
 59:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: test
 60:   LDA 0, -4(5)	load id address
* <- id
 61:    ST 0, -10(5)	store lhs address
* -> op
* -> id
* looking up id: x
 62:    LD 0, -2(5)	load id value
* <- id
 63:    ST 0, -11(5)	op: push left
* -> constant
 64:   LDC 0, 1(0)	load const
* <- constant
 65:    LD 1, -11(5)	op: load left
 66:   SUB 0, 1, 0	op >
 67:   JGT 0, 2(7)	br if true
 68:   LDC 0, 0(0)	false case
 69:   LDA 7, 1(7)	unconditional jmp
 70:   LDC 0, 1(0)	true case
* <- op
 71:    LD 1, -10(5)	load lhs address
 72:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
 73:   LDA 7, -34(7)	while: absolute jmp to test
 41:   JEQ 0, 32(7)	while: jmp to end
* <- while
* -> call of function: output
* -> id
* looking up id: fac
 74:    LD 0, -3(5)	load id value
* <- id
 75:    ST 0, -9(5)	save output value
 76:    ST 5, -10(5)	push ofp
 77:   LDA 5, -10(5)	push frame
 78:    LD 1, 0(5)	load old frame pointer
 79:    LD 0, -9(1)	load saved output value
 80:    ST 0, -2(5)	store arg for output
 81:   LDA 0, 1(7)	load ac with ret ptr
 82:   LDA 7, -76(7)	jump to output routine
 83:    LD 5, 0(5)	pop frame
* <- compound statement
 84:    LD 7, -1(5)	return to caller
 11:   LDA 7, 73(7)	jump around fn body
* <- fundecl
 85:    ST 5, 0(5)	push ofp
 86:   LDA 5, 0(5)	push frame
 87:   LDA 0, 1(7)	load ac with ret ptr
 88:   LDA 7, -77(7)	jump to main loc
 89:    LD 5, 0(5)	pop frame
* End of execution.
 90:  HALT 0, 0, 0	
