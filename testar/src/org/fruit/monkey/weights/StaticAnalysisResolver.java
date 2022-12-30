package org.fruit.monkey.weights;

import lombok.AllArgsConstructor;
import org.fruit.monkey.jacoco.JacocoReportParser;
import org.fruit.monkey.jacoco.MethodCoverage;
import org.fruit.monkey.javaparser.JavaProjectParser;
import org.fruit.monkey.javaparser.JavaUnit;
import org.fruit.monkey.javaparser.MethodDeclaration;
import org.fruit.monkey.sonarqube.api.SQIssue;
import org.fruit.monkey.sonarqube.api.SonarqubeApiClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
public class StaticAnalysisResolver {

    private SonarqubeApiClient sonarqubeApiClient;
    private JacocoReportParser jacocoReportParser;
    private JavaProjectParser javaProjectParser;

    private static final String WINDOWS_SEPARATOR = "\\";

    public HashMap<String, List<AnalysedMethodEntry>> resolveMethodEntries() {
        var javaUnits = javaProjectParser.parseJavaUnits();
        var projectKey = sonarqubeApiClient.getProjectComponentKey();
        var sqIssues = sonarqubeApiClient.getDetectedIssues(projectKey);
        var jacocoCoverage = jacocoReportParser.parseReport();

        return javaUnits.stream()
                        .map(javaUnit -> Map.entry(javaUnit.getUnitName(), createMethodEntries(javaUnit, sqIssues, jacocoCoverage)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
    }

    private List<AnalysedMethodEntry> createMethodEntries(JavaUnit javaUnit, List<SQIssue> sqIssues, List<MethodCoverage> jacocoCoverage) {
        var javaUnitIssues = sqIssues.stream()
                                               .filter(sqIssue -> checkLocation(sqIssue.getLocation(), javaUnit.getFileLocation()))
                                               .filter(sqIssue -> checkIfIssueInJavaUnit(javaUnit, sqIssue))
                                               .map(this::createIssue)
                                               .collect(Collectors.toList());

        var javaUnitCoverage = jacocoCoverage.stream()
                                             .filter(coverage -> coverage.getClassName().endsWith(javaUnit.getUnitName()))
                                             .filter(methodCoverage -> checkIfJavaUnitIsCovered(javaUnit, methodCoverage))
                                             .collect(Collectors.toList());

        return javaUnit.getMethods()
                       .stream()
                       .map(methodDeclaration -> new AnalysedMethodEntry(null,
                                                                         javaUnit.getUnitName(),
                                                                         methodDeclaration.getName(),
                                                                         methodDeclaration.getParameters(),
                                                                         findCoverage(methodDeclaration, javaUnitCoverage),
                                                                         findIssues(methodDeclaration, javaUnitIssues)))
                       .collect(Collectors.toList());
    }

    private boolean checkLocation(String issueFileLocation, String javaUnitLocation) {
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            return javaUnitLocation.replace(WINDOWS_SEPARATOR, "/").endsWith(issueFileLocation);
        } else {
            return javaUnitLocation.endsWith(issueFileLocation);
        }
    }

    private boolean checkIfIssueInJavaUnit(JavaUnit javaUnit, SQIssue sqIssue) {
        if(sqIssue == null || sqIssue.getLine() == null || javaUnit == null) {
            System.out.println("SQ issue or line or javaunit is null");
            return false;
        }
        return checkIfInJavaUnitRanges(sqIssue.getLine(), javaUnit);
    }

    private boolean checkIfInJavaUnitRanges(long line, JavaUnit javaUnit) {
        return javaUnit.getMethods()
                       .stream()
                       .map(method -> checkIfInMethodRange(line, method))
                       .reduce(Boolean::logicalOr)
                       .orElse(false);
    }

    private boolean checkIfInMethodRange(long line, MethodDeclaration methodDeclaration) {
        return line >= methodDeclaration.getStartLine() && line <= methodDeclaration.getEndLine();
    }

    private Issue createIssue(SQIssue sqIssue) {
        return new Issue(null, sqIssue.getKey(), sqIssue.getType(), sqIssue.getSeverity(), sqIssue.getLine());
    }

    private boolean checkIfJavaUnitIsCovered(JavaUnit javaUnit, MethodCoverage coverage) {
        return checkIfInJavaUnitRanges(coverage.getInitLine(), javaUnit);
    }

    private BigDecimal findCoverage(MethodDeclaration methodDeclaration, List<MethodCoverage> coverages) {
        return coverages.stream()
                        .filter(coverage -> checkIfInMethodRange(coverage.getInitLine(), methodDeclaration))
                        .findFirst()
                        .map(this::calculateCoverage)
                        .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateCoverage(MethodCoverage methodCoverage) {
        return BigDecimal.valueOf(methodCoverage.getLinesCovered())
                         .divide(BigDecimal.valueOf(methodCoverage.getLinesCovered() + methodCoverage.getLinesMissed()),
                                 MathContext.DECIMAL64);
    }

    private List<Issue> findIssues(MethodDeclaration methodDeclaration, List<Issue> issues) {
        return issues.stream()
                .filter(issue -> checkIfInMethodRange(issue.getLine(), methodDeclaration))
                .collect(Collectors.toList());
    }


}
