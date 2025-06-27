package parsing;

import antlrfour.oracles.OraclesBaseVisitor;
import antlrfour.oracles.OraclesParser;
import oracle_objects.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OracleBuilder extends OraclesBaseVisitor<Predicate<State>>
{
    public GrammarOracle buildOracle(OraclesParser.OracleContext ctx, String originalText)
    {
        String name = stripOuterQuotes(String.valueOf(ctx.STRING()));
    
        GrammarOracle oracle = new GrammarOracle(ctx.IGNORE() != null, name);
    
        List<Predicate<State>> groupBlocks = new ArrayList<>();
        for(int j = 0; j < ctx.group_block().size(); j++)
        { groupBlocks.set(j, visit(ctx.group_block(j))); }
        oracle.setGroupLogic(groupBlocks);
    
        // set false if pass, otherwise set true for fail
        boolean failValue = !(ctx.check_block().check_type() instanceof OraclesParser.PassContext);
        oracle.setCheckLogic(failValue, visit(ctx.check_block()));
        
        String triggerText = (ctx.trigger_block() == null) ? "" : ctx.trigger_block().STRING().getText();
        oracle.setTrigger(stripOuterQuotes(triggerText));
        
        oracle.setGrammar(originalText);
        
        return oracle;
    }
    
    @Override public Predicate<State> visitGroup_block(OraclesParser.Group_blockContext ctx)
    {
        return visitChildren(ctx);
    }
    
    @Override public Predicate<State> visitCheck_block(OraclesParser.Check_blockContext ctx)
    {
        return visitChildren(ctx);
    }
    
    @Override public Predicate<State> visitNotExpr(OraclesParser.NotExprContext ctx)
    {
        return visitChildren(ctx).negate();
    }
    
    @Override public Predicate<State> visitOperatorExpr(OraclesParser.OperatorExprContext ctx)
    {
        Predicate<State> left = visit(ctx.left);
        Predicate<State> right = visit(ctx.right);
        
        if(ctx.operator() instanceof OraclesParser.Operator_andContext)
            return left.and(right);
        else if(ctx.operator() instanceof OraclesParser.Operator_xorContext)
            return xor(left, right);
        else if(ctx.operator() instanceof OraclesParser.Operator_orContext)
            return left.or(right);
        else if(ctx.operator() instanceof OraclesParser.Operator_equalsContext)
            return valueEquals(left, right);
        else return null; //shouldn't ever happen
    }
    
    @Override public Predicate<State> visitPlainBoolExpr(OraclesParser.PlainBoolExprContext ctx)
    {
        Predicate<State> boolPredicate;
        if(ctx.BOOL().getText().equalsIgnoreCase("true"))
            boolPredicate = state -> true;
        else
            boolPredicate = state -> false;
        return valueEquals(visit(ctx.bool_expr()), boolPredicate);
    }
    
    @Override public Predicate<State> visitPropKeyValue(OraclesParser.PropKeyValueContext ctx)
    {
        SearchTerm searchTerm = SearchTerm.pair
                (stripOuterQuotes(ctx.key.getText()),
                 stripOuterQuotes(ctx.value.getText()));
    
        return FunctionFactory.createPredicate
                (SearchLocation.PAIR,
                 getSearchComparator(ctx.comparator()),
                 searchTerm);
    }
    
    @Override public  Predicate<State> visitPropIsBool(OraclesParser.PropIsBoolContext ctx)
    {
        SearchTerm searchTerm = SearchTerm.singleBoolean(ctx.BOOL().getText());
    
        return FunctionFactory.createPredicate
                (SearchLocation.VALUE,
                 SearchComparator.IS,
                 searchTerm);
    }
    
    @Override public Predicate<State> visitPropIsInList(OraclesParser.PropIsInListContext ctx)
    {
        ArrayList<String> list = new ArrayList<>();
        for(TerminalNode item : ctx.list().STRING())
            list.add(stripOuterQuotes(item.getText()));
        SearchTerm searchTerm = SearchTerm.list(list);
        
        return FunctionFactory.createPredicate
                (getSearchLocation(ctx.location()),
                 SearchComparator.IS,
                 searchTerm);
    }
    
    @Override public Predicate<State> visitPropIsInRange(OraclesParser.PropIsInRangeContext ctx)
    {
        return FunctionFactory.createPredicate
                (SearchLocation.VALUE,
                 SearchComparator.IS,
                 SearchTerm.range(ctx.range().low.getText(), ctx.range().high.getText()));
    }
    
    @Override public Predicate<State> visitPropStandard(OraclesParser.PropStandardContext ctx)
    {
        return FunctionFactory.createPredicate
                (getSearchLocation(ctx.location()),
                 getSearchComparator(ctx.comparator()),
                 SearchTerm.single(stripOuterQuotes(ctx.STRING().getText())));
    }
    
    //////////////////////
    // helper functions //
    //////////////////////
    
    
    // only strips one set of outside quotes if it has them, leaves other quotes untouched
    // todo: add triple quotes once they're relevant
    private String stripOuterQuotes(String inputString)
    {
        if (inputString.length() >= 2 &&
            ((inputString.startsWith("\"") && inputString.endsWith("\"")) ||
            (inputString.startsWith("'") && inputString.endsWith("'"))))
            return inputString.substring(1, inputString.length() - 1);
        return inputString;
    }
    
    private SearchLocation getSearchLocation(OraclesParser.LocationContext ctx)
    {
        SearchLocation location = SearchLocation.PAIR; //should be overwritten in most cases
        if(ctx instanceof OraclesParser.KeyLocationContext) location = SearchLocation.KEY;
        else if(ctx instanceof OraclesParser.ValueLocationContext) location = SearchLocation.VALUE;
        else if(ctx instanceof OraclesParser.AnyLocationContext) location = SearchLocation.ANY;
    
        return location;
    }
    private SearchComparator getSearchComparator(OraclesParser.ComparatorContext ctx)
    {
        SearchComparator comparator = SearchComparator.IS; //should be overwritten in most cases
        if (ctx instanceof OraclesParser.Comparator_equalsContext) comparator = SearchComparator.EQUALS;
        else if (ctx instanceof OraclesParser.Comparator_matchesContext) comparator = SearchComparator.MATCHES;
        else if (ctx instanceof OraclesParser.Comparator_containsContext) comparator = SearchComparator.CONTAINS;
        else if (ctx instanceof OraclesParser.Comparator_startsWithContext) comparator = SearchComparator.STARTS_WITH;
        else if (ctx instanceof OraclesParser.Comparator_endsWithContext) comparator = SearchComparator.ENDS_WITH;
        
        return comparator;
    }
    
    public static <T> Predicate<T> xor(Predicate<T> a, Predicate<T> b) // xor for Predicate classes
    { // left.and(right.negate()).or(left.negate().and(right));
        return t -> a.test(t) ^ b.test(t);
    }
    
    // returns true if both sides are true or both sides are false
    public static <T> Predicate<T> valueEquals(Predicate<T> a, Predicate<T> b)
    {
        return xor(a, b).negate();
    }
}