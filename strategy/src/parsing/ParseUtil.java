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

public class ParseUtil
{
    private StrategyNode primaryASTTree;
    private StrategyNode secondaryASTTree = null;
    
    public ParseUtil(String filePath)
    {
        CharStream chars  = PathToCharstream(filePath);
        primaryASTTree = BuildAST(chars);
        
        System.out.println("Debug:");
        System.out.println(primaryASTTree.toString());
    }
    
    public ParseUtil(String primaryFilePath, String secondaryFilePath)
    {
        CharStream chars1  = PathToCharstream(primaryFilePath);
        primaryASTTree = BuildAST(chars1);
        CharStream chars2  = PathToCharstream(secondaryFilePath);
        secondaryASTTree = BuildAST(chars2);
        
        System.out.println("Debug:");
        System.out.println(primaryASTTree.toString());
        System.out.println("---");
        System.out.println(secondaryASTTree.toString());
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
        StrategyLexer                       lexer  = new StrategyLexer(chars);
        CommonTokenStream                   tokens = new CommonTokenStream(lexer);
        StrategyParser                      parser = new StrategyParser(tokens);
        StrategyParser.Strategy_fileContext tree   = parser.strategy_file();
        
        ASTBuilder   visitor = new ASTBuilder();
        return visitor.visitStrategy_file(tree); //create and return AST tree
    }
    
    public Action selectAction(State state, Set<Action> actions, Map<String, Integer> actionsExecuted)
    {return selectAction(state, actions, actionsExecuted, false);}
    
    public Action selectAction(State state, Set<Action> actions, Map<String, Integer> actionsExecuted, boolean useSecondaryStrategy)
    {
        if(!useSecondaryStrategy || secondaryASTTree == null) //either the primary is preferred or there is no secondary strategy
            return primaryASTTree.GetResult(state, actions, actionsExecuted);
        else
            return secondaryASTTree.GetResult(state, actions, actionsExecuted);
    }
}