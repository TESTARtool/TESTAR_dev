grammar Oracles;


oracles_file: (oracle | COMMENT)+ EOF;

oracle: IGNORE? ORACLE STRING LB given_block* group_block* check_block trigger_block? RB;

given_block: GIVEN STATE IN STRING
|            GIVEN LAST_ACTION COLON property_block
;

group_block:    GROUP       WIDGET      STRING   COLON                   property_block;
check_block:    CHECK       check_type  COLON   (APPLY_TO STRING COLON)? property_block;
trigger_block:  TRIGGER     STRING; //maybe change to MESSAGE?


check_type: PASS_IF #pass
|           FAIL_IF #fail
;

////////////////////////
// logic parser rules //
////////////////////////

property_block: bool_expr;

bool_expr:  LP          bool_expr       RP          #parenExpr
|           NOT         bool_expr                   #notExpr
|      left=bool_expr   operator  right=bool_expr   #operatorExpr
|           bool_expr   IS              BOOL        #plainBoolExpr
|                       property_line               #propertyBool
;

operator:   ( AND | COMMA )     #operator_and
|           ( XOR | '^' )       #operator_xor
|           ( OR  | '|' )       #operator_or
|             EQUALS            #operator_equals
;

//WIDGET BASE_STRING ('='|'==') WIDGET BASE_STRING        #widgetIsWidget

property_line:
    PROP      PAIR      key=STRING      WITH        value=STRING    #propKeyValue
|   PROP      VALUE     IS              BOOL                        #propIsBool
|   PROP      VALUE     IS              IN          range           #propIsInRange
|   PROP      location  comparator      list                        #propIsInList
|   PROP      location  comparator      STRING                      #propStandard
;

list:    list_access    LP      STRING (COMMA   STRING)* RP;
range:   RANGE  LP  low=INT     COMMA high=INT  RP;

list_access:
    ALL     #access_all
|   ANY     #access_any
|   NONE    #access_none
|   SOME    #access_some
;


location:    KEY    #location_key
|            VALUE  #location_value
|            ANY    #location_any
;

comparator:  REGEX          #comparator_regex
|            IS             #comparator_is
|            CONTAINS       #comparator_contains
|            STARTS_WITH    #comparator_startsWith
|            ENDS_WITH      #comparator_endsWith
;

special_value:  NULL        #value_null
|               EMPTY       #value_empty
|               NOT_EMPTY   #value_not_empty
;

/////////////////////////
// string parser rules //
/////////////////////////

regex_string:        REGEX    STRING;
raw_string:          RAW      STRING; //no interpretation
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

ALL:            'ALL';
AND:            'AND';
ANY:            'ANY';
APPLY_TO:       'APPLY_TO';
CHECK:          'CHECK';
CONTAINS:       'CONTAINS';
EMPTY:          'EMPTY';
ENDS_WITH:      'ENDS_WITH';
EQUALS:         'EQUALS';
EXIST:          'EXIST';
FAIL_IF:        'FAIL_IF';
GIVEN:          'GIVEN';
GROUP:          'GROUP';
IGNORE:         'IGNORE';
IN:             'IN';
IS:             'IS';
KEY:            'KEY';
LAST_ACTION:    'LAST_ACTION';
//LIST:           'LIST';
MATCHES:        'MATCHES';
NONE:           'NONE';
NOT_EMPTY:      'NOT_EMPTY';
NOT:            'NOT';
NULL:           'NULL';
OR:             'OR';
ORACLE:         'ORACLE';
PAIR:           'PAIR';
PASS_IF:        'PASS_IF';
PROP:           'PROP' | 'PROPERTY';
RANGE:          'RANGE';
RAW:            'RAW';
REGEX:          'REGEX';
SOME:           'SOME';
STARTS_WITH:    'STARTS_WITH';
STATE:          'STATE';
TRIGGER:        'TRIGGER';
VALUE:          'VALUE';
WIDGET:         'WIDGET';
WITH:           'WITH';
XOR:            'XOR';



///////////////////////////////
// miscellaneous lexer rules //
///////////////////////////////

BOOL: TRUE | FALSE;

// must be after any STRING rule, or it'll snap up occurances that should go into string
COMMA:      ',';
COLON:      ':';

LB: '{';
RB: '}';

LP: '(';
RP: ')';

COMMENT:        .+? -> skip;
WHITESPACE:     (' ' | '\t' | '\r' | '\n')+     -> skip;

/////////////////////
// lexer fragments //
/////////////////////

fragment TRUE:      T R U E; // 'TRUE'
fragment FALSE:     F A L S E; // 'FALSE'

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