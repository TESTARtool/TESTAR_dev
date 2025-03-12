package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GrammarOracle
{
    private final String oracleName;
    
    private List<Predicate<State>> givenLogic = new ArrayList<>();
    private List<Predicate<State>> groupLogic = new ArrayList<>();
    private Predicate<State>       checkLogic;
    private String                 triggerTrue;
    private String                 triggerFalse;
    
    public GrammarOracle(String oracleName)
    {
        this.oracleName = oracleName;
    }
    
    public void setGivenLogic(List<Predicate<State>> givenLogic)
    {
        this.givenLogic.addAll(givenLogic);
    }
    public void setGroupLogic(List<Predicate<State>> groupLogic)
    {
        this.groupLogic.addAll(groupLogic);
    }
    
    public void setCheckLogic(Predicate<State> checkLogic)
    {
        this.checkLogic = checkLogic;
    }
    
    public void setTriggerTrue(String message)
    {
        this.triggerTrue = message;
    }
    
    public void setTriggerFalse(String message)
    {
        this.triggerFalse = message;
    }
    
//    public Verdict getVerdict(State state)
//    {
//        if(checkLogic.test(state))
//            return new Verdict(Verdict.SEVERITY_MIN, triggerTrue); // OK
//        else
//            return new Verdict(Verdict.SEVERITY_WARNING, triggerFalse);
//    }
    
    public boolean getVerdict(State state)
    {
        return checkLogic.test(state);
    }
}
