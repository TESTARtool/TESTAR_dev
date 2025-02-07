package parsing;

import antlrfour.oracles.OraclesBaseVisitor;
import antlrfour.oracles.OraclesParser;
import antlrfour.strategy.StrategyLexer;
import oracle_objects.LogicBlock;
import oracle_objects.OracleManager;
import oracle_objects.TestOracle;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

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
    
        LogicBlock[] selectBlocks = new LogicBlock[ctx.select_block().size()];
        for(int i = 0; i < ctx.select_block().size(); i++)
        { selectBlocks[i] = (LogicBlock) visit(ctx.select_block(i)); }
        oracle.setSelectLogic(selectBlocks);
    
        LogicBlock checkBlock = (LogicBlock) visit(ctx.check_block());
        oracle.setCheckLogic(checkBlock);
        
        for(OraclesParser.Trigger_blockContext triggerBlock : ctx.trigger_block())
        {
            if(triggerBlock.BOOL().getText().equals("TRUE"))
                oracle.setTriggerTrue(triggerBlock.basic_string());
            if(triggerBlock.BOOL().getText().equals("FALSE"))
                oracle.setTriggerFalse(triggerBlock.basic_string());
        }
        
        return oracle;
    }
}
