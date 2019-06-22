package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.CSVConvertValStatus;
import nl.ou.testar.temporal.util.CSVConvertVerdict;
import nl.ou.testar.temporal.util.ValStatus;
import nl.ou.testar.temporal.util.Verdict;

import java.util.LinkedList;
import java.util.List;

public class TemporalOracle extends TemporalPattern{

    @CsvBindAndSplitByName(elementType = String.class,collectionType = LinkedList.class)
    private List<String> substitutions; //b0,b1,b2,bn; //b25-> 'Button_OK_ParentTitle'
    @CsvCustomBindByName(converter = CSVConvertValStatus.class)
    private ValStatus validationStatus;
    @CsvCustomBindByName(converter = CSVConvertVerdict.class)
    private Verdict verdict;
    @CsvBindByName
    private double traceSupport;
    @CsvBindByName
    private double traceConfidence;
    @CsvBindByName
    private double traceLift;
    @CsvBindAndSplitByName(elementType = String.class,collectionType = LinkedList.class)
    private List<String> testsequenceIDs;
    @CsvBindAndSplitByName(elementType = String.class,collectionType = LinkedList.class)
    private List<String> prefixOfRun; //state -> edge->state-> etc,  encoding is "S<node id>" or "T<edge id>"
    @CsvBindAndSplitByName(elementType = String.class,collectionType = LinkedList.class)
    private List<String> cycleOfRun;  // idem
    @CsvBindByName
    private String runDate;
    @CsvBindAndSplitByName(elementType = String.class,collectionType = LinkedList.class)
    private List<String> log;


    public List<String> getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(List<String> substitutions) {
        this.substitutions = substitutions;
    }
    public List<String> getTestsequenceIDs() {
        return testsequenceIDs;
    }

    public void setTestsequenceIDs(List<String> testsequenceIDs) {
        this.testsequenceIDs = testsequenceIDs;
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

    public List<String> getPrefixOfRun() {
        return prefixOfRun;
    }

    public void setPrefixOfRun(List<String> prefixOfRun) {
        this.prefixOfRun = prefixOfRun;
    }

    public List<String> getCycleOfRun() {
        return cycleOfRun;
    }

    public void setCycleOfRun(List<String> cycleOfRun) {
        this.cycleOfRun = cycleOfRun;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}