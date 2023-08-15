// Generated from C:/Users/lh3/IdeaProjects/TESTAR/strategy/src/antlrfour\Strategy.g4 by ANTLR 4.12.0
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
	 * Visit a parse tree produced by {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(StrategyParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#cond_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond_action(StrategyParser.Cond_actionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectRelation}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectRelation(StrategyParser.SelectRelationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#uncond_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plainBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlainBool(StrategyParser.PlainBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(StrategyParser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntOprExpr(StrategyParser.IntOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stateBool}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateBool(StrategyParser.StateBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stateChanged}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateChanged(StrategyParser.StateChangedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code anyExistRelatedAction}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyExistRelatedAction(StrategyParser.AnyExistRelatedActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code anyExist}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyExist(StrategyParser.AnyExistContext ctx);
	/**
	 * Visit a parse tree produced by the {@code previousExist}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreviousExist(StrategyParser.PreviousExistContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sutType}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSutType(StrategyParser.SutTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nActions}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNActions(StrategyParser.NActionsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plainInt}
	 * labeled alternative in {@link StrategyParser#int_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlainInt(StrategyParser.PlainIntContext ctx);
}