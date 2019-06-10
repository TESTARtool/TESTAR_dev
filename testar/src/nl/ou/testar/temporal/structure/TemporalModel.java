package nl.ou.testar.temporal.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@JsonRootName(value="TemporalProperties")
public class TemporalModel extends TemporalBean{

    private List<StateEncoding> stateEncodings; //Integer:  to concretstateID
    private List<TemporalTrace> testSequences; //
    private List<String> apOfModel; //AP<digits> to widget property map:
    private String formatVersion="20190603";



    public TemporalModel(String applicationName, String applicationVersion, String modelIdentifier, Set abstractionAttributes) {
        super(applicationName, applicationVersion, modelIdentifier, abstractionAttributes);
        this.stateEncodings = new ArrayList<>();
        this.apOfModel = new ArrayList<String>();
    }



    public List<String> getApOfModel() {
        return apOfModel;
    }

    public void setApOfModel(List<String> apOfModel) {
        this.apOfModel = apOfModel;
    }

    public List<TemporalTrace> getStateTransistionSequence() {
        return testSequences;
    }

    public void setStateTransistionSequence(List<TemporalTrace> stateTransistionSequence) {
        this.testSequences = stateTransistionSequence;
    }

    public List<StateEncoding> getStateEncodings() {
        return stateEncodings;
    }

    public void setStateEncodings(List<StateEncoding> stateEncodings) {
        this.stateEncodings = stateEncodings;
    }


    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }



    //custom
    public void addStateEncoding(StateEncoding state) {
        stateEncodings.add(state);
    }

    public void removeStateEncoding(StateEncoding state) { stateEncodings.remove(state); }

    public void setAllEncodedAPConjuncts(){
        for (StateEncoding stateEnc: stateEncodings) {
            stateEnc.setEncodedAPConjuncts(apOfModel);
        }
    }

    public void fetchDBModel(String filter){
        //loop through model ,
        //  query db model and set in header properties
        // query concret states and moke stat encoding per state
        //===> define properties to collect strategies,
        //per state get outbound edges and make transition encodings
        //
        //
        //  collect AP's make a listentry : [Statexxx, list of AP's] apply filters
        //   state list, +ap's



    }
    private String makeHOAOutput(){
        //see http://adl.github.io/hoaf/
    StringBuilder result=new StringBuilder();
    result.append("HOA v1\n");
    result.append("States: ");
    result.append(stateEncodings.size());
    result.append("\n");
    result.append("Start: 0\n");
    result.append("Acceptance: 1 Inf(1))\n");  //==Buchi
    result.append("AP: ");
    result.append(apOfModel.size());
        int i=0;
        for (String ap:apOfModel             ) {
            result.append(" \"ap");
            result.append(i);
            result.append("\"");
            i++;
        }
     result.append("\n");
     result.append("--BODY--");
        int s=0;
        for (StateEncoding stateenc: stateEncodings) {
            result.append("State: ");
            result.append(s);
            result.append("\n");
            for (TransitionEncoding trans:stateenc.getTransitionColl()  ) {
                result.append(trans.getEncodedAPConjunct());
                int targetstateindex= stateEncodings.indexOf(trans.getTargetState());
                result.append(" "+targetstateindex);
                result.append(" {0}\n");  //all are in the same buchi acceptance set
            }
        }
        result.append("--END--");
        result.append("EOF_HOA");
        return result.toString();
    }



}