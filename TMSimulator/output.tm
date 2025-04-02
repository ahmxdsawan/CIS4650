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
* processing function: add
* jump around function body here
 12:    ST 0, -1(5)	store return
* -> compound statement
* -> return
* -> op
* -> id
* looking up id: x
 13:    LD 0, -2(5)	load id value
* <- id
 14:    ST 0, -4(5)	op: push left
* -> id
* looking up id: y
 15:    LD 0, -3(5)	load id value
* <- id
 16:    LD 1, -4(5)	op: load left
 17:   ADD 0, 1, 0	op +
* <- op
 18:    LD 7, -1(5)	return to caller
* <- return
* <- compound statement
 19:    LD 7, -1(5)	return to caller
 11:   LDA 7, 8(7)	jump around fn body
* <- fundecl
* processing function: subtract
* jump around function body here
 21:    ST 0, -1(5)	store return
* -> compound statement
* -> return
* -> op
* -> id
* looking up id: x
 22:    LD 0, -2(5)	load id value
* <- id
 23:    ST 0, -4(5)	op: push left
* -> id
* looking up id: y
 24:    LD 0, -3(5)	load id value
* <- id
 25:    LD 1, -4(5)	op: load left
 26:   SUB 0, 1, 0	op -
* <- op
 27:    LD 7, -1(5)	return to caller
* <- return
* <- compound statement
 28:    LD 7, -1(5)	return to caller
 20:   LDA 7, 8(7)	jump around fn body
* <- fundecl
* processing function: multiply
* jump around function body here
 30:    ST 0, -1(5)	store return
* -> compound statement
* -> return
* -> op
* -> id
* looking up id: x
 31:    LD 0, -2(5)	load id value
* <- id
 32:    ST 0, -4(5)	op: push left
* -> id
* looking up id: y
 33:    LD 0, -3(5)	load id value
* <- id
 34:    LD 1, -4(5)	op: load left
 35:   MUL 0, 1, 0	op *
* <- op
 36:    LD 7, -1(5)	return to caller
* <- return
* <- compound statement
 37:    LD 7, -1(5)	return to caller
 29:   LDA 7, 8(7)	jump around fn body
* <- fundecl
* processing function: divide
* jump around function body here
 39:    ST 0, -1(5)	store return
* -> compound statement
* -> return
* -> op
* -> id
* looking up id: x
 40:    LD 0, -2(5)	load id value
* <- id
 41:    ST 0, -4(5)	op: push left
* -> id
* looking up id: y
 42:    LD 0, -3(5)	load id value
* <- id
 43:    LD 1, -4(5)	op: load left
 44:   DIV 0, 1, 0	op /
* <- op
 45:    LD 7, -1(5)	return to caller
* <- return
* <- compound statement
 46:    LD 7, -1(5)	return to caller
 38:   LDA 7, 8(7)	jump around fn body
* <- fundecl
* processing function: main
* jump around function body here
 48:    ST 0, -1(5)	store return
* -> compound statement
* processing local var: a
* processing local var: b
* -> assign
* -> id
* looking up id: a
 49:   LDA 0, -2(5)	load id address
* <- id
 50:    ST 0, -4(5)	store lhs address
* -> call of function: input
 51:    ST 5, -5(5)	push ofp
 52:   LDA 5, -5(5)	push frame
 53:   LDA 0, 1(7)	load ac with ret ptr
 54:   LDA 7, -51(7)	jump to fun loc
 55:    LD 5, 0(5)	pop frame
 56:    LD 1, -4(5)	load lhs address
 57:    ST 0, 0(1)	assign: store value
* <- assign
* -> assign
* -> id
* looking up id: b
 58:   LDA 0, -3(5)	load id address
* <- id
 59:    ST 0, -5(5)	store lhs address
* -> call of function: input
 60:    ST 5, -6(5)	push ofp
 61:   LDA 5, -6(5)	push frame
 62:   LDA 0, 1(7)	load ac with ret ptr
 63:   LDA 7, -60(7)	jump to fun loc
 64:    LD 5, 0(5)	pop frame
 65:    LD 1, -5(5)	load lhs address
 66:    ST 0, 0(1)	assign: store value
* <- assign
* -> call of function: output
* -> call of function: add
* -> id
* looking up id: a
 67:    LD 0, -2(5)	load id value
* <- id
 68:    ST 0, -6(5)	store argument value
* -> id
* looking up id: b
 69:    LD 0, -3(5)	load id value
* <- id
 70:    ST 0, -7(5)	store argument value
 71:    ST 5, -8(5)	push ofp
 72:   LDA 5, -8(5)	push frame
 73:    LD 1, 0(5)	load old frame pointer
 74:    LD 0, -6(1)	load argument
 75:    ST 0, -2(5)	store parameter
 76:    LD 1, 0(5)	load old frame pointer
 77:    LD 0, -7(1)	load argument
 78:    ST 0, -3(5)	store parameter
 79:   LDA 0, 1(7)	load ac with ret ptr
 80:   LDA 7, -69(7)	jump to function add
 81:    LD 5, 0(5)	pop frame
