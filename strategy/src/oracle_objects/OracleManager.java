package oracle_objects;

import antlrfour.oracles.OraclesLexer;
import antlrfour.oracles.OraclesParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.testar.monkey.alayer.State;
import parsing.OracleBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OracleManager
{
    private List<GrammarOracle> oracles;
    
    public OracleManager(String filePath)
    {
        this.oracles = new ArrayList<>();
        CharStream                          chars  = pathToCharstream(filePath);
        OraclesLexer                        lexer  = new OraclesLexer(chars);
        CommonTokenStream                   tokens = new CommonTokenStream(lexer);
        OraclesParser                       parser = new OraclesParser(tokens);
        OraclesParser.Oracles_fileContext   tree   = parser.oracles_file();
    
        tokens.fill();
        for (Token token : tokens.getTokens()) {
            System.out.println("TOKEN: " + token.getText() + " -> " + lexer.getVocabulary().getSymbolicName(token.getType()));
        }
    
        OracleBuilder visitor = new OracleBuilder();
        oracles = visitor.parseOracleInstructions(tree);
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
    
    public boolean runOracles(State state)
    {
        System.out.println("number of oracles in list: " + oracles.size());
        if(oracles.isEmpty())
            throw new ArrayIndexOutOfBoundsException("Error: there are no oracles to run.");
        
        List<Boolean> verdicts = new ArrayList<>();
        for(GrammarOracle oracle : oracles)
            verdicts.add(oracle.verdict(state));
    
//        for(boolean b : verdicts)
//            if(!b) return false; //if any verdict is false, return false
//        return true; //otherwise, return true
        
        return oracles.get(0).verdict(state);
    }
}
