lexer grammar TgherkinLexer;

import Variables;

@lexer::members {
boolean ignoreEOL=false;
boolean ignoreWS=true;
}

TAGNAME: '@' ~(' ' | '\t' | '\n' | '\r')+ {ignoreWS=true;};

FEATURE_KEYWORD : 'Feature:' {ignoreEOL=false;ignoreWS=false;};

BACKGROUND_KEYWORD: 'Background:' {ignoreEOL=false;ignoreWS=false;};

SCENARIO_KEYWORD: 'Scenario:' {ignoreEOL=false;ignoreWS=false;};

SCENARIO_OUTLINE_KEYWORD: 'Scenario Outline:' {ignoreEOL=false;ignoreWS=false;};

EXAMPLES_KEYWORD: 'Examples:' {ignoreEOL=false;ignoreWS=false;};

SELECTION_KEYWORD: 'Selection:' {ignoreEOL=true;ignoreWS=true;};

ORACLE_KEYWORD: 'Oracle:' {ignoreEOL=true;ignoreWS=true;};

STEP_KEYWORD: 'Step:' {ignoreEOL=false;ignoreWS=false;};

STEP_RANGE_KEYWORD: 'Range' {ignoreEOL=true;ignoreWS=true;};

STEP_GIVEN_KEYWORD: 'Given' {ignoreEOL=true;ignoreWS=true;};

STEP_WHEN_KEYWORD: 'When' {ignoreEOL=true;ignoreWS=true;};

STEP_THEN_KEYWORD: 'Then';

STEP_ALSO_KEYWORD: 'Also';

STEP_EITHER_KEYWORD: 'Either';

TABLE_ROW: {ignoreWS=true;}'|' (~('\n' | '\r')* '|')+ WS* (EOL|EOF);

DECIMAL_NUMBER: [0-9]+'.'[0-9]+;

INTEGER_NUMBER: [0-9]+;

PLACEHOLDER: '<' ~('>' | ' ' | '\t' | '\n' | '\r')+ '>';

STRING:
	'"' ('\\' ('b' | 't' | 'n' | 'f' | 'r' | 'u' | DQUOTE | QUOTE | '\\') | ~('\\' | '"' | '\r' | '\n'))* '"';
 
COMMENT: '#' ~('\n' | '\r')* EOL {skip();};	

AND : 'and' ;
OR  : 'or' ;
NOT : 'not' | '!';

TRUE  : 'true' ;
FALSE : 'false' ;

POW	:	'^';
MULT  : '*' ;
DIV   : '/' ;
MOD	:	'%';
PLUS  : '+' ;
MINUS : '-' ;

GT : '>' ;
GE : '>=' ;
LT : '<' ;
LE : '<=' ;
EQ : '=' ;
NE : '!='|'<>' ;

LPAREN : '(' ;
RPAREN : ')' ;

COMMA: ',';

// function names
MATCHES_NAME: 'matches' ;

// gesture names
CLICK_NAME: 'click' ;
TYPE_NAME: 'type' ;
DRAG_NAME: 'drag' ;
ANY_NAME: 'anyGesture' ;
DOUBLE_CLICK_NAME: 'doubleClick';
TRIPLE_CLICK_NAME: 'tripleClick';
RIGHT_CLICK_NAME: 'rightClick';
MOUSE_MOVE_NAME: 'mouseMove';
DROP_DOWN_AT_NAME: 'dropDownAt';

// standard variables for widget attributes
BOOLEAN_VARIABLE : VARIABLE_PREFIX BOOLEAN_VARIABLE_NAME;

NUMBER_VARIABLE : VARIABLE_PREFIX NUMBER_VARIABLE_NAME;

STRING_VARIABLE : VARIABLE_PREFIX STRING_VARIABLE_NAME;

fragment VARIABLE_PREFIX :  '$';

// End of line, also consume leading and trailing whitespaces
EOL:  WS* ('\r'? '\n' | '\r') WS* { if(ignoreEOL) skip();} ;	

// Whitespace
WS: (' ' | '\t')+ { if(ignoreWS) skip();};

// Any other characters
OTHER: .+?;

fragment DQUOTE : '"';

fragment QUOTE : '\'';