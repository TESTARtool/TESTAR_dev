package org.testar.securityanalysis;

import java.util.Map;

public class SecurityResult {
    public SecurityResult(String path, String cwe, String result)
    {
        this.path = path;
        this.cwe = cwe;
        this.result = result;
    }

    public String path;
    public String cwe;
    public String result;
}