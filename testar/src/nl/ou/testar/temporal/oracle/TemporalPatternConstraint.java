package nl.ou.testar.temporal.oracle;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvRecurse;
import nl.ou.testar.temporal.foundation.TemporalMeta;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;

public class TemporalPatternConstraint {
    @CsvBindByName()
    private String pattern_Formula;  // e.g. G(b0->Fb1)
    // e.g.Constraintset_1_p0, Constraintset_9_p23
    @CsvBindAndJoinByName(column = "(?i)CONSTRAINTSET_[1-9][0-9]*_P[0-9]+", elementType = String.class)
    private MultiValuedMap<String,String> constraintMapper;


    @CsvIgnore
    private TreeMap<Integer, Map<String,String>> constraintSets;


    @CsvIgnore
    private  static String version = "20200104";
    @CsvRecurse
    private TemporalMeta metaData;

    public TemporalPatternConstraint() {
        metaData = new TemporalMeta();
        constraintSets = new TreeMap<>();
        constraintMapper = new HashSetValuedHashMap<>() ; //values are 'unique'
    }

    public String getPattern_Formula() {
        return pattern_Formula;
    }

    private  void setPattern_Formula(String pattern_Formula) {
        this.pattern_Formula = pattern_Formula;
    }

    private  MultiValuedMap<String, String> getConstraintMapper() {
        return constraintMapper;
    }

    public void setConstraintMapper(MultiValuedMap<String, String> constraintMapper) {
        this.constraintMapper = constraintMapper;
        for (Map.Entry<String,String> entrie:constraintMapper.entries()
             ) {
            //Constraintset_9_p23
            int setnumber;
            String paramnumber;
            String constraint;
            String[] splits = entrie.getKey().split("_", 3);
            setnumber= Integer.parseInt(splits[1]);
            paramnumber=splits[2];
            constraint=entrie.getValue();
            Map<String, String> cset;
            if (constraintSets.containsKey(setnumber)){
             cset= constraintSets.get(setnumber);
            }
            else{
                cset = new HashMap<>();
            }
            cset.put(paramnumber,constraint);
            constraintSets.put(setnumber,cset);
        }

    }
    private void updateConstraintmapper(){
        String basekey;
        for (Integer setIndex:constraintSets.keySet()
             ) {
            basekey="Constraintset_"+setIndex+"_";
            String constraint;
            for (Map.Entry<String,String> entrie:constraintSets.get(setIndex).entrySet()
                 ) {
                String finalkey;
                finalkey=basekey+entrie.getKey();
                constraint=entrie.getValue();
              //  if(!constraintMapper.containsMapping(finalkey,constraint)) {//is a list :-(
                    constraintMapper.put(finalkey, constraint);
               // }
            }
        }
    }

    public TreeMap<Integer, Map<String, String>> getConstraintSets() {
        return constraintSets;
    }
    public Map<String, String> getConstraintSetByIndex(int index) {
        return constraintSets.get(index);
    }

    private void setConstraintSets(TreeMap<Integer, Map<String, String>> constraintSets) {
        this.constraintSets = constraintSets;
        updateConstraintmapper();
    }
    private void addConstraintSet(int index, Map<String, String> constraintSet) {
        this.constraintSets.put(index,constraintSet);
        updateConstraintmapper();
    }
    public void set_comments(List< String > _comments) {
        metaData.set_comments(_comments);
    }
    public void addComments ( String  comment) {
        metaData.addComments( comment);
    }

    public void set_modifieddate(String _modifieddate){
        metaData.set_modifieddate( _modifieddate);
    }

    public void set_log(List < String > _log) {
        metaData.set_log(_log);
    }
    public void addLog ( String  log) {
        metaData.addLog(log);
    }

    public  String getVersion() {
        return version;
    }
    public static List<TemporalPatternConstraint> getSampleConstraints()  {
       List<TemporalPatternConstraint> constraints =new ArrayList<>();
        TemporalPatternConstraint c;
        c=new TemporalPatternConstraint();
        c.pattern_Formula ="!p1 U p0";
        Map<String,String> constraintset1 = new HashMap<>();
        constraintset1.put("p1","\\\\*Untitled - Notepad_.*UIAWindow.*_Blocked__"); //double backslash (java also consumes one!)
        constraintset1.put("p0","UIAButton.*Desc_textmatch_(?i:CLOSE)__");
        c.addConstraintSet(1,constraintset1);

        Map<String,String> constraintset2= new HashMap<>();
        constraintset2.put("p1",".*UIAWindow.*");
        constraintset2.put("p0",".*UIAButton.*");
        c.addConstraintSet(2,constraintset2);

        Map<String,String> constraintset3= new HashMap<>();
        constraintset3.put("p0",".*UIAButton.*");
        c.addConstraintSet(3,constraintset3);
        c.addComments("Format Version: "+version);
        c.addComments("Sample of a constraint on a temporal pattern. constraint key and value must be aligned with the APEncodedModel.json");
        c.addComments("Column order is not important. Header names are case insensitive but structure requirements apply");
        c.addComments("ConstraintSet syntax: 'ConstraintSet_[1-9][0-9]*_P[0-9]' , where 'P' must match a parameter in the formula.");
        constraints.add(c);
        return constraints;
    }
}


