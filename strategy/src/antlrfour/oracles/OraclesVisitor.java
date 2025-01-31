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
	 * Visit a parse tree produced by {@link OraclesParser#oracle_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOracle_block(OraclesParser.Oracle_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#select_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_block(OraclesParser.Select_blockContext ctx);
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
	 * Visit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(OraclesParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolOprExpr(OraclesParser.BoolOprExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code propertyBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPropertyBool(OraclesParser.PropertyBoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty_line(OraclesParser.Property_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(OraclesParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#regex_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegex_string(OraclesParser.Regex_stringContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#property_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty_string(OraclesParser.Property_stringContext ctx);
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
	/**
	 * Visit a parse tree produced by {@link OraclesParser#oracle_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOracle_name(OraclesParser.Oracle_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link OraclesParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(OraclesParser.ListContext ctx);
}