package org.fruit.monkey.weights;

import org.fruit.monkey.btrace.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeightCalculatorTest {

    @Mock
    private AnalysedMethodEntryRepository repository;

    @Test
    public void shouldCalculateWeights() {
        var mockEntries = expectedEntriesWithCoverageAndIssues();
        mockEntries.keySet().forEach(key -> when(repository.findByClassName(key)).thenReturn(mockEntries.get(key)));

        var methodInvocations = singleMethodInvocation();

        var weightCalculator = new WeightCalculator();

        var actualVerdicts = weightCalculator.calculateWeight(methodInvocations, repository);

        var expectedVerdict = new WeightVerdict(BigDecimal.valueOf(22).setScale(2, RoundingMode.HALF_UP), List.of(
                new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill",
                                        List.of("String", "BigDecimal"), BigDecimal.valueOf(0.5),
                                        List.of(new Issue(null, "key", "BUG", "MINOR", 53L),
                                                new Issue(null, "key", "VULNERABILITY", "MINOR", 56L)))));

        assertEquals(0, expectedVerdict.getSeverityVerdict().compareTo(actualVerdicts.getSeverityVerdict()));
        assertEquals(expectedVerdict.getMethodsIncluded().size(), actualVerdicts.getMethodsIncluded().size());
        actualVerdicts.getMethodsIncluded().forEach(actualMethodIncluded ->
                                                            assertTrue(expectedVerdict.getMethodsIncluded().contains(actualMethodIncluded))
        );
    }

    @Test
    public void shouldCalculateWeightsWithMultipleEntries() {
        var mockEntries = expectedEntriesWithCoverageAndIssues();
        mockEntries.keySet().forEach(key -> when(repository.findByClassName(key)).thenReturn(mockEntries.get(key)));

        var methodInvocations = multipleMethodInvocation();

        var weightCalculator = new WeightCalculator();

        var actualVerdicts = weightCalculator.calculateWeight(methodInvocations, repository);

        var expectedVerdict = new WeightVerdict(BigDecimal.valueOf(56.5)
                                                          .add(BigDecimal.valueOf(0.1)
                                                                         .divide(BigDecimal.valueOf(3),
                                                                                 MathContext.DECIMAL64))
                                                        .setScale(2, RoundingMode.HALF_UP), List.of(
                new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill", List.of("String", "com.example.monetary.BigDecimal"), BigDecimal.valueOf(0.5),
                                        List.of(new Issue(null, "key", "BUG", "MAJOR", 149L),
                                                new Issue(null, "key", "CODE_SMELL", "MAJOR", 150L))),
                new AnalysedMethodEntry(null, "com.example.OrderManager.Bill", "prepareBill",
                                        List.of("String", "BigDecimal"), BigDecimal.valueOf(0.5),
                                        List.of(new Issue(null, "key", "BUG", "MINOR", 53L),
                                                new Issue(null, "key", "VULNERABILITY", "MINOR", 56L))),
                new AnalysedMethodEntry(null, "com.example.Order", "<init>", List.of("List<OrderLine>", "BigDecimal"), BigDecimal.ZERO, List.of())));

        assertEquals(expectedVerdict.getSeverityVerdict(), actualVerdicts.getSeverityVerdict());
        assertEquals(expectedVerdict.getMethodsIncluded().size(), actualVerdicts.getMethodsIncluded().size());
        actualVerdicts.getMethodsIncluded().forEach(actualMethodIncluded ->
                                                            assertTrue(expectedVerdict.getMethodsIncluded().contains(actualMethodIncluded))
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

    private List<MethodInvocation> singleMethodInvocation() {
        return List.of(
                new MethodInvocation("com.example.OrderManager.Bill", "prepareBill", List.of("String", "BigDecimal"), 2)
        );
    }

    private List<MethodInvocation> multipleMethodInvocation() {
        return List.of(
                new MethodInvocation("com.example.OrderManager.Bill", "prepareBill", List.of("String", "BigDecimal"), 2),
                new MethodInvocation("com.example.OrderManager.Bill", "prepareBill", List.of("String", "com.example.monetary.BigDecimal"), 4),
                new MethodInvocation("com.example.Order", "<init>", List.of("List<OrderLine>", "BigDecimal"), 1)
        );
    }

}