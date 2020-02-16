package nl.ou.testar.temporal.model;

import java.util.Set;

public class TransitionEncoding {
    private String transition;
    private Set<String > transitionAPs;
    private String targetState;
    private String encodedTransitionAPConjunct;

    public String getTargetState() {
        return targetState;
    }

    public void setTargetState(String targetState) {
        this.targetState = targetState;
    }




    public TransitionEncoding() {
    }

    public TransitionEncoding(String transition, Set<String> transitionAPs) {
        this.transition = transition;
        this.transitionAPs = transitionAPs;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public Set<String> getTransitionAPs() {
        return transitionAPs;
    }

    public void setTransitionAPs(Set<String> transitionAPs) {
        this.transitionAPs = transitionAPs;
    }

    public String getEncodedTransitionAPConjunct() {
        return encodedTransitionAPConjunct;
    }

    public void setEncodedTransitionAPConjunct(String encodedTransitionAPConjunct) {
        this.encodedTransitionAPConjunct = encodedTransitionAPConjunct;

    }
    public void setEncodedTransitionConjunct(Set<String> modelAPs, Set<String> stateAPs){
        StringBuilder encodedresult= new StringBuilder();
        //encodedresult.append("[");
        int i=0;
        for (String modelap: modelAPs                 ) {
            boolean b = transitionAPs.stream().anyMatch(str -> str.trim().equals(modelap));
            //encodedresult.append("[");
            if (b){
                encodedresult.append(""+i); }
            else{
                boolean be = stateAPs.stream().anyMatch(str -> str.trim().equals(modelap));
                if(be){encodedresult.append(""+i);}
                else{encodedresult.append("!"+i);}
            }
            //encodedresult.append("]");
            i++;
            if(i<modelAPs.size()) encodedresult.append("&");
        }
        //encodedresult.append("]");
        encodedTransitionAPConjunct =encodedresult.toString();

    }
}