* <- call
 82:    ST 0, -6(5)	save output value
 83:    ST 5, -7(5)	push ofp
 84:   LDA 5, -7(5)	push frame
 85:    LD 1, 0(5)	load old frame pointer
 86:    LD 0, -6(1)	load saved output value
 87:    ST 0, -2(5)	store arg for output
 88:   LDA 0, 1(7)	load ac with ret ptr
 89:   LDA 7, -83(7)	jump to output routine
 90:    LD 5, 0(5)	pop frame
* -> call of function: output
* -> call of function: subtract
* -> id
* looking up id: a
 91:    LD 0, -2(5)	load id value
* <- id
 92:    ST 0, -7(5)	store argument value
* -> id
* looking up id: b
 93:    LD 0, -3(5)	load id value
* <- id
 94:    ST 0, -8(5)	store argument value
 95:    ST 5, -9(5)	push ofp
 96:   LDA 5, -9(5)	push frame
 97:    LD 1, 0(5)	load old frame pointer
 98:    LD 0, -7(1)	load argument
 99:    ST 0, -2(5)	store parameter
100:    LD 1, 0(5)	load old frame pointer
101:    LD 0, -8(1)	load argument
102:    ST 0, -3(5)	store parameter
103:   LDA 0, 1(7)	load ac with ret ptr
104:   LDA 7, -84(7)	jump to function subtract
105:    LD 5, 0(5)	pop frame
* <- call
106:    ST 0, -7(5)	save output value
107:    ST 5, -8(5)	push ofp
108:   LDA 5, -8(5)	push frame
109:    LD 1, 0(5)	load old frame pointer
110:    LD 0, -7(1)	load saved output value
111:    ST 0, -2(5)	store arg for output
112:   LDA 0, 1(7)	load ac with ret ptr
113:   LDA 7, -107(7)	jump to output routine
114:    LD 5, 0(5)	pop frame
* -> call of function: output
* -> call of function: multiply
* -> id
* looking up id: a
115:    LD 0, -2(5)	load id value
* <- id
116:    ST 0, -8(5)	store argument value
* -> id
* looking up id: b
117:    LD 0, -3(5)	load id value
* <- id
118:    ST 0, -9(5)	store argument value
119:    ST 5, -10(5)	push ofp
120:   LDA 5, -10(5)	push frame
121:    LD 1, 0(5)	load old frame pointer
122:    LD 0, -8(1)	load argument
123:    ST 0, -2(5)	store parameter
124:    LD 1, 0(5)	load old frame pointer
125:    LD 0, -9(1)	load argument
126:    ST 0, -3(5)	store parameter
127:   LDA 0, 1(7)	load ac with ret ptr
128:   LDA 7, -99(7)	jump to function multiply
129:    LD 5, 0(5)	pop frame
* <- call
130:    ST 0, -8(5)	save output value
131:    ST 5, -9(5)	push ofp
132:   LDA 5, -9(5)	push frame
133:    LD 1, 0(5)	load old frame pointer
134:    LD 0, -8(1)	load saved output value
135:    ST 0, -2(5)	store arg for output
136:   LDA 0, 1(7)	load ac with ret ptr
137:   LDA 7, -131(7)	jump to output routine
138:    LD 5, 0(5)	pop frame
* -> call of function: output
* -> call of function: divide
* -> id
* looking up id: a
139:    LD 0, -2(5)	load id value
* <- id
140:    ST 0, -9(5)	store argument value
* -> id
* looking up id: b
141:    LD 0, -3(5)	load id value
* <- id
142:    ST 0, -10(5)	store argument value
143:    ST 5, -11(5)	push ofp
144:   LDA 5, -11(5)	push frame
145:    LD 1, 0(5)	load old frame pointer
146:    LD 0, -9(1)	load argument
147:    ST 0, -2(5)	store parameter
148:    LD 1, 0(5)	load old frame pointer
149:    LD 0, -10(1)	load argument
150:    ST 0, -3(5)	store parameter
151:   LDA 0, 1(7)	load ac with ret ptr
152:   LDA 7, -114(7)	jump to function divide
153:    LD 5, 0(5)	pop frame
* <- call
154:    ST 0, -9(5)	save output value
155:    ST 5, -10(5)	push ofp
156:   LDA 5, -10(5)	push frame
157:    LD 1, 0(5)	load old frame pointer
158:    LD 0, -9(1)	load saved output value
159:    ST 0, -2(5)	store arg for output
160:   LDA 0, 1(7)	load ac with ret ptr
161:   LDA 7, -155(7)	jump to output routine
162:    LD 5, 0(5)	pop frame
* <- compound statement
163:    LD 7, -1(5)	return to caller
 47:   LDA 7, 116(7)	jump around fn body
* <- fundecl
164:    ST 5, 0(5)	push ofp
165:   LDA 5, 0(5)	push frame
166:   LDA 0, 1(7)	load ac with ret ptr
167:   LDA 7, -120(7)	jump to main loc
168:    LD 5, 0(5)	pop frame
* End of execution.
169:  HALT 0, 0, 0	
