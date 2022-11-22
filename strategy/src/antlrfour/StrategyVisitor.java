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
	 * Visit a parse tree produced by the {@code anyActionsExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyActionsExists(StrategyParser.AnyActionsExistsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sutType}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSutType(StrategyParser.SutTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relatedActionExists}
	 * labeled alternative in {@link StrategyParser#state_boolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelatedActionExists(StrategyParser.RelatedActionExistsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code subStrategy}
	 * labeled alternative in {@link StrategyParser#action_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubStrategy(StrategyParser.SubStrategyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code actionList}
	 * labeled alternative in {@link StrategyParser#action_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionList(StrategyParser.ActionListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectPreviousAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectRandomAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code selectRelatedAction}
	 * labeled alternative in {@link StrategyParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code siblingAction}
	 * labeled alternative in {@link StrategyParser#related_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSiblingAction(StrategyParser.SiblingActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code childAction}
	 * labeled alternative in {@link StrategyParser#related_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChildAction(StrategyParser.ChildActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code childOrSiblingAction}
	 * labeled alternative in {@link StrategyParser#related_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChildOrSiblingAction(StrategyParser.ChildOrSiblingActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code windows}
	 * labeled alternative in {@link StrategyParser#sut_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWindows(StrategyParser.WindowsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code linux}
	 * labeled alternative in {@link StrategyParser#sut_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinux(StrategyParser.LinuxContext ctx);
	/**
	 * Visit a parse tree produced by the {@code android}
	 * labeled alternative in {@link StrategyParser#sut_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndroid(StrategyParser.AndroidContext ctx);
	/**
	 * Visit a parse tree produced by the {@code web}
	 * labeled alternative in {@link StrategyParser#sut_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeb(StrategyParser.WebContext ctx);
	/**
	 * Visit a parse tree produced by the {@code click}
	 * labeled alternative in {@link StrategyParser#action_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClick(StrategyParser.ClickContext ctx);
	/**
	 * Visit a parse tree produced by the {@code typing}
	 * labeled alternative in {@link StrategyParser#action_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTyping(StrategyParser.TypingContext ctx);
	/**
	 * Visit a parse tree produced by the {@code drag}
	 * labeled alternative in {@link StrategyParser#action_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDrag(StrategyParser.DragContext ctx);
	/**
	 * Visit a parse tree produced by the {@code scroll}
	 * labeled alternative in {@link StrategyParser#action_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScroll(StrategyParser.ScrollContext ctx);
	/**
	 * Visit a parse tree produced by the {@code hitKey}
	 * labeled alternative in {@link StrategyParser#action_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHitKey(StrategyParser.HitKeyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code input}
	 * labeled alternative in {@link StrategyParser#action_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(StrategyParser.InputContext ctx);
}