package nl.ou.testar.temporal.behavior;

import nl.ou.testar.temporal.structure.TemporalModel;
import nl.ou.testar.temporal.structure.TemporalOracle;
import nl.ou.testar.temporal.structure.TemporalOracleCollection;
import nl.ou.testar.temporal.util.JSONHandler;

public class TemporalChecker {
    private TemporalModel tm;
    private TemporalOracleCollection toc;
    private TemporalOracle to;

    public void loadTemporalproperties(String fromFile) {

        TemporalOracleCollection toc = (TemporalOracleCollection) JSONHandler.load("temporalOracles-" + fromFile, TemporalOracleCollection.class);
        TemporalModel tm = (TemporalModel) JSONHandler.load("temporalModel-" + fromFile, TemporalModel.class);

    }

    public void storeTemporalproperties(String outFile){
        JSONHandler.save(toc,"temporalOracles-"+outFile);
        JSONHandler.save(tm,"temporalModel-"+outFile);
    }
    private void startTemporalChecker(String temporalType, String automaton){

    }
    private void stopTemporalChecker(String temporalType){

    }
    private String makeAutomaton(String outputType,TemporalModel tmodel){

        return null;
    }
}
