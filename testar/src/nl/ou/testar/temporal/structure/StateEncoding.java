package nl.ou.testar.temporal.structure;

import java.util.List;

public class StateEncoding {
    private String state;
    private List<TransitionEncoding> transitionColl;
    private List<String> stateAPs;

    public StateEncoding(String state) {
        this.state = state;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<TransitionEncoding> getTransitionColl() {
        return transitionColl;
    }

    public void setTransitionColl(List<TransitionEncoding> transitionColl) {
        this.transitionColl = transitionColl;
    }

    public void addtransition(TransitionEncoding tenc){
        transitionColl.add(tenc);
    }
    public void removetransition(TransitionEncoding tenc){
        transitionColl.remove(tenc);
    }

    public List<String> getStateAPs() {
        return stateAPs;
    }

    public void setStateAPs(List<String> stateAPs) {
        this.stateAPs = stateAPs;
    }


    public String lookupEdge(String decodedAPConjunct) {
        String prefix = "ap";
        String result = "";
        for (TransitionEncoding trans : transitionColl) {
            String strippedDecodedTrans = decodedAPConjunct.replace(prefix, "");
            if (strippedDecodedTrans.equals(trans.getEncodedAPConjunct())) {
                result = trans.getEdge();
                break;
            }
        }
        return result;
    }

    public void updateAllTransitionConjuncts(List<String> modelAPs) {

        for (TransitionEncoding trans : transitionColl) {
            trans.setEncodedTransitionConjunct(modelAPs, stateAPs);

        }
    }
}







