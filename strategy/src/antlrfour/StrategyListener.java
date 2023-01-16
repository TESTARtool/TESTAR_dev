// Generated from java-escape by ANTLR 4.11.1
package antlrfour;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link StrategyParser}.
 */
public interface StrategyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link StrategyParser#strategy_file}.
	 * @param ctx the parse tree
	 */
	void enterStrategy_file(StrategyParser.Strategy_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#strategy_file}.
	 * @param ctx the parse tree
	 */
	void exitStrategy_file(StrategyParser.Strategy_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#strategy}.
	 * @param ctx the parse tree
	 */
	void enterStrategy(StrategyParser.StrategyContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#strategy}.
	 * @param ctx the parse tree
	 */
	void exitStrategy(StrategyParser.StrategyContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#if_else_then}.
	 * @param ctx the parse tree
	 */
	void enterIf_else_then(StrategyParser.If_else_thenContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#if_else_then}.
	 * @param ctx the parse tree
	 */
	void exitIf_else_then(StrategyParser.If_else_thenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code plainBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterPlainBool(StrategyParser.PlainBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code plainBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitPlainBool(StrategyParser.PlainBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterNotExpr(StrategyParser.NotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitNotExpr(StrategyParser.NotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stateBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterStateBool(StrategyParser.StateBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stateBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitStateBool(StrategyParser.StateBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterBoolOprExpr(StrategyParser.BoolOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitBoolOprExpr(StrategyParser.BoolOprExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterNumberOprExpr(StrategyParser.NumberOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitNumberOprExpr(StrategyParser.NumberOprExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#number_expr}.
	 * @param ctx the parse tree
	 */
	void enterNumber_expr(StrategyParser.Number_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#number_expr}.
	 * @param ctx the parse tree
	 */
	void exitNumber_expr(StrategyParser.Number_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterNumber_of_actions(StrategyParser.Number_of_actionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stateChanged}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterStateChanged(StrategyParser.StateChangedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stateChanged}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitStateChanged(StrategyParser.StateChangedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code anyActionsExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterAnyActionsExists(StrategyParser.AnyActionsExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code anyActionsExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitAnyActionsExists(StrategyParser.AnyActionsExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sutType}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterSutType(StrategyParser.SutTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sutType}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitSutType(StrategyParser.SutTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relatedActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterRelatedActionExists(StrategyParser.RelatedActionExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relatedActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitRelatedActionExists(StrategyParser.RelatedActionExistsContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#action_expr}.
	 * @param ctx the parse tree
	 */
	void enterAction_expr(StrategyParser.Action_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#action_expr}.
	 * @param ctx the parse tree
	 */
	void exitAction_expr(StrategyParser.Action_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#action_list}.
	 * @param ctx the parse tree
	 */
	void enterAction_list(StrategyParser.Action_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#action_list}.
	 * @param ctx the parse tree
	 */
	void exitAction_list(StrategyParser.Action_listContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectRelatedAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectRelatedAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx);
}