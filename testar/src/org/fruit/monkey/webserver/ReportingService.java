package org.fruit.monkey.webserver;

import java.io.IOException;

public interface  ReportingService {
    ReportingServiceDelegate getDelegate();
    void setDelegate(ReportingServiceDelegate delegate);
    void start() throws IOException;
}
