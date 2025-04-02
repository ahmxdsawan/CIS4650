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
* processing function: fib
* jump around function body here
 12:    ST 0, -1(5)	store return
* -> compound statement
* processing local var: result
* processing local var: a
* processing local var: b
* -> assign
* -> id
* looking up id: result
 13:   LDA 0, -3(5)	load id address
* <- id
 14:    ST 0, -6(5)	store lhs address
* -> constant
 15:   LDC 0, 0(0)	load const
* <- constant
 16:    LD 1, -6(5)	load lhs address
 17:    ST 0, 0(1)	assign: store value
* <- assign
* -> if
* -> op
* -> id
* looking up id: n
 18:    LD 0, -2(5)	load id value
* <- id
 19:    ST 0, -7(5)	op: push left
* -> constant
 20:   LDC 0, 1(0)	load const
* <- constant
 21:    LD 1, -7(5)	op: load left
 22:   SUB 0, 1, 0	op <=
 23:   JLE 0, 2(7)	br if true
 24:   LDC 0, 0(0)	false case
 25:   LDA 7, 1(7)	unconditional jmp
 26:   LDC 0, 1(0)	true case
* <- op
* if: jump to else belongs here
* -> compound statement
* -> assign
* -> id
* looking up id: result
 28:   LDA 0, -3(5)	load id address
* <- id
 29:    ST 0, -7(5)	store lhs address
* -> id
* looking up id: n
 30:    LD 0, -2(5)	load id value
* <- id
 31:    LD 1, -7(5)	load lhs address
 32:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
* if: jump to end belongs here
 27:   JEQ 0, 6(7)	if: jmp to else if test is false
* -> compound statement
* -> assign
* -> id
* looking up id: a
 34:   LDA 0, -4(5)	load id address
* <- id
 35:    ST 0, -7(5)	store lhs address
* -> call of function: fib
* -> op
* -> id
* looking up id: n
 36:    LD 0, -2(5)	load id value
* <- id
 37:    ST 0, -8(5)	op: push left
* -> constant
 38:   LDC 0, 1(0)	load const
* <- constant
 39:    LD 1, -8(5)	op: load left
 40:   SUB 0, 1, 0	op -
* <- op
 41:    ST 0, -8(5)	store argument value
 42:    ST 5, -9(5)	push ofp
 43:   LDA 5, -9(5)	push frame
 44:    LD 1, 0(5)	load old frame pointer
 45:    LD 0, -8(1)	load argument
 46:    ST 0, -2(5)	store parameter
 47:   LDA 0, 1(7)	load ac with ret ptr
 48:   LDA 7, -37(7)	jump to function fib
 49:    LD 5, 0(5)	pop frame
* <- call
 50:    LD 1, -7(5)	load lhs address
 51:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: b
 52:   LDA 0, -5(5)	load id address
* <- id
 53:    ST 0, -8(5)	store lhs address
* -> call of function: fib
* -> op
* -> id
* looking up id: n
 54:    LD 0, -2(5)	load id value
* <- id
 55:    ST 0, -9(5)	op: push left
* -> constant
 56:   LDC 0, 2(0)	load const
* <- constant
 57:    LD 1, -9(5)	op: load left
 58:   SUB 0, 1, 0	op -
* <- op
 59:    ST 0, -9(5)	store argument value
 60:    ST 5, -10(5)	push ofp
 61:   LDA 5, -10(5)	push frame
 62:    LD 1, 0(5)	load old frame pointer
 63:    LD 0, -9(1)	load argument
 64:    ST 0, -2(5)	store parameter
 65:   LDA 0, 1(7)	load ac with ret ptr
 66:   LDA 7, -55(7)	jump to function fib
 67:    LD 5, 0(5)	pop frame
* <- call
 68:    LD 1, -8(5)	load lhs address
 69:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: result
 70:   LDA 0, -3(5)	load id address
* <- id
 71:    ST 0, -9(5)	store lhs address
* -> op
* -> id
* looking up id: a
 72:    LD 0, -4(5)	load id value
* <- id
 73:    ST 0, -10(5)	op: push left
* -> id
* looking up id: b
 74:    LD 0, -5(5)	load id value
* <- id
 75:    LD 1, -10(5)	op: load left
 76:   ADD 0, 1, 0	op +
* <- op
 77:    LD 1, -9(5)	load lhs address
 78:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
 33:   LDA 7, 45(7)	if: jmp to end
* <- if
* -> return
* -> id
* looking up id: result
 79:    LD 0, -3(5)	load id value
