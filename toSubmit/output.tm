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
* processing var: x
* Global var x at offset -1
* processing var: y
* Global var y at offset -2
* processing function: f
* jump around function body here
 12:    ST 0, -1(5)	store return
* -> compound statement
* -> call of function: g
* -> id
* looking up id: n
 13:    LD 0, -2(5)	load id value
* <- id
 14:    ST 0, -3(5)	store argument value
 15:    ST 5, -4(5)	push ofp
 16:   LDA 5, -4(5)	push frame
 17:    LD 1, 0(5)	load old frame pointer
 18:    LD 0, -3(1)	load argument
 19:    ST 0, -2(5)	store parameter
 20:   LDA 0, 1(7)	load ac with ret ptr
* Error: Cannot find function address for g
 21:    LD 5, 0(5)	pop frame
* <- call
* -> assign
* -> id
* looking up id: y
 22:   LDA 0, -2(6)	load id address
* <- id
 23:    ST 0, -4(5)	store lhs address
* -> op
* -> id
* looking up id: y
 24:    LD 0, -2(6)	load id value
* <- id
 25:    ST 0, -5(5)	op: push left
* -> constant
 26:   LDC 0, 1(0)	load const
* <- constant
 27:    LD 1, -5(5)	op: load left
 28:   SUB 0, 1, 0	op -
* <- op
 29:    LD 1, -4(5)	load lhs address
 30:    ST 0, 0(1)	assign: store value
* <- assign
* <- compound statement
 31:    LD 7, -1(5)	return to caller
 11:   LDA 7, 20(7)	jump around fn body
* <- fundecl
* processing function: g
* jump around function body here
 33:    ST 0, -1(5)	store return
* -> compound statement
* -> assign
* -> id
* looking up id: m
 34:   LDA 0, -2(5)	load id address
* <- id
 35:    ST 0, -3(5)	store lhs address
* -> op
* -> id
* looking up id: m
 36:    LD 0, -2(5)	load id value
* <- id
 37:    ST 0, -4(5)	op: push left
* -> constant
 38:   LDC 0, 1(0)	load const
* <- constant
 39:    LD 1, -4(5)	op: load left
 40:   SUB 0, 1, 0	op -
* <- op
 41:    LD 1, -3(5)	load lhs address
 42:    ST 0, 0(1)	assign: store value
* <- assign
* -> if
* -> op
* -> id
* looking up id: m
 43:    LD 0, -2(5)	load id value
* <- id
 44:    ST 0, -4(5)	op: push left
* -> constant
 45:   LDC 0, 0(0)	load const
* <- constant
 46:    LD 1, -4(5)	op: load left
 47:   SUB 0, 1, 0	op >
 48:   JGT 0, 2(7)	br if true
 49:   LDC 0, 0(0)	false case
 50:   LDA 7, 1(7)	unconditional jmp
 51:   LDC 0, 1(0)	true case
* <- op
* if: jump to else belongs here
* -> compound statement
* -> call of function: f
* -> id
* looking up id: m
 53:    LD 0, -2(5)	load id value
* <- id
 54:    ST 0, -4(5)	store argument value
 55:    ST 5, -5(5)	push ofp
 56:   LDA 5, -5(5)	push frame
 57:    LD 1, 0(5)	load old frame pointer
 58:    LD 0, -4(1)	load argument
 59:    ST 0, -2(5)	store parameter
 60:   LDA 0, 1(7)	load ac with ret ptr
 61:   LDA 7, -50(7)	jump to function f
 62:    LD 5, 0(5)	pop frame
* <- call
* -> assign
* -> id
* looking up id: y
 63:   LDA 0, -2(6)	load id address
* <- id
 64:    ST 0, -5(5)	store lhs address
* -> op
* -> id
* looking up id: y
 65:    LD 0, -2(6)	load id value
* <- id
 66:    ST 0, -6(5)	op: push left
* -> constant
 67:   LDC 0, 1(0)	load const
* <- constant
 68:    LD 1, -6(5)	op: load left
 69:   SUB 0, 1, 0	op -
* <- op
 70:    LD 1, -5(5)	load lhs address
 71:    ST 0, 0(1)	assign: store value
