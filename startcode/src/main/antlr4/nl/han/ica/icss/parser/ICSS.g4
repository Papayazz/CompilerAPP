grammar ICSS;

//--- LEXER: ---
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;
WS: [ \t\r\n]+ -> skip;
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';
OPEN_PAREN: '(';
CLOSE_PAREN: ')';

//--- PARSER: ---
stylesheet
    : statement* EOF
    ;

statement
    : variableAssignment
    | stylerule
    ;

variableAssignment
    : CAPITAL_IDENT ASSIGNMENT_OPERATOR expression SEMICOLON
    ;

stylerule
    : selector OPEN_BRACE statementBody* CLOSE_BRACE
    ;

statementBody
    : variableAssignment
    | declaration
    | ifClause
    | stylerule
    ;

selector
    : ID_IDENT
    | CLASS_IDENT
    | LOWER_IDENT
    ;

declaration
    : LOWER_IDENT COLON expression SEMICOLON
    ;

ifClause
    : IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE statementBody* CLOSE_BRACE elseClause?
    ;

elseClause
    : ELSE OPEN_BRACE statementBody* CLOSE_BRACE
    ;

expression
    : addExpression
    ;

addExpression
    : mulExpression ( (PLUS | MIN) mulExpression )*
    ;

mulExpression
    : atom (MUL atom)*
    ;

atom
    : literal
    | variableReference
    | OPEN_PAREN expression CLOSE_PAREN
    ;

literal
    : PIXELSIZE
    | PERCENTAGE
    | COLOR
    | SCALAR
    | TRUE
    | FALSE
    ;

variableReference
    : CAPITAL_IDENT
    ;
