package nl.ou.testar.temporal.oracle;

import com.opencsv.bean.*;

import java.util.List;

/**
 * Contains the core elements of a pattern: formula as string, formalism, parameters in the formula and descriptions
 */
public class TemporalPatternBase implements Cloneable{
    /**
     * candidate for refactoring
     * csvsep is declared in the same way in several classes
     */
    protected static final String csvsep=";";

    @CsvCustomBindByName(converter = CSVConvertTemporalType.class)
    private TemporalFormalism pattern_TemporalFormalism;
    @CsvBindByName
    private String pattern_Description; //short pattern_Description e.g. spec pattern_Formula name

    @CsvBindByName
    private String pattern_Scope;  // see spec pattern_Formula http://patterns.projects.cs.ksu.edu
    @CsvBindByName
    private String pattern_Class; // see taxonomy http://patterns.projects.cs.ksu.edu
    @CsvBindByName
    private String pattern_Formula;  // e.g. G(b0->Fb1)
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> pattern_Parameters; //b0,b1,b2,bn

    public TemporalPatternBase() {
    }


    public TemporalFormalism getPattern_TemporalFormalism() {
        return pattern_TemporalFormalism;
    }

    public void setPattern_TemporalFormalism(TemporalFormalism pattern_TemporalFormalism) {
        //convert defaults when reading CSV files
        switch (pattern_TemporalFormalism) {
            case CTL:
                this.pattern_TemporalFormalism = TemporalFormalism.CTL_LTSMIN;
                break;
             case LTL:
                 this.pattern_TemporalFormalism = TemporalFormalism.LTL_SPOT;
                break;
            default:
               this.pattern_TemporalFormalism = pattern_TemporalFormalism;
                break;
        }

    }
    @SuppressWarnings("unused")
    public String getPattern_Scope() {
        return pattern_Scope;
    }

    public void setPattern_Scope(String pattern_Scope) {
        this.pattern_Scope = pattern_Scope;
    }
    @SuppressWarnings("unused")
    public String getPattern_Class() {
        return pattern_Class;
    }

    public void setPattern_Class(String pattern_Class) {
        this.pattern_Class = pattern_Class;
    }

    @SuppressWarnings("unused")
    public String getPattern_Description() {
        return pattern_Description;
    }

    public void setPattern_Description(String pattern_Description) {
        this.pattern_Description = pattern_Description;
    }

    public String getPattern_Formula() {
        return pattern_Formula;
    }

    public void setPattern_Formula(String pattern_Formula) {
        this.pattern_Formula = pattern_Formula;
    }

    public List<String> getPattern_Parameters() {
        return pattern_Parameters;
    }

    public void setPattern_Parameters(List<String> pattern_Parameters) {
        this.pattern_Parameters = pattern_Parameters;
    }
    public String getVersion() {
        return "20200104";
    }


    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}