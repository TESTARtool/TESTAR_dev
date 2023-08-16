grammar Strategy;

strategy_file:  strategy EOF;

strategy:       action+;

/////////////////////////
// action parser rules //
/////////////////////////

action: cond_action | uncond_action;

cond_action:    INT? IF ifExpr=bool_expr     THEN thenExpr=strategy   ELSE elseExpr=strategy
|               INT? IF ifExpr=bool_expr     THEN thenExpr=strategy;

uncond_action:  INT? 'select-previous'                                                   #selectPreviousAction
|               INT? 'select-random'             MODIFIER?      RELATION                 #selectRelation
|               INT? 'select-random'             MODIFIER?     (FILTER ACTION_TYPE)?     #selectRandomAction
;

//////////////////////////
// boolean parser rules //
//////////////////////////

bool_expr:                        NOT                               expr=bool_expr              #notExpr
|   LP                            NOT                               expr=bool_expr      RP      #notExpr
|       left=bool_expr      opr=( AND | XOR | OR | EQUALS )         right=bool_expr             #boolOprExpr
|   LP  left=bool_expr      opr=( AND | XOR | OR | EQUALS )         right=bool_expr     RP      #boolOprExpr
|       left=int_expr       opr=( LT | LE | GT | GE | EQ | NE )     right=int_expr              #intOprExpr
|   LP  left=int_expr       opr=( LT | LE | GT | GE | EQ | NE )     right=int_expr      RP      #intOprExpr
|                           state_boolean                                                       #stateBool
|   LP                      state_boolean                                               RP      #stateBool
|                           BOOL                                                                #plainBool
;
state_boolean:      'state-changed'                                                       #stateChanged
|                   'any-exist'             MODIFIER?    RELATION                         #anyExistRelatedAction
|                   'any-exist'             MODIFIER?   (FILTER     ACTION_TYPE)?         #anyExist
|                   'previous-exist'                    (FILTER     ACTION_TYPE)?         #previousExist
|                   'sut'                   FILTER       SUT                              #sutType
;

int_expr:           'n-actions'             MODIFIER?   (FILTER     ACTION_TYPE)?         #nActions
|                   INT                                                                   #plainInt
;

///////////////////////////
// filtering lexer rules //
///////////////////////////

MODIFIER:       'visited' | 'unvisited' | 'most-visited' | 'least-visited';

RELATION:       'sibling' | 'child' | 'sibling-or-child';

SUT:            'windows' | 'linux' | 'android' | 'web';

ACTION_TYPE:    'click-action' | 'type-action' | 'drag-action' | 'scroll-action' | 'hit-key-action'
|               'form-input-action' | 'form-submit-action' | 'form-field-action';

FILTER:         'of-type' | 'not-of-type';

//////////////////////////
// operator lexer rules //
//////////////////////////

NOT:                N O T   | '!'   | '~';
AND:                A N D   | '&&'  | '&';
XOR:                X O R   | '^';
OR:                 O R     | '||'  | '|';
EQUALS:             E Q U A L S | I S;

GT                  : '>';
GE                  : '>=';
LT                  : '<';
LE                  : '<=';
EQ                  : '==' | '=';
NE                  : '!=';

INT:                [0-9]+; //[1-9][0-9]* for non-zero
BOOL:               TRUE | FALSE;

//////////////////////
// term lexer rules //
//////////////////////

IF:                 I F;
THEN:               T H E N;
ELSE:               E L S E;

LP:                 '(';
RP:                 ')';
COMMENT:            '/*' .*? '*/'                   -> skip;
WHITESPACE:         (' ' | '\t' | '\r' | '\n')+     -> skip;

/////////////////////
// lexer fragments //
/////////////////////

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