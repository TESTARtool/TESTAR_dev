package nl.ou.testar.temporal.structure;

import java.util.ArrayList;
import java.util.List;

public class TemporalTraceEvent implements Cloneable{





    private String state; //
    private String transition;  //
    private List<String> properties;



    public TemporalTraceEvent(String state, String transition) {
        this.state = state;
        this.transition = transition;
        this.properties = new ArrayList<>();
    }

    public TemporalTraceEvent(String state, String transition, List<String> properties) {
        this.state = state;
        this.transition = transition;
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

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
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