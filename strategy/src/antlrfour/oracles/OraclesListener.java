// Generated from E:/lianne_dev/strategy/src/antlrfour/oracles/Oracles.g4 by ANTLR 4.13.1
package antlrfour.oracles;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link OraclesParser}.
 */
public interface OraclesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link OraclesParser#oracles_file}.
	 * @param ctx the parse tree
	 */
	void enterOracles_file(OraclesParser.Oracles_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#oracles_file}.
	 * @param ctx the parse tree
	 */
	void exitOracles_file(OraclesParser.Oracles_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#oracle}.
	 * @param ctx the parse tree
	 */
	void enterOracle(OraclesParser.OracleContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#oracle}.
	 * @param ctx the parse tree
	 */
	void exitOracle(OraclesParser.OracleContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#select_block}.
	 * @param ctx the parse tree
	 */
	void enterSelect_block(OraclesParser.Select_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#select_block}.
	 * @param ctx the parse tree
	 */
	void exitSelect_block(OraclesParser.Select_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#check_block}.
	 * @param ctx the parse tree
	 */
	void enterCheck_block(OraclesParser.Check_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#check_block}.
	 * @param ctx the parse tree
	 */
	void exitCheck_block(OraclesParser.Check_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#trigger_block}.
	 * @param ctx the parse tree
	 */
	void enterTrigger_block(OraclesParser.Trigger_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#trigger_block}.
	 * @param ctx the parse tree
	 */
	void exitTrigger_block(OraclesParser.Trigger_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#property_block}.
	 * @param ctx the parse tree
	 */
	void enterProperty_block(OraclesParser.Property_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#property_block}.
	 * @param ctx the parse tree
	 */
	void exitProperty_block(OraclesParser.Property_blockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code plainBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterPlainBool(OraclesParser.PlainBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code plainBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitPlainBool(OraclesParser.PlainBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterNotExpr(OraclesParser.NotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitNotExpr(OraclesParser.NotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterOrOprExpr(OraclesParser.OrOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitOrOprExpr(OraclesParser.OrOprExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code xorOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterXorOprExpr(OraclesParser.XorOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code xorOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitXorOprExpr(OraclesParser.XorOprExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterAndOprExpr(OraclesParser.AndOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitAndOprExpr(OraclesParser.AndOprExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(OraclesParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(OraclesParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterBoolOprExpr(OraclesParser.BoolOprExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolOprExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitBoolOprExpr(OraclesParser.BoolOprExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propertyBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterPropertyBool(OraclesParser.PropertyBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propertyBool}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitPropertyBool(OraclesParser.PropertyBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code hasProperty}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterHasProperty(OraclesParser.HasPropertyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code hasProperty}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitHasProperty(OraclesParser.HasPropertyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propIs}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropIs(OraclesParser.PropIsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propIs}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropIs(OraclesParser.PropIsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propContains}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropContains(OraclesParser.PropContainsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propContains}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropContains(OraclesParser.PropContainsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propIsInList}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropIsInList(OraclesParser.PropIsInListContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propIsInList}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropIsInList(OraclesParser.PropIsInListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code widgetIsWidget}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterWidgetIsWidget(OraclesParser.WidgetIsWidgetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code widgetIsWidget}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitWidgetIsWidget(OraclesParser.WidgetIsWidgetContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(OraclesParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(OraclesParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#regex_string}.
	 * @param ctx the parse tree
	 */
	void enterRegex_string(OraclesParser.Regex_stringContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#regex_string}.
	 * @param ctx the parse tree
	 */
	void exitRegex_string(OraclesParser.Regex_stringContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#property_string}.
	 * @param ctx the parse tree
	 */
	void enterProperty_string(OraclesParser.Property_stringContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#property_string}.
	 * @param ctx the parse tree
	 */
	void exitProperty_string(OraclesParser.Property_stringContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#raw_string}.
	 * @param ctx the parse tree
	 */
	void enterRaw_string(OraclesParser.Raw_stringContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#raw_string}.
	 * @param ctx the parse tree
	 */
	void exitRaw_string(OraclesParser.Raw_stringContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#basic_string}.
	 * @param ctx the parse tree
	 */
	void enterBasic_string(OraclesParser.Basic_stringContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#basic_string}.
	 * @param ctx the parse tree
	 */
	void exitBasic_string(OraclesParser.Basic_stringContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#oracle_name}.
	 * @param ctx the parse tree
	 */
	void enterOracle_name(OraclesParser.Oracle_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#oracle_name}.
	 * @param ctx the parse tree
	 */
	void exitOracle_name(OraclesParser.Oracle_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#list}.
	 * @param ctx the parse tree
	 */
	void enterList(OraclesParser.ListContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#list}.
	 * @param ctx the parse tree
	 */
	void exitList(OraclesParser.ListContext ctx);
}