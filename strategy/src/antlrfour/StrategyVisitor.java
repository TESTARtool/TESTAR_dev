// Generated from C:/Users/lh3/IdeaProjects/TESTAR/strategy/src/antlrfour\Strategy.g4 by ANTLR 4.9.2
package antlrfour;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link StrategyParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface StrategyVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link StrategyParser#strategy_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrategy_file(StrategyParser.Strategy_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#strategy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrategy(StrategyParser.StrategyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code greaterThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGreaterThanExpr(StrategyParser.GreaterThanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code baseBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseBool(StrategyParser.BaseBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(StrategyParser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lessThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessThanExpr(StrategyParser.LessThanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code greaterEqualThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGreaterEqualThanExpr(StrategyParser.GreaterEqualThanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notEqualExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotEqualExpr(StrategyParser.NotEqualExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stateBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateBool(StrategyParser.StateBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExpr(StrategyParser.OrExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lessEqualThanExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessEqualThanExpr(StrategyParser.LessEqualThanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code xorExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXorExpr(StrategyParser.XorExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpr(StrategyParser.AndExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code equalExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualExpr(StrategyParser.EqualExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#number_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber_expr(StrategyParser.Number_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#action_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_expr(StrategyParser.Action_exprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code availableActionsOftype}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAvailableActionsOftype(StrategyParser.AvailableActionsOftypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sutType}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSutType(StrategyParser.SutTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stateChanged}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateChanged(StrategyParser.StateChangedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tnActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTnActions(StrategyParser.TnActionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tnUnexActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTnUnexActions(StrategyParser.TnUnexActionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tnExActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTnExActions(StrategyParser.TnExActionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nActionsOfType}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNActionsOfType(StrategyParser.NActionsOfTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nExecActions}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNExecActions(StrategyParser.NExecActionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nUnexActionsOfType}
	 * labeled alternative in {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNUnexActionsOfType(StrategyParser.NUnexActionsOfTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRAction(StrategyParser.RActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prevAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrevAction(StrategyParser.PrevActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rUnexAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRUnexAction(StrategyParser.RUnexActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rLeastExAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRLeastExAction(StrategyParser.RLeastExActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rMostExAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRMostExAction(StrategyParser.RMostExActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rActionOfType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRActionOfType(StrategyParser.RActionOfTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rUnexActionOfType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRUnexActionOfType(StrategyParser.RUnexActionOfTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rActionNotType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRActionNotType(StrategyParser.RActionNotTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rUnexActionNotType}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRUnexActionNotType(StrategyParser.RUnexActionNotTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sSubmitAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSSubmitAction(StrategyParser.SSubmitActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sSiblingAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSSiblingAction(StrategyParser.SSiblingActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sChildAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSChildAction(StrategyParser.SChildActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sChildOrSiblingAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSChildOrSiblingAction(StrategyParser.SChildOrSiblingActionContext ctx);
}