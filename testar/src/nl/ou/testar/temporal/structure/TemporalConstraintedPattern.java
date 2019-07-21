package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.CSVConvertMap;
import nl.ou.testar.temporal.util.TemporalType;

import java.time.LocalDateTime;
import java.util.*;

public class TemporalConstraintedPattern extends TemporalPattern{


    @CsvCustomBindByName( converter = CSVConvertMap.class)
    private Map<String,String> concreteParameterConstraints; //a:Button_OK_IsWindowsModel b: Window_Main_Exists

    @CsvCustomBindByName( converter = CSVConvertMap.class)
    private Map<String,String> widgetRoleParameterConstraints; //a:UIAButton b: UIAWindow



    @CsvCustomBindByName( converter = CSVConvertMap.class)
    private Map<String,String> attributeParameterConstraints; //a:<*>_IsWindowsModal b:<*>zIndex


    @CsvBindByName
    private String formatVersion="20190629";


    public TemporalConstraintedPattern() {
        super();

    }



    public Map<String, String> getConcreteParameterConstraints() {
        return concreteParameterConstraints;
    }

    public void setConcreteParameterConstraints(Map<String, String> concreteParameterConstraints) {
        this.concreteParameterConstraints = concreteParameterConstraints;
    }

    public Map<String, String> getWidgetRoleParameterConstraints() {
        return widgetRoleParameterConstraints;
    }

    public void setWidgetRoleParameterConstraints(Map<String, String> widgetRoleParameterConstraints) {
        this.widgetRoleParameterConstraints = widgetRoleParameterConstraints;
    }

    public Map<String, String> getAttributeParameterConstraints() {
        return attributeParameterConstraints;
    }

    public void setAttributeParameterConstraints(Map<String, String> attributeParameterConstraints) {
        this.attributeParameterConstraints = attributeParameterConstraints;
    }
    public Object clone() throws            CloneNotSupportedException
    {
        return super.clone();
    }

    public static TemporalConstraintedPattern getSamplePattern(){
        TemporalConstraintedPattern pat = new TemporalConstraintedPattern(); //new TemporalOracle("notepad","v10","34d23", attrib);
       Map<String,String> widgetConstraint = new HashMap<>();
        widgetConstraint.put("a","UIAButton");
        Map<String,String> attributeConstraint = new HashMap<>();
        attributeConstraint.put("b","ZIndex_value_lt_10");
        Map<String,String> concreteConstraint = new HashMap<>();
        concreteConstraint.put("b","UIAWindow_SAVE_[0,1]_ZIndex_value_lt_10");
        pat.setWidgetRoleParameterConstraints(widgetConstraint);
        pat.setAttributeParameterConstraints(attributeConstraint);
        pat.setConcreteParameterConstraints(concreteConstraint);
        pat.setTemporalFormalism(TemporalType.LTL);
        pat.setDescription("a precedes b");
        pat.setScope("globally");
        pat.setPatternclass("precedence");
        pat.setPattern("!b U a");
        List<String> comments= new ArrayList<String>();
        List<String> log= new ArrayList<String>();
        comments.add("constraints are optional and used to reduce the number of potential oracles during mining.");
        comments.add("concreteConstraints supersede widget or attribute constraints for the same parameter");


        LocalDateTime localDateTime = LocalDateTime.now();
pat.setModifieddate(localDateTime.toString());
        log.add("temporal pattern generated");
        pat.setComments(comments);
        pat.setLog(log);
        pat.setParameters(Arrays.asList("a", "b"));

      return   pat;
    }
}
