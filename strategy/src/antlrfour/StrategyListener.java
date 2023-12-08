// Generated from C:/Users/testar/Desktop/TESTAR_dev/strategy/src/antlrfour/Strategy.g4 by ANTLR 4.13.1
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
	 * Enter a parse tree produced by {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(StrategyParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(StrategyParser.ActionContext ctx);
	/**
	 * Enter a parse tree produced by {@link StrategyParser#cond_action}.
	 * @param ctx the parse tree
	 */
	void enterCond_action(StrategyParser.Cond_actionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StrategyParser#cond_action}.
	 * @param ctx the parse tree
	 */
	void exitCond_action(StrategyParser.Cond_actionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code repeatPreviousAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void enterRepeatPreviousAction(StrategyParser.RepeatPreviousActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code repeatPreviousAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void exitRepeatPreviousAction(StrategyParser.RepeatPreviousActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void enterSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void exitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectByRelation}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void enterSelectByRelation(StrategyParser.SelectByRelationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectByRelation}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void exitSelectByRelation(StrategyParser.SelectByRelationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void enterSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 */
	void exitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
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
	 * Enter a parse tree produced by the {@code intOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterIntOprExpr(StrategyParser.IntOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitIntOprExpr(StrategyParser.IntOprExprContext ctx);
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
	 * Enter a parse tree produced by the {@code anyExistByRelation}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterAnyExistByRelation(StrategyParser.AnyExistByRelationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code anyExistByRelation}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitAnyExistByRelation(StrategyParser.AnyExistByRelationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code anyExist}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterAnyExist(StrategyParser.AnyExistContext ctx);
	/**
	 * Exit a parse tree produced by the {@code anyExist}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitAnyExist(StrategyParser.AnyExistContext ctx);
	/**
	 * Enter a parse tree produced by the {@code previousExist}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void enterPreviousExist(StrategyParser.PreviousExistContext ctx);
	/**
	 * Exit a parse tree produced by the {@code previousExist}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 */
	void exitPreviousExist(StrategyParser.PreviousExistContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nActions}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 */
	void enterNActions(StrategyParser.NActionsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nActions}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 */
	void exitNActions(StrategyParser.NActionsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nPrevious}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 */
	void enterNPrevious(StrategyParser.NPreviousContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nPrevious}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 */
	void exitNPrevious(StrategyParser.NPreviousContext ctx);
	/**
	 * Enter a parse tree produced by the {@code plainInt}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 */
	void enterPlainInt(StrategyParser.PlainIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code plainInt}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 */
	void exitPlainInt(StrategyParser.PlainIntContext ctx);
}