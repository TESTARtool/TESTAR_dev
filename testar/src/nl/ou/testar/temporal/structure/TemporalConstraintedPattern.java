package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.CSVConvertMap;
import nl.ou.testar.temporal.util.TemporalType;

import java.time.LocalDateTime;
import java.util.*;

public class TemporalConstraintedPattern extends TemporalPattern{


    @CsvCustomBindByName( converter = CSVConvertMap.class)
    private Map<String,String> parameter_ConcreteConstraints; //a:Button_OK_IsWindowsModel b: Window_Main_Exists

    @CsvCustomBindByName( converter = CSVConvertMap.class)
    private Map<String,String> parameter_WidgetRoleConstraints; //a:UIAButton b: UIAWindow



    @CsvCustomBindByName( converter = CSVConvertMap.class)
    private Map<String,String> parameter_AttributeConstraints; //a:<*>_IsWindowsModal b:<*>zIndex



    public TemporalConstraintedPattern() {
        super();
        this.set_formatVersion("20190629");

    }



    public Map<String, String> getParameter_ConcreteConstraints() {
        return parameter_ConcreteConstraints;
    }

    public void setParameter_ConcreteConstraints(Map<String, String> parameter_ConcreteConstraints) {
        this.parameter_ConcreteConstraints = parameter_ConcreteConstraints;
    }

    public Map<String, String> getParameter_WidgetRoleConstraints() {
        return parameter_WidgetRoleConstraints;
    }

    public void setParameter_WidgetRoleConstraints(Map<String, String> parameter_WidgetRoleConstraints) {
        this.parameter_WidgetRoleConstraints = parameter_WidgetRoleConstraints;
    }

    public Map<String, String> getParameter_AttributeConstraints() {
        return parameter_AttributeConstraints;
    }

    public void setParameter_AttributeConstraints(Map<String, String> parameter_AttributeConstraints) {
        this.parameter_AttributeConstraints = parameter_AttributeConstraints;
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
        pat.setParameter_WidgetRoleConstraints(widgetConstraint);
        pat.setParameter_AttributeConstraints(attributeConstraint);
        pat.setParameter_ConcreteConstraints(concreteConstraint);
        pat.setPattern_TemporalFormalism(TemporalType.LTL);
        pat.setPattern_Description("a precedes b");
        pat.setPattern_Scope("globally");
        pat.setPattern_Class("precedence");
        pat.setPattern_Formula("!b U a");
        List<String> comments= new ArrayList<String>();
        List<String> log= new ArrayList<String>();
        comments.add("constraints are optional and used to reduce the number of potential oracles during mining.");
        comments.add("concreteConstraints supersede widget or attribute constraints for the same parameter");


        LocalDateTime localDateTime = LocalDateTime.now();
        pat.set_modifieddate(localDateTime.toString());
        log.add("temporal pattern generated");
        pat.set_comments(comments);
        pat.setApplication_log(log);
        pat.setPattern_Parameters(Arrays.asList("a", "b"));

      return   pat;
    }
}
