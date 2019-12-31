package nl.ou.testar.temporal.structure;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.HashBiMap;
import nl.ou.testar.temporal.util.ValStatus;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

//@JsonRootName(value="TemporalProperties")
public class TemporalModel extends TemporalBean{

    private List<StateEncoding> stateEncodings; //Integer:  to concretstateID



    private  List<String> InitialStates;
    private List<TemporalTrace> traces; //

    @JsonIgnore
    private Set<String> modelAPs; //AP<digits> to widget property map:
    private String formatVersion="20190721";
    private static String APPrefix = "ap";


public  TemporalModel(){
    super(); // needed ?
    this.stateEncodings = new ArrayList<>();
    this.modelAPs = new LinkedHashSet<>();  //must maintain order
}


    public List<String> getInitialStates() {
        return InitialStates;
    }

    public void setInitialStates(List<String> initialStates) {
        InitialStates = initialStates;
    }

    public Set<String> getModelAPs() {
        return modelAPs;
    }

    public void setModelAPs(Set<String> modelAPs) {
        this.modelAPs = modelAPs;
    }

    public List<TemporalTrace> getTraces() {
        return traces;
    }

    public void setTraces(List<TemporalTrace> stateTransistionSequence) {
        this.traces = stateTransistionSequence;
    }

    public List<StateEncoding> getStateEncodings() {
        return stateEncodings;
    }

    public void setStateEncodings(List<StateEncoding> stateEncodings) {

        this.stateEncodings = stateEncodings;
        for (StateEncoding stateEnc: stateEncodings) {
            this.modelAPs.addAll(stateEnc.getStateAPs());
            this.modelAPs.addAll(stateEnc.retrieveAllTransitionAPs());
        }
        updateTransitions();
    }


    public String get_formatVersion() {        return formatVersion;   }

    public void set_formatVersion(String _formatVersion) {    this.formatVersion = _formatVersion;    }


    //custom
    public void addStateEncoding(StateEncoding stateEncoding, boolean updateTransitionsImmediate) {

        stateEncodings.add(stateEncoding);
        this.modelAPs.addAll(stateEncoding.getStateAPs());
        this.modelAPs.addAll(stateEncoding.retrieveAllTransitionAPs());


        if (updateTransitionsImmediate) {updateTransitions();        }
    }
    public void updateTransitions() {
        for (StateEncoding stateEnc : stateEncodings) {// observer pattern?
            stateEnc.updateAllTransitionConjuncts(modelAPs);
        }

    }




    public String makeHOAOutput(){
        //see http://adl.github.io/hoaf/
        StringBuilder result=new StringBuilder();
        result.append("HOA: v1\n");
        result.append("tool: \"TESTAR-CSS20190914\"\n");
        result.append("name: \""+ "app= "+this.getApplicationName()+", ver="+this.getApplicationVersion()+", modelid= "+this.getApplication_ModelIdentifier()+", abstraction= "+this.getApplication_AbstractionAttributes()+"\"\n");
        result.append("States: ");
        result.append(stateEncodings.size());
        result.append("\n");
        String initState = InitialStates.get(0);
        int stateindex =0;
        for (StateEncoding se: stateEncodings )
        {if (se.getState().equals(initState)) break;
            stateindex++;
        }
        result.append("Start: "+stateindex+"\n");
        result.append("Acceptance: 1 Inf(0)\n");  //==Buchi
        result.append("AP: ");
        result.append(modelAPs.size());
        int i=0;
        for (String ap: modelAPs) {
            result.append(" \""+APPrefix);
            result.append(i);
            result.append("\"");
            i++;
        }
        result.append("\n");
        result.append("--BODY--\n");


        int s=0;
        for (StateEncoding stateenc: stateEncodings) {
            result.append("State: ");
            result.append(s);
            result.append("\n");
            for (TransitionEncoding trans:stateenc.getTransitionColl()  ) {
                result.append("[");
                result.append(trans.getEncodedAPConjunct());
                result.append("]");
                String targetState = trans.getTargetState();
                int targetStateindex =0;
                for (StateEncoding se: stateEncodings )
                {if (se.getState().equals(targetState)) break;
                    targetStateindex++;
                }

                result.append(" "+targetStateindex);
                result.append(" {0}\n");  //all are in the same buchi acceptance set
            }
            s++;
        }
        result.append("--END--\n");
        result.append("EOF_HOA");
        return result.toString();
    }

