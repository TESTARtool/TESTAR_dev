package nl.ou.testar.temporal.structure;

import java.util.Set;

public class TransitionEncoding {
    private String edge;
    private Set<String > edgeAPs;
    private String targetState;
    private String encodedAPConjunct;

    public String getTargetState() {
        return targetState;
    }

    public void setTargetState(String targetState) {
        this.targetState = targetState;
    }




    public TransitionEncoding() {
    }

    public TransitionEncoding(String edge, Set<String> edgeAPs) {
        this.edge = edge;
        this.edgeAPs = edgeAPs;
    }

    public String getEdge() {
        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }

    public Set<String> getEdgeAPs() {
        return edgeAPs;
    }

    public void setEdgeAPs(Set<String> edgeAPs) {
        this.edgeAPs = edgeAPs;
    }

    public String getEncodedAPConjunct() {
        return encodedAPConjunct;
    }

    public void setEncodedAPConjunct(String encodedAPConjunct) {
        this.encodedAPConjunct = encodedAPConjunct;

    }
    public void setEncodedTransitionConjunct(Set<String> modelAPs, Set<String> stateAPs){
        StringBuilder encodedresult= new StringBuilder();

        int i=0;
        for (String modelap: modelAPs                 ) {
            boolean b = edgeAPs.stream().anyMatch(str -> str.trim().equals(modelap));
            encodedresult.append("[");
            if (b){
                encodedresult.append(""+i); }
            else{
                boolean be = stateAPs.stream().anyMatch(str -> str.trim().equals(modelap));
                if(be){encodedresult.append(""+i);}
                else{encodedresult.append("!"+i);}
            }
            encodedresult.append("]");
            i++;
            if(i<modelAPs.size()) encodedresult.append("&");
        }
        encodedAPConjunct=encodedresult.toString();

    }
}
