lexer grammar TgherkinLexer;
@header {package nl.ou.testar.tgherkin.gen;}
import Variables, Constants;

@lexer::members {
boolean ignoreEOL=false;
boolean ignoreWS=true;
}

OPTION_KEYWORD_INCLUDE: 'include:';
OPTION_KEYWORD_EXCLUDE: 'exclude:';

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

STEP_WHILE_KEYWORD: 'While' {ignoreEOL=true;ignoreWS=true;};

STEP_REPEAT_KEYWORD: 'Repeat' {ignoreEOL=true;ignoreWS=true;};

STEP_UNTIL_KEYWORD: 'until' {ignoreEOL=true;ignoreWS=true;};

STEP_GIVEN_KEYWORD: 'Given' {ignoreEOL=true;ignoreWS=true;};

STEP_WHEN_KEYWORD: 'When' {ignoreEOL=true;ignoreWS=true;};

STEP_THEN_KEYWORD: 'Then';

STEP_ALSO_KEYWORD: 'Also';

STEP_EITHER_KEYWORD: 'Either';

TABLE_ROW: {ignoreWS=true;}'|' (~('\n' | '\r')* '|')+ WS* (EOL|EOF);

DECIMAL_NUMBER: [0-9]+'.'[0-9]+;

INTEGER_NUMBER: [0-9]+;

PLACEHOLDER: '<' ~('>' | ' ' | '\t' | '\n' | '\r')+ '>';

STRING: '"' (ESC|.)*? '"';
 
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
XPATH_NAME: 'xpath' ;
XPATH_BOOLEAN_NAME: 'xpathBoolean' ;
XPATH_NUMBER_NAME: 'xpathNumber' ;
XPATH_STRING_NAME: 'xpathString' ;
IMAGE_NAME: 'image' ;
OCR_NAME: 'ocr' ;
STATE_NAME: 'state' ;

// gesture names
CLICK_NAME: 'click' ;
TYPE_NAME: 'type' ;
DRAG_SLIDER_NAME: 'dragSlider' ;
ANY_NAME: 'anyGesture' ;
DOUBLE_CLICK_NAME: 'doubleClick';
TRIPLE_CLICK_NAME: 'tripleClick';
RIGHT_CLICK_NAME: 'rightClick';
MOUSE_MOVE_NAME: 'mouseMove';
DROP_DOWN_AT_NAME: 'dropDownAt';
HIT_KEY_NAME: 'hitKey' ;
DRAG_DROP_NAME: 'dragDrop' ;

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

fragment ESC : '\\"' | '\\\\' ;