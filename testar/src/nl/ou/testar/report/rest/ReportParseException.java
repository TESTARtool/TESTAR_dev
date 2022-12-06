package nl.ou.testar.report.rest;

/**
 * ReportParseException
 */
public class ReportParseException extends Exception {

    private String source;

    public String getSource() {
        return source;
    }

    public ReportParseException(String message, String source) {
        super(message);
        this.source = source;
    }
}
