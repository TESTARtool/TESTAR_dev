package nl.ou.testar.temporal.util;

public enum TemporalSubType {
    LTL("","","",new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely","")),
    LTL_SPOT("","","",new PairBean<>("~~unlikely",""),new PairBean<>("~~unlikely","")),
    LTL_ITS("(\""," = 1\")","",new PairBean<>("<>","F"),new PairBean<>("[]","G")),
    LTL_LTSMIN("("," == \"1\")","",new PairBean<>("F","<>"),new PairBean<>("G","[]")),
    CTL("("," = \"1\")",";",new PairBean<>("<>","F"),new PairBean<>("[]","G")),
    CTL_ITS("("," = \"1\")",";",new PairBean<>("<>","F"),new PairBean<>("[]","G"));
    //futures: ,CTLSTAR, LTLTRACEPROM, LTLTRACEQUARRY , MUCALC }

    public final String ap_prepend;
    public final String ap_append;
    public final String line_append;
    public final PairBean<String, String> finally_replace;
    public final PairBean<String, String> globally_replace;

    private TemporalSubType(String ap_prepend , String ap_append, String line_append,
                            PairBean<String, String> finally_replace, PairBean<String, String> globally_replace) {

        this.ap_prepend = ap_prepend;
        this.ap_append = ap_append;
        this.line_append = line_append;
        this.finally_replace = finally_replace;
        this.globally_replace = globally_replace;
    }
}