package nl.ou.testar.report.rest;

/**
 * ReportNetworkException
 */
public class ReportNetworkException extends Exception {

    private String uri;

    public String getUri() {
        return uri;
    }

    public ReportNetworkException(String uri, String message) {
        super(message);
        this.uri = uri;
    }
}
