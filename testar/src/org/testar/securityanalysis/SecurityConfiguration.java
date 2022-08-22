package org.testar.securityanalysis;

import java.util.Arrays;
import java.util.List;

public class SecurityConfiguration {
    public static int maxBenchmarkExecutionTimes = 2;
    public static String resultWriterOutput = "JSON";

    public List<String> oracles = Arrays.asList("HeaderAnalysisSecurityOracle", "SqlInjectionSecurityOracle");

    public String loginUrl = "http://localhost:41948/Account/Login";
    public String usernameField = "username";
    public String passwordField = "password";
    public String submitButton = "1, 0, 2, 0, 0, 3, 0";
    public String logoutButton = "logout";

    public String username = "Admin";
    public String password = "123";

    public long tokenInvalidationWaitTime = 60000; /** One minute **/
}
