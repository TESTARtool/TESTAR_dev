package oracle_objects;

import antlrfour.oracles.OraclesLexer;
import antlrfour.oracles.OraclesParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import parsing.OracleBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class OracleManager
{
    private List<GrammarOracle> oracles;
    private final boolean debugOn = false;
    
    public OracleManager(String filePath)
    {
        this.oracles = new ArrayList<>();
        CharStream                          chars  = pathToCharstream(filePath);
        OraclesLexer                        lexer  = new OraclesLexer(chars);
        CommonTokenStream                   tokens = new CommonTokenStream(lexer);
        OraclesParser                       parser = new OraclesParser(tokens);
        OraclesParser.Oracles_fileContext   tree   = parser.oracles_file();
    
        if(debugOn)
            printParserInput(tokens, lexer);
    
        OracleBuilder visitor = new OracleBuilder();
    
        CharStream input = parser.getTokenStream().getTokenSource().getInputStream();
    
        List<GrammarOracle> grammarOracles = new ArrayList<>();
        for(int i = 0; i < tree.oracle().size(); i++)
        {
            String oracleSource = input.getText(Interval.of(tree.oracle(i).getStart().getStartIndex(),
                                                            tree.oracle(i).getStop().getStopIndex()));
            grammarOracles.add(visitor.buildOracle(tree.oracle(i), oracleSource));
        }
        oracles = grammarOracles;
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
    
    public Verdict runOracles(State state)
    {
        if(oracles.isEmpty())
            throw new ArrayIndexOutOfBoundsException("Error: there are no oracles to run.");

        Verdict jointVerdict = Verdict.OK;
        for(GrammarOracle oracle : oracles)
        {
            if(!oracle.isIgnored())
            {
                Verdict newVerdict = oracle.getVerdict(state);
                jointVerdict = jointVerdict.join(newVerdict);
            }
        }
        return jointVerdict;
    }
    
    private void printParserInput(CommonTokenStream tokens, OraclesLexer lexer) //for debugging purposes
    {
        tokens.fill();
        for (Token token : tokens.getTokens())
        {
            System.out.println("TOKEN: " + token.getText() + " -> " + lexer.getVocabulary().getSymbolicName(token.getType()));
        }
    }
    
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        for(GrammarOracle oracle : oracles)
            joiner.add(oracle.toString());
        return joiner.toString();
    }
}
