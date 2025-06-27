package oracle_objects;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GrammarOracle
{
    private final boolean ignored;
    private final String oracleName;
    
    private List<Predicate<State>> givenLogic = new ArrayList<>();
    private List<Predicate<State>> groupLogic = new ArrayList<>();
    private boolean failEquals;
    private Predicate<State>       checkLogic;
    private String trigger = "";
    private String grammar;
    
    public GrammarOracle(String oracleName)
    {
        this.ignored = false;
        this.oracleName = oracleName;
    }
    public GrammarOracle(boolean ignored, String oracleName)
    {
        this.ignored = ignored;
        this.oracleName = oracleName;
    }
    
    public boolean isIgnored() { return ignored; }
    
    public void setGivenLogic(List<Predicate<State>> givenLogic)
    {
        this.givenLogic.addAll(givenLogic);
    }
    public void setGroupLogic(List<Predicate<State>> groupLogic)
    {
        this.groupLogic.addAll(groupLogic);
    }
    
    public void setCheckLogic(boolean failEquals, Predicate<State> checkLogic)
    {
        this.failEquals = failEquals; // set which outcome equals fail
        this.checkLogic = checkLogic;
    }
    
    public void setTrigger(String message)
    {
        this.trigger = message;
    }
    
    public void setGrammar(String grammar) {this.grammar = grammar;}
    
    public Verdict getVerdict(State state)
    {
        boolean result = checkLogic.test(state);
        if(failEquals == result) // if result is a fail value
            return new Verdict(Verdict.SEVERITY_GRAMMAR_DETECTED_ISSUE, buildMessage());
        else
            return new Verdict(Verdict.SEVERITY_OK, "");
    }
    
    private String buildMessage()
    {
        String preString = "Oracle " + oracleName;
        if(trigger.isBlank())
            return preString;
        else return preString + ": " + trigger;
    }
    
    @Override
    public String toString()
    {
        return grammar;
    }
}
