grammar Oracles;


oracles_file: (oracle | COMMENT)+ EOF;

oracle: 'ORACLE' STRING LB given_block* group_block* check_block trigger_block* RB;


given_block: 'GIVEN' 'STATE' 'IN' STRING
|            'GIVEN' 'LAST_ACTION' property_block
;

group_block:    'GROUP'   'WIDGET' STRING property_block;
check_block:    'CHECK'   property_block;
trigger_block:  'TRIGGER' BOOL STRING;



////////////////////////
// logic parser rules //
////////////////////////

property_block: bool_expr;

bool_expr:          LP  bool_expr   RP                          #parenExpr
|                   'NOT' bool_expr                             #notExpr
| left=bool_expr    ( 'AND' | ',' )         right=bool_expr     #andOprExpr
| left=bool_expr    ( 'XOR' | '^' )         right=bool_expr     #xorOprExpr
| left=bool_expr    ( 'OR'  | '|' )         right=bool_expr     #orOprExpr
| left=bool_expr    ( 'IS'  | 'EQUALS' )    right=bool_expr     #isOprExpr
|                       BOOL                                    #plainBool
|                       property_line                           #propertyBool
;

property_line:
    'PROP' LP 'KEY' ',' 'VALUE' RP  comparator LP   key=STRING ',' value=STRING RP    #propKeyValue
|   'PROP'    'KEY'                 comparator          STRING                        #propKey
|   'PROP'    'VALUE'               comparator          STRING                        #propValue
|   'PROP'    'ANY'                 comparator          STRING                        #propAny
|   'PROP'    'VALUE'               'IS'                BOOL                          #propIsBool
|   'PROP'    type=('KEY'|'VALUE'|'ANY')          'IS'  'IN'      list                #propIsInList
;

//WIDGET BASE_STRING ('='|'==') WIDGET BASE_STRING        #widgetIsWidget

list: 'LIST' LP STRING (',' STRING)* RP;

comparator: 'IS'                #comparator_is
|           'MATCHES'           #comparator_matches
|           'CONTAINS'          #comparator_contains
|           'STARTS_WITH'       #comparator_startsWith
|           'ENDS_WITH'         #comparator_endsWith
;


/////////////////////////
// string parser rules //
/////////////////////////

regex_string:       'REGEX'   STRING;
raw_string:         'RAW'     STRING; //no interpretation
basic_string:                 STRING; //interpretation of escaped characters (\t\n,etc.)


////////////////////////
// string lexer rules //
////////////////////////

STRING: '"' (~["\r\n] | '\'' )* '"' //single quotes, no newline
|       '\'' (~['\r\n] | '"' )* '\'' //double quotes, no newline
;

//TRIPLE_SINGLE_QUOTE_STRING: '\'\'\'' .*? '\'\'\'';
//TRIPLE_DOUBLE_QUOTE_STRING: '"""' .*? '"""';

//SINGLE_QUOTE_STRING: '\'' .*? '\'';
//DOUBLE_QUOTE_STRING: '"' .*? '"';

INT: [0-9]+;

/////////////////////////
// keyword lexer rules //
/////////////////////////

//ANY:            'ANY';
//CHECK:          'CHECK';
//CONTAINS:       'CONTAINS';
//ENDS_WITH:      'ENDS_WITH';
//GIVEN:          'GIVEN';
//GROUP:          'GROUP';
//KEY:            'KEY';
//LAST_ACTION:    'LAST_ACTION';
//LIST:           'LIST';
//MATCHES:        'MATCHES';
//ORACLE:         'ORACLE';
//PROP:           'PROP' | 'PROPERTY';
//RAW:            'RAW';
//REGEX:          'REGEX';
//STARTS_WITH:    'STARTS_WITH';
//STATE:          'STATE';
//TRIGGER:        'TRIGGER';
//VALUE:          'VALUE';
//WIDGET:         'WIDGET';

///////////////////////
// logic lexer rules //
///////////////////////

BOOL: TRUE | FALSE;
//COMMA:      ',';

//in:         'IN';
//is:         'IS';
//not:        '!' | '~' | 'NOT';
//and:        'AND'; //comma provided by a different rule
//or:         '|' | 'OR';
//xor:        '^' | 'XOR';


///////////////////////////////
// miscellaneous lexer rules //
///////////////////////////////

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

ANY_CHARACTERS: . ; //for error-catching purposes