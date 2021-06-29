package org.fruit.monkey.webserver;

import java.io.IOException;

public interface ReportingService {
    ReportingServiceDelegate getDelegate();
    void setDelegate(ReportingServiceDelegate delegate);
    void start(int port, String dbHostname, int dbPort, String dbName, String dbUsername, String dbPassword) throws IOException;
}
