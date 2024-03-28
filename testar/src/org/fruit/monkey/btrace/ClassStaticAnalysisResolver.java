package org.fruit.monkey.btrace;

import lombok.AllArgsConstructor;
import org.fruit.monkey.jacoco.MethodCoverage;
import org.fruit.monkey.javaparser.JavaProjectParser;
import org.fruit.monkey.javaparser.JavaUnit;
import org.fruit.monkey.javaparser.MethodDeclaration;
import org.fruit.monkey.sonarqube.api.SQIssue;
import org.fruit.monkey.sonarqube.api.SonarqubeApiClient;
import org.fruit.monkey.weights.AnalysedMethodEntry;
import org.fruit.monkey.weights.Issue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AllArgsConstructor
public class ClassStaticAnalysisResolver {


    private SonarqubeApiClient sonarqubeApiClient;
    private JavaProjectParser javaProjectParser;
    private DBStaticAnalysisRepository repository;

    private static final String WINDOWS_SEPARATOR = "\\";

    public void performStaticAnalysis(){
        System.out.println("\nbeginning analysis: ");
        var javaUnits = javaProjectParser.parseJavaUnits();
        var projectKey = sonarqubeApiClient.getProjectComponentKey();
        var sqIssues = sonarqubeApiClient.getDetectedIssues(projectKey);

        System.out.println("\nStarting analysis: ");

        try {

        for(JavaUnit javaUnit: javaUnits){
            System.out.println("\nJAVAUNIT: ");
            System.out.println(javaUnit.getUnitName());
            System.out.println("\t" + javaUnit.getFileLocation());
                repository.saveClass(javaUnit);

                List<SQIssue> issuesPerJavaUnit = sqIssues.stream()
                        .filter(sqIssue -> checkLocation(sqIssue.getLocation(), javaUnit.getFileLocation()) &&
                                checkIfIssueInJavaUnit(javaUnit, sqIssue))
                        .collect(Collectors.toList());
                System.out.println("\t" + issuesPerJavaUnit.size());

                for(SQIssue sqIssue: issuesPerJavaUnit){
                    System.out.println("\t" + sqIssue.getKey());
                    boolean methodFlag = false;
    //                repository.saveM;

                    for(MethodDeclaration method: javaUnit.getMethods()){
                        repository.saveMethod(javaUnit, method);
                        if (checkIfInMethodRange(sqIssue.getLine(), method)){
                            System.out.println("Saved method");
                            repository.saveIssueWithMethod(sqIssue, javaUnit, method);
                            methodFlag = true;
                        }
                    }
                    if(!methodFlag){
                        System.out.println("Saved without method");
                        repository.saveIssueWithoutMethod(sqIssue, javaUnit);
                    }
                }
            }
        }
        catch (SQLException e) {
            System.out.println("ERROR");
            System.out.println(e.getMessage());
        }

    }

//    public Map<String, List<Issue>> resolveClassEntries(List<JavaUnit> javaUnits, List<SQIssue> sqIssues) {
//        return javaUnits.stream()
//                .map(javaUnit -> Map.entry(javaUnit.getUnitName(), createUnitEntries(javaUnit, sqIssues)))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
//
//    }
//
//    public Map<String, AbstractMap.SimpleEntry<MethodDeclaration, List<Issue>>> resolveMethodEntries(List<JavaUnit> javaUnits, Map<String, List<Issue>> javaUnitToIssues) {
//
//        return javaUnits.stream()
//                .flatMap(javaUnit -> createMethodEntries(javaUnit, javaUnitToIssues.getOrDefault(javaUnit.getUnitName(), new ArrayList<Issue>())))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//
////        return javaUnits.stream()
////                .map(javaUnit -> Map.entry(javaUnit.getUnitName(), createMethodEntries(javaUnit, sqIssues)))
////                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
//    }
//
//    private Stream<Map.Entry<String, AbstractMap.SimpleEntry<MethodDeclaration, List<Issue>>>> createMethodEntries(JavaUnit javaUnit, List<Issue> issues){
//        return javaUnit.getMethods()
//                .stream()
//                .map(methodDeclaration ->
//                        new AbstractMap.SimpleEntry<>(
//                                javaUnit.getUnitName(),
//                                new AbstractMap.SimpleEntry<>(methodDeclaration, getIssuesForMethod(methodDeclaration, issues))));
////                .map(methodDeclaration -> getIssuesForMethod(methodDeclaration, issues));
//    }
//
//    private List<Issue> getIssuesForMethod(MethodDeclaration methodDeclaration, List<Issue> issues){
//        return issues.stream()
//                .filter(issue -> checkIfInMethodRange(issue.getLine(), methodDeclaration))
//                .toList();
//    }


    private List<Issue> createUnitEntries(JavaUnit javaUnit, List<SQIssue> sqIssues) {
        var javaUnitIssues = sqIssues.stream()
                .filter(sqIssue -> checkLocation(sqIssue.getLocation(), javaUnit.getFileLocation()))
//                .filter(sqIssue -> checkIfIssueInJavaUnit(javaUnit, sqIssue))
                .map(this::createIssue)
                .collect(Collectors.toList());
        return javaUnitIssues;

//        var javaUnitCoverage = jacocoCoverage.stream()
//                .filter(coverage -> coverage.getClassName().endsWith(javaUnit.getUnitName()))
//                .filter(methodCoverage -> checkIfJavaUnitIsCovered(javaUnit, methodCoverage))
//                .collect(Collectors.toList());
//
//        return javaUnit.getMethods()
//                .stream()
//                .map(methodDeclaration -> new AnalysedMethodEntry(null,
//                        javaUnit.getUnitName(),
//                        methodDeclaration.getName(),
//                        methodDeclaration.getParameters(),
//                        findCoverage(methodDeclaration, javaUnitCoverage),
//                        findIssues(methodDeclaration, javaUnitIssues)))
//                .collect(Collectors.toList());
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
        return checkIfInJavaUnitRange(line, javaUnit) || javaUnit.getMethods()
                .stream()
                .map(method -> checkIfInMethodRange(line, method))
                .reduce(Boolean::logicalOr)
                .orElse(false);
    }

    private boolean checkIfInMethodRange(long line, MethodDeclaration methodDeclaration) {
        return line >= methodDeclaration.getStartLine() && line <= methodDeclaration.getEndLine();
    }

    private boolean checkIfInJavaUnitRange(long line, JavaUnit javaUnit) {
        return line >= javaUnit.getStartLine() && line <= javaUnit.getEndLine();
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
