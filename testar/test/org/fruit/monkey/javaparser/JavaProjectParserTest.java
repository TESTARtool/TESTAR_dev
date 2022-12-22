package org.fruit.monkey.javaparser;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaProjectParserTest {


    @Test
    public void shouldParseSources() {
        var file = new File(getClass().getClassLoader().getResource("javaparser/src").getFile());
        var parser = new JavaProjectParser(file.getPath());
        var parsed = parser.parseJavaUnits();

        assertEquals(6, parsed.size());
        assertTrue(compareJavaUnits(parsed));
    }

    private boolean compareJavaUnits(List<JavaUnit> parsedJavaUnits) {
        var expectedJavaUnits = expectedJavaUnits();
        return parsedJavaUnits.stream().map(parsedUnit -> {
            var found = expectedJavaUnits.stream()
                                         .filter(expectedJavaUnit -> expectedJavaUnit.getUnitName().equals(parsedUnit.getUnitName()))
                                         .findFirst();
            if (found.isPresent()) {
                return compareMethods(parsedUnit.getMethods(), found.get().getMethods());
            } else {
                return false;
            }
        }).reduce(Boolean::logicalAnd).orElse(false);
    }

    private boolean compareMethods(List<MethodDeclaration> parsedMethods, List<MethodDeclaration> expectedMethods) {
        return parsedMethods.stream()
                            .map(parsed ->
                                         expectedMethods.stream()
                                                        .filter(expected -> expected.getName().equals(parsed.getName()))
                                                        .map(expected -> expected.getParameters()
                                                                                 .containsAll(parsed.getParameters()))
                                                        .reduce(Boolean::logicalOr)
                                                        .orElse(false))
                            .reduce(Boolean::logicalAnd)
                            .orElse(false);

    }

    private List<JavaUnit> expectedJavaUnits() {
        return List.of(
                new JavaUnit("com.example.orderline.OrderLine", List.of(
                        new MethodDeclaration("<init>", List.of("String"), 7, 9),
                        new MethodDeclaration("getProductNumber", List.of(), 11, 13),
                        new MethodDeclaration("setProductNumber", List.of("String"), 15, 17)
                )),

                new JavaUnit("com.example.OrderManager", List.of(
                        new MethodDeclaration("<init>", List.of(), 7, 8),
                        new MethodDeclaration("submitOrder", List.of("List<OrderLine>", "BigDecimal"), 10, 13)
                )),

                new JavaUnit("com.example.OrderManager.OrderFactoryImpl", List.of(
                        new MethodDeclaration("createOrder", List.of("List<OrderLine>", "BigDecimal"), 17, 19)
                )),

                new JavaUnit("com.example.OrderManager.Bill", List.of(
                        new MethodDeclaration("<init>", List.of("String", "BigDecimal"), 23, 23),
                        new MethodDeclaration("prepareBill", List.of("String", "BigDecimal"), 25, 27),
                        new MethodDeclaration("prepareBill", List.of("BigDecimal"), 29, 31),
                        new MethodDeclaration("prepareBill", List.of("String", "com.example.monetary.BigDecimal"), 33, 35)
                )),

                new JavaUnit("com.example.OrderFactory", List.of(
                        new MethodDeclaration("createOrder", List.of("List<OrderLine>", "BigDecimal"), 6, 6)
                )),

                new JavaUnit("com.example.Order", List.of(
                        new MethodDeclaration("<init>", List.of("List<OrderLine>", "BigDecimal"), 6, 9)
                ))
        );
    }
}