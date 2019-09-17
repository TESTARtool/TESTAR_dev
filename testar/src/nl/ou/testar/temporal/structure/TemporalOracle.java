package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.*;

import java.util.*;

public class TemporalOracle extends TemporalPattern{


    @CsvCustomBindByName(converter = CSVConvertValStatus.class)
    private ValStatus oracle_validationstatus;  //strange case sensitivity problem with CSV converter: leave all lowercase
    @CsvCustomBindByName(converter = CSVConvertVerdict.class)
    private Verdict oracle_verdict;         //strange case sensitivity problem with CSV converter: leave all lowercase
    @CsvBindByName
    private double log_TraceSupport;
    @CsvBindByName
    private double log_TraceConfidence;
    @CsvBindByName
    private double log_TraceLift;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> log_TestsequenceIDs;
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> log_PrefixOfRun; //state -> edge->state-> etc,  encoding is "S<node id>" or "T<edge id>"
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> log_CycleOfRun;  // idem
    @CsvBindByName
    private String log_RunDate;



    public TemporalOracle() {

        super();
        this.set_formatVersion("20190629");
    }



    public List<String> getLog_TestsequenceIDs() {
        return log_TestsequenceIDs;
    }

    public void setLog_TestsequenceIDs(List<String> log_TestsequenceIDs) {
        this.log_TestsequenceIDs = log_TestsequenceIDs;
    }




    public ValStatus getOracle_validationstatus() {
        return oracle_validationstatus;
    }

    public void setOracle_validationstatus(ValStatus oracle_validationstatus) {
        this.oracle_validationstatus = oracle_validationstatus;
    }


    public Verdict getOracle_verdict() {
        return oracle_verdict;
    }

    public void setOracle_verdict(Verdict oracle_verdict) {
        this.oracle_verdict = oracle_verdict;
    }

    public double getLog_TraceSupport() {
        return log_TraceSupport;
    }

    public void setLog_TraceSupport(double log_TraceSupport) {
        this.log_TraceSupport = log_TraceSupport;
    }

    public double getLog_TraceConfidence() {
        return log_TraceConfidence;
    }

    public void setLog_TraceConfidence(double log_TraceConfidence) {
        this.log_TraceConfidence = log_TraceConfidence;
    }

    public double getLog_TraceLift() {
        return log_TraceLift;
    }

    public void setLog_TraceLift(double log_TraceLift) {
        this.log_TraceLift = log_TraceLift;
    }

    public List<String> getLog_PrefixOfRun() {
        return log_PrefixOfRun;
    }

    public void setLog_PrefixOfRun(List<String> log_PrefixOfRun) {
        this.log_PrefixOfRun = log_PrefixOfRun;
    }

    public List<String> getLog_CycleOfRun() {
        return log_CycleOfRun;
    }

    public void setLog_CycleOfRun(List<String> log_CycleOfRun) {
        this.log_CycleOfRun = log_CycleOfRun;
    }

    public String getLog_RunDate() {
        return log_RunDate;
    }

    public void setLog_RunDate(String log_RunDate) {
        this.log_RunDate = log_RunDate;
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

    to.setApplication_ModelIdentifier("34edf5");
    to.setApplication_AbstractionAttributes(attrib);
    to.setPattern_TemporalFormalism(TemporalType.LTL);
    to.setOracle_validationstatus(ValStatus.ACCEPTED);
    to.setPattern_Description("a precedes b");
    to.setPattern_Scope("globally");
    to.setPattern_Class("precedence");
    to.setPattern_Formula("!b U a");
    to.setPattern_Parameters(Arrays.asList("a", "b"));
    to.setPattern_Substitutions(Arrays.asList("a:UIButton_OK", "b:UIWindow_Title_main_exists"));
    List<String> comments= new ArrayList<String>();
    comments.add("this is a sample oracle. for valid substitutions, please see the APSelectorManager.JSON");
    comments.add("avoid using 'X,F,G,U,W,R,M' as parameters, as they are used in LTL syntax");
    comments.add("CSV editor of choice is LibreCalc as this tool can explicitly quote all text field");
    comments.add("Excel does not quote common text fields during export. Try MS Access as alternative");
    to.set_comments(comments);
    return to;
}

}
