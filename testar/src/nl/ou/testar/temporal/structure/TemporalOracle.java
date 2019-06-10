package nl.ou.testar.temporal.structure;

import nl.ou.testar.temporal.util.TemporalType;
import nl.ou.testar.temporal.util.ValStatus;
import nl.ou.testar.temporal.util.Verdict;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TemporalOracle implements Cloneable{



    private long id;// sequential nr
    private TemporalType temporalType;
    private String alias; //short description e.g. spec pattern name
    private String propertytemplate;  // e.g. G(b0->Fb1)
    private Map<String, String> paramSubstitutions; //b25-> 'Button_OK_ParentTitle'
    private ValStatus validationStatus;
    private Verdict verdict;
    private double traceSupport;
    private double traceConfidence;
    private double traceLift;

    public List<String> getTestsequenceIDs() {
        return testsequenceIDs;
    }

    public void setTestsequenceIDs(List<String> testsequenceIDs) {
        this.testsequenceIDs = testsequenceIDs;
    }

    private  List<String> testsequenceIDs;
    private LinkedHashMap<String, String> prefixOfRun; //state -> edge->state-> etc
    private LinkedHashMap<String, String> cycleOfRun;  // idem
    private String runDate;
    private List<String> log;


    private List<String> comments;
    private String modifieddate;


    public TemporalOracle() {
    }

    public TemporalOracle(long id, TemporalType tlType, String alias, String propertypattern) {
        this.id = id;
        this.temporalType = tlType;
        this.alias = alias;
        this.propertytemplate = propertypattern;
        this.log=new ArrayList<String>();
        this.comments=new ArrayList<String>();
        this.modifieddate= LocalDateTime.now().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPropertytemplate() {
        return propertytemplate;
    }

    public void setPropertytemplate(String propertytemplate) {
        this.propertytemplate = propertytemplate;
    }

    public ValStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(Verdict verdict) {
        this.verdict = verdict;
    }

    public double getTraceSupport() {
        return traceSupport;
    }

    public void setTraceSupport(double traceSupport) {
        this.traceSupport = traceSupport;
    }

    public double getTraceConfidence() {
        return traceConfidence;
    }

    public void setTraceConfidence(double traceConfidence) {
        this.traceConfidence = traceConfidence;
    }

    public double getTraceLift() {
        return traceLift;
    }

    public void setTraceLift(double traceLift) {
        this.traceLift = traceLift;
    }

    public LinkedHashMap<String, String> getPrefixOfRun() {
        return prefixOfRun;
    }

    public void setPrefixOfRun(LinkedHashMap<String, String> prefixOfRun) {
        this.prefixOfRun = prefixOfRun;
    }

    public LinkedHashMap<String, String> getCycleOfRun() {
        return cycleOfRun;
    }

    public void setCycleOfRun(LinkedHashMap<String, String> cycleOfRun) {
        this.cycleOfRun = cycleOfRun;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getModifieddate() {
        return modifieddate;
    }

    public void setModifieddate(String modifieddate) {
        this.modifieddate = modifieddate;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}