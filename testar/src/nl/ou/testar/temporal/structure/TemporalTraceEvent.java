package nl.ou.testar.temporal.structure;

import java.util.ArrayList;
import java.util.List;

public class TemporalTraceEvent implements Cloneable{





    private String state; //
    private String edge;  //
    private List<String> properties;



    public TemporalTraceEvent(String state, String edge) {
        this.state = state;
        this.edge = edge;
        this.properties = new ArrayList<>();
    }

    public TemporalTraceEvent(String state, String edge, List<String> properties) {
        this.state = state;
        this.edge = edge;
        this.properties = properties;
    }

    public TemporalTraceEvent() {
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEdge() {
        return edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}