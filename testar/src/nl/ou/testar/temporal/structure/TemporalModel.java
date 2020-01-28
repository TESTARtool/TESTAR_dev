package nl.ou.testar.temporal.structure;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.HashBiMap;
import nl.ou.testar.temporal.util.ValStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.types.selectors.SelectSelector;

import java.util.*;

//@JsonRootName(value="TemporalProperties")
public class TemporalModel extends TemporalBean{

    private List<StateEncoding> stateEncodings; //Integer:  to concretstateID
    private List<String> InitialStates;
    private List<TemporalTrace> traces; //
    private List<String> stateList;



    private List<String> transitionList;
    private Set<String> modelAPs; //AP<digits> to widget property map:
    private String formatVersion="20200104";
    private static String APPrefix = "ap";
    private  String APSeparator;




    public  TemporalModel(){
    super(); // needed ?
    this.stateEncodings = new ArrayList<>();
    this.stateList = new ArrayList<>();
    this.transitionList = new ArrayList<>();
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

    public List<String> getStateList() {
        return stateList;
    }

    public void setStateList(List<String> stateList) {
        this.stateList = stateList;
    }
    public List<String> getTransitionList() {
        return transitionList;
    }

    public void setTransitionList(List<String> transitionList) {
        this.transitionList = transitionList;
    }

    public  String getAPSeparator() {
        return APSeparator;
    }

    public void setAPSeparator(String APSeperator) {
        APSeparator = APSeperator;
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
        stateList.clear();
        for (StateEncoding stateEnc: stateEncodings) {
            this.modelAPs.addAll(stateEnc.getStateAPs());
            this.modelAPs.addAll(stateEnc.retrieveAllTransitionAPs());
            stateList.add(stateEnc.getState());
            for (TransitionEncoding trenc:stateEnc.getTransitionColl()
                 ) {
                transitionList.add(trenc.getTransition());
            }

        }

        finalizeTransitions();
    }


    public String get_formatVersion() {        return formatVersion;   }

    public void set_formatVersion(String _formatVersion) {    this.formatVersion = _formatVersion;    }


    //custom
    public void addStateEncoding(StateEncoding stateEncoding, boolean updateTransitionsImmediate) {

        stateEncodings.add(stateEncoding);
        this.modelAPs.addAll(stateEncoding.getStateAPs());
        this.modelAPs.addAll(stateEncoding.retrieveAllTransitionAPs());
        stateList.add(stateEncoding.getState());
        for (TransitionEncoding trenc:stateEncoding.getTransitionColl()
        ) {
            transitionList.add(trenc.getTransition());
        }

        if (updateTransitionsImmediate) {
            finalizeTransitions();        }
    }
    public void finalizeTransitions() {
        for (StateEncoding stateEnc : stateEncodings) {// observer pattern?
            stateEnc.updateAPConjuncts(modelAPs);
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
        Set<String> initialStatesSet = new HashSet<>(InitialStates);
        int stateindex=-1;
        for (String initialState : initialStatesSet
        ) {
            stateindex = stateList.indexOf(initialState);
            assert stateindex==-1:"initial state not in statelist";
            result.append("Start: ").append(stateindex).append("\n");
        }
        result.append("Acceptance: 1 Inf(0)\n");  //==Buchi
        result.append("AP: ");
        result.append(modelAPs.size());
        int i=0;
        for (String ap: modelAPs) {
            result.append(" \"").append(APPrefix);
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
                result.append(trans.getEncodedTransitionAPConjunct());
                result.append("]");
                String targetState = trans.getTargetState();
                int targetStateindex =stateList.indexOf(targetState);
                assert targetStateindex==-1:"target  state not in statelist";
                result.append(" ").append(targetStateindex);
                result.append(" {0}\n");  //all are in the same buchi acceptance set
            }
            s++;
        }
        result.append("--END--\n");
        result.append("EOF_HOA");
        return result.toString();
    }

    public String makeETFOutput(){
        //see https://ltsmin.utwente.nl/assets/man/etf.html
        StringBuilder result=new StringBuilder();
        result.append("%tool: \"TESTAR-CSS20200126\"\n");
        result.append("%name: \"" + "app= ").
                append(this.getApplicationName()).
                append(", ver=").
                append(this.getApplicationVersion()).
                append(", modelid= ").
                append(this.getApplication_ModelIdentifier()).
                append(", abstraction= ").
                append(this.getApplication_AbstractionAttributes()).
                append("\"\n");
        result.append("%modified: ").append(get_modifieddate()).append("\n");
        result.append("%\n");
        result.append("begin state\n");
        int chunk=25;
        int i=0;
        for (String ap: modelAPs) {
            result.append(APPrefix).append(i).append(":bool ");
            if (i>0 && (i%chunk)==0){
                result.append("\n");
            }
            i++;
        }
        result.append("\n");
        result.append("end state\n");
        result.append("begin edge\n");
        result.append("transition:transition\n");
        result.append("end edge\n");
        result.append("begin init\n");
        Set<String> initialStatesSet = new HashSet<>(InitialStates);


        for (String initstate : initialStatesSet
        ) {

            for (StateEncoding stenc : stateEncodings
            ) {
                if (stenc.getState().equals(initstate)) {
                    String[] stateaps = stenc.getEncodedStateAPConjunct().split("&");
                    for (String ap : stateaps
                    ) {
                        if (ap.startsWith("!")) {
                            result.append("0 ");
                        } else {
                            result.append("1 ");
                        }
                    }
                    result.append("\n");
                }
            }
        }
        result.append("end init\n");

        result.append("begin trans\n");
        for (StateEncoding stenc : stateEncodings
        ) {
            String[] stateaps = stenc.getEncodedStateAPConjunct().split("&");

            int transindex = 0;
            for (TransitionEncoding trenc : stenc.getTransitionColl()
            ) {
                String targetstate = trenc.getTargetState();
                StateEncoding targetenc=null;
                for (StateEncoding stenc1 : stateEncodings
                ) {
                    if (targetstate.equals(stenc1.getState())) {
                        targetenc = stenc1;
                        break;
                    }
                }
                String[] targetaps = targetenc.getEncodedStateAPConjunct().split("&");
                int idex = 0;
                for (String ap : stateaps
                ) {
                    if (ap.startsWith("!")) {
                        result.append("0/");
                    } else {
                        result.append("1/");
                    }
                    if (targetaps[idex].startsWith("!")) {
                        result.append("0 ");
                    } else {
                        result.append("1 ");
                    }
                    idex++;
                }
                result.append(" " + transindex).append("\n");
                transindex++;
            }

        }
        result.append("end trans\n");
        result.append("begin sort transition\n");

        for (String transid: transitionList) {
            result.append("\"").append(transid).append("\"\n");
         }
         result.append("end sort\n");
        result.append("begin sort bool\n");
        result.append("\"false\"\n");
        result.append("\"true\"\n");
        result.append("end sort\n");
        return result.toString();
    }





    public String validateAndMakeFormulas(List<TemporalOracle> oracleColl) {

        StringBuilder Formulas=new StringBuilder();
        for (TemporalOracle candidateOracle : oracleColl) {
            String formula;
            List<String> sortedparameters = candidateOracle.getPatternBase().getPattern_Parameters();
            Collections.sort(sortedparameters);
            List<String> sortedsubstitionvalues =  new ArrayList<>(candidateOracle.getSortedPattern_Substitutions().values());
            sortedsubstitionvalues.removeAll(Arrays.asList(""));  // discard empty substitutions


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
            if (!importStatus) {
                formula= candidateOracle.getPatternBase().getPattern_Formula();
                // will certainly fail during model-check, because parameters are not prefixed with 'ap'
            }else{
                formula= StringUtils.replaceEach(candidateOracle.getPatternBase().getPattern_Formula(),
                        sortedparameters.toArray(new String[0]),
                        apindex.toArray(new String[0]));
            }

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