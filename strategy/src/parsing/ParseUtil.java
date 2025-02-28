package parsing;

import antlrfour.strategy.StrategyLexer;
import antlrfour.strategy.StrategyParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.instructions.ListNode;

import java.io.IOException;
import java.util.Set;

public class ParseUtil
{
    private ListNode StrategyTree;
    
    public ParseUtil(String filePath)
    {
        CharStream chars  = pathToCharstream(filePath);
        StrategyTree = buildAST(chars);
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
    
    private ListNode buildAST(CharStream chars)
    {
        StrategyLexer     lexer  = new StrategyLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StrategyParser                      parser = new StrategyParser(tokens);
        StrategyParser.Strategy_fileContext tree   = parser.strategy_file();
        
        StrategyBuilder visitor = new StrategyBuilder();
        return visitor.visitStrategy_file(tree); //create and return AST tree
    }
    
    public Action selectAction(State state, Set<Action> actions)
    {
        System.out.println("DEBUG strategy:");
        printStrategy();
        return StrategyTree.getResult(state, actions);
    }
    
    public void printStrategy()
    {
        System.out.println("Strategy:");
        System.out.println(StrategyTree.toString());
    }
}