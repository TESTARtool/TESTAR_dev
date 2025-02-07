package oracle_objects;

import java.util.ArrayList;

public class OracleManager
{
    private ArrayList<TestOracle> oracles;
    
    public OracleManager()
    {
        this.oracles = new ArrayList<>();
    }
    
    public void addOracle(TestOracle oracle)
    {
        this.oracles.add(oracle);
    }
}
