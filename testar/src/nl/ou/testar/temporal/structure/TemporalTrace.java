package nl.ou.testar.temporal.structure;

import java.util.List;

public class TemporalTrace implements Cloneable{


    public TemporalTrace() {
    }

    private List<TemporalTraceEvent> trace;

    public List<TemporalTraceEvent> getTrace() {
        return trace;
    }

    public void setTrace(List<TemporalTraceEvent> trace) {
        this.trace = trace;
    }

    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}