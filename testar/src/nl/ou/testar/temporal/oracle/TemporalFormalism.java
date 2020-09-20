package nl.ou.testar.temporal.oracle;

import nl.ou.testar.temporal.foundation.PairBean;

public enum TemporalFormalism {
//    LTL,    LTL_SPOT,
//    LTL_ITS,
//    LTL_LTSMIN,
//    CTL,  CTL_ITS;
//    CTL_LTSMIN,
//    CTL_GAL



    LTL("","","","",new PairBean<>(),new PairBean<>(),
            new PairBean<>(),new PairBean<>(), new PairBean<>(),true,"",false),
    CTL("","","","",new PairBean<>(),new PairBean<>(),
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
            new PairBean<>("~~unlikely",""),false,"etf",false),
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