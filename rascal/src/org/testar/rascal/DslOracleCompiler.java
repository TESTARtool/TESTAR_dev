/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.rascal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rascalmpl.exceptions.Throw;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.staticErrors.StaticError;
import org.rascalmpl.interpreter.staticErrors.SyntaxError;
import org.rascalmpl.parser.gtd.exception.ParseError;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.usethesource.vallang.IConstructor;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.IValue;
import io.usethesource.vallang.IValueFactory;
import io.usethesource.vallang.IWithKeywordParameters;

public final class DslOracleCompiler {

    private static final String STATUS_ERROR = "ERROR";
    private static final String DSL_PARSE_ERROR_MESSAGE = "DSL parse error. Check the syntax near this position.";
    private static final Pattern RASCAL_PARSE_ERROR_LOCATION_PATTERN = Pattern.compile(
        "ParseError\\([^|]*\\|[^|]+\\|\\([^,]+,[^,]+,<([0-9]+),([0-9]+)>,<([0-9]+),([0-9]+)>\\)"
    );

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
        IString diagnosticsJson = (IString) evaluator.call("validateAtWithModelJson", oracleLocation, modelLocation);

        return parseDiagnostics(diagnosticsJson.getValue());
    }

    private List<DslOracleDiagnostic> parseDiagnostics(String diagnosticsJson) {
        List<DslOracleDiagnostic> diagnostics = new ArrayList<>();
        JsonElement parsedJson = new JsonParser().parse(diagnosticsJson);
        if (!parsedJson.isJsonArray()) {
            diagnostics.add(new DslOracleDiagnostic("ERROR", -1, -1, "Rascal validation returned invalid diagnostic JSON."));
            return diagnostics;
        }

        JsonArray diagnosticsArray = parsedJson.getAsJsonArray();
        for (JsonElement element : diagnosticsArray) {
            if (!element.isJsonObject()) {
                diagnostics.add(new DslOracleDiagnostic("ERROR", -1, -1, "Rascal validation returned a non-object diagnostic."));
                continue;
            }

            diagnostics.add(toDiagnostic(element.getAsJsonObject()));
        }

        return diagnostics;
    }

    private DslOracleDiagnostic toDiagnostic(JsonObject diagnosticJson) {
        String severity = jsonString(diagnosticJson, "severity", "ERROR");
        String message = jsonString(diagnosticJson, "message", "");
        JsonObject location = jsonObject(diagnosticJson, "location");
        int line = jsonLocationValue(location, "begin", 0);
        int column = jsonLocationValue(location, "begin", 1);
        int endLine = jsonLocationValue(location, "end", 0);
        int endColumn = jsonLocationValue(location, "end", 1);

        return new DslOracleDiagnostic(severity, line, column, endLine, endColumn, message);
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
        if (throwable instanceof ParseError parseError) {
            return parseErrorDiagnostic(parseError);
        }

        Throwable cause = throwable.getCause();
        if (cause instanceof ParseError parseError) {
            return parseErrorDiagnostic(parseError);
        }

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

            if (isRawParseErrorMessage(message)) {
                DslOracleDiagnostic diagnostic = diagnosticFromRawParseErrorMessage(message);
                if (diagnostic != null) {
                    return diagnostic;
                }

                return diagnosticFromLocation("ERROR", location, DSL_PARSE_ERROR_MESSAGE);
            }

            return diagnosticFromLocation("ERROR", location, normalizeDiagnosticMessage(message));
        }

        return new DslOracleDiagnostic(
            "ERROR",
            -1,
            -1,
            normalizeDiagnosticMessage(throwable.getClass().getSimpleName() + " - " + throwable.getMessage())
        );
    }

    private DslOracleDiagnostic parseErrorDiagnostic(ParseError parseError) {
        ISourceLocation location = parseError.getLocation();
        if (location != null && location.hasLineColumn()) {
            return diagnosticFromLocation("ERROR", location, DSL_PARSE_ERROR_MESSAGE);
        }

        return new DslOracleDiagnostic(
            "ERROR",
            parseError.getBeginLine(),
            parseError.getBeginColumn(),
            parseError.getEndLine(),
            parseError.getEndColumn(),
            DSL_PARSE_ERROR_MESSAGE
        );
    }

    private DslOracleDiagnostic diagnosticFromLocation(String severity, ISourceLocation location, String message) {
        return new DslOracleDiagnostic(
            severity,
            location != null && location.hasLineColumn() ? location.getBeginLine() : -1,
            location != null && location.hasLineColumn() ? location.getBeginColumn() : -1,
            location != null && location.hasLineColumn() ? location.getEndLine() : -1,
            location != null && location.hasLineColumn() ? location.getEndColumn() : -1,
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

    private boolean isRawParseErrorMessage(String message) {
        return message != null && message.contains("ParseError(");
    }

    private DslOracleDiagnostic diagnosticFromRawParseErrorMessage(String message) {
        Matcher matcher = RASCAL_PARSE_ERROR_LOCATION_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }

        return new DslOracleDiagnostic(
            "ERROR",
            Integer.parseInt(matcher.group(1)),
            Integer.parseInt(matcher.group(2)),
            Integer.parseInt(matcher.group(3)),
            Integer.parseInt(matcher.group(4)),
            DSL_PARSE_ERROR_MESSAGE
        );
    }

    private String normalizeDiagnosticMessage(String message) {
        if (isRawParseErrorMessage(message)) {
            return DSL_PARSE_ERROR_MESSAGE;
        }

        return message == null ? "" : message;
    }

    private JsonObject jsonObject(JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || !parent.get(key).isJsonObject()) {
            return null;
        }

        return parent.getAsJsonObject(key);
    }

    private String jsonString(JsonObject parent, String key, String defaultValue) {
        if (parent == null || !parent.has(key) || parent.get(key).isJsonNull()) {
            return defaultValue;
        }

        return parent.get(key).getAsString();
    }

    private int jsonLocationValue(JsonObject location, String key, int index) {
        if (location == null || !location.has(key) || !location.get(key).isJsonArray()) {
            return -1;
        }

        JsonArray values = location.getAsJsonArray(key);
        if (values.size() <= index || values.get(index).isJsonNull()) {
            return -1;
        }

        return values.get(index).getAsInt();
    }

    private String sourceBaseName(String sourceName) {
        String fileName = sourceName == null || sourceName.isBlank()
            ? "RuntimeGeneratedOracles.testar"
            : Path.of(sourceName.replace('\\', '/')).getFileName().toString();

        return fileName.replaceFirst("(?i)\\.testar$", "");
    }
}
