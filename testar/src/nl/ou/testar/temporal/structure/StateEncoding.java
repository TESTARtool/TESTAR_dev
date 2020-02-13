package nl.ou.testar.temporal.structure;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StateEncoding {
    private String state;
    private List<TransitionEncoding> transitionColl;
    private Set<String> stateAPs;
    private String encodedStateAPConjunct;

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

    public String getEncodedStateAPConjunct() {
        return encodedStateAPConjunct;
    }

    public void setEncodedStateAPConjunct(String encodedStateAPConjunct) {
        this.encodedStateAPConjunct = encodedStateAPConjunct;
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
            if (strippedDecodedTrans.equals(trans.getEncodedTransitionAPConjunct())) {
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
    public void updateAPConjuncts(Set<String> modelAPs) {

        for (TransitionEncoding trans : transitionColl) {
            trans.setEncodedTransitionConjunct(modelAPs, stateAPs);
        }
        Set<String> transitionAPs=retrieveAllTransitionAPs();
        StringBuilder encodedresult= new StringBuilder();
        int i=0;
        for (String modelAP: modelAPs                 ) {
            boolean b = stateAPs.stream().anyMatch(str -> str.trim().equals(modelAP));
            if (b){
                encodedresult.append(i); }
            else{
                boolean be = transitionAPs.stream().anyMatch(str -> str.trim().equals(modelAP));
                if(be){
                    encodedresult.append(i);}
                else{
                    encodedresult.append("!").append(i);}
            }
            i++;
            if(i<modelAPs.size()) encodedresult.append("&");
        }
        encodedStateAPConjunct =encodedresult.toString();
    }
}







