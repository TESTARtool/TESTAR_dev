package org.testar.securityanalysis;

public class SecurityResultDto {
    public SecurityResultDto(String path, String cwe, String result)
    {
        this.path = path;
        this.cwe = cwe;
        this.result = result;
    }

    public String path;
    public String cwe;
    public String result;
}