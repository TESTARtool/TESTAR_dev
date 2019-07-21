package nl.ou.testar.temporal.structure;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StateEncoding {
    private String state;
    private List<TransitionEncoding> transitionColl;
    private Set<String> stateAPs;

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

    public Set<String> getStateAPs() {
        return stateAPs;
    }

    public void setStateAPs(Set<String> stateAPs) {
        this.stateAPs = stateAPs;
    }


    public String lookupEdge(String decodedAPConjunct) {
        String prefix = "ap";
        String result = "";
        for (TransitionEncoding trans : transitionColl) {
            String strippedDecodedTrans = decodedAPConjunct.replace(prefix, "");
            if (strippedDecodedTrans.equals(trans.getEncodedAPConjunct())) {
                result = trans.getTransition();
                break;
            }
        }
        return result;
    }

    public Set<String> retrieveAllTransitionAPs(){
        Set<String> edgeAPS = new LinkedHashSet<>();
        for (TransitionEncoding trans : transitionColl) {
            edgeAPS.addAll(trans.getTransitionAPs());
        }
            return edgeAPS;
    }
    public void updateAllTransitionConjuncts(Set<String> modelAPs) {

        for (TransitionEncoding trans : transitionColl) {
            trans.setEncodedTransitionConjunct(modelAPs, stateAPs);

        }
    }
}







