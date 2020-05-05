package nl.ou.testar.temporal.proposition;

public enum PropositionConstants {
//utf 8 encoding required for non-ascii chars
// difficult to manually maintain??
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


    PropositionConstants(String subKeySeparator, String outputPrefix, String terminalProposition) {
        this.subKeySeparator = subKeySeparator;
        this.outputPrefix=outputPrefix;
        this.terminalProposition = terminalProposition;
    }


}

