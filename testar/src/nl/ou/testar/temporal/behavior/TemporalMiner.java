package nl.ou.testar.temporal.behavior;

import nl.ou.testar.temporal.scratch.ReverseTransitionKey;
import nl.ou.testar.temporal.structure.TemporalModel;
import nl.ou.testar.temporal.scratch.TemporalOracle;
import nl.ou.testar.temporal.structure.TemporalOracleCollection;
import nl.ou.testar.temporal.util.JSONHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemporalMiner {

    private List<String> stateMap; //Integer:  to concretstateID
    private Map<ReverseTransitionKey, String> reverseEdgeMap;
    private LinkedHashMap<String, String> atomicPropositionsOfModelMap; //AP<digits> to widget property map:

    private TemporalModel tm;
    private TemporalOracleCollection toc;
    private TemporalOracle to;
    private String applicationName ;
    private String applicationVersion;
    private String modelIdentifier ;
    private Set abstractionAttributes;

    private List<String> nodeAP;
    private Map<String,List<String>> stateAPs;
    private Map<String,List<String>> actionAPs;

    public TemporalMiner() {
    }
    public void connectTestarModel(){

    }


    public void storeTemporalOracles(String outFile){
        JSONHandler.save(toc,"temporalOracles-"+outFile);
        JSONHandler.save(tm,"temporalModel-"+outFile);
    }
    private List<String> retrieveAPOfNode(String vertex){
        return null;
    }
    private List<String> retrieveAPOfEdge(String edge){
        // widget is also in the state ap, BUT NOW THE ACTION-ap IS THERE
        return null;
    }
}
