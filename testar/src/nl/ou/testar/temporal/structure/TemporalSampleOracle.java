package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import nl.ou.testar.temporal.util.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.*;

public class TemporalSampleOracle extends TemporalOracle{


    // 2 explicit fiekds to export a sample.
    // users can can more fields/columns in the csv files according to pattern "(?i)pattern_Substitutions[0-9]+"
    @CsvBindByName
    private String pattern_Substitutions0;
    @CsvBindByName
    private String pattern_Substitutions1;



    @CsvBindByName
    private String pattern_Substitutions2;



    public TemporalSampleOracle() {
        super();
        this.set_formatVersion("20191230");
    }
    public String getPattern_Substitutions0() {
        return pattern_Substitutions0;
    }

    public String getPattern_Substitutions1() {
        return pattern_Substitutions1;
    }

    public String getPattern_Substitutions2() { return pattern_Substitutions2;  }



public static TemporalSampleOracle getSampleLTLOracle(){
    TemporalSampleOracle to = new TemporalSampleOracle(); //new TemporalOracle("notepad","v10","34d23", attrib);
    Set<String> attrib = new HashSet<>();
    attrib.add("Role");
    attrib.add("Path");
    attrib.add("Title");
    //attrib.add("Enabled");
    to.setApplicationName("my app");
    to.setApplicationVersion("my version");
    to.setApplication_ModelIdentifier("my modelidentifier");
    to.setApplication_AbstractionAttributes(attrib);
    to.setPattern_TemporalFormalism(TemporalType.LTL);
    to.setOracle_validationstatus(ValStatus.ACCEPTED);
    to.setPattern_Description("(a and c) precedes b");
    to.setPattern_Scope("globally");
    to.setPattern_Class("precedence");
    to.setPattern_Formula("!b W (a & c)");
    to.setPattern_Parameters(Arrays.asList("a", "b","c"));
    //List<String> mappie = new HashMap<String,String>() {{put("a", "UIButton_OK");put("b", "UIWindow_Title_main_exists");}};
   // MultiValueMap mappie = new ArrayList<String>(){{add("UIButton_OK");add("UIWindow_Title_main_exists");}};

    //MultiValuedMap<Integer, String> mappie = new MultiValuedHashMap<Integer, String>(){{put(0,"UIButton_OK");put(1,"UIWindow_Title_main_exists");}};
    MultiValuedMap<String, String> mappie = new ArrayListValuedHashMap<>();
    mappie.put("0","UIButton_OK");
    mappie.put("1","UIWindow_Title_main_exists");
    to.setPattern_Substitutions(mappie);
    //@todo get rid of the 3 above codeline?
    to.pattern_Substitutions0="UIButton"+APEncodingSeparator.CUSTOM.symbol+"Title_Match_OK";
    to.pattern_Substitutions1="UIWindow_Title_main"+APEncodingSeparator.CUSTOM.symbol+"exists";
    to.pattern_Substitutions2="UIWindow_Title_closure"+APEncodingSeparator.CUSTOM.symbol+"exists";

    List<String> comments= new ArrayList<String>();
    comments.add("this is a sample oracle. for valid substitutions, please see the APSelectorManager.JSON");
    comments.add("avoid using 'X,F,G,U,W,R,M' as parameters, as they are used in LTL syntax");
    comments.add("the separator for substitutions is hardcoded set (in regex syntx)  ';=+;'  so ;===========; is considered a valid separator");
    //comments.add("Excel does not quote common text fields during export. Try MS Access as alternative");
    to.set_comments(comments);
    return to;
}

}
