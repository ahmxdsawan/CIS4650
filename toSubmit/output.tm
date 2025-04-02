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
* processing local var: i
* -> assign
* -> id
* looking up id: i
 13:   LDA 0, -7(5)	load id address
* <- id
 14:    ST 0, -8(5)	store lhs address
* -> constant
 15:   LDC 0, 4(0)	load const
* <- constant
 16:    LD 1, -8(5)	load lhs address
 17:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> subs
 18:   LDA 0, -2(5)	load array base address
 19:    ST 0, -9(5)	save array base address
* -> constant
 20:   LDC 0, 0(0)	load const
* <- constant
 21:    LD 1, -9(5)	load array base address
 22:   ADD 0, 1, 0	compute element address
* <- subs
 23:    ST 0, -9(5)	store lhs address
* -> constant
 24:   LDC 0, 1(0)	load const
* <- constant
 25:    LD 1, -9(5)	load lhs address
 26:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> subs
 27:   LDA 0, -2(5)	load array base address
 28:    ST 0, -10(5)	save array base address
* -> constant
 29:   LDC 0, 1(0)	load const
* <- constant
 30:    LD 1, -10(5)	load array base address
 31:   ADD 0, 1, 0	compute element address
* <- subs
 32:    ST 0, -10(5)	store lhs address
* -> constant
 33:   LDC 0, 2(0)	load const
* <- constant
 34:    LD 1, -10(5)	load lhs address
 35:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> subs
 36:   LDA 0, -2(5)	load array base address
 37:    ST 0, -11(5)	save array base address
* -> constant
 38:   LDC 0, 2(0)	load const
* <- constant
 39:    LD 1, -11(5)	load array base address
 40:   ADD 0, 1, 0	compute element address
* <- subs
 41:    ST 0, -11(5)	store lhs address
* -> constant
 42:   LDC 0, 3(0)	load const
* <- constant
 43:    LD 1, -11(5)	load lhs address
 44:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> subs
 45:   LDA 0, -2(5)	load array base address
 46:    ST 0, -12(5)	save array base address
* -> constant
 47:   LDC 0, 3(0)	load const
* <- constant
 48:    LD 1, -12(5)	load array base address
 49:   ADD 0, 1, 0	compute element address
* <- subs
 50:    ST 0, -12(5)	store lhs address
* -> constant
 51:   LDC 0, 4(0)	load const
* <- constant
 52:    LD 1, -12(5)	load lhs address
 53:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> subs
 54:   LDA 0, -2(5)	load array base address
 55:    ST 0, -13(5)	save array base address
* -> constant
 56:   LDC 0, 4(0)	load const
* <- constant
 57:    LD 1, -13(5)	load array base address
 58:   ADD 0, 1, 0	compute element address
* <- subs
 59:    ST 0, -13(5)	store lhs address
* -> constant
 60:   LDC 0, 5(0)	load const
* <- constant
 61:    LD 1, -13(5)	load lhs address
 62:    ST 0, 0(1)	assign: store value
* <- assign
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id: i
 63:    LD 0, -7(5)	load id value
* <- id
 64:    ST 0, -14(5)	op: push left
* -> constant
 65:   LDC 0, 0(0)	load const
* <- constant
 66:    LD 1, -14(5)	op: load left
 67:   SUB 0, 1, 0	op >
 68:   JGT 0, 2(7)	br if true
 69:   LDC 0, 0(0)	false case
 70:   LDA 7, 1(7)	unconditional jmp
 71:   LDC 0, 1(0)	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* -> call of function: output
* -> subs
 73:   LDA 0, -2(5)	load array base address
 74:    ST 0, -14(5)	save array base address
* -> id
* looking up id: i
 75:    LD 0, -7(5)	load id value
* <- id
 76:    LD 1, -14(5)	load array base address
 77:   ADD 0, 1, 0	compute element address
 78:    LD 0, 0(0)	load array element
* <- subs
 79:    ST 0, -14(5)	save output value
 80:    ST 5, -15(5)	push ofp
 81:   LDA 5, -15(5)	push frame
 82:    LD 1, 0(5)	load old frame pointer
 83:    LD 0, -14(1)	load saved output value
 84:    ST 0, -2(5)	store arg for output
 85:   LDA 0, 1(7)	load ac with ret ptr
 86:   LDA 7, -80(7)	jump to output routine
 87:    LD 5, 0(5)	pop frame
* -> assign
* -> id
* looking up id: i
 88:   LDA 0, -7(5)	load id address
* <- id
 89:    ST 0, -15(5)	store lhs address
* -> op
* -> id
* looking up id: i
 90:    LD 0, -7(5)	load id value
* <- id
 91:    ST 0, -16(5)	op: push left
* -> constant
 92:   LDC 0, 1(0)	load const
* <- constant
 93:    LD 1, -16(5)	op: load left
 94:   SUB 0, 1, 0	op -
* <- op
 95:    LD 1, -15(5)	load lhs address
 96:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
 97:   LDA 7, -35(7)	while: absolute jmp to test
 72:   JEQ 0, 25(7)	while: jmp to end
* <- while
* <- compound statement
 98:    LD 7, -1(5)	return to caller
 11:   LDA 7, 87(7)	jump around fn body
* <- fundecl
 99:    ST 5, 0(5)	push ofp
100:   LDA 5, 0(5)	push frame
101:   LDA 0, 1(7)	load ac with ret ptr
102:   LDA 7, -91(7)	jump to main loc
103:    LD 5, 0(5)	pop frame
* End of execution.
104:  HALT 0, 0, 0	
