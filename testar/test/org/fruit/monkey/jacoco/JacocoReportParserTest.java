package org.fruit.monkey.jacoco;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JacocoReportParserTest {

    @Test
    public void shouldParseReportFile() {
        var file = new File(getClass().getClassLoader().getResource("jacoco-xml/jacoco-test-report.xml").getFile());
        var jacocoReportParser = new JacocoReportParser(file.getAbsolutePath());
        var methodsCoverage = jacocoReportParser.parseReport();
        assertEquals(expectedMethodsCoverage().size(), methodsCoverage.size());
        assertTrue(methodsCoverage.containsAll(expectedMethodsCoverage()));

    }

    private List<MethodCoverage> expectedMethodsCoverage() {
        return List.of(
                new MethodCoverage("com.testar.testarmicroservice.ActionController.FinishActionRecordingResponse",
                                   "<init>",
                                   47,
                                   1,
                                   0),
                new MethodCoverage("com.testar.testarmicroservice.ActionController.FinishActionRecordingResponse",
                                   "getStatus",
                                   50,
                                   1,
                                   0),
                new MethodCoverage("com.testar.testarmicroservice.ActionController.FinishActionRecordingResponse",
                                   "getMethodsExecuted",
                                   51,
                                   1,
                                   0),
                new MethodCoverage("com.testar.testarmicroservice.service.MethodInvocationService",
                                   "listenToMethodInvocations",
                                   24,
                                   4,
                                   3),
                new MethodCoverage("com.testar.testarmicroservice.service.MethodInvocationService",
                                   "<init>",
                                   13,
                                   1,
                                   0),
                new MethodCoverage("com.testar.testarmicroservice.model.MethodInvocation",
                                   "<init>",
                                   15,
                                   4,
                                   0),
                new MethodCoverage("com.testar.testarmicroservice.model.MethodInvocation",
                                   "getId",
                                   22,
                                   1,
                                   0),
                new MethodCoverage("com.testar.testarmicroservice.model.MethodInvocation",
                                   "getClassName",
                                   24,
                                   1,
                                   0)
        );
    }
}