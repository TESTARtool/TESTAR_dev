package org.fruit.monkey.btrace;

public class BtraceApiException extends RuntimeException {

    private BtraceApiException(String message) {
        super(message);
    }

    private BtraceApiException(String message, Throwable e) {
        super(message, e);
    }

    public static BtraceApiException btraceApiCallResultedInError(Throwable e) {
        return new BtraceApiException("Sending request to Btrace API service resulted in error", e);
    }
}
