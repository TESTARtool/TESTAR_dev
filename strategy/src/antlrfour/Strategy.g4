grammar Strategy;

strategy_file:  strategy EOF;

strategy:       if_then_else | action_list;

if_then_else:   IF ifExpr=bool_expr     THEN thenExpr=action_expr   ELSE elseExpr=action_expr;

/////////////////////
// IF parser rules //
/////////////////////

bool_expr:                        NOT                               expr=bool_expr              #notExpr
|   LP                            NOT                               expr=bool_expr      RP      #notExpr
|       left=bool_expr      opr=( AND | XOR | OR | IS )             right=bool_expr             #boolOprExpr
|   LP  left=bool_expr      opr=( AND | XOR | OR | IS )             right=bool_expr     RP      #boolOprExpr
|       left=number_expr    opr=( LT | LE | GT | GE | EQ | NE )     right=number_expr           #numberOprExpr
|   LP  left=number_expr    opr=( LT | LE | GT | GE | EQ | NE )     right=number_expr   RP      #numberOprExpr
|                           state_boolean                                                       #stateBool
|   LP                      state_boolean                                               RP      #stateBool
|                           BOOLEAN                                                             #plainBool
;


number_expr:         number_of_actions | NUMBER;

number_of_actions:  'n-actions'      VISIT_MODIFIER?   (FILTER     ACTION_TYPE)?;

state_boolean:      'state-changed'                                                         #stateChanged
|                   'any-exist'             VISIT_MODIFIER?    RELATED_ACTION               #anyExistRelatedAction
|                   'any-exist'             VISIT_MODIFIER?   (FILTER     ACTION_TYPE)?     #anyExist
|                   'prev-action'                             (FILTER     ACTION_TYPE)?     #prevAction
|                   'sut'                   FILTER             SUT_TYPE                     #sutType
;

////////////////////////////////
// THEN and ELSE parser rules //
////////////////////////////////


action_expr:        if_then_else | action_list;

action_list:        action+;

action: NUMBER?     'select-previous'                                                       #selectPreviousAction
|       NUMBER?     'select-random'             VISIT_MODIFIER?      RELATED_ACTION         #selectRelatedAction
|       NUMBER?     'select-random'             VISIT_MODIFIER?     (FILTER ACTION_TYPE)?   #selectRandomAction
;


////////////////////////
// common lexer rules //
////////////////////////

VISIT_MODIFIER:     'visited' | 'unvisited' | 'most-visited' | 'least-visited';

RELATED_ACTION:     'sibling-action' | 'child-action' | 'sibling-or-child-action';

SUT_TYPE:           'windows' | 'linux' | 'android' | 'web';

ACTION_TYPE:        'click-action' | 'type-action' | 'drag-action' | 'scroll-action' | 'hit-key-action'
|                   'form-input-action' | 'form-submit-action' | 'form-field-action';

/////////////////
// lexer rules //
/////////////////

FILTER:             'of-type' | 'not-of-type';

NOT:                N O T   | '!'   | '~';
AND:                A N D   | '&&'  | '&';
XOR:                X O R   | '^';
OR:                 O R     | '||'  | '|';
IS:                 I S     | E Q U A L S;

GT                  : '>';
GE                  : '>=';
LT                  : '<';
LE                  : '<=';
EQ                  : '==' | '=';
NE                  : '!=';

IF:                 I F;
THEN:               T H E N;
ELSE:               E L S E;

NUMBER:             [0-9]+; //[1-9][0-9]* for non-zero
BOOLEAN:            TRUE | FALSE;
LP:                 '(';
RP:                 ')';
COMMENT:            '/*' .*? '*/'                   -> skip;
WHITESPACE:         (' ' | '\t' | '\r' | '\n')+     -> skip;

fragment TRUE:      T R U E;
fragment FALSE:     F A L S E;

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