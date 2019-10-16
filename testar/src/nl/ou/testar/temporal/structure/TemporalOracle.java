package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.*;

import java.util.*;

public class TemporalOracle extends TemporalPattern{

   // @CsvCustomBindByName( converter = CSVConvertMap.class)
   @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"=+"+csvsep, writeDelimiter = csvsep+"====="+csvsep)
    private List<String> pattern_Substitutions; //b0:Button_OK_IsWindowsModel,b1:<>,b2:<>,bn:'Button_OK_ParentTitle'

    @CsvCustomBindByName(converter = CSVConvertValStatus.class)
    private ValStatus oracle_validationstatus;  //strange case sensitivity problem with CSV converter: leave all lowercase
    @CsvCustomBindByName(converter = CSVConvertVerdict.class )
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
    private List<String> exampleRun_Prefix_States; //state -> edge->state-> etc,  encoding is "S<node id>" or "T<edge id>"
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> exampleRun_Prefix_Transitions; //state -> edge->state-> etc,  encoding is "S<node id>" or "T<edge id>"

    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> exampleRun_Cycle_States;  // idem



    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> exampleRun_Cycle_Transitions;  // idem
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

    public List<String> getExampleRun_Prefix_Transitions() {
        return exampleRun_Prefix_Transitions;
    }

    public void setExampleRun_Prefix_Transitions(List<String> exampleRun_Prefix_Transitions) {
        this.exampleRun_Prefix_Transitions = exampleRun_Prefix_Transitions;
    }

    public List<String> getExampleRun_Cycle_Transitions() {
        return exampleRun_Cycle_Transitions;
    }

    public void setExampleRun_Cycle_Transitions(List<String> exampleRun_Cycle_Transitions) {
        this.exampleRun_Cycle_Transitions = exampleRun_Cycle_Transitions;
    }

    public List<String> getPattern_Substitutions() {
        return pattern_Substitutions;
    }

    public void setPattern_Substitutions(List<String> pattern_Substitutions) {
        this.pattern_Substitutions = pattern_Substitutions;
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

    public List<String> getExampleRun_Prefix_States() {
        return exampleRun_Prefix_States;
    }

    public void setExampleRun_Prefix_States(List<String> exampleRun_Prefix_States) {
        this.exampleRun_Prefix_States = exampleRun_Prefix_States;
    }

    public List<String> getExampleRun_Cycle_States() {
        return exampleRun_Cycle_States;
    }

    public void setExampleRun_Cycle_States(List<String> exampleRun_Cycle_States) {
        this.exampleRun_Cycle_States = exampleRun_Cycle_States;
    }

    public String getLog_RunDate() {
        return log_RunDate;
    }

    public void setLog_RunDate(String log_RunDate) {
        this.log_RunDate = log_RunDate;
    }

    public TemporalOracle clone() throws            CloneNotSupportedException
    {
        return (TemporalOracle)super.clone();
    }
public static TemporalOracle getSampleOracle(){
    TemporalOracle to = new TemporalOracle(); //new TemporalOracle("notepad","v10","34d23", attrib);
    Set attrib = new HashSet<String>();
    attrib.add("R");
    attrib.add("T");
    attrib.add("P");
    attrib.add("E");
    to.setApplicationName("my app");
    to.setApplicationVersion("my version");
    to.setApplication_ModelIdentifier("my modelidentifier");
    to.setApplication_AbstractionAttributes(attrib);
    to.setPattern_TemporalFormalism(TemporalType.LTL);
    to.setOracle_validationstatus(ValStatus.ACCEPTED);
    to.setPattern_Description("a precedes b");
    to.setPattern_Scope("globally");
    to.setPattern_Class("precedence");
    to.setPattern_Formula("!b U a");
    to.setPattern_Parameters(Arrays.asList("a", "b"));
    //List<String> mappie = new HashMap<String,String>() {{put("a", "UIButton_OK");put("b", "UIWindow_Title_main_exists");}};
    List<String> mappie = new ArrayList<String>(){{add("UIButton_OK");add("UIWindow_Title_main_exists");}};
    to.setPattern_Substitutions(mappie);
    List<String> comments= new ArrayList<String>();
    comments.add("this is a sample oracle. for valid substitutions, please see the APSelectorManager.JSON");
    comments.add("avoid using 'X,F,G,U,W,R,M' as parameters, as they are used in LTL syntax");
    comments.add("the separator for substitutions is hardcoded set as regex ';=+;'  so ;===========; is considered a valid separator");
    //comments.add("Excel does not quote common text fields during export. Try MS Access as alternative");
    to.set_comments(comments);
    return to;
}

}
