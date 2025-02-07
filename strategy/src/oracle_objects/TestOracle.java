package oracle_objects;

import java.util.ArrayList;
import java.util.List;

public class TestOracle
{
    private final int id;
    private final String oracleName;
    
    private List<LogicBlock> selectLogic = new ArrayList<>();
    private LogicBlock checkLogic;
    private String TriggerTrue;
    private String TriggerFalse;
    
    public TestOracle(int id, String oracleName)
    {
        this.id = id;
        this.oracleName = oracleName;
    }
    
    public void setSelectLogic(LogicBlock... selectLogic)
    {
        this.selectLogic.addAll(List.of(selectLogic));
    }
    
    public void setCheckLogic(LogicBlock checkLogic)
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
