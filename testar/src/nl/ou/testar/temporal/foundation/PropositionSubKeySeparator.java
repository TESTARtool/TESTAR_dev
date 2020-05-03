package nl.ou.testar.temporal.foundation;

public enum PropositionSubKeySeparator {
    //utf 8 encoding required for non-ascii chars
    // list of symbol characters in the title of a widget.
    SECTIONSIGN("§§"),
    RIGHTWARDSARROW ("→→"),
    DOUBLEDAGGER ("‡‡"),
    BROKENBAR ("¦¦"),
    CUSTOM ("_||_");


    public final String symbol;

    PropositionSubKeySeparator(String unlikely) {
        this.symbol = unlikely;
    }
}

