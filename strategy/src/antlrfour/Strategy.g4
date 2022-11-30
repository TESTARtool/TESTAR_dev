grammar Strategy;

strategy_file:      strategy EOF;

strategy:           IF ifExpr=bool_expr     THEN thenExpr=action_expr   ELSE elseExpr=action_expr;

/////////////////////
// IF parser rules //
/////////////////////

bool_expr:                        NOT                               expr=bool_expr              #notExpr
|   LP                            NOT                               expr=bool_expr      RP      #notExpr
|       left=bool_expr      opr=( AND | XOR | OR )                  right=bool_expr             #boolOprExpr
|   LP  left=bool_expr      opr=( AND | XOR | OR )                  right=bool_expr     RP      #boolOprExpr
|       left=number_expr    opr=( LT | LE | GT | GE | EQ | NE )     right=number_expr           #numberOprExpr
|   LP  left=number_expr    opr=( LT | LE | GT | GE | EQ | NE )     right=number_expr   RP      #numberOprExpr
|                           state_boolean                                                       #stateBool
|   LP                      state_boolean                                               RP      #stateBool
|                           BOOLEAN                                                             #baseBool
;


number_expr:         number_of_actions | NUMBER;

number_of_actions:  'n-actions'      VISIT_MODIFIER?   (FILTER     ACTION_TYPE)?;

state_boolean:      'state-changed'                                                             #stateChanged
|                   'any-actions'    VISIT_MODIFIER?   (FILTER     ACTION_TYPE)?      EXIST     #anyActionsExists
|                   'sut'            FILTER             SUT_TYPE                                #sutType
|                    RELATED_ACTION  EXIST                                                      #relatedActionExists
;

////////////////////////////////
// THEN and ELSE parser rules //
////////////////////////////////

action_expr:        strategy        #subStrategy
|                   action+         #actionList;

action: NUMBER?     'select-previous-action'                                                #selectPreviousAction
|       NUMBER?     'select-random'             VISIT_MODIFIER?     (FILTER ACTION_TYPE)?   #selectRandomAction
|       NUMBER?     'select-by-related'         RELATED_ACTION                              #selectRelatedAction
;


////////////////////////
// common lexer rules //
////////////////////////

VISIT_MODIFIER:     'visited' | 'unvisited' | 'most-visited' | 'least-visited';

RELATED_ACTION:     'sibling-action' | 'child-action' | 'sibling-or-child-action';

SUT_TYPE:           'windows' | 'linux' | 'android' | 'web';

ACTION_TYPE:        'click-action' | 'type-action' | 'drag-action' | 'scroll-action' | 'hit-key-action'
|                   'input-action' | 'submit-action';

/////////////////
// lexer rules //
/////////////////

EXIST:              'exist' | 'exists';
FILTER:             'of-type' | 'not-of-type';

NOT:                N O T   | '!'   | '~';
AND:                A N D   | '&&'  | '&';
XOR:                X O R   | '^';
OR:                 O R     | '||'  | '|';

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