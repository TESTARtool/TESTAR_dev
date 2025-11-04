package org.testar.oracles;

public abstract class DslOracle implements Oracle, OracleWidgetsMapping {

    private boolean vacuousPass = true;

    public boolean isVacuousPass() { 
        return vacuousPass;
    }

    @Override
    public void initialize() {}

    public void markAsNonVacuous() {
        vacuousPass = false;
    }

}
