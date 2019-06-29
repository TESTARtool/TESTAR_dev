package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.CSVConvertTemporalType;
import nl.ou.testar.temporal.util.TemporalType;

import java.util.List;

public class TemporalPattern extends TemporalBean implements Cloneable{



    @CsvCustomBindByName(converter = CSVConvertTemporalType.class)
    private TemporalType temporalFormalism;
    @CsvBindByName
    private String description; //short description e.g. spec pattern name

    @CsvBindByName
    private String scope;  // see spec pattern http://patterns.projects.cs.ksu.edu
    @CsvBindByName
    private String patternclass; // see taxonomy http://patterns.projects.cs.ksu.edu
    @CsvBindByName
    private String pattern;  // e.g. G(b0->Fb1)
    @CsvBindAndSplitByName(elementType = String.class, splitOn = csvsep+"+", writeDelimiter = csvsep)//, collectionType = LinkedList.class)
    private List<String> parameters; //b0,b1,b2,bn

    @CsvBindByName
    private String formatVersion="20190629";


    public TemporalPattern() {
    super();
    }

    public TemporalType getTemporalFormalism() {
        return temporalFormalism;
    }

    public void setTemporalFormalism(TemporalType temporalFormalism) {
        this.temporalFormalism = temporalFormalism;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPatternclass() {
        return patternclass;
    }

    public void setPatternclass(String patternclass) {
        this.patternclass = patternclass;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public int getParamcount() {
        return parameters.size();
    }



    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }


    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

}