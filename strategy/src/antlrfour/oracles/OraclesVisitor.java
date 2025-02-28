// Generated from E:/lianne_dev/strategy/src/antlrfour/oracles/Oracles.g4 by ANTLR 4.13.1
package antlrfour.oracles;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link OraclesParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface OraclesVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link OraclesParser#oracles_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOracles_file(OraclesParser.Oracles_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#oracle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOracle(OraclesParser.OracleContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#given_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGiven_block(OraclesParser.Given_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#group_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup_block(OraclesParser.Group_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#check_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheck_block(OraclesParser.Check_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#trigger_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrigger_block(OraclesParser.Trigger_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#property_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty_block(OraclesParser.Property_blockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code plainBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlainBool(OraclesParser.PlainBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(OraclesParser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrOprExpr(OraclesParser.OrOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsOprExpr(OraclesParser.IsOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code xorOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitXorOprExpr(OraclesParser.XorOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndOprExpr(OraclesParser.AndOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(OraclesParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propertyBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyBool(OraclesParser.PropertyBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propKeyValue}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropKeyValue(OraclesParser.PropKeyValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propKey}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropKey(OraclesParser.PropKeyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propValue}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropValue(OraclesParser.PropValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propAny}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropAny(OraclesParser.PropAnyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propIsBool}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropIsBool(OraclesParser.PropIsBoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propIsInList}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropIsInList(OraclesParser.PropIsInListContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(OraclesParser.ListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparator_is}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator_is(OraclesParser.Comparator_isContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparator_matches}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator_matches(OraclesParser.Comparator_matchesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparator_contains}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator_contains(OraclesParser.Comparator_containsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparator_startsWith}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator_startsWith(OraclesParser.Comparator_startsWithContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparator_endsWith}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator_endsWith(OraclesParser.Comparator_endsWithContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#regex_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegex_string(OraclesParser.Regex_stringContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#raw_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRaw_string(OraclesParser.Raw_stringContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#basic_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasic_string(OraclesParser.Basic_stringContext ctx);
}