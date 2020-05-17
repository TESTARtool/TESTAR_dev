package nl.ou.testar.temporal.oracle;

import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvRecurse;
import nl.ou.testar.temporal.foundation.LogData;

import java.util.*;

public class TemporalPattern extends TemporalPatternBase {

    @CsvIgnore
    private  static String version = "20200104";
    @CsvRecurse
    private LogData logData;

    public TemporalPattern() {
        super();
        logData = new LogData();
    }

    public void set_comments(List< String > _comments) {
        logData.set_comments(_comments);
    }
    public void addComments ( String  comment) {
        logData.addComments( comment);
    }

    public void set_modifieddate(String _modifieddate){
        logData.set_modifieddate( _modifieddate);
    }

    public void set_log(List < String > _log) {
        logData.set_log(_log);
    }
    public void addLog ( String  log) {
        logData.addLog(log);
    }

    @Override
    public  String getVersion() {
        return version;
    }

    public static TemporalPattern getSamplePattern(){
        TemporalPattern pat = new TemporalPattern(); //new TemporalOracle("notepad","v10","34d23", attrib);

        pat.setPattern_TemporalFormalism(TemporalFormalism.LTL);
        pat.setPattern_Description("p0 precedes p1");
        pat.setPattern_Scope("globally");
        pat.setPattern_Class("precedence");
        pat.setPattern_Formula("!p1 U p0");
        pat.setPattern_Parameters(Arrays.asList("p0", "p1"));
       pat.set_comments(Collections.singletonList("User remarks"));
        pat.addLog("Format version: "+version);
        pat.addLog("This is a sample temporal pattern. Formula,parameters and formalism are the key elements. parameter syntax: p[0-9]+");
        pat.addLog("Column order is not important. Header names are case insensitive but structure is important");

        return   pat;
    }
}