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
	 * Visit a parse tree produced by {@link WidgetConditionParser#matchesFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchesFunction(WidgetConditionParser.MatchesFunctionContext ctx);
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
}