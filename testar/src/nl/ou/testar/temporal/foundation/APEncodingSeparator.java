package nl.ou.testar.temporal.foundation;

public enum APEncodingSeparator {
    //utf 8 encoding required for non-ascii chars
    // list of symbol characters in the title of a widget.
    SECTIONSIGN("§§"),
    RIGHTWARDSARROW ("→→"),
    DOUBLEDAGGER ("‡‡"),
    BROKENBAR ("¦¦"),
    CUSTOM ("_||_");


    public final String symbol;

    APEncodingSeparator(String unlikely) {
        this.symbol = unlikely;
    }
}

