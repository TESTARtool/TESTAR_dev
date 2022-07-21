// Generated from C:/Users/lh3/IdeaProjects/TESTAR/strategy/src/antlr4\Strategy.g4 by ANTLR 4.9.2
package antlr4;
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
	 * Enter a parse tree produced by the {@code stateUnchanged}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterStateUnchanged(StrategyParser.StateUnchangedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stateUnchanged}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitStateUnchanged(StrategyParser.StateUnchangedContext ctx);
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
	 * Enter a parse tree produced by {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(StrategyParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(StrategyParser.ActionContext ctx);
}