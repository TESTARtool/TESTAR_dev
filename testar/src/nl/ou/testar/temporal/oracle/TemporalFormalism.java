package nl.ou.testar.temporal.oracle;

import nl.ou.testar.temporal.util.foundation.PairBean;

/**
 * Model checker dependant configurations to be applied for each formula/property or model file
 */
public enum TemporalFormalism {

    LTL("","","","",new PairBean<>(),new PairBean<>(), //equals LTL_SPOT
            new PairBean<>(),new PairBean<>(), new PairBean<>(),true,"",false),
    CTL("","","","",new PairBean<>(),new PairBean<>(), //equals CTL_LTSMIN
            new PairBean<>(),new PairBean<>(), new PairBean<>(),true,"",true),


    LTL_SPOT("","","","",
                     new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("~~unlikely",""),false,"txt",false),
        //spot adds untractable edges when model is disconnect: observed on a 'singular'  initial state
    LTL_ITS("(\""," = 1\")","","",
                    new PairBean<>("<>","F"),new PairBean<>("[]","G"),
            //new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely",""),false,"txt",false),
    LTL_LTSMIN("("," == \"1\")","","",
                       new PairBean<>("F","<>"),new PairBean<>("G","[]"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely",""),false,"etf",true),
    CTL_LTSMIN("("," == \"1\")","","",
                       new PairBean<>("F","<>"),new PairBean<>("G","[]"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely",""),true,"etf",true),

    CTL_ITS("("," = \"1\")",";","",
                    new PairBean<>("<>","F"),new PairBean<>("[]","G"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely",""),false,"txt",false),
    CTL_GAL("("," == 1)",";","property dummypropertyname [ctl] : ",
                    new PairBean<>("<>","F"),new PairBean<>("[]","G"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely",""),false,"gal",false);
    //'dummypropertyname' :static for all properties, apparently GAL syntax allows this to be a dummy, we don't need it.

    //futures: CTLSTAR,  MUCALC }

    public final String ap_prepend;
    public final String ap_append;
    public final String line_append;
    public final String line_prepend;
    public final PairBean<String, String> finally_replace;
    public final PairBean<String, String> globally_replace;
    public final PairBean<String, String> and_replace;
    public final PairBean<String, String> or_replace;
    public final PairBean<String, String> false_replace;
    public final boolean supportsMultiInitialStates;
    public final String fileExtension; //LTSMIN determines the type based on the extension
    public final boolean parenthesesNextOperator;


    /**
     * @param ap_prepend if used, this is usually  an opening bracket
     * @param ap_append if used, this is usually  a closing bracket
     * @param line_append fixed text string at every formula line
     * @param line_prepend fixed text string at every formula line
     * @param finally_replace replacement string for 'F', when used it is usually '<>'
     * @param globally_replace replacement string for 'G', when used it is usually '[]'
     * @param and_replace replacement string for '&', when used it is usually '&&'
     * @param or_replace replacement string for '|', when used it is usually '||'
     * @param false_replace replacement string for 'FALSE, when used it is usually '0'
     * @param supportsMultiInitialStates boolean to indicate whether an artificial initial state is needed
     * @param fileExtension fixed model-file extension as mandated by the model checker
     * @param parenthesesNextOperator boolean:  the Next operator ('X') need to be parenthesised for CTL formulas.
     */
    TemporalFormalism(String ap_prepend , String ap_append,
                      String line_append,String line_prepend,
                      PairBean<String, String> finally_replace,
                      PairBean<String, String> globally_replace,
                      PairBean<String, String> and_replace,
                      PairBean<String, String> or_replace,
                      PairBean<String, String> false_replace,
                      boolean supportsMultiInitialStates,
                      String fileExtension,
                      boolean parenthesesNextOperator
    ) {

        this.ap_prepend = ap_prepend;
        this.ap_append = ap_append;
        this.line_append = line_append;
        this.line_prepend=line_prepend;
        this.finally_replace = finally_replace;
        this.globally_replace = globally_replace;
        this.and_replace=and_replace;
        this.or_replace=or_replace;
        this.false_replace=false_replace;
        this.supportsMultiInitialStates=supportsMultiInitialStates;
        this.fileExtension=fileExtension;
        this.parenthesesNextOperator=parenthesesNextOperator;
    }

}