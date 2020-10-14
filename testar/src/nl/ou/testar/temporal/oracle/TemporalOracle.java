package nl.ou.testar.temporal.oracle;

import com.opencsv.bean.*;
import nl.ou.testar.temporal.util.foundation.ModelBean;
import nl.ou.testar.temporal.model.proposition.PropositionConstants;
import nl.ou.testar.temporal.util.foundation.ValStatus;
import nl.ou.testar.temporal.util.foundation.Verdict;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;

/**
 * Container of a Temporal oracle
 */
public class TemporalOracle extends ModelBean implements Cloneable{
    @CsvIgnore
    private static String version = "20200104";

    @CsvBindAndJoinByName(column = "(?i)PATTERN_SUBSTITUTION_P[0-9]+", elementType = String.class)
    private MultiValuedMap<String,String> pattern_Substitutions;
    //@CsvBindByName
    @CsvIgnore
    private int pattern_ConstraintSet;  // based on which set of constraints. feature no longer used.
    @CsvCustomBindByName(converter = CSVConvertValStatus.class)
    private ValStatus oracle_validationstatus;  //strange case sensitivity problem with CSV converter: leave all lowercase
    @CsvCustomBindByName(converter = CSVConvertVerdict.class )
    private Verdict oracle_verdict;         //strange case sensitivity problem with CSV converter: leave all lowercase
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

    @CsvRecurse
    private TemporalPatternBase patternBase;


    public TemporalOracle() {
        super();
    }

    /**
     * not in actual use anymore
     * @return hardcoded version of the an oracle.
     */
    public static String getVersion() {
        return version;
    }

    /**
     * used when reading an oracle CSV file
     * @return the oraclePattern, which contains desciptions, but most importantly , the formula and the parameters
     */
    public TemporalPatternBase getPatternBase() {
        return patternBase;
    }

    /**
     * used when reading an oracle CSV file
     * @return enumeration of the Patterntype. E.g. LTL_SPOT, CTL-LTSMIN
     */
    public TemporalFormalism getPatternTemporalType() {
        return patternBase.getPattern_TemporalFormalism();
    }


    /**
     * used when writing  an oracle CSV file (after they are model-checked)
     * @param patternBase
     */
    public void setPatternBase(TemporalPatternBase patternBase) {
        this.patternBase = patternBase;
    }

    /**
     * used for compatibility when reading an oracle CSV file
     * traces that are read from an oracle file are not used.
     * @return sequence of transitions-ids
     */
    @SuppressWarnings("unused")
    public List<String> getExampleRun_Prefix_Transitions() {
        return exampleRun_Prefix_Transitions;
    }

    /**
     * used when writing  an oracle CSV file (after they are model-checked)
     * @param exampleRun_Prefix_Transitions sequence of transitions-ids
     */
    public void setExampleRun_Prefix_Transitions(List<String> exampleRun_Prefix_Transitions) {
        this.exampleRun_Prefix_Transitions = exampleRun_Prefix_Transitions;
    }

    /**
     * used for compatibility when reading an oracle CSV file
     * traces that are read from an oracle file are not used.
     * @return sequence of transitions-ids
     */
    @SuppressWarnings("unused")
    public List<String> getExampleRun_Cycle_Transitions() {
        return exampleRun_Cycle_Transitions;
    }

    /**
     * used when writing  an oracle CSV file (after they are model-checked)
     * @param exampleRun_Cycle_Transitions sequence of transitions-ids
     */
    public void setExampleRun_Cycle_Transitions(List<String> exampleRun_Cycle_Transitions) {
        this.exampleRun_Cycle_Transitions = exampleRun_Cycle_Transitions;
    }

    /**
     * used for  reading an oracle CSV file
     * @return map of parameters 'pX' in a formula pattern to the actual atomic proposition available the model
     */
    @SuppressWarnings("unused")
    public MultiValuedMap<String,String> getPattern_Substitutions() {
        return pattern_Substitutions;
    }

    /**
     * @return lexicographic sorted version of the Substitution Map. parameters are the keys, so p0 before p1, before p2
     * Implemented as a treemap because this is sirted by default on keys
     */
    public TreeMap<String,String> getSortedPattern_Substitutions(){
        TreeMap<String, String> treeMap = new TreeMap<>();
        for(String str : pattern_Substitutions.keySet()){
            List<String> valueList=new ArrayList<>(pattern_Substitutions.get(str));
            treeMap.put(str, valueList.get(0)); // first list entry
        }
      return treeMap;
    }

    /**
     * used when writing  an oracle CSV file (after they are model-checked)
     * @param pattern_Substitutions map  of parameters and the atomic proposition  they represent
     */
    public void setPattern_Substitutions(MultiValuedMap<String,String> pattern_Substitutions) {

        this.pattern_Substitutions = pattern_Substitutions;
    }

    /**
     *  candidate for refactoring. not used anymore
     * @return
     */
    @SuppressWarnings("unused")
    public int getPattern_ConstraintSet() {
        return pattern_ConstraintSet;
    }
    /**
     *  candidate for refactoring. not used anymore
     * @return
     */
    public void setPattern_ConstraintSet(int pattern_ConstraintSet) {
        this.pattern_ConstraintSet = pattern_ConstraintSet;
    }

    /**
     * used for  reading an oracle CSV file
     * @return indicator whether a human has evaluated the Oracle.
     */
    @SuppressWarnings("unused")
    public ValStatus getOracle_validationstatus() {
        return oracle_validationstatus;
    }

