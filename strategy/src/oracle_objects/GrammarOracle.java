package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GrammarOracle
{
    private final int id;
    private final String oracleName;
    
    private List<Predicate<State>> givenLogic = new ArrayList<>();
    private List<Predicate<State>> groupLogic = new ArrayList<>();
    private Predicate<State>       checkLogic;
    private String                 TriggerTrue;
    private String                 TriggerFalse;
    
    public GrammarOracle(int id, String oracleName)
    {
        this.id = id;
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
        this.TriggerTrue = message;
    }
    
    public void setTriggerFalse(String message)
    {
        this.TriggerFalse = message;
    }
    
    public boolean verdict(State state)
    {
        System.out.println("Predicate says " + checkLogic.test(state));
        return checkLogic.test(state);
    }
}
