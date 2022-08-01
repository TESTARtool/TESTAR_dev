package org.fruit.monkey.webserver;

import org.fruit.monkey.TestarServiceException;

import java.io.IOException;

public interface  ReportingService {
    ReportingServiceDelegate getDelegate();
    void setDelegate(ReportingServiceDelegate delegate);
    void start() throws IOException, TestarServiceException;
}
