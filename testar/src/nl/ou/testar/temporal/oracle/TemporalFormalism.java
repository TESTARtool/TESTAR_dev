package nl.ou.testar.temporal.oracle;

import nl.ou.testar.temporal.foundation.PairBean;

public enum TemporalFormalism {
//    LTL,
//    LTL_SPOT,
//    LTL_ITS,
//    LTL_LTSMIN,
//    CTL,
//    CTL_ITS;



    LTL("","","",
                new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("~~unlikely","")),
    LTL_SPOT("","","",
                     new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("~~unlikely","")),
    LTL_ITS("(\""," = 1\")","",
                    new PairBean<>("<>","F"),new PairBean<>("[]","G"),
            //new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely",""),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely","")),
    LTL_LTSMIN("("," == \"1\")","",
                       new PairBean<>("F","<>"),new PairBean<>("G","[]"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely","")),
    CTL_LTSMIN("("," == \"1\")","",
                       new PairBean<>("F","<>"),new PairBean<>("G","[]"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely","")),
    CTL("("," = \"1\")",";",
                new PairBean<>("<>","F"),new PairBean<>("[]","G"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            //new PairBean<>("false","(\"0\" = \"1\")")),
            new PairBean<>("~~unlikely","")),
    CTL_ITS("("," = \"1\")",";",
                    new PairBean<>("<>","F"),new PairBean<>("[]","G"),
            new PairBean<>("&","&&"),new PairBean<>("|","||"),
            new PairBean<>("~~unlikely",""));
    //futures: CTLSTAR,  MUCALC }

    public final String ap_prepend;
    public final String ap_append;
    public final String line_append;
    public final PairBean<String, String> finally_replace;
    public final PairBean<String, String> globally_replace;
    public final PairBean<String, String> and_replace;
    public final PairBean<String, String> or_replace;
    public final PairBean<String, String> false_replace;

    //futures: ,CTLSTAR, LTLTRACEPROM, LTLTRACEQUARRY , MUCALC }

    TemporalFormalism(String ap_prepend , String ap_append, String line_append,
                              PairBean<String, String> finally_replace,
                              PairBean<String, String> globally_replace,
                              PairBean<String, String> and_replace,
                              PairBean<String, String> or_replace,
                              PairBean<String, String> false_replace
    ) {

        this.ap_prepend = ap_prepend;
        this.ap_append = ap_append;
        this.line_append = line_append;
        this.finally_replace = finally_replace;
        this.globally_replace = globally_replace;
        this.and_replace=and_replace;
        this.or_replace=or_replace;
        this.false_replace=false_replace;
    }

}