package org.fruit.monkey.weights;

import org.fruit.monkey.btrace.MethodInvocation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeightCalculator {

    private final static Map<String, BigDecimal> SEVERITY_SCORES = Map.of(
            "BLOCKER", BigDecimal.valueOf(10),
            "CRITICAL", BigDecimal.valueOf(5),
            "MAJOR", BigDecimal.valueOf(3),
            "MINOR", BigDecimal.valueOf(1),
            "INFO", BigDecimal.valueOf(0)
    );
    private final static Map<String, BigDecimal> ISSUE_TYPE_SCORES = Map.of(
            "BUG", BigDecimal.valueOf(10),
            "VULNERABILITY", BigDecimal.valueOf(5),
            "CODE_SMELL", BigDecimal.valueOf(3)
    );

    private final static BigDecimal ISSUES_COEFFICIENT = BigDecimal.valueOf(0.8);
    private final static BigDecimal COVERAGE_COEFFICIENT = BigDecimal.valueOf(0.2);

    public WeightVerdict calculateWeight(List<MethodInvocation> methodInvocations, AnalysedMethodEntryRepository repository) {
        var analysedMethodEntriesToInclude = extractAnalysedMethodEntries(methodInvocations, repository);
        var overallScore = ISSUES_COEFFICIENT.multiply(calculateIssuesScore(analysedMethodEntriesToInclude))
                                             .add(COVERAGE_COEFFICIENT.multiply(calculateCoverageScore(analysedMethodEntriesToInclude)));
        return new WeightVerdict(overallScore.setScale(2, RoundingMode.HALF_UP), analysedMethodEntriesToInclude);
    }

    private List<AnalysedMethodEntry> extractAnalysedMethodEntries(List<MethodInvocation> methodInvocations,
                                                                   AnalysedMethodEntryRepository repository) {
        return methodInvocations.stream()
                                .map(methodInvocation -> findMethod(methodInvocation,
                                                                    repository.findByClassName(methodInvocation.getClassName())))
                                .collect(Collectors.toList());
    }

    private AnalysedMethodEntry findMethod(MethodInvocation methodInvocation, List<AnalysedMethodEntry> analysedMethodEntries) {
        var methodEntriesOfName = analysedMethodEntries.stream()
                                                       .filter(analysedMethodEntry -> analysedMethodEntry.getMethodName()
                                                                                                         .equals(methodInvocation.getMethodName()))
                                                       .filter(analysedMethodEntry ->
                                                                       analysedMethodEntry.getParameters().size() == methodInvocation.getParameterTypes().size())
                                                       .collect(Collectors.toList());
        AnalysedMethodEntry analyzedMethodFound = null;
        if (methodEntriesOfName.size() == 1) {
            analyzedMethodFound = methodEntriesOfName.get(0);
        } else if (methodEntriesOfName.size() > 1) {
            analyzedMethodFound = findOverloadedMethod(methodInvocation, methodEntriesOfName);
        }
        return analyzedMethodFound;
    }

    private AnalysedMethodEntry findOverloadedMethod(MethodInvocation methodInvocation, List<AnalysedMethodEntry> methodEntriesOfName) {
        var foundMethodEntry = methodEntriesOfName.stream()
                                                  .filter(methodEntry -> methodInvocation.getParameterTypes().containsAll(methodEntry.getParameters()))
                                                  .findFirst();
        if (foundMethodEntry.isPresent()) {
            return foundMethodEntry.get();
        }

        var parameters = methodInvocation.getParameterTypes().stream()
                                         .map(parameter -> {
                                             var splitParameterName = parameter.split("\\.");
                                             var simpleParameterName = splitParameterName[splitParameterName.length - 1];
                                             return new String[]{parameter, simpleParameterName};
                                         }).collect(Collectors.toList());

        var similarity = new ArrayList<Integer>();
        for (AnalysedMethodEntry methodEntry : methodEntriesOfName) {
            var temp = new ArrayList<>(parameters);
            var similarityEntry = 0;
            for (String[] param : temp) {
                if (methodEntry.getParameters().contains(param[0])) {
                    similarityEntry += 2;
                } else if (methodEntry.getParameters().contains(param[1])) {
                    similarityEntry += 1;
                }
            }
            similarity.add(similarityEntry);
        }

        var index = similarity.indexOf(Collections.max(similarity));

        return methodEntriesOfName.get(index);
    }

    private BigDecimal calculateCoverageScore(List<AnalysedMethodEntry> analysedMethodEntries) {
        var coverageValues = analysedMethodEntries.stream()
                                                  .map(analysedMethodEntry -> analysedMethodEntry.getCoverage())
                                                  .collect(Collectors.toList());
        var averageCoverage = coverageValues.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return BigDecimal.ONE
                .subtract(averageCoverage.divide(BigDecimal.valueOf(coverageValues.size()), MathContext.DECIMAL64))
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateIssuesScore(List<AnalysedMethodEntry> analysedMethodEntries) {
        return analysedMethodEntries.stream()
                                    .flatMap(analysedMethodEntry -> analysedMethodEntry.getIssues().stream())
                                    .map(issue -> ISSUE_TYPE_SCORES.getOrDefault(issue.getType(), BigDecimal.ONE)
                                                                   .multiply(SEVERITY_SCORES.getOrDefault(issue.getSeverity(),
                                                                                                          BigDecimal.ONE)))
                                    .reduce(BigDecimal::add)
                                    .orElse(BigDecimal.ZERO);
    }
}