* <- assign
* -> call of function: g
* -> id
* looking up id: m
 72:    LD 0, -2(5)	load id value
* <- id
 73:    ST 0, -6(5)	store argument value
 74:    ST 5, -7(5)	push ofp
 75:   LDA 5, -7(5)	push frame
 76:    LD 1, 0(5)	load old frame pointer
 77:    LD 0, -6(1)	load argument
 78:    ST 0, -2(5)	store parameter
 79:   LDA 0, 1(7)	load ac with ret ptr
 80:   LDA 7, -48(7)	jump to function g
 81:    LD 5, 0(5)	pop frame
* <- call
* <- compound statement
 52:   JEQ 0, 29(7)	if: jmp to else if test is false
* <- if
* <- compound statement
 82:    LD 7, -1(5)	return to caller
 32:   LDA 7, 50(7)	jump around fn body
* <- fundecl
* processing function: main
* jump around function body here
 84:    ST 0, -1(5)	store return
* -> compound statement
* -> assign
* -> id
* looking up id: x
 85:   LDA 0, -1(6)	load id address
* <- id
 86:    ST 0, -2(5)	store lhs address
* -> call of function: input
 87:    ST 5, -3(5)	push ofp
 88:   LDA 5, -3(5)	push frame
 89:   LDA 0, 1(7)	load ac with ret ptr
 90:   LDA 7, -87(7)	jump to fun loc
 91:    LD 5, 0(5)	pop frame
 92:    LD 1, -2(5)	load lhs address
 93:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: y
 94:   LDA 0, -2(6)	load id address
* <- id
 95:    ST 0, -3(5)	store lhs address
* -> call of function: input
 96:    ST 5, -4(5)	push ofp
 97:   LDA 5, -4(5)	push frame
 98:   LDA 0, 1(7)	load ac with ret ptr
 99:   LDA 7, -96(7)	jump to fun loc
100:    LD 5, 0(5)	pop frame
101:    LD 1, -3(5)	load lhs address
102:    ST 0, 0(1)	assign: store value
* <- assign
* -> call of function: g
* -> id
* looking up id: x
103:    LD 0, -1(6)	load id value
* <- id
104:    ST 0, -4(5)	store argument value
105:    ST 5, -5(5)	push ofp
106:   LDA 5, -5(5)	push frame
107:    LD 1, 0(5)	load old frame pointer
108:    LD 0, -4(1)	load argument
109:    ST 0, -2(5)	store parameter
110:   LDA 0, 1(7)	load ac with ret ptr
111:   LDA 7, -79(7)	jump to function g
112:    LD 5, 0(5)	pop frame
* <- call
* -> call of function: output
* -> id
* looking up id: x
113:    LD 0, -1(6)	load id value
* <- id
114:    ST 0, -5(5)	save output value
115:    ST 5, -6(5)	push ofp
116:   LDA 5, -6(5)	push frame
117:    LD 1, 0(5)	load old frame pointer
118:    LD 0, -5(1)	load saved output value
119:    ST 0, -2(5)	store arg for output
120:   LDA 0, 1(7)	load ac with ret ptr
121:   LDA 7, -115(7)	jump to output routine
122:    LD 5, 0(5)	pop frame
* -> call of function: output
* -> id
* looking up id: y
123:    LD 0, -2(6)	load id value
* <- id
124:    ST 0, -6(5)	save output value
125:    ST 5, -7(5)	push ofp
126:   LDA 5, -7(5)	push frame
127:    LD 1, 0(5)	load old frame pointer
128:    LD 0, -6(1)	load saved output value
129:    ST 0, -2(5)	store arg for output
130:   LDA 0, 1(7)	load ac with ret ptr
131:   LDA 7, -125(7)	jump to output routine
132:    LD 5, 0(5)	pop frame
* <- compound statement
133:    LD 7, -1(5)	return to caller
 83:   LDA 7, 50(7)	jump around fn body
* <- fundecl
134:    ST 5, -2(5)	push ofp
135:   LDA 5, -2(5)	push frame
136:   LDA 0, 1(7)	load ac with ret ptr
137:   LDA 7, -54(7)	jump to main loc
138:    LD 5, 0(5)	pop frame
* End of execution.
139:  HALT 0, 0, 0	
