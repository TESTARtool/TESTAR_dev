// Generated from C:/Users/lh3/IdeaProjects/TESTAR/strategy/src/antlrfour\Strategy.g4 by ANTLR 4.9.2
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
	 * Enter a parse tree produced by the {@code greaterThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterGreaterThanExpr(StrategyParser.GreaterThanExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code greaterThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitGreaterThanExpr(StrategyParser.GreaterThanExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code baseBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterBaseBool(StrategyParser.BaseBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code baseBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitBaseBool(StrategyParser.BaseBoolContext ctx);
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
	 * Enter a parse tree produced by the {@code lessThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterLessThanExpr(StrategyParser.LessThanExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lessThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitLessThanExpr(StrategyParser.LessThanExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code greaterEqualThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterGreaterEqualThanExpr(StrategyParser.GreaterEqualThanExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code greaterEqualThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitGreaterEqualThanExpr(StrategyParser.GreaterEqualThanExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notEqualExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterNotEqualExpr(StrategyParser.NotEqualExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notEqualExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitNotEqualExpr(StrategyParser.NotEqualExprContext ctx);
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
	 * Enter a parse tree produced by the {@code orExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(StrategyParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(StrategyParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lessEqualThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterLessEqualThanExpr(StrategyParser.LessEqualThanExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lessEqualThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitLessEqualThanExpr(StrategyParser.LessEqualThanExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code xorExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterXorExpr(StrategyParser.XorExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code xorExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitXorExpr(StrategyParser.XorExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(StrategyParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(StrategyParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code equalExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterEqualExpr(StrategyParser.EqualExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code equalExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitEqualExpr(StrategyParser.EqualExprContext ctx);
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
	 * Enter a parse tree produced by the {@code availableActionsOftype}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterAvailableActionsOftype(StrategyParser.AvailableActionsOftypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code availableActionsOftype}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitAvailableActionsOftype(StrategyParser.AvailableActionsOftypeContext ctx);
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
	 * Enter a parse tree produced by the {@code siblingActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterSiblingActionExists(StrategyParser.SiblingActionExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code siblingActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitSiblingActionExists(StrategyParser.SiblingActionExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code childActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterChildActionExists(StrategyParser.ChildActionExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code childActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitChildActionExists(StrategyParser.ChildActionExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code childOrSiblingActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterChildOrSiblingActionExists(StrategyParser.ChildOrSiblingActionExistsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code childOrSiblingActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitChildOrSiblingActionExists(StrategyParser.ChildOrSiblingActionExistsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tnActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterTnActions(StrategyParser.TnActionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tnActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitTnActions(StrategyParser.TnActionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tnUnexActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterTnUnexActions(StrategyParser.TnUnexActionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tnUnexActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitTnUnexActions(StrategyParser.TnUnexActionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tnExActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterTnExActions(StrategyParser.TnExActionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tnExActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitTnExActions(StrategyParser.TnExActionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nActionsOfType}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterNActionsOfType(StrategyParser.NActionsOfTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nActionsOfType}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitNActionsOfType(StrategyParser.NActionsOfTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nExecActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterNExecActions(StrategyParser.NExecActionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nExecActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitNExecActions(StrategyParser.NExecActionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nUnexActionsOfType}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void enterNUnexActionsOfType(StrategyParser.NUnexActionsOfTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nUnexActionsOfType}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 */
	void exitNUnexActionsOfType(StrategyParser.NUnexActionsOfTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRAction(StrategyParser.RActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRAction(StrategyParser.RActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prevAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterPrevAction(StrategyParser.PrevActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prevAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitPrevAction(StrategyParser.PrevActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rUnexAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRUnexAction(StrategyParser.RUnexActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rUnexAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRUnexAction(StrategyParser.RUnexActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rLeastExAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRLeastExAction(StrategyParser.RLeastExActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rLeastExAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRLeastExAction(StrategyParser.RLeastExActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rMostExAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRMostExAction(StrategyParser.RMostExActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rMostExAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRMostExAction(StrategyParser.RMostExActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rActionOfType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRActionOfType(StrategyParser.RActionOfTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rActionOfType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRActionOfType(StrategyParser.RActionOfTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rUnexActionOfType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRUnexActionOfType(StrategyParser.RUnexActionOfTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rUnexActionOfType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRUnexActionOfType(StrategyParser.RUnexActionOfTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rActionNotType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRActionNotType(StrategyParser.RActionNotTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rActionNotType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRActionNotType(StrategyParser.RActionNotTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rUnexActionNotType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterRUnexActionNotType(StrategyParser.RUnexActionNotTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rUnexActionNotType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitRUnexActionNotType(StrategyParser.RUnexActionNotTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sSubmitAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSSubmitAction(StrategyParser.SSubmitActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sSubmitAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSSubmitAction(StrategyParser.SSubmitActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sSiblingAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSSiblingAction(StrategyParser.SSiblingActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sSiblingAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSSiblingAction(StrategyParser.SSiblingActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sChildAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSChildAction(StrategyParser.SChildActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sChildAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSChildAction(StrategyParser.SChildActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sChildOrSiblingAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterSChildOrSiblingAction(StrategyParser.SChildOrSiblingActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sChildOrSiblingAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitSChildOrSiblingAction(StrategyParser.SChildOrSiblingActionContext ctx);
}