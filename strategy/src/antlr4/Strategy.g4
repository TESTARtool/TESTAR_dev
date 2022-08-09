grammar Strategy;

strategy_file:      strategy EOF;

strategy:           IF ifExpr=bool_expr     THEN thenExpr=action_expr   ELSE elseExpr=action_expr;

bool_expr:
                            NOT             expr=bool_expr              #notExpr
|   LP                      NOT             expr=bool_expr      RP      #notExpr
|       left=bool_expr      AND             right=bool_expr             #andExpr
|   LP  left=bool_expr      AND             right=bool_expr     RP      #andExpr
|       left=bool_expr      XOR             right=bool_expr             #xorExpr
|   LP  left=bool_expr      XOR             right=bool_expr     RP      #xorExpr
|       left=bool_expr      OR              right=bool_expr             #orExpr
|   LP  left=bool_expr      OR              right=bool_expr     RP      #orExpr
|       left=number_expr    GT              right=number_expr           #greaterThanExpr
|   LP  left=number_expr    GT              right=number_expr   RP      #greaterThanExpr
|       left=number_expr    GE              right=number_expr           #greaterEqualThanExpr
|   LP  left=number_expr    GE              right=number_expr   RP      #greaterEqualThanExpr
|       left=number_expr    LT              right=number_expr           #lessThanExpr
|   LP  left=number_expr    LT              right=number_expr   RP      #lessThanExpr
|       left=number_expr    LE              right=number_expr           #lessEqualThanExpr
|   LP  left=number_expr    LE              right=number_expr   RP      #lessEqualThanExpr
|       left=number_expr    EQ              right=number_expr           #equalExpr
|   LP  left=number_expr    EQ              right=number_expr   RP      #equalExpr
|       left=number_expr    NE              right=number_expr           #notEqualExpr
|   LP  left=number_expr    NE              right=number_expr   RP      #notEqualExpr
|                           state_boolean                               #stateBool
|   LP                      state_boolean                       RP      #stateBool
|                           BOOLEAN                                     #baseBool
;

number_expr:        number_of_actions | NUMBER;

action_expr:        strategy | action+;

state_boolean:
    'available-actions-of-type'     ACTION_TYPE         #availableActionsOftype
|   'sut-type-is'                   SUT_TYPE            #sutType
|   'state-changed'                                     #stateChanged
;

number_of_actions:  'total-n-actions'                                       #tnActions
|                   'total-n-unexecuted-actions'                            #tnUnexActions
|                   'total-n-executed-actions'                              #tnExActions
|                   'n-actions-of-type'                     ACTION_TYPE     #nActionsOfType
|                   'n-executed-actions-of-type'            ACTION_TYPE     #nExecActions
|                   'n-unexecuted-actions-of-type'          ACTION_TYPE     #nUnexActionsOfType
;

action: NUMBER?     'random-action'                                            #rAction
|       NUMBER?     'previous-action'                                          #prevAction
|       NUMBER?     'r-unexecuted-action'                                      #rUnexAction
|       NUMBER?     'r-least-executed-action'                                  #rLeastExAction
|       NUMBER?     'r-most-executed-action'                                   #rMostExAction
|       NUMBER?     'r-action-of-type'                         ACTION_TYPE     #rActionOfType
|       NUMBER?     'r-unexecuted-action-of-type'              ACTION_TYPE     #rUnexActionOfType
|       NUMBER?     'r-action-not-of-type'                     ACTION_TYPE     #rActionNotType
|       NUMBER?     'r-unexecuted-action-not-of-type'          ACTION_TYPE     #rUnexActionNotType
|       NUMBER?     'select-submit-action'                                     #sSubmitAction
|       NUMBER?     'select-sibling-action'                                    #sSiblingAction
|       NUMBER?     'select-child-action'                                      #sChildAction
|       NUMBER?     'select-child-or-sibling-action'                           #sChildOrSiblingAction
|       NUMBER?     'select-sibling-or-child-action'                           #sChildOrSiblingAction
;

SUT_TYPE:           'windows' | 'linux' | 'android' | 'web';

ACTION_TYPE:        'click-action' | 'type-action' | 'drag-action' | 'scroll-action' | 'hit-key-action';

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