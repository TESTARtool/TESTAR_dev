// Generated from TgherkinParser.g4 by ANTLR 4.5

package nl.ou.testar.tgherkin.gen;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TgherkinParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TgherkinParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#document}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDocument(TgherkinParser.DocumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#execOptions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExecOptions(TgherkinParser.ExecOptionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#execOptionExclude}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExecOptionExclude(TgherkinParser.ExecOptionExcludeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#execOptionInclude}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExecOptionInclude(TgherkinParser.ExecOptionIncludeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#feature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeature(TgherkinParser.FeatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#background}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackground(TgherkinParser.BackgroundContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#scenarioDefiniton}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenarioDefiniton(TgherkinParser.ScenarioDefinitonContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#scenario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenario(TgherkinParser.ScenarioContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#scenarioOutline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenarioOutline(TgherkinParser.ScenarioOutlineContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#examples}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExamples(TgherkinParser.ExamplesContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#table}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable(TgherkinParser.TableContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#title}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTitle(TgherkinParser.TitleContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#narrativeLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNarrativeLine(TgherkinParser.NarrativeLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#tagname}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagname(TgherkinParser.TagnameContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#selection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelection(TgherkinParser.SelectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#oracle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOracle(TgherkinParser.OracleContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#step}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStep(TgherkinParser.StepContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#givenClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGivenClause(TgherkinParser.GivenClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#whenClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenClause(TgherkinParser.WhenClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#thenClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThenClause(TgherkinParser.ThenClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#stepIteration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepIteration(TgherkinParser.StepIterationContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#stepRange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepRange(TgherkinParser.StepRangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#stepWhile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepWhile(TgherkinParser.StepWhileContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#stepRepeatUntil}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepRepeatUntil(TgherkinParser.StepRepeatUntilContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#conditional_gesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional_gesture(TgherkinParser.Conditional_gestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#gesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGesture(TgherkinParser.GestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#typeGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeGesture(TgherkinParser.TypeGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#clickGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClickGesture(TgherkinParser.ClickGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#doubleClickGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleClickGesture(TgherkinParser.DoubleClickGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#tripleClickGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTripleClickGesture(TgherkinParser.TripleClickGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#anyGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyGesture(TgherkinParser.AnyGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#hitKeyGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHitKeyGesture(TgherkinParser.HitKeyGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#hitKeyArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHitKeyArgument(TgherkinParser.HitKeyArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#dragDropGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDragDropGesture(TgherkinParser.DragDropGestureContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#gestureName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGestureName(TgherkinParser.GestureNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#parameterlessGesture}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterlessGesture(TgherkinParser.ParameterlessGestureContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalEntity}
	 * labeled alternative in {@link TgherkinParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalEntity(TgherkinParser.LogicalEntityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalExpression}
	 * labeled alternative in {@link TgherkinParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(TgherkinParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetConditionOr}
	 * labeled alternative in {@link TgherkinParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetConditionOr(TgherkinParser.WidgetConditionOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NegationWidgetCondition}
	 * labeled alternative in {@link TgherkinParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegationWidgetCondition(TgherkinParser.NegationWidgetConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetConditionInParen}
	 * labeled alternative in {@link TgherkinParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetConditionInParen(TgherkinParser.WidgetConditionInParenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetConditionAnd}
	 * labeled alternative in {@link TgherkinParser#widget_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetConditionAnd(TgherkinParser.WidgetConditionAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalNumericExpressionWithOperator}
	 * labeled alternative in {@link TgherkinParser#relational_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalNumericExpressionWithOperator(TgherkinParser.RelationalNumericExpressionWithOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalStringExpressionWithOperator}
	 * labeled alternative in {@link TgherkinParser#relational_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalStringExpressionWithOperator(TgherkinParser.RelationalStringExpressionWithOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalExpressionParens}
	 * labeled alternative in {@link TgherkinParser#relational_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpressionParens(TgherkinParser.RelationalExpressionParensContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational_operator(TgherkinParser.Relational_operatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionPow}
	 * labeled alternative in {@link TgherkinParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionPow(TgherkinParser.ArithmeticExpressionPowContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionParens}
	 * labeled alternative in {@link TgherkinParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionParens(TgherkinParser.ArithmeticExpressionParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionNumericEntity}
	 * labeled alternative in {@link TgherkinParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionNumericEntity(TgherkinParser.ArithmeticExpressionNumericEntityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionMultDivMod}
	 * labeled alternative in {@link TgherkinParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionMultDivMod(TgherkinParser.ArithmeticExpressionMultDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionNegation}
	 * labeled alternative in {@link TgherkinParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionNegation(TgherkinParser.ArithmeticExpressionNegationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArithmeticExpressionPlusMinus}
	 * labeled alternative in {@link TgherkinParser#arithmetic_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticExpressionPlusMinus(TgherkinParser.ArithmeticExpressionPlusMinusContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#string_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_expr(TgherkinParser.String_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#booleanFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanFunction(TgherkinParser.BooleanFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#stringFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringFunction(TgherkinParser.StringFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#numericFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericFunction(TgherkinParser.NumericFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#matchesFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchesFunction(TgherkinParser.MatchesFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#xpathFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathFunction(TgherkinParser.XpathFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#xpathBooleanFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathBooleanFunction(TgherkinParser.XpathBooleanFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#xpathNumberFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathNumberFunction(TgherkinParser.XpathNumberFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#xpathStringFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXpathStringFunction(TgherkinParser.XpathStringFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#imageFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImageFunction(TgherkinParser.ImageFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#ocrFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOcrFunction(TgherkinParser.OcrFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#stateFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateFunction(TgherkinParser.StateFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetCondition}
	 * labeled alternative in {@link TgherkinParser#widget_tree_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetCondition(TgherkinParser.WidgetConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetTreeConditionEither}
	 * labeled alternative in {@link TgherkinParser#widget_tree_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetTreeConditionEither(TgherkinParser.WidgetTreeConditionEitherContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WidgetTreeConditionAlso}
	 * labeled alternative in {@link TgherkinParser#widget_tree_condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWidgetTreeConditionAlso(TgherkinParser.WidgetTreeConditionAlsoContext ctx);
	/**
	 * Visit a parse tree produced by {@link TgherkinParser#bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(TgherkinParser.BoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalConst}
	 * labeled alternative in {@link TgherkinParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalConst(TgherkinParser.LogicalConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalVariable}
	 * labeled alternative in {@link TgherkinParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalVariable(TgherkinParser.LogicalVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalPlaceholder}
	 * labeled alternative in {@link TgherkinParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalPlaceholder(TgherkinParser.LogicalPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalFunction}
	 * labeled alternative in {@link TgherkinParser#logical_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalFunction(TgherkinParser.LogicalFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntegerConst}
	 * labeled alternative in {@link TgherkinParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerConst(TgherkinParser.IntegerConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DecimalConst}
	 * labeled alternative in {@link TgherkinParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalConst(TgherkinParser.DecimalConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumericVariable}
	 * labeled alternative in {@link TgherkinParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericVariable(TgherkinParser.NumericVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumericPlaceholder}
	 * labeled alternative in {@link TgherkinParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericPlaceholder(TgherkinParser.NumericPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberFunction}
	 * labeled alternative in {@link TgherkinParser#numeric_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberFunction(TgherkinParser.NumberFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringConst}
	 * labeled alternative in {@link TgherkinParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringConst(TgherkinParser.StringConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringVariable}
	 * labeled alternative in {@link TgherkinParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringVariable(TgherkinParser.StringVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringPlaceholder}
	 * labeled alternative in {@link TgherkinParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringPlaceholder(TgherkinParser.StringPlaceholderContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StrFunction}
	 * labeled alternative in {@link TgherkinParser#string_entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrFunction(TgherkinParser.StrFunctionContext ctx);
}