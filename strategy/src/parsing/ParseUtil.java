package parsing;

import antlrfour.StrategyLexer;
import antlrfour.StrategyParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategynodes.StrategyNode;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ParseUtil
{
    private StrategyNode StrategyTree;
    
    public ParseUtil(String filePath)
    {
        CharStream chars  = PathToCharstream(filePath);
        StrategyTree = BuildAST(chars);
    }
    
    private CharStream PathToCharstream(String filePath)
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
    
    private StrategyNode BuildAST(CharStream chars)
    {
        StrategyLexer     lexer  = new StrategyLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StrategyParser                      parser = new StrategyParser(tokens);
        StrategyParser.Strategy_fileContext tree   = parser.strategy_file();
        
        ASTBuilder   visitor = new ASTBuilder();
        return visitor.visitStrategy_file(tree); //create and return AST tree
    }
    
    public Action selectAction(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return StrategyTree.getResult(state, actions, actionsExecuted);
    }
    
    public void printStrategy()
    {
        System.out.println("Strategy:");
        System.out.println(StrategyTree.toString());
    }
}