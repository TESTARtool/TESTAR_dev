package nl.ou.testar.temporal.structure;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.HashBiMap;
import nl.ou.testar.temporal.util.TemporalSubType;
import nl.ou.testar.temporal.util.ValStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.types.selectors.SelectSelector;

import java.util.*;

//@JsonRootName(value="TemporalProperties")
public class TemporalModel extends TemporalBean {

    private List<StateEncoding> stateEncodings; //Integer:  to concretstateID
    private List<String> InitialStates;
    private List<TemporalTrace> traces; //
    private List<String> stateList;


    private List<String> transitionList;
    private Set<String> modelAPs; //AP<digits> to widget property map:
    private String formatVersion = "20200104";
    private static String APPrefix = "ap";


    private static String deadProposition = "dead";


    private String APSeparator;


    public TemporalModel() {
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

    public String getAPSeparator() {
        return APSeparator;
    }

    public void setAPSeparator(String APSeperator) {
        APSeparator = APSeperator;
    }

    public static String getDeadProposition() {
        return deadProposition;
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
        for (StateEncoding stateEnc : stateEncodings) {
            this.modelAPs.addAll(stateEnc.getStateAPs());
            this.modelAPs.addAll(stateEnc.retrieveAllTransitionAPs());
            stateList.add(stateEnc.getState());
            for (TransitionEncoding trenc : stateEnc.getTransitionColl()
            ) {
                transitionList.add(trenc.getTransition());
            }
        }
        finalizeTransitions();
    }


    public String get_formatVersion() {
        return formatVersion;
    }

    public void set_formatVersion(String _formatVersion) {
        this.formatVersion = _formatVersion;
    }


    //custom
    public void addStateEncoding(StateEncoding stateEncoding, boolean updateTransitionsImmediate) {

        stateEncodings.add(stateEncoding);
        this.modelAPs.addAll(stateEncoding.getStateAPs());
        this.modelAPs.addAll(stateEncoding.retrieveAllTransitionAPs());
        stateList.add(stateEncoding.getState());
        for (TransitionEncoding trenc : stateEncoding.getTransitionColl()
        ) {
            transitionList.add(trenc.getTransition());
        }

        if (updateTransitionsImmediate) {
            finalizeTransitions();
        }
    }

    public void finalizeTransitions() {
        for (StateEncoding stateEnc : stateEncodings) {// observer pattern?
            stateEnc.updateAPConjuncts(modelAPs);
        }

    }


    public String makeHOAOutput() {
        //see http://adl.github.io/hoaf/
        StringBuilder result = new StringBuilder();
        result.append("HOA: v1\n");
        result.append("tool: \"TESTAR-CSS20190914\"\n");
        result.append("name: \"" + "app= " + this.getApplicationName() + ", ver=" + this.getApplicationVersion() + ", modelid= " + this.getApplication_ModelIdentifier() + ", abstraction= " + this.getApplication_AbstractionAttributes() + "\"\n");
        result.append("States: ");
        result.append(stateEncodings.size());
        result.append("\n");
        Set<String> initialStatesSet = new HashSet<>(InitialStates);
        int stateindex = -1;
        for (String initialState : initialStatesSet
        ) {
            stateindex = stateList.indexOf(initialState);
            assert stateindex == -1 : "initial state not in statelist";
            result.append("Start: ").append(stateindex).append("\n");
        }
        result.append("Acceptance: 1 Inf(0)\n");  //==Buchi
        result.append("AP: ");
        result.append(modelAPs.size());
        int i = 0;
        for (String ap : modelAPs) {
            result.append(" \"").append(APPrefix);
            result.append(i);
            result.append("\"");
            i++;
        }
        result.append("\n");
        result.append("--BODY--\n");


        int s = 0;
        for (StateEncoding stateenc : stateEncodings) {
            result.append("State: ");
            result.append(s);
            result.append("\n");
            for (TransitionEncoding trans : stateenc.getTransitionColl()) {
                result.append("[");
                result.append(trans.getEncodedTransitionAPConjunct());
                result.append("]");
                String targetState = trans.getTargetState();
                int targetStateindex = stateList.indexOf(targetState);
                assert targetStateindex == -1 : "target  state not in statelist";
                result.append(" ").append(targetStateindex);
                result.append(" {0}\n");  //all are in the same buchi acceptance set
            }
            s++;
        }
        result.append("--END--\n");
        result.append("EOF_HOA");
        return result.toString();
    }

    public String makeETFOutput() {
        //see https://ltsmin.utwente.nl/assets/man/etf.html
        StringBuilder result = new StringBuilder();
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
        int chunk = 25;
        int i = 0;
        //   result.append("stateid:stateid\n");
        for (String ap : modelAPs) {
            result.append(APPrefix).append(i).append(":bool ");
            if (i > 0 && (i % chunk) == 0) {
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
            int stateid = 0;
            for (StateEncoding stenc : stateEncodings
            ) {
                if (stenc.getState().equals(initstate)) {
                    //           result.append("" + stateid + " ");
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
                stateid++;
            }
        }
        result.append("end init\n");

        result.append("begin trans\n");
        int stateid = 0;
        for (StateEncoding stenc : stateEncodings
        ) {
            String[] stateaps = stenc.getEncodedStateAPConjunct().split("&");

            int transindex = 0;
            for (TransitionEncoding trenc : stenc.getTransitionColl()
            ) {
                //       result.append("" + stateid + " ");
                String targetstate = trenc.getTargetState();
                StateEncoding targetenc = null;
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
//                    String step="";
//                    if (ap.startsWith("!")) {
//                        step=("0/");
//                    } else {
//                        step=("1/");
//                    }
//                    if (targetaps[idex].startsWith("!")) {
//                        step=step+("0 ");
//                    } else {
//                        step=step+("1 ");
//                    }
//                    if (!step.equals("0/0 ") && !step.equals("1/1 ")){
//                        result.append(step);
//                    }
//                    else
//                        result.append("* ");

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
            stateid++;
        }
        result.append("end trans\n");
        result.append("begin sort transition\n");

        for (String transid : transitionList) {
            result.append("\"").append(transid).append("\"\n");
        }

        result.append("end sort\n");
//        result.append("begin sort stateid\n");
//        for (StateEncoding stenc : stateEncodings) {
//            result.append("\"").append(stenc.getState()).append("\"\n");
//        }
//        result.append("end sort\n");
        result.append("begin sort bool\n");
        result.append("\"0\"\n"); //"false" an d"true are common alternatives
        result.append("\"1\"\n");
        result.append("end sort\n");
        return result.toString();
    }

    public String makeGALOutput() {
        //see https://lip6.github.io/ITSTools-web/galmm.html
        StringBuilder result = new StringBuilder();
        result.append("//tool: \"TESTAR-CSS20200126\"\n");
        result.append("//name: \"" + "app= ").
                append(this.getApplicationName()).
                append(", ver=").
                append(this.getApplicationVersion()).
                append(", modelid= ").
                append(this.getApplication_ModelIdentifier()).
                append(", abstraction= ").
                append(this.getApplication_AbstractionAttributes()).
                append("\"\n");
        result.append("//modified: ").append(get_modifieddate()).append("\n");
        result.append("//\n");

        result.append("gal TESTAR {\n");
        int chunk = 25;
        int i = 0;
        result.append("int ");
        String artifical_StartState=""+ (int)Math.pow(2,20); //assume max 1 million states
        result.append("stateindex = "+artifical_StartState +" ;\n");

        for (String ap : modelAPs) {
            result.append("int ");
            result.append(APPrefix).append(i).append( " = 0 ; ");
            if (i > 0 && (i % chunk) == 0) {
                result.append("\n");
            }
            i++;
        }
        result.append("\n");
        //result.append("begin init\n");
        Set<String> initialStatesSet = new HashSet<>(InitialStates);

        result.append("// BEGIN artificial initial states\n");
        for (String initstate : initialStatesSet
        ) {
            for (StateEncoding stenc : stateEncodings
            ) {
                if (stenc.getState().equals(initstate)) {

                    String artificalEdge = "artificial_init_transition_to_" + stateList.indexOf(stenc.getState());
                    result.append("    transition ").append(artificalEdge).
                            append(" [ stateindex == ").append(artifical_StartState).append(" ] label \"").
                            append(artificalEdge).append("\" {\n");
                    result.append("        stateindex = ").
                            append(stateList.indexOf(stenc.getState())).append(" ;\n");

                    String[] stateaps = stenc.getEncodedStateAPConjunct().split("&");
                    int j = 0;
                    int chunk1 = 25;
                    result.append("        ");
                    for (String ap : stateaps
                    ) {
                        if (ap.startsWith("!")) {
                            result.append(APPrefix).append(j).append(" = 0").append(" ; ");
                        } else {
                            result.append(APPrefix).append(j).append(" = 1").append(" ; ");
                        }
                        if (j > 0 && (j % chunk1) == 0) {
                            result.append("\n");
                            result.append("        ");
                        }
                        j++;

                    }
                    result.append("    }\n");
                }
            }
        }
        result.append("// END artificial initial states\n");


        result.append("//BEGIN explicit transitions\n");
        int stateid = 0;
        for (StateEncoding stenc : stateEncodings
        ) {
            String[] stateaps = stenc.getEncodedStateAPConjunct().split("&");

            int transindex = 0;
            for (TransitionEncoding trenc : stenc.getTransitionColl()
            ) {
                //       result.append("" + stateid + " ");
                String targetstate = trenc.getTargetState();
                StateEncoding targetenc = null;
                for (StateEncoding stenc1 : stateEncodings
                ) {
                    if (targetstate.equals(stenc1.getState())) {
                        targetenc = stenc1;
                        break;
                    }
                }
                String artificalEdge = "_F"+trenc.getTransition().replace("#","_").replace(":","_");
                StringBuilder condition=new StringBuilder();
                StringBuilder assignment=new StringBuilder();

                condition.append("[ stateindex == "+stateList.indexOf(stenc.getState())+ " ");


                String[] targetaps = targetenc.getEncodedStateAPConjunct().split("&");
                int idex = 0;
                int chunk2 = 25;
                assignment.append("        stateindex = ").append(stateList.indexOf(targetenc.getState())).append(" ;\n");
                assignment .append("        ");

                for (String ap : stateaps
                ) {
                    if (ap.startsWith("!")) {
                  //      condition.append(" &&  ").append(APPrefix).append(idex).append(" == 0").append(" ");
                    } else {
                        condition.append(" &&  ").append(APPrefix).append(idex).append(" == 1").append(" ");
                    }
                    if (idex> 0 && (idex % chunk2) == 0) {
                        condition.append("\n");
                        condition.append("        ");
                    }


                    if (targetaps[idex].startsWith("!")) {
                        assignment.append(APPrefix).append(idex).append(" = 0").append(" ; ");
                    } else {
                        assignment.append(APPrefix).append(idex).append(" = 1").append(" ; ");
                    }
                    if (idex> 0 && (idex % chunk2) == 0) {
                        assignment.append("\n");
                        assignment.append("        ");
                    }

                    idex++;
                }
                condition.append(" ]\n");
               // result.append("    transition ").append(artificalEdge).append(" ").append(condition.toString()).append( "label "+"\""+trenc.getTransition()+"\""+" {\n");
                result.append("    transition ").append(artificalEdge).append(" ").append(condition.toString()).append( " {\n");

                result.append(assignment.toString());
                result.append("    }\n");


                //result.append(" " + transindex).append("\n");
                transindex++;
            }
            stateid++;
        }

        result.append("}\n\n");
        result.append("main TESTAR;\n");
        return result.toString();
    }


    public String validateAndMakeFormulas(List<TemporalOracle> oracleColl, boolean doTransformation) {

        StringBuilder Formulas = new StringBuilder();
        for (TemporalOracle candidateOracle : oracleColl) {
            String formula;
            List<String> sortedparameters = new ArrayList<>(candidateOracle.getPatternBase().getPattern_Parameters());//clone list
            Collections.sort(sortedparameters);
            List<String> sortedsubstitionvalues = new ArrayList<>(candidateOracle.getSortedPattern_Substitutions().values());
            sortedsubstitionvalues.removeAll(Collections.singletonList(""));  // discard empty substitutions
            TemporalSubType tst = TemporalSubType.valueOf(candidateOracle.getPatternTemporalType().name());

            boolean importStatus;
            importStatus = sortedparameters.size() == sortedsubstitionvalues.size();
            if (!importStatus) {
                candidateOracle.addLog("inconsistent number of parameter <-> substitutions");
            }
            if (importStatus)
                importStatus = getModelAPs().containsAll(sortedsubstitionvalues);

            if (!importStatus) {
                candidateOracle.addLog("not all propositions (parameter-substitutions) are found in the Model:");
                for (String subst : sortedsubstitionvalues
                ) {
                    if (!getModelAPs().contains(subst)) candidateOracle.addLog("not found: " + subst);
                }
            }
            if (!importStatus) {
                String falseFormula="false";
                candidateOracle.addLog("setting formula to 'false'");
                String  formulalvl6= StringUtils.replace(falseFormula,tst.false_replace.getLeft(), tst.false_replace.getRight()) + tst.line_append;;
                Formulas.append(formulalvl6);
                candidateOracle.setOracle_validationstatus(ValStatus.ERROR);
            } else {

                HashBiMap<Integer, String> aplookup = HashBiMap.create();
                aplookup.putAll(getSimpleModelMap());
                ArrayList<String> apindex = new ArrayList<>();
                if (doTransformation) {

                    String deadprop = getPropositionIndex(deadProposition, true);
                    if (!deadprop.equals("")) { // model has 'dead' as an atomic  property
                        sortedsubstitionvalues.add(deadProposition);
                        sortedparameters.add(deadProposition); // consider 'dead' as a kind of parameter
                    }

                    for (String v : sortedsubstitionvalues
                    ) {
                        if (aplookup.inverse().containsKey(v)) {
                            apindex.add(APPrefix + aplookup.inverse().get(v));
                        } else
                            apindex.add(APPrefix + "_indexNotFound");
                        // will certainly fail if during model-check, because parameters are not prefixed with 'ap'

                    }

                    {
                        String rawFormula = candidateOracle.getPatternBase().getPattern_Formula();
                        String formulalvl1 = rawFormula + tst.line_append;
                        String formulalvl2 = StringUtils.replace(formulalvl1,
                                tst.finally_replace.getLeft(), tst.finally_replace.getRight());
                        String formulalvl3 = StringUtils.replace(formulalvl2,
                                tst.globally_replace.getLeft(), tst.globally_replace.getRight());
                        String formulalvl4 = StringUtils.replace(formulalvl3,
                                tst.and_replace.getLeft(), tst.and_replace.getRight());
                        String formulalvl5 = StringUtils.replace(formulalvl4,
                                tst.or_replace.getLeft(), tst.or_replace.getRight());
                        String formulalvl6 = StringUtils.replace(formulalvl5,
                                tst.false_replace.getLeft(), tst.false_replace.getRight());

                        apindex.replaceAll(s -> tst.ap_prepend + s + tst.ap_append);

                        formula = StringUtils.replaceEach(formulalvl6,
                                sortedparameters.toArray(new String[0]), apindex.toArray(new String[0]));

                    }
                } else {
                    formula = candidateOracle.getPatternBase().getPattern_Formula();
                }
                Formulas.append(formula);
            }
            Formulas.append("\n"); //always a new line . if formula is not validated a blank line appears

        }

        return Formulas.toString();

    }

    public String getPropositionIndex(String proposition) {

        return getPropositionIndex(proposition, false);
    }

    public String getPropositionIndex(String proposition, boolean raw) {
        HashBiMap<Integer, String> aplookup = HashBiMap.create();
        aplookup.putAll(getSimpleModelMap());
        String encodedAP = "";
        // we encode alive as not dead "!dead"
        // so we strip the negation from the alive property, by default: "!dead"
        if (proposition.startsWith("!") && aplookup.inverse().containsKey(proposition.toLowerCase().substring(1))) {
            encodedAP = "!" + (!raw ? APPrefix : "") + aplookup.inverse().get(proposition.toLowerCase().substring(1));
        }
        if (!proposition.startsWith("!") && aplookup.inverse().containsKey(proposition.toLowerCase())) {
            encodedAP = (!raw ? APPrefix : "") + aplookup.inverse().get(proposition.toLowerCase());
        }
        return encodedAP;
    }


    @JsonGetter("modelAPs")
    private LinkedHashMap<Integer, String> getSimpleModelMap() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();

        int i = 0;
        for (String ap : modelAPs) {
            map.put(i, ap);
            i++;
        }
        return map;
    }

    @JsonSetter("modelAPs")
    private void setFromSimpleModelMap(LinkedHashMap<Integer, String> map) {
        this.modelAPs.addAll(map.values());
    }
}