    public String makeFormulaOutput(List<TemporalOracle> oracleColl) {

        StringBuilder Formulas=new StringBuilder();
        for (TemporalOracle candidateOracle : oracleColl) {
            String formula;
            List<String> sortedparameters = candidateOracle.getPattern_Parameters();
            Collections.sort(sortedparameters);
            List<String> sortedsubstitionvalues =  new ArrayList<>(candidateOracle.getSortedPattern_Substitutions().values());

            boolean importStatus;
                importStatus = sortedparameters.size()==sortedsubstitionvalues.size();
            if (!importStatus) {
                candidateOracle.addLog("inconsistent number of parameter <-> substitutions");
                candidateOracle.setOracle_validationstatus(ValStatus.ERROR);
            }
            if (importStatus)
                importStatus = getModelAPs().containsAll(sortedsubstitionvalues);

            if (!importStatus) {
                candidateOracle.addLog("not all propositions (parameter-substitutions) are found in the Model:");
                for (String subst:sortedsubstitionvalues
                     ) {
                    if (!getModelAPs().contains(subst))  candidateOracle.addLog("not found: "+ subst);
                }
                candidateOracle.setOracle_validationstatus(ValStatus.ERROR);
            }
            HashBiMap<Integer, String> aplookup = HashBiMap.create();
            aplookup.putAll(getSimpleModelMap());
            ArrayList<String> apindex=new ArrayList<>();

            for (String v:sortedsubstitionvalues
                 ) {
                if(aplookup.inverse().containsKey(v)){
                    apindex.add(APPrefix+aplookup.inverse().get(v));
                }else
                    apindex.add(APPrefix+"_indexNotFound");

            }

            formula= StringUtils.replaceEach(candidateOracle.getPattern_Formula(),
                    sortedparameters.stream().toArray(String[]::new),
                    apindex.stream().toArray(String[]::new));

            Formulas.append(formula);
            Formulas.append("\n");
        }

        return Formulas.toString();

    }
    public String getAliveProposition(String alive){
        HashBiMap<Integer, String> aplookup = HashBiMap.create();
        aplookup.putAll(getSimpleModelMap());
        String encodedAliveAP = "";
        // we encode alive as not dead "!dead"
        // so we strip the negation from the alive property, by default: "!dead"
        if (alive.startsWith("!") && aplookup.inverse().containsKey(alive.toLowerCase().substring(1))){
            encodedAliveAP= "!"+APPrefix+aplookup.inverse().get(alive.toLowerCase().substring(1));
        }
        if (!alive.startsWith("!") && aplookup.inverse().containsKey(alive.toLowerCase())){
            encodedAliveAP= APPrefix+aplookup.inverse().get(alive.toLowerCase());
        }
         return  encodedAliveAP;
    };


    public List<TemporalOracle> ReadModelCheckerResults(String results){
        List<TemporalOracle> oracleresults = new ArrayList<>();
        TemporalOracle oracleresult=new TemporalOracle();

// add to log file.
        // parse header block
        //parseformulablock
       // while (formulablock){ }



    return oracleresults ;
    }

    @JsonGetter("modelAPs")
    private LinkedHashMap<Integer,String> getSimpleModelMap(){
        LinkedHashMap<Integer,String> map = new LinkedHashMap<>();

        int i=0;
        for (String ap:modelAPs         ) {
                map.put(i, ap);
                i++;
        }
        return map;
    }
    @JsonSetter("modelAPs")
    private void setFromSimpleModelMap(LinkedHashMap<Integer,String> map){
        this.modelAPs.addAll(map.values());
    }
}