package org.fruit.monkey.weights;

import org.fruit.monkey.jacoco.JacocoReportParser;
import org.fruit.monkey.jacoco.MethodCoverage;
import org.fruit.monkey.javaparser.JavaProjectParser;
import org.fruit.monkey.javaparser.JavaUnit;
import org.fruit.monkey.javaparser.MethodDeclaration;
import org.fruit.monkey.sonarqube.api.SQIssue;
import org.fruit.monkey.sonarqube.api.SonarqubeApiClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StaticAnalysisResolverTest {

    @Mock
    private SonarqubeApiClient sonarqubeApiClient;

    @Mock
    private JavaProjectParser javaProjectParser;

    @Mock
    private JacocoReportParser jacocoReportParser;

    @Test
    public void shouldResolveMethodEntries() {
        when(jacocoReportParser.parseReport()).thenReturn(List.of());
        when(sonarqubeApiClient.getProjectComponentKey()).thenReturn("");
        when(sonarqubeApiClient.getDetectedIssues(anyString())).thenReturn(List.of());
        when(javaProjectParser.parseJavaUnits()).thenReturn(expectedJavaUnits());

        var resolver = new StaticAnalysisResolver(sonarqubeApiClient, jacocoReportParser, javaProjectParser);

        HashMap<String, List<AnalysedMethodEntry>> actualMethodEntries = resolver.resolveMethodEntries();

        assertEquals(6, actualMethodEntries.size());
        var expectedMethodEntries = expectedEntries();
        assertExpectedVsActualEntries(expectedMethodEntries, actualMethodEntries);
    }

    @Test
    public void shouldResolveMethodEntriesWithCoverage() {
        when(jacocoReportParser.parseReport()).thenReturn(expectedCoverage());
        when(sonarqubeApiClient.getProjectComponentKey()).thenReturn("");
        when(sonarqubeApiClient.getDetectedIssues(anyString())).thenReturn(List.of());
        when(javaProjectParser.parseJavaUnits()).thenReturn(expectedJavaUnits());

        var resolver = new StaticAnalysisResolver(sonarqubeApiClient, jacocoReportParser, javaProjectParser);

        HashMap<String, List<AnalysedMethodEntry>> actualMethodEntries = resolver.resolveMethodEntries();

        assertEquals(6, actualMethodEntries.size());
        var expectedMethodEntries = expectedEntriesWithCoverage();
        assertExpectedVsActualEntries(expectedMethodEntries, actualMethodEntries);
    }

    @Test
    public void shouldResolveMethodEntriesWithCoverageAndIssues() {
        when(jacocoReportParser.parseReport()).thenReturn(expectedCoverage());
        when(sonarqubeApiClient.getProjectComponentKey()).thenReturn("");
        when(sonarqubeApiClient.getDetectedIssues(anyString())).thenReturn(expectedSQIssues());
        when(javaProjectParser.parseJavaUnits()).thenReturn(expectedJavaUnits());

        var resolver = new StaticAnalysisResolver(sonarqubeApiClient, jacocoReportParser, javaProjectParser);

        HashMap<String, List<AnalysedMethodEntry>> actualMethodEntries = resolver.resolveMethodEntries();

        assertEquals(6, actualMethodEntries.size());
        var expectedMethodEntries = expectedEntriesWithCoverageAndIssues();
        assertExpectedVsActualEntries(expectedMethodEntries, actualMethodEntries);
    }

    private void assertExpectedVsActualEntries(Map<String, List<AnalysedMethodEntry>> expectedMethodEntries,
                                               Map<String, List<AnalysedMethodEntry>> actualMethodEntries) {
        expectedMethodEntries.keySet()
                             .forEach(key -> {
                                 var expectedEntries = expectedMethodEntries.get(key);
                                 var actualEntries = actualMethodEntries.get(key);
                                 expectedEntries.forEach(expectedEntry -> {
                                     var equalMethodEntry = actualEntries.stream().filter(actualEntry ->
                                                                                                  Objects.equals(actualEntry.getId(), expectedEntry.getId()) &&
                                                                                                          Objects.equals(actualEntry.getClassName(), expectedEntry.getClassName()) &&
                                                                                                          Objects.equals(actualEntry.getMethodName(), expectedEntry.getMethodName()) &&
                                                                                                          Objects.equals(actualEntry.getCoverage(), expectedEntry.getCoverage()) &&
                                                                                                          actualEntry.getIssues().containsAll(expectedEntry.getIssues()) &&
                                                                                                          actualEntry.getParameters().containsAll(expectedEntry.getParameters()))
                                                                         .findFirst();
                                     assertTrue(equalMethodEntry.isPresent());
                                 });
                             });
    }

    private Map<String, List<AnalysedMethodEntry>> expectedEntries() {
        return Map.of("com.example.orderline.OrderLine",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "<init>", List.of("String"), BigDecimal.ZERO, List.of()),
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "getProductNumber", List.of(), BigDecimal.ZERO, List.of()),
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "setProductNumber", List.of("String"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.OrderManager",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager", "<init>", List.of(), BigDecimal.ZERO, List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager", "submitOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.OrderManager.OrderFactoryImpl",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager.OrderFactoryImpl", "createOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.OrderManager.Bill",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "<init>", List.of("String", "BigDecimal"), BigDecimal.ZERO, List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "com.example.monetary.BigDecimal"), BigDecimal.ZERO, List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("BigDecimal"), BigDecimal.ZERO, List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.OrderFactory",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderFactory", "createOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.Order",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.Order", "<init>", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ));
    }

    private Map<String, List<AnalysedMethodEntry>> expectedEntriesWithCoverage() {
        return Map.of("com.example.orderline.OrderLine",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "<init>", List.of("String"), BigDecimal.valueOf(4).divide(BigDecimal.valueOf(6), MathContext.DECIMAL64), List.of()),
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "getProductNumber", List.of(), BigDecimal.valueOf(0.25), List.of()),
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "setProductNumber", List.of("String"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.OrderManager",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager", "<init>", List.of(), BigDecimal.ONE, List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager", "submitOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.valueOf(0.5), List.of())
                      ),
                      "com.example.OrderManager.OrderFactoryImpl",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager.OrderFactoryImpl", "createOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.valueOf(0.75), List.of())
                      ),
                      "com.example.OrderManager.Bill",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "<init>", List.of("String", "BigDecimal"), BigDecimal.valueOf(0.8), List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "com.example.monetary.BigDecimal"), BigDecimal.valueOf(0.5), List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("BigDecimal"), BigDecimal.valueOf(0.5), List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "BigDecimal"), BigDecimal.valueOf(0.5), List.of())
                      ),
                      "com.example.OrderFactory",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderFactory", "createOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.Order",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.Order", "<init>", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ));
    }

    private List<MethodCoverage> expectedCoverage() {
        return List.of(
                new MethodCoverage("com.example.orderline.OrderLine", "<init>", 8, 4, 2),
                new MethodCoverage("com.example.orderline.OrderLine", "getProductNumber", 20, 5, 15),
                new MethodCoverage("com.example.orderline.OrderLine", "setProductNumber", 100, 0, 20),


                new MethodCoverage("com.example.OrderManager", "<init>", 202, 10, 0),
                new MethodCoverage("com.example.OrderManager", "submitOrder", 253, 5, 5),


                new MethodCoverage("com.example.OrderManager.OrderFactoryImpl", "createOrder", 303, 15, 5),


                new MethodCoverage("com.example.OrderManager.Bill", "<init>", 4, 20, 5),
                new MethodCoverage("com.example.OrderManager.Bill", "prepareBill", 50, 5, 5),
                new MethodCoverage("com.example.OrderManager.Bill", "prepareBill", 100, 5, 5),
                new MethodCoverage("com.example.OrderManager.Bill", "prepareBill", 150, 5, 5)


        );
    }

    private List<JavaUnit> expectedJavaUnits() {
        return List.of(
                new JavaUnit("com.example.orderline.OrderLine",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\orderline\\OrderLine.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of("String"), 7, 13),
                                     new MethodDeclaration("getProductNumber", List.of(), 19, 39),
                                     new MethodDeclaration("setProductNumber", List.of("String"), 99, 120)
                             )),

                new JavaUnit("com.example.OrderManager",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderManager.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of(), 200, 249),
                                     new MethodDeclaration("submitOrder", List.of("List<OrderLine>", "BigDecimal"), 250, 300)
                             )),

                new JavaUnit("com.example.OrderManager.OrderFactoryImpl",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderManager.java",
                             List.of(
                                     new MethodDeclaration("createOrder", List.of("List<OrderLine>", "BigDecimal"), 301, 350)
                             )),

                new JavaUnit("com.example.OrderManager.Bill",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderManager.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of("String", "BigDecimal"), 3, 30),
                                     new MethodDeclaration("prepareBill", List.of("String", "BigDecimal"), 48, 60),
                                     new MethodDeclaration("prepareBill", List.of("BigDecimal"), 99, 111),
                                     new MethodDeclaration("prepareBill", List.of("String", "com.example.monetary.BigDecimal"), 148, 151)
                             )),

                new JavaUnit("com.example.OrderFactory",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\OrderFactory.java",
                             List.of(
                                     new MethodDeclaration("createOrder", List.of("List<OrderLine>", "BigDecimal"), 6, 6)
                             )),

                new JavaUnit("com.example.Order",
                             "C:\\Users\\Kacper\\Documents\\Marviq\\TESTAR_dev_clean\\testar\\test_resources\\javaparser\\src\\com\\example\\Order.java",
                             List.of(
                                     new MethodDeclaration("<init>", List.of("List<OrderLine>", "BigDecimal"), 6, 9)
                             ))
        );
    }


    private Map<String, List<AnalysedMethodEntry>> expectedEntriesWithCoverageAndIssues() {
        return Map.of("com.example.orderline.OrderLine",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "<init>", List.of("String"), BigDecimal.valueOf(4).divide(BigDecimal.valueOf(6), MathContext.DECIMAL64),
                                                      List.of(new Issue(null, "key", "CODE_SMELL", "MAJOR", 9L))),
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "getProductNumber", List.of(), BigDecimal.valueOf(0.25),
                                                      List.of(new Issue(null, "key", "BUG", "MINOR", 37L),
                                                              new Issue(null, "key", "CODE_SMELL", "INFO", 22L))),
                              new AnalysedMethodEntry(null, "com.example.orderline.OrderLine", "setProductNumber", List.of("String"), BigDecimal.ZERO,
                                                      List.of())
                      ),
                      "com.example.OrderManager",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager", "<init>", List.of(), BigDecimal.ONE,
                                                      List.of(new Issue(null, "key", "VULNERABILITY", "BLOCKER", 210L),
                                                              new Issue(null, "key", "CODE_SMELL", "MAJOR", 211L))),
                              new AnalysedMethodEntry(null, "com.example.OrderManager", "submitOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.valueOf(0.5),
                                                      List.of())


                      ),
                      "com.example.OrderManager.OrderFactoryImpl",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager.OrderFactoryImpl", "createOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.valueOf(0.75), List.of())
                      ),
                      "com.example.OrderManager.Bill",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "<init>", List.of("String", "BigDecimal"), BigDecimal.valueOf(0.8), List.of()),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "com.example.monetary.BigDecimal"), BigDecimal.valueOf(0.5),
                                                      List.of(new Issue(null, "key", "BUG", "MAJOR", 149L),
                                                              new Issue(null, "key", "CODE_SMELL", "MAJOR", 150L))),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("BigDecimal"), BigDecimal.valueOf(0.5),
                                                      List.of(new Issue(null, "key", "CODE_SMELL", "BLOCKER", 103L))),
                              new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "BigDecimal"), BigDecimal.valueOf(0.5),
                                                      List.of(new Issue(null, "key", "BUG", "MINOR", 53L),
                                                              new Issue(null, "key", "VULNERABILITY", "MINOR", 56L)))
                      ),
                      "com.example.OrderFactory",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.OrderFactory", "createOrder", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ),
                      "com.example.Order",
                      List.of(
                              new AnalysedMethodEntry(null, "com.example.Order", "<init>", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())
                      ));
    }

    private List<SQIssue> expectedSQIssues() {
        return List.of(
                new SQIssue("key","rule", "MAJOR", "src/com/example/orderline/OrderLine.java", 9L, "OPEN", "MAJOR", "CODE_SMELL"),
                new SQIssue("key","rule", "MINOR", "src/com/example/orderline/OrderLine.java", 37L, "OPEN", "MINOR", "BUG"),
                new SQIssue("key","rule", "INFO", "src/com/example/orderline/OrderLine.java", 22L, "OPEN", "MINOR", "CODE_SMELL"),

                new SQIssue("key","rule", "BLOCKER", "src/com/example/OrderManager.java", 210L, "OPEN", "MINOR", "VULNERABILITY"),
                new SQIssue("key","rule", "MAJOR", "src/com/example/OrderManager.java", 211L, "OPEN", "MINOR", "CODE_SMELL"),

                new SQIssue("key","rule", "MAJOR", "src/com/example/OrderManager.java", 149L, "OPEN", "MINOR", "BUG"),
                new SQIssue("key","rule", "MAJOR", "src/com/example/OrderManager.java", 150L, "OPEN", "MINOR", "CODE_SMELL"),
                new SQIssue("key","rule", "BLOCKER", "src/com/example/OrderManager.java", 103L, "OPEN", "MINOR", "CODE_SMELL"),
                new SQIssue("key","rule", "MINOR", "src/com/example/OrderManager.java", 53L, "OPEN", "MINOR", "BUG"),
                new SQIssue("key","rule", "MINOR", "src/com/example/OrderManager.java", 56L, "OPEN", "MINOR", "VULNERABILITY")
        );
    }
}