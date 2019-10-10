grammar Function;

func: add STOP;

add: add OP mul
	| mul;

mul: mul OP term
	| term;

term: NUMBER
	| VARIABLE
	| LP add RP;

WHITESPACE: [ \t\r\n]+ ;

NUMBER: [0-9]+ ;

VARIABLE: [a-zA-Z]+[0-9]+; 

OP: '+'|'-'|'*'|'/';

LP: '(';
RP: ')';

STOP: ';';