    /**
     * used when writing  an oracle CSV file (after they are model-checked)
     * @param oracle_validationstatus human evaluation of the Oracle.
     */
    public void setOracle_validationstatus(ValStatus oracle_validationstatus) {
        this.oracle_validationstatus = oracle_validationstatus;
    }

    /**
     * @return
     */
    @SuppressWarnings("unused")
    public Verdict getOracle_verdict() {
        return oracle_verdict;
    }

    public void setOracle_verdict(Verdict oracle_verdict) {
        this.oracle_verdict = oracle_verdict;
    }
    @SuppressWarnings("unused")
    public List<String> getExampleRun_Prefix_States() {
        return exampleRun_Prefix_States;
    }

    public void setExampleRun_Prefix_States(List<String> exampleRun_Prefix_States) {
        this.exampleRun_Prefix_States = exampleRun_Prefix_States;
    }
    @SuppressWarnings("unused")
    public List<String> getExampleRun_Cycle_States() {
        return exampleRun_Cycle_States;
    }

    public void setExampleRun_Cycle_States(List<String> exampleRun_Cycle_States) {
        this.exampleRun_Cycle_States = exampleRun_Cycle_States;
    }
    @SuppressWarnings("unused")
    public String getLog_RunDate() {
        return log_RunDate;
    }

    public void setLog_RunDate(String log_RunDate) {
        this.log_RunDate = log_RunDate;
    }

    public TemporalOracle clone() throws            CloneNotSupportedException
    {
        TemporalOracle cloned=(TemporalOracle)super.clone();
        cloned.setPatternBase((TemporalPatternBase)cloned.getPatternBase().clone());
        cloned.exampleRun_Cycle_States= new ArrayList<>(exampleRun_Cycle_States);
        cloned.exampleRun_Cycle_Transitions=new ArrayList<>(exampleRun_Cycle_Transitions);
        cloned.exampleRun_Prefix_States= new ArrayList<>(exampleRun_Prefix_States);
        cloned.exampleRun_Prefix_Transitions=new ArrayList<>(exampleRun_Prefix_Transitions);
        MultiValuedMap<String,String> mvmap = new HashSetValuedHashMap<>();
        for (Map.Entry<String,String> entry :pattern_Substitutions.entries()
             ) {
            mvmap.put(entry.getKey(),entry.getValue());
        }
        cloned.setPattern_Substitutions(mvmap);
        return cloned;
    }

    public static TemporalOracle getSampleLTLOracle(){
        TemporalOracle to = new TemporalOracle(); //new TemporalOracle("notepad","v10","34d23", attrib);
        Set<String> dummyset = new HashSet<>();
        List<String> dummylist= new ArrayList<>();

        dummyset.add("(populated by TESTAR)");
        dummylist.add("(populated by TESTAR)");
        to.setApplication_AbstractionAttributes(dummyset);
        to.setApplicationName("(populated by TESTAR)");
        to.setApplicationVersion("(populated by TESTAR)");
        to.setApplication_ModelIdentifier("(populated by TESTAR)");
        to.setExampleRun_Cycle_States(dummylist);
        to.setExampleRun_Prefix_States(dummylist);
        to.setExampleRun_Cycle_Transitions(dummylist);
        to.setExampleRun_Prefix_Transitions(dummylist);
        to.setLog_RunDate("(populated by TESTAR)");
        to.setOracle_verdict(Verdict.UNDEF);
        TemporalPatternBase p = new TemporalPatternBase();
        p.setPattern_TemporalFormalism(TemporalFormalism.LTL);
        p.setPattern_Description("(p0 and p2) precedes p1");
        p.setPattern_Scope("globally");
        p.setPattern_Class("precedence");
        p.setPattern_Formula("!p1 W (p0 & p2)");
        p.setPattern_Parameters(Arrays.asList("p0", "p1","p2"));
        to.setPatternBase(p);
        to.setPattern_ConstraintSet(1);


        to.setOracle_validationstatus(ValStatus.ACCEPTED);
        MultiValuedMap<String,String> pattern_Substitutions = new HashSetValuedHashMap<>();
        pattern_Substitutions.put("PATTERN_SUBSTITUTION_P0","UIButton"+ PropositionConstants.SETTING.subKeySeparator +"Title_Match_OK");
        pattern_Substitutions.put("PATTERN_SUBSTITUTION_P1","UIWindow_Title_main"+ PropositionConstants.SETTING.subKeySeparator +"exists");
        pattern_Substitutions.put("PATTERN_SUBSTITUTION_P2","UIWindow_Title_closure"+ PropositionConstants.SETTING.subKeySeparator +"exists");
        to.setPattern_Substitutions(pattern_Substitutions);

        to.set_userComments(Collections.singletonList("User remarks"));
        to.addLog("Format version: "+version);
        to.addLog("This is a sample oracle. for valid substitutions, please see the Model file");
        to.addLog("Formula is instantiated by parameters and substitutions. parameter syntax: 'p[0-9]+'");
        to.addLog("Substitution must match a parameter. Header syntax: 'pattern_Substitution_P[0-9]+'");
        to.addLog("AVOID literals 'A,E,X,F,G,U,W,R,M' as substitutions: syntactical elements of Formulas");
        to.addLog("Column order is not important. Header names are case insensitive");
        to.set_modifieddate("(populated by TESTAR)");
        return to;
    }
}
