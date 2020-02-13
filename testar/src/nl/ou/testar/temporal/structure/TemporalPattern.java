package nl.ou.testar.temporal.structure;

import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvRecurse;
import nl.ou.testar.temporal.util.TemporalType;

import java.time.LocalDateTime;
import java.util.*;

public class TemporalPattern extends TemporalPatternBase {

    @CsvIgnore
    private  static String version = "20200104";
    @CsvRecurse
    private TemporalMeta metaData;

    public TemporalPattern() {
        super();
        metaData = new TemporalMeta();
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

    @Override
    public  String getVersion() {
        return version;
    }

    public static TemporalPattern getSamplePattern(){
        TemporalPattern pat = new TemporalPattern(); //new TemporalOracle("notepad","v10","34d23", attrib);

        pat.setPattern_TemporalFormalism(TemporalType.LTL);
        pat.setPattern_Description("p0 precedes p1");
        pat.setPattern_Scope("globally");
        pat.setPattern_Class("precedence");
        pat.setPattern_Formula("!p1 U p0");
        pat.setPattern_Parameters(Arrays.asList("p0", "p1"));
        pat.addComments("Format version: "+version);
        pat.addComments("This is a sample temporal pattern. Formula,parameters and formalism are the key elements. parameter syntax: p[0-9]+");
        pat.addComments("AVOID using literals 'X,F,G,U,W,R,M' as  substitutions, as they are used in LTL syntax");
        pat.addComments("Column order is not important. Header names are case insensitive but structure is important");

        return   pat;
    }
}