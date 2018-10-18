parser grammar WidgetConditionParser;

options { tokenVocab=TgherkinLexer; }

@header {
package nl.ou.testar.tgherkin.gen;
}

widget_condition: 
   logical_entity                                   #LogicalEntity
 | LPAREN widget_condition RPAREN                   #WidgetConditionInParen
 | NOT widget_condition                             #NegationWidgetCondition
 | relational_expr                                  #RelationalExpression
 | left=widget_condition AND right=widget_condition #WidgetConditionAnd
 | left=widget_condition OR right=widget_condition  #WidgetConditionOr;
 
relational_expr : 
   left=arithmetic_expr relational_operator right=arithmetic_expr       #RelationalNumericExpressionWithOperator
 | left=string_expr relational_operator right=string_expr               #RelationalStringExpressionWithOperator   
 | LPAREN relational_expr RPAREN                                        #RelationalExpressionParens;

relational_operator : 
   GT
 | GE
 | LT
 | LE
 | EQ
 | NE;

arithmetic_expr:
   numeric_entity                                            #ArithmeticExpressionNumericEntity
 | LPAREN arithmetic_expr RPAREN                             #ArithmeticExpressionParens
 | MINUS arithmetic_expr                                     #ArithmeticExpressionNegation
 | left=arithmetic_expr POW right=arithmetic_expr            #ArithmeticExpressionPow 
 | left=arithmetic_expr (MULT|DIV|MOD) right=arithmetic_expr #ArithmeticExpressionMultDivMod
 | left=arithmetic_expr (PLUS|MINUS) right=arithmetic_expr   #ArithmeticExpressionPlusMinus;

string_expr : string_entity; 
 
booleanFunction: matchesFunction | xpathFunction | xpathBooleanFunction | imageFunction | stateFunction;
stringFunction: ocrFunction | xpathStringFunction;
numericFunction: xpathNumberFunction;

matchesFunction: MATCHES_NAME LPAREN string_entity COMMA STRING RPAREN; 
xpathFunction: XPATH_NAME LPAREN STRING RPAREN;
xpathBooleanFunction: XPATH_BOOLEAN_NAME LPAREN STRING RPAREN;
xpathNumberFunction: XPATH_NUMBER_NAME LPAREN STRING RPAREN;
xpathStringFunction: XPATH_STRING_NAME LPAREN STRING RPAREN;
imageFunction: IMAGE_NAME LPAREN STRING RPAREN;
ocrFunction: OCR_NAME LPAREN RPAREN;
stateFunction: STATE_NAME LPAREN widget_tree_condition RPAREN;
 
widget_tree_condition : 
   widget_condition                                                           #WidgetCondition
 | left=widget_tree_condition STEP_ALSO_KEYWORD right=widget_tree_condition   #WidgetTreeConditionAlso
 | left=widget_tree_condition STEP_EITHER_KEYWORD right=widget_tree_condition #WidgetTreeConditionEither; 

bool: TRUE | FALSE;
				
logical_entity : 
   bool                 #LogicalConst
 | BOOLEAN_VARIABLE     #LogicalVariable
 | PLACEHOLDER          #LogicalPlaceholder
 | booleanFunction      #LogicalFunction;

numeric_entity : 
   INTEGER_NUMBER       #IntegerConst
 | DECIMAL_NUMBER       #DecimalConst
 | NUMBER_VARIABLE      #NumericVariable
 | PLACEHOLDER          #NumericPlaceholder
 | numericFunction      #NumberFunction; 	
 
string_entity : 
   STRING               #StringConst
 | STRING_VARIABLE      #StringVariable
 | PLACEHOLDER          #StringPlaceholder
 | stringFunction       #StrFunction;
		   
