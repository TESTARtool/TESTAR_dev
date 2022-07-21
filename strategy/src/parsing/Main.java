package parsing;

import antlr4.StrategyLexer;
import antlr4.StrategyParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import strategy_nodes.StrategyNode;
import java.util.Set;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        CharStream chars  = CharStreams.fromFileName("strategy\\src\\parsing\\test_strategy.txt");
        StrategyLexer                       lexer  = new StrategyLexer(chars);
        CommonTokenStream                   tokens = new CommonTokenStream(lexer);
        StrategyParser                      parser = new StrategyParser(tokens);
        StrategyParser.Strategy_fileContext tree   = parser.strategy_file();
        
        ASTBuilder   visitor = new ASTBuilder();
        StrategyNode ast     = visitor.visitStrategy_file(tree);
        System.out.println(ast.toString());
    }
    
    private Action selectAction(State state, Set<Action> actions, StrategyNode ast)
    {
        return ast.GetResult(state, actions);
    }
}