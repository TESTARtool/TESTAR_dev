package parsing;

import antlrfour.oracles.OraclesBaseVisitor;
import antlrfour.oracles.OraclesParser;
import oracle_objects.GrammarOracle;
import oracle_objects.PredicateFactory;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class OracleBuilder extends OraclesBaseVisitor<Predicate<State>>
{
    
    public List<GrammarOracle> parseOracleInstructions(OraclesParser.Oracles_fileContext ctx)
    {
        List<GrammarOracle> grammarOracles = new ArrayList<>();
        for(int i = 0; i < ctx.oracle().size(); i++)
        {
            grammarOracles.add(createOracle(ctx.oracle(i)));
        }
        return grammarOracles;
    }
    
    private GrammarOracle createOracle(OraclesParser.OracleContext ctx)
    {
        String name = stripOuterQuotes(String.valueOf(ctx.STRING()));
    
        GrammarOracle oracle = new GrammarOracle(name);
    
        List<Predicate<State>> groupBlocks = new ArrayList<>();
        for(int j = 0; j < ctx.group_block().size(); j++)
        { groupBlocks.set(j, visit(ctx.group_block(j))); }
        oracle.setGroupLogic(groupBlocks);
    
        oracle.setCheckLogic(visit(ctx.check_block()));
   
        for(OraclesParser.Trigger_blockContext triggerBlock : ctx.trigger_block())
        {
            if(triggerBlock.BOOL().getText().equals("TRUE"))
                oracle.setTriggerTrue(stripOuterQuotes(triggerBlock.STRING().getText()));
            if(triggerBlock.BOOL().getText().equals("FALSE"))
                oracle.setTriggerFalse(stripOuterQuotes(triggerBlock.STRING().getText()));
        }
        
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
        return PredicateFactory.negatePredicate(visitChildren(ctx));
    }
    
    @Override public Predicate<State> visitAndOprExpr(OraclesParser.AndOprExprContext ctx)
    {
        return PredicateFactory.andPredicates(visit(ctx.left), visit(ctx.right));
    }
    
    @Override public Predicate<State> visitOrOprExpr(OraclesParser.OrOprExprContext ctx)
    {
        return PredicateFactory.orPredicates(visit(ctx.left), visit(ctx.right));
    }
    
    @Override public Predicate<State> visitXorOprExpr(OraclesParser.XorOprExprContext ctx)
    {
        return PredicateFactory.xorPredicates(visit(ctx.left), visit(ctx.right));
    }
    
    @Override public Predicate<State> visitIsOprExpr(OraclesParser.IsOprExprContext ctx)
    {
        return PredicateFactory.equalPredicates(visit(ctx.left), visit(ctx.right));
    }
    
    @Override public Predicate<State> visitPlainBool(OraclesParser.PlainBoolContext ctx)
    {
        Predicate<State> boolPredicate;
        if(ctx.BOOL().getText().equalsIgnoreCase("true"))
            boolPredicate = state -> true;
        else
            boolPredicate = state -> false;
        return boolPredicate;
    }
    
    @Override public Predicate<State> visitPropKey(OraclesParser.PropKeyContext ctx)
    {
        Map<String,String> args = new HashMap<>();
        args.put("key", stripOuterQuotes(ctx.STRING().getText()));
        return PredicateFactory.createPredicate("key", args);
    }
    
    @Override public Predicate<State> visitPropValue(OraclesParser.PropValueContext ctx)
    {
        Map<String,String> args = new HashMap<>();
        args.put("value", stripOuterQuotes(ctx.STRING().getText()));
        return PredicateFactory.createPredicate("value", args);
    }
    
    @Override public Predicate<State> visitPropAny(OraclesParser.PropAnyContext ctx)
    {
        Map<String,String> args = new HashMap<>();
        args.put("any", stripOuterQuotes(ctx.STRING().getText()));
        return PredicateFactory.createPredicate("any", args);
    }
    
    @Override public Predicate<State> visitPropKeyValue(OraclesParser.PropKeyValueContext ctx)
    {
        Map<String,String> args = new HashMap<>();
        args.put("key", stripOuterQuotes(ctx.key.getText()));
        args.put("value", stripOuterQuotes(ctx.value.getText()));
        return PredicateFactory.createPredicate("pair", args);
    }
    
    @Override public Predicate<State> visitPropIsInList(OraclesParser.PropIsInListContext ctx)
    {
        Map<String,String> args = new HashMap<>();
        args.put("list", ctx.type.getText().toLowerCase()); //KEY, VALUE, or ANY
        
        int itemCounter = 0;
        for(TerminalNode item : ctx.list().STRING())
        {
            args.put(String.valueOf(itemCounter), stripOuterQuotes(item.getText()));
            itemCounter++;
        }
        
        return PredicateFactory.createPredicate("list", args);
    }
    
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
}