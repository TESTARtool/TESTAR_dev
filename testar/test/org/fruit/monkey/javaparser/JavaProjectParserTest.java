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
        var expectedJavaUnits = JavaProjectParserTestHelper.expectedJavaUnits();
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
}