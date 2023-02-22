// Generated from java-escape by ANTLR 4.11.1
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
	 * Visit a parse tree produced by {@link StrategyParser#if_then_else}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_then_else(StrategyParser.If_then_elseContext ctx);
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
	 * Visit a parse tree produced by the {@code numberOprExpr}
	 * labeled alternative in {@link StrategyParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberOprExpr(StrategyParser.NumberOprExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#number_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber_expr(StrategyParser.Number_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#number_of_actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx);
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
	 * Visit a parse tree produced by the {@code sutType}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSutType(StrategyParser.SutTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#action_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_expr(StrategyParser.Action_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link StrategyParser#action_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_list(StrategyParser.Action_listContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectRelatedAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
}