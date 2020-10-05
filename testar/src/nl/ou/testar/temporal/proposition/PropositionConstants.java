package nl.ou.testar.temporal.proposition;

public enum PropositionConstants {
//utf 8 encoding required for non-ascii chars. // difficult to maintain manually and also with Excel !?

//    SECTIONSIGN("§§"),
//    RIGHTWARDSARROW ("→→"),
//    DOUBLEDAGGER ("‡‡"),
//    BROKENBAR ("¦¦"),
//    CUSTOM ("_||_");

SETTING("_||_","ap","dead");

   // public final String symbol;
    public final String subKeySeparator; //used in JSON model and in CSV formulas (HCI)
    public final String outputPrefix;//used in model and formulas for model checkers
    public final String terminalProposition; //used in model and formulas both HCi and model checkers


    /**
     * @param subKeySeparator unique string to separate abstration parts. set hardcoded to "_||_",
     * @param outputPrefix leading string added to the index of a proposition. set hardcoded to  "ap"
     * @param terminalProposition string indication the special terminal proposition.  set hardcoded to "dead"
     */
    PropositionConstants(String subKeySeparator, String outputPrefix, String terminalProposition) {
        this.subKeySeparator = subKeySeparator;
        this.outputPrefix=outputPrefix;
        this.terminalProposition = terminalProposition;
    }


}

