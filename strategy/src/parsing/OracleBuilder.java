package parsing;

import antlrfour.oracles.OraclesBaseVisitor;
import antlrfour.oracles.OraclesParser;
import antlrfour.strategy.StrategyLexer;
import oracle_objects.GrammarPredicate;
import oracle_objects.OracleManager;
import oracle_objects.PredicateFactory;
import oracle_objects.TestOracle;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testar.monkey.alayer.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OracleBuilder extends OraclesBaseVisitor<Object>
{
    private final OracleManager manager;
    private       int           oracleCounter = 1;

    public OracleBuilder(String filePath)
    {
        this.manager = new OracleManager();
        
        CharStream chars  = pathToCharstream(filePath);
        
        StrategyLexer                     lexer  = new StrategyLexer(chars);
        CommonTokenStream                 tokens = new CommonTokenStream(lexer);
        OraclesParser                     parser = new OraclesParser(tokens);
        OraclesParser.Oracles_fileContext tree   = parser.oracles_file();
    
        for(int i = 0; i < tree.oracle().size(); i++)
        {
            manager.addOracle((TestOracle) visit(tree.oracle(i)));
        }
    }
    
    private CharStream pathToCharstream(String filePath)
    {
        CharStream chars  = null;
        try
        {
            chars = CharStreams.fromFileName(filePath);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return chars;
    }
    
    
    @Override public TestOracle visitOracle(OraclesParser.OracleContext ctx)
    {
        oracleCounter += 1;
        String name = String.valueOf(ctx.oracle_name().BASE_STRING());
        
        TestOracle oracle = new TestOracle(oracleCounter, name);
    
//        Predicate<State>[] selectBlocks = new Predicate<State>[ctx.select_block().size()];
        List<Predicate<State>> selectBlocks = new ArrayList<>();
        for(int i = 0; i < ctx.select_block().size(); i++)
        { selectBlocks.set(i, (Predicate<State>) visit(ctx.select_block(i))); }
        oracle.setSelectLogic(selectBlocks);
        
        oracle.setCheckLogic((Predicate<State>) visit(ctx.check_block()));
        
        for(OraclesParser.Trigger_blockContext triggerBlock : ctx.trigger_block())
        {
            if(triggerBlock.BOOL().getText().equals("TRUE"))
                oracle.setTriggerTrue(triggerBlock.basic_string().getText());
            if(triggerBlock.BOOL().getText().equals("FALSE"))
                oracle.setTriggerFalse(triggerBlock.basic_string().getText());
        }
        
        return oracle;
    }
    
    @Override public GrammarPredicate visitSelect_block(OraclesParser.Select_blockContext ctx)
    {
        return (GrammarPredicate) visitChildren(ctx);
    }
    
    @Override public GrammarPredicate visitCheck_block(OraclesParser.Check_blockContext ctx)
    {
        return (GrammarPredicate) visitChildren(ctx);
    }
    
    @Override public Predicate<State> visitNotExpr(OraclesParser.NotExprContext ctx)
    {
        return PredicateFactory.negatePredicate((Predicate<State>) visitChildren(ctx));
    }
    
    @Override public Predicate<State> visitAndOprExpr(OraclesParser.AndOprExprContext ctx)
    {
        return PredicateFactory.andPredicates(
                (Predicate<State>) visit(ctx.left),
                (Predicate<State>) visit(ctx.right));
    }
    
//    @Override public Predicate<State> visitBoolOprExpr(OraclesParser.BoolOprExprContext ctx)
//    {
//        return
//    }
    
    @Override public Predicate<State> visitPlainBool(OraclesParser.PlainBoolContext ctx)
    {
        Predicate<State> boolPredicate;
        if(ctx.BOOL().getText().equalsIgnoreCase("true"))
            boolPredicate = state -> true;
        else
            boolPredicate = state -> false;
        return boolPredicate;
    }
    
    @Override public GrammarPredicate visitHasProperty(OraclesParser.HasPropertyContext ctx)
    {
        return PredicateFactory.createPredicate("HAS", ctx.property_string().BASE_STRING().getText(), null);
    }
    
    @Override public GrammarPredicate visitPropIs(OraclesParser.PropIsContext ctx)
    {
        return PredicateFactory.createPredicate("IS", ctx.property_string().BASE_STRING().getText(), null);
    }
}