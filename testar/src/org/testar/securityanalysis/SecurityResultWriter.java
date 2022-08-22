package org.testar.securityanalysis;

public interface SecurityResultWriter {
    void WriteVisit(String url);
    void WriteResult(String url, String cwe, String description);
}
