/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.rascal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.rascalmpl.exceptions.Throw;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.staticErrors.StaticError;
import org.rascalmpl.interpreter.staticErrors.SyntaxError;

import io.usethesource.vallang.IConstructor;
import io.usethesource.vallang.IList;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.IValue;
import io.usethesource.vallang.IValueFactory;
import io.usethesource.vallang.IWithKeywordParameters;

public final class DslOracleCompiler {

    private static final String STATUS_ERROR = "ERROR";

    private final DslOracleLoader loader;

    public DslOracleCompiler() {
        try {
            loader = new DslOracleLoader();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to initialize TESTAR DSL oracle compiler.", exception);
        }
    }

    public synchronized DslOracleOperationResult validate(String sourceName, String dslSource) {
        try {
            List<DslOracleDiagnostic> diagnostics = validateDslSource(sourceName, dslSource);
            boolean success = diagnostics.stream()
                .noneMatch(diagnostic -> STATUS_ERROR.equalsIgnoreCase(diagnostic.severity()));
            String message = success ? "DSL validation succeeded." : "DSL validation failed.";

            return new DslOracleOperationResult(success, message, "", diagnostics);
        } catch (Throwable throwable) {
            return failure("DSL validation failed.", throwable);
        }
    }

    public synchronized DslOracleOperationResult generateJava(String sourceName, String dslSource) {
        try {
            List<DslOracleDiagnostic> diagnostics = validateDslSource(sourceName, dslSource);
            boolean hasErrors = diagnostics.stream()
                .anyMatch(diagnostic -> STATUS_ERROR.equalsIgnoreCase(diagnostic.severity()));
            if (hasErrors) {
                return new DslOracleOperationResult(false, "DSL generation blocked by validation errors.", "", diagnostics);
            }

            ISourceLocation oracleLocation = DslOracleHelpers.writeDslToTemp(
                loader.getValueFactory(),
                sourceBaseName(sourceName),
                dslSource
            );
            String javaSource = DslOracleHelpers.compileAt(loader.getEvaluator(), oracleLocation);

            return new DslOracleOperationResult(true, "Java oracle generated.", javaSource, diagnostics);
        } catch (Throwable throwable) {
            return failure("DSL generation failed.", throwable);
        }
    }

    private List<DslOracleDiagnostic> validateDslSource(String sourceName, String dslSource) throws Exception {
        IValueFactory valueFactory = loader.getValueFactory();
        Evaluator evaluator = loader.getEvaluator();
        ISourceLocation oracleLocation = DslOracleHelpers.writeDslToTemp(
            valueFactory,
            sourceBaseName(sourceName),
            dslSource
        );
        Path modelPath = loader.getModulePath().resolve("lang").resolve("testar").resolve("testar.model");
        ISourceLocation modelLocation = valueFactory.sourceLocation(modelPath.toUri());
        IList rascalDiagnostics = (IList) evaluator.call("validateAtWithModel", oracleLocation, modelLocation);

        List<DslOracleDiagnostic> diagnostics = new ArrayList<>();
        for (IValue value : rascalDiagnostics) {
            diagnostics.add(toDiagnostic(value));
        }

        return diagnostics;
    }

    private DslOracleDiagnostic toDiagnostic(IValue value) {
        IConstructor diagnostic = (IConstructor) value;
        String severity = formatSeverity(diagnostic.get(0).toString());
        ISourceLocation location = (ISourceLocation) diagnostic.get(1);
        String message = ((IString) diagnostic.get(2)).getValue();

        return new DslOracleDiagnostic(
            severity,
            location.hasLineColumn() ? location.getBeginLine() : -1,
            location.hasLineColumn() ? location.getBeginColumn() : -1,
            message
        );
    }

    private DslOracleOperationResult failure(String message, Throwable throwable) {
        return new DslOracleOperationResult(
            false,
            message,
            "",
            List.of(toDiagnostic(throwable))
        );
    }

    private DslOracleDiagnostic toDiagnostic(Throwable throwable) {
        if (throwable instanceof SyntaxError syntaxError) {
            return diagnosticFromLocation("ERROR", syntaxError.getLocation(), syntaxError.getMessage());
        }

        if (throwable instanceof StaticError staticError) {
            return diagnosticFromLocation("ERROR", staticError.getLocation(), staticError.getMessage());
        }

        if (throwable instanceof Throw rascalThrow) {
            ISourceLocation location = locationFromThrow(rascalThrow);
            String message = rascalThrow.getMessage();
            if (message == null || message.isBlank()) {
                message = rascalThrow.getException() == null
                    ? "Rascal execution failed."
                    : rascalThrow.getException().toString();
            }

            return diagnosticFromLocation("ERROR", location, message);
        }

        return new DslOracleDiagnostic(
            "ERROR",
            -1,
            -1,
            throwable.getClass().getSimpleName() + " - " + throwable.getMessage()
        );
    }

    private DslOracleDiagnostic diagnosticFromLocation(String severity, ISourceLocation location, String message) {
        return new DslOracleDiagnostic(
            severity,
            location != null && location.hasLineColumn() ? location.getBeginLine() : -1,
            location != null && location.hasLineColumn() ? location.getBeginColumn() : -1,
            message == null ? "" : message
        );
    }

    private ISourceLocation locationFromThrow(Throw rascalThrow) {
        IValue exception = rascalThrow.getException();
        if (!(exception instanceof IConstructor constructor)) {
            return null;
        }

        if (constructor.arity() > 0 && constructor.get(0) instanceof ISourceLocation sourceLocation) {
            return sourceLocation;
        }

        if (!constructor.mayHaveKeywordParameters()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        IWithKeywordParameters<IConstructor> keywordParameters = (IWithKeywordParameters<IConstructor>) constructor;
        IValue location = keywordParameters.getParameter("loc");
        if (location instanceof ISourceLocation sourceLocation) {
            return sourceLocation;
        }

        return null;
    }

    private String formatSeverity(String severity) {
        String formatted = severity.replace("()", "");
        if (formatted.startsWith("Sev")) {
            formatted = formatted.substring(3);
        }

        return formatted.toUpperCase();
    }

    private String sourceBaseName(String sourceName) {
        String fileName = sourceName == null || sourceName.isBlank()
            ? "RuntimeGeneratedOracles.testar"
            : Path.of(sourceName.replace('\\', '/')).getFileName().toString();

        return fileName.replaceFirst("(?i)\\.testar$", "");
    }
}
