grammar Oracles;

oracles_file: (oracle | COMMENT)* EOF;

oracle: oracle_name LB select_block* check_block trigger_block* RB;

select_block: FOR_ALL WIDGET BASE_STRING? property_block;
check_block: CHECK property_block;
trigger_block: TRIGGER BOOL basic_string;


////////////////////////
// logic parser rules //
////////////////////////

property_block: bool_expr;

bool_expr:           LP bool_expr RP                                    #parenExpr
|                       NOT bool_expr                                   #notExpr
| left=bool_expr  opr=( AND | COMMA | XOR | OR | IS ) right=bool_expr   #boolOprExpr
|                       BOOL                                            #plainBool
|                       property_line                                   #propertyBool
;

property_line:    HAS property_string
| property_string IS ( BASE_STRING | BOOL )
| property_string CONTAINS BASE_STRING
| property_string IN list
| WIDGET BASE_STRING ('='|'==') WIDGET BASE_STRING
;



///////////////////////
// logic lexer rules //
///////////////////////

BOOL: TRUE | FALSE;

CONTAINS:   'CONTAINS';
HAS:        'HAS';
IN:         'IN';
IS:         'IS';
NOT:        '!' | '~' | 'NOT';
AND:        'AND'; //comma provided by a different rule
COMMA:      ',';
OR:         '|' | 'OR';
XOR:        '^' | 'XOR';


/////////////////////////
// string parser rules //
/////////////////////////

string: regex_string | property_string | raw_string | basic_string;

regex_string:       REGEX   BASE_STRING;
property_string:    PROP    BASE_STRING;
raw_string:         RAW     BASE_STRING; //no interpretation
basic_string:               BASE_STRING; //interpretation of escaped characters (\t\n,etc.)

oracle_name:        ORACLE  BASE_STRING;


////////////////////////
// string lexer rules //
////////////////////////

BASE_STRING: SINGLE_QUOTE_STRING | DOUBLE_QUOTE_STRING
| TRIPLE_SINGLE_QUOTE_STRING | TRIPLE_DOUBLE_QUOTE_STRING
| INT;

TRIPLE_SINGLE_QUOTE_STRING: '\'\'\'' .*? '\'\'\'';
TRIPLE_DOUBLE_QUOTE_STRING: '"""' .*? '"""';

SINGLE_QUOTE_STRING: '\'' .*? '\'';
DOUBLE_QUOTE_STRING: '"' .*? '"';

INT: [0-9]+;

/////////////////////////
// keyword lexer rules //
/////////////////////////

CHECK:      'CHECK';
FOR_ALL:    'FOR_ALL';
LIST:       'LIST';
ORACLE:     'ORACLE';
PROP:       'PROP' | 'PROPERTY';
RAW:        'RAW';
REGEX:      'REGEX';
TRIGGER:    'TRIGGER';
WIDGET:     'WIDGET';

///////////////////////////////
// miscellaneous lexer rules //
///////////////////////////////

list: LIST LP BASE_STRING (COMMA BASE_STRING)* RP;

LB: '{';
RB: '}';

LP: '(';
RP: ')';

COMMENT:        .+? -> skip;
WHITESPACE:     (' ' | '\t' | '\r' | '\n')+     -> skip;

/////////////////////
// lexer fragments //
/////////////////////

fragment TRUE:      'TRUE';
fragment FALSE:     'FALSE';

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

ANY: . ; //for error-catching purposes