* <- id
 80:    LD 7, -1(5)	return to caller
* <- return
* <- compound statement
 81:    LD 7, -1(5)	return to caller
 11:   LDA 7, 70(7)	jump around fn body
* <- fundecl
* processing function: main
* jump around function body here
 83:    ST 0, -1(5)	store return
* -> compound statement
* processing local var: n
* processing local var: i
* processing local var: fibValue
* -> assign
* -> id
* looking up id: n
 84:   LDA 0, -2(5)	load id address
* <- id
 85:    ST 0, -5(5)	store lhs address
* -> call of function: input
 86:    ST 5, -6(5)	push ofp
 87:   LDA 5, -6(5)	push frame
 88:   LDA 0, 1(7)	load ac with ret ptr
 89:   LDA 7, -86(7)	jump to fun loc
 90:    LD 5, 0(5)	pop frame
 91:    LD 1, -5(5)	load lhs address
 92:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: i
 93:   LDA 0, -3(5)	load id address
* <- id
 94:    ST 0, -6(5)	store lhs address
* -> constant
 95:   LDC 0, 0(0)	load const
* <- constant
 96:    LD 1, -6(5)	load lhs address
 97:    ST 0, 0(1)	assign: store value
* <- assign
* -> while
* while: jump after body comes back here
* -> op
* -> id
* looking up id: i
 98:    LD 0, -3(5)	load id value
* <- id
 99:    ST 0, -7(5)	op: push left
* -> id
* looking up id: n
100:    LD 0, -2(5)	load id value
* <- id
101:    LD 1, -7(5)	op: load left
102:   SUB 0, 1, 0	op <
103:   JLT 0, 2(7)	br if true
104:   LDC 0, 0(0)	false case
105:   LDA 7, 1(7)	unconditional jmp
106:   LDC 0, 1(0)	true case
* <- op
* while: jump to end belongs here
* -> compound statement
* -> assign
* -> id
* looking up id: fibValue
108:   LDA 0, -4(5)	load id address
* <- id
109:    ST 0, -7(5)	store lhs address
* -> call of function: fib
* -> id
* looking up id: i
110:    LD 0, -3(5)	load id value
* <- id
111:    ST 0, -8(5)	store argument value
112:    ST 5, -9(5)	push ofp
113:   LDA 5, -9(5)	push frame
114:    LD 1, 0(5)	load old frame pointer
115:    LD 0, -8(1)	load argument
116:    ST 0, -2(5)	store parameter
117:   LDA 0, 1(7)	load ac with ret ptr
118:   LDA 7, -107(7)	jump to function fib
119:    LD 5, 0(5)	pop frame
* <- call
120:    LD 1, -7(5)	load lhs address
121:    ST 0, 0(1)	assign: store value
* <- assign
* -> call of function: output
* -> id
* looking up id: fibValue
122:    LD 0, -4(5)	load id value
* <- id
123:    ST 0, -8(5)	save output value
124:    ST 5, -9(5)	push ofp
125:   LDA 5, -9(5)	push frame
126:    LD 1, 0(5)	load old frame pointer
127:    LD 0, -8(1)	load saved output value
128:    ST 0, -2(5)	store arg for output
129:   LDA 0, 1(7)	load ac with ret ptr
130:   LDA 7, -124(7)	jump to output routine
131:    LD 5, 0(5)	pop frame
* -> assign
* -> id
* looking up id: i
132:   LDA 0, -3(5)	load id address
* <- id
133:    ST 0, -9(5)	store lhs address
* -> op
* -> id
* looking up id: i
134:    LD 0, -3(5)	load id value
* <- id
135:    ST 0, -10(5)	op: push left
* -> constant
136:   LDC 0, 1(0)	load const
* <- constant
137:    LD 1, -10(5)	op: load left
138:   ADD 0, 1, 0	op +
* <- op
139:    LD 1, -9(5)	load lhs address
140:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
141:   LDA 7, -44(7)	while: absolute jmp to test
107:   JEQ 0, 34(7)	while: jmp to end
* <- while
* <- compound statement
142:    LD 7, -1(5)	return to caller
 82:   LDA 7, 60(7)	jump around fn body
* <- fundecl
143:    ST 5, 0(5)	push ofp
144:   LDA 5, 0(5)	push frame
145:   LDA 0, 1(7)	load ac with ret ptr
146:   LDA 7, -64(7)	jump to main loc
147:    LD 5, 0(5)	pop frame
* End of execution.
148:  HALT 0, 0, 0	
