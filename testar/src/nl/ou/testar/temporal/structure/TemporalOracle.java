package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.*;

import java.util.*;

public class TemporalOracle extends TemporalPattern{

    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
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
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> testsequenceIDs;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> prefixOfRun; //state -> edge->state-> etc,  encoding is "S<node id>" or "T<edge id>"
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> cycleOfRun;  // idem
    @CsvBindByName
    private String runDate;

    @CsvBindByName
    private String formatVersion="20190629";

    public TemporalOracle() {
        super();
    }


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

    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }
public static TemporalOracle getSampleOracle(){
    TemporalOracle to = new TemporalOracle(); //new TemporalOracle("notepad","v10","34d23", attrib);
    Set attrib = new HashSet();
    attrib.add("R");
    attrib.add("T");
    attrib.add("P");
    attrib.add("E");
    to.setApplicationName("notepad");
    to.setApplicationVersion("v10");

    to.setModelIdentifier("34edf5");
    to.setAbstractionAttributes(attrib);
    to.setTemporalFormalism(TemporalType.LTL);
    to.setValidationStatus(ValStatus.ACCEPTED);
    to.setDescription("a precedes b");
    to.setScope("globally");
    to.setPatternclass("precedence");
    to.setPattern("!b U a");
    to.setParameters(Arrays.asList("a", "b"));
    to.setSubstitutions(Arrays.asList("a:UIButton_OK", "b:UIWindow_Title_main_exists"));
    List<String> comments= new ArrayList<String>();
    comments.add("this is a sample oracle. for valid substitutions, please see the APSelectorManager.JSON");
    comments.add("CSV editor of choice is LibreCalc as this tool can explicitly quote all text field");
    comments.add("Excel does not quote common text fields during export. Try MS Access as alternative");
    to.setComments(comments);
    return to;
}

}
