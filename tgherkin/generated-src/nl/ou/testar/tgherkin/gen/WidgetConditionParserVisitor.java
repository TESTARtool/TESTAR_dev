// Generated from WidgetConditionParser.g4 by ANTLR 4.5

package nl.ou.testar.tgherkin.gen;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WidgetConditionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WidgetConditionParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code LogicalEntity}
	 * labeled alternative in {@link WidgetConditionParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalEntity(WidgetConditionParser.LogicalEntityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalExpression}
	 * labeled alternative in {@link WidgetConditionParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(WidgetConditionParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetConditionOr}
	 * labeled alternative in {@link WidgetConditionParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetConditionOr(WidgetConditionParser.WidgetConditionOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NegationWidgetCondition}
	 * labeled alternative in {@link WidgetConditionParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegationWidgetCondition(WidgetConditionParser.NegationWidgetConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetConditionInParen}
	 * labeled alternative in {@link WidgetConditionParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetConditionInParen(WidgetConditionParser.WidgetConditionInParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetConditionAnd}
	 * labeled alternative in {@link WidgetConditionParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetConditionAnd(WidgetConditionParser.WidgetConditionAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalNumericExpressionWithOperator}
	 * labeled alternative in {@link WidgetConditionParser#relational_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalNumericExpressionWithOperator(WidgetConditionParser.RelationalNumericExpressionWithOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalStringExpressionWithOperator}
	 * labeled alternative in {@link WidgetConditionParser#relational_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalStringExpressionWithOperator(WidgetConditionParser.RelationalStringExpressionWithOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalExpressionParens}
	 * labeled alternative in {@link WidgetConditionParser#relational_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpressionParens(WidgetConditionParser.RelationalExpressionParensContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational_operator(WidgetConditionParser.Relational_operatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionPow}
	 * labeled alternative in {@link WidgetConditionParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionPow(WidgetConditionParser.ArithmeticExpressionPowContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionParens}
	 * labeled alternative in {@link WidgetConditionParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionParens(WidgetConditionParser.ArithmeticExpressionParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionNumericEntity}
	 * labeled alternative in {@link WidgetConditionParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionNumericEntity(WidgetConditionParser.ArithmeticExpressionNumericEntityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionMultDivMod}
	 * labeled alternative in {@link WidgetConditionParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionMultDivMod(WidgetConditionParser.ArithmeticExpressionMultDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionNegation}
	 * labeled alternative in {@link WidgetConditionParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionNegation(WidgetConditionParser.ArithmeticExpressionNegationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionPlusMinus}
	 * labeled alternative in {@link WidgetConditionParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionPlusMinus(WidgetConditionParser.ArithmeticExpressionPlusMinusContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#string_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_expr(WidgetConditionParser.String_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#booleanFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanFunction(WidgetConditionParser.BooleanFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#stringFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringFunction(WidgetConditionParser.StringFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#numericFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericFunction(WidgetConditionParser.NumericFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#matchesFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchesFunction(WidgetConditionParser.MatchesFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#xpathFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathFunction(WidgetConditionParser.XpathFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#xpathBooleanFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathBooleanFunction(WidgetConditionParser.XpathBooleanFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#xpathNumberFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathNumberFunction(WidgetConditionParser.XpathNumberFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#xpathStringFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathStringFunction(WidgetConditionParser.XpathStringFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#imageFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImageFunction(WidgetConditionParser.ImageFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#ocrFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOcrFunction(WidgetConditionParser.OcrFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#stateFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateFunction(WidgetConditionParser.StateFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetCondition}
	 * labeled alternative in {@link WidgetConditionParser#widget_tree_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetCondition(WidgetConditionParser.WidgetConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetTreeConditionEither}
	 * labeled alternative in {@link WidgetConditionParser#widget_tree_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetTreeConditionEither(WidgetConditionParser.WidgetTreeConditionEitherContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetTreeConditionAlso}
	 * labeled alternative in {@link WidgetConditionParser#widget_tree_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetTreeConditionAlso(WidgetConditionParser.WidgetTreeConditionAlsoContext ctx);
	/**
	 * Visit a parse tree produced by {@link WidgetConditionParser#bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(WidgetConditionParser.BoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalConst}
	 * labeled alternative in {@link WidgetConditionParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalConst(WidgetConditionParser.LogicalConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalVariable}
	 * labeled alternative in {@link WidgetConditionParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalVariable(WidgetConditionParser.LogicalVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalPlaceholder}
	 * labeled alternative in {@link WidgetConditionParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalPlaceholder(WidgetConditionParser.LogicalPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalFunction}
	 * labeled alternative in {@link WidgetConditionParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalFunction(WidgetConditionParser.LogicalFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntegerConst}
	 * labeled alternative in {@link WidgetConditionParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerConst(WidgetConditionParser.IntegerConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DecimalConst}
	 * labeled alternative in {@link WidgetConditionParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalConst(WidgetConditionParser.DecimalConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumericVariable}
	 * labeled alternative in {@link WidgetConditionParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericVariable(WidgetConditionParser.NumericVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumericPlaceholder}
	 * labeled alternative in {@link WidgetConditionParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericPlaceholder(WidgetConditionParser.NumericPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberFunction}
	 * labeled alternative in {@link WidgetConditionParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberFunction(WidgetConditionParser.NumberFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringConst}
	 * labeled alternative in {@link WidgetConditionParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringConst(WidgetConditionParser.StringConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringVariable}
	 * labeled alternative in {@link WidgetConditionParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringVariable(WidgetConditionParser.StringVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringPlaceholder}
	 * labeled alternative in {@link WidgetConditionParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringPlaceholder(WidgetConditionParser.StringPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StrFunction}
	 * labeled alternative in {@link WidgetConditionParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrFunction(WidgetConditionParser.StrFunctionContext ctx);
}