package parsing;

import antlr4.StrategyLexer;
import antlr4.StrategyParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.StrategyNode;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Parse
{
    private StrategyNode astTree;
    
    public Parse(String filePath)
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
        StrategyLexer                       lexer  = new StrategyLexer(chars);
        CommonTokenStream                   tokens = new CommonTokenStream(lexer);
        StrategyParser                      parser = new StrategyParser(tokens);
        StrategyParser.Strategy_fileContext tree   = parser.strategy_file();
        
        ASTBuilder   visitor = new ASTBuilder();
        astTree = visitor.visitStrategy_file(tree); //create AST tree
        
        System.out.println("Debug:");
        System.out.println(astTree.toString());
    }
    
    public Action selectAction(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {
        return astTree.GetResult(state, actions, actionsExecuted);
    }
}