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
	 * Enter a parse tree produced by {@link OraclesParser#given_block}.
	 * @param ctx the parse tree
	 */
	void enterGiven_block(OraclesParser.Given_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#given_block}.
	 * @param ctx the parse tree
	 */
	void exitGiven_block(OraclesParser.Given_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OraclesParser#group_block}.
	 * @param ctx the parse tree
	 */
	void enterGroup_block(OraclesParser.Group_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#group_block}.
	 * @param ctx the parse tree
	 */
	void exitGroup_block(OraclesParser.Group_blockContext ctx);
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
	 * Enter a parse tree produced by the {@code pass}
	 * labeled alternative in {@link OraclesParser#check_type}.
	 * @param ctx the parse tree
	 */
	void enterPass(OraclesParser.PassContext ctx);
	/**
	 * Exit a parse tree produced by the {@code pass}
	 * labeled alternative in {@link OraclesParser#check_type}.
	 * @param ctx the parse tree
	 */
	void exitPass(OraclesParser.PassContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fail}
	 * labeled alternative in {@link OraclesParser#check_type}.
	 * @param ctx the parse tree
	 */
	void enterFail(OraclesParser.FailContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fail}
	 * labeled alternative in {@link OraclesParser#check_type}.
	 * @param ctx the parse tree
	 */
	void exitFail(OraclesParser.FailContext ctx);
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
	 * Enter a parse tree produced by the {@code operatorExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterOperatorExpr(OraclesParser.OperatorExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code operatorExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitOperatorExpr(OraclesParser.OperatorExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code plainBoolExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void enterPlainBoolExpr(OraclesParser.PlainBoolExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code plainBoolExpr}
	 * labeled alternative in {@link OraclesParser#bool_expr}.
	 * @param ctx the parse tree
	 */
	void exitPlainBoolExpr(OraclesParser.PlainBoolExprContext ctx);
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
	 * Enter a parse tree produced by the {@code operator_and}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator_and(OraclesParser.Operator_andContext ctx);
	/**
	 * Exit a parse tree produced by the {@code operator_and}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator_and(OraclesParser.Operator_andContext ctx);
	/**
	 * Enter a parse tree produced by the {@code operator_xor}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator_xor(OraclesParser.Operator_xorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code operator_xor}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator_xor(OraclesParser.Operator_xorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code operator_or}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator_or(OraclesParser.Operator_orContext ctx);
	/**
	 * Exit a parse tree produced by the {@code operator_or}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator_or(OraclesParser.Operator_orContext ctx);
	/**
	 * Enter a parse tree produced by the {@code operator_equals}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator_equals(OraclesParser.Operator_equalsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code operator_equals}
	 * labeled alternative in {@link OraclesParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator_equals(OraclesParser.Operator_equalsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propKeyValue}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropKeyValue(OraclesParser.PropKeyValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propKeyValue}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropKeyValue(OraclesParser.PropKeyValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propIsBool}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropIsBool(OraclesParser.PropIsBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propIsBool}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropIsBool(OraclesParser.PropIsBoolContext ctx);
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
	 * Enter a parse tree produced by the {@code propIsInRange}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropIsInRange(OraclesParser.PropIsInRangeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propIsInRange}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropIsInRange(OraclesParser.PropIsInRangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propStandard}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void enterPropStandard(OraclesParser.PropStandardContext ctx);
	/**
	 * Exit a parse tree produced by the {@code propStandard}
	 * labeled alternative in {@link OraclesParser#property_line}.
	 * @param ctx the parse tree
	 */
	void exitPropStandard(OraclesParser.PropStandardContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link OraclesParser#range}.
	 * @param ctx the parse tree
	 */
	void enterRange(OraclesParser.RangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link OraclesParser#range}.
	 * @param ctx the parse tree
	 */
	void exitRange(OraclesParser.RangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code keyLocation}
	 * labeled alternative in {@link OraclesParser#location}.
	 * @param ctx the parse tree
	 */
	void enterKeyLocation(OraclesParser.KeyLocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code keyLocation}
	 * labeled alternative in {@link OraclesParser#location}.
	 * @param ctx the parse tree
	 */
	void exitKeyLocation(OraclesParser.KeyLocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code valueLocation}
	 * labeled alternative in {@link OraclesParser#location}.
	 * @param ctx the parse tree
	 */
	void enterValueLocation(OraclesParser.ValueLocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code valueLocation}
	 * labeled alternative in {@link OraclesParser#location}.
	 * @param ctx the parse tree
	 */
	void exitValueLocation(OraclesParser.ValueLocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code anyLocation}
	 * labeled alternative in {@link OraclesParser#location}.
	 * @param ctx the parse tree
	 */
	void enterAnyLocation(OraclesParser.AnyLocationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code anyLocation}
	 * labeled alternative in {@link OraclesParser#location}.
	 * @param ctx the parse tree
	 */
	void exitAnyLocation(OraclesParser.AnyLocationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparator_equals}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator_equals(OraclesParser.Comparator_equalsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparator_equals}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator_equals(OraclesParser.Comparator_equalsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparator_matches}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator_matches(OraclesParser.Comparator_matchesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparator_matches}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator_matches(OraclesParser.Comparator_matchesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparator_contains}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator_contains(OraclesParser.Comparator_containsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparator_contains}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator_contains(OraclesParser.Comparator_containsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparator_startsWith}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator_startsWith(OraclesParser.Comparator_startsWithContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparator_startsWith}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator_startsWith(OraclesParser.Comparator_startsWithContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparator_endsWith}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator_endsWith(OraclesParser.Comparator_endsWithContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparator_endsWith}
	 * labeled alternative in {@link OraclesParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator_endsWith(OraclesParser.Comparator_endsWithContext ctx);
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
}