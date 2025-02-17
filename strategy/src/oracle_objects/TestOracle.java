package oracle_objects;

import org.testar.monkey.alayer.State;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TestOracle
{
    private final int id;
    private final String oracleName;
    
    private List<Predicate<State>> selectLogic = new ArrayList<>();
    private Predicate<State>       checkLogic;
    private String                 TriggerTrue;
    private String                 TriggerFalse;
    
    public TestOracle(int id, String oracleName)
    {
        this.id = id;
        this.oracleName = oracleName;
    }
    
    public void setSelectLogic(List<Predicate<State>> selectLogic)
    {
        this.selectLogic.addAll(selectLogic);
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
}
