/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.rascal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DslOracleMetadataGenerator {

    private static final Pattern MODEL_RECORD_PATTERN = Pattern.compile(
        "(?m)^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\{([\\s\\S]*?)^\\s*\\}"
    );
    private static final Pattern MODEL_FIELD_PATTERN = Pattern.compile(
        "(?m)^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*([^\\r\\n]+)"
    );
    private static final Pattern RASCAL_LITERAL_PATTERN = Pattern.compile("\"((?:\\\\.|[^\"])*)\"");
    private static final Pattern RESERVED_PATTERN = Pattern.compile("keyword\\s+Reserved\\s*=\\s*([\\s\\S]*?);");
    private static final Pattern SYNTAX_BODY_PATTERN = Pattern.compile("syntax\\s+%s\\s*=\\s*([\\s\\S]*?);");
    private static final Pattern LOCALE_PATTERN = Pattern.compile("\"([A-Za-z]{2,3}(?:_[A-Za-z0-9]+)*)\"\\s*:");

    private final Path modulePath;

    public DslOracleMetadataGenerator() {
        this(resolveDefaultModulePath());
    }

    public DslOracleMetadataGenerator(Path modulePath) {
        this.modulePath = modulePath.toAbsolutePath().normalize();
    }

    public DslOracleMetadata generate() {
        try {
            Path testarDirectory = modulePath.resolve("lang").resolve("testar");
            String modelSource = Files.readString(testarDirectory.resolve("testar.model"), StandardCharsets.UTF_8);
            String oracleSource = Files.readString(testarDirectory.resolve("Oracle.rsc"), StandardCharsets.UTF_8);
            String localeSource = Files.readString(testarDirectory.resolve("util").resolve("Locale.rsc"), StandardCharsets.UTF_8);

            Map<String, List<DslOracleModelField>> fieldsByWidgetType = parseModelFields(modelSource);
            List<String> widgetTypes = new ArrayList<>(fieldsByWidgetType.keySet());
            List<String> fieldNames = parseFieldNames(fieldsByWidgetType);
            List<String> keywords = parseKeywords(oracleSource);
            List<String> rootKeywords = parseRootKeywords(oracleSource);
            List<String> conditionOperators = parseConditionOperators(oracleSource);
            List<String> connectorKeywords = parseConnectorKeywords(oracleSource);
            List<String> locales = parseLocales(localeSource);

            return new DslOracleMetadata(
                keywords,
                widgetTypes,
                fieldsByWidgetType,
                fieldNames,
                rootKeywords,
                conditionOperators,
                connectorKeywords,
                locales
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to generate TESTAR DSL oracle metadata from: " + modulePath, exception);
        }
    }

    private static Path resolveDefaultModulePath() {
        Path current = Paths.get("").toAbsolutePath().normalize();
        while (current != null) {
            Path candidate = current.resolve("rascal").resolve("testar-oracle").normalize();
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();
        }

        return Paths.get("").toAbsolutePath().resolve("rascal").resolve("testar-oracle").normalize();
    }

    private static Map<String, List<DslOracleModelField>> parseModelFields(String modelSource) {
        Map<String, List<DslOracleModelField>> fieldsByWidgetType = new TreeMap<>();
        Matcher recordMatcher = MODEL_RECORD_PATTERN.matcher(modelSource);
        while (recordMatcher.find()) {
            String widgetType = recordMatcher.group(1);
            String fieldsSource = recordMatcher.group(2);
            List<DslOracleModelField> fields = new ArrayList<>();
            Matcher fieldMatcher = MODEL_FIELD_PATTERN.matcher(fieldsSource);
            while (fieldMatcher.find()) {
                fields.add(new DslOracleModelField(
                    fieldMatcher.group(1),
                    normalizeType(fieldMatcher.group(2))
                ));
            }
            fieldsByWidgetType.put(widgetType, List.copyOf(fields));
        }

        return fieldsByWidgetType;
    }

    private static List<String> parseFieldNames(Map<String, List<DslOracleModelField>> fieldsByWidgetType) {
        Set<String> fieldNames = new TreeSet<>();
        for (List<DslOracleModelField> fields : fieldsByWidgetType.values()) {
            for (DslOracleModelField field : fields) {
                fieldNames.add(field.name());
            }
        }

        return new ArrayList<>(fieldNames);
    }

    private static List<String> parseKeywords(String oracleSource) {
        String grammarSource = oracleSource;
        int helperStartIndex = oracleSource.indexOf("str unescapeJava");
        if (helperStartIndex > 0) {
            grammarSource = oracleSource.substring(0, helperStartIndex);
        }

        Set<String> keywords = new TreeSet<>();
        collectReservedKeywords(grammarSource, keywords);

        Matcher literalMatcher = RASCAL_LITERAL_PATTERN.matcher(grammarSource);
        while (literalMatcher.find()) {
            String literal = unescape(literalMatcher.group(1));
            if (literal.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                keywords.add(literal);
            }
        }

        keywords.add("context");
        keywords.add("import");
        keywords.add("package");
        keywords.add("pattern");

        return new ArrayList<>(keywords);
    }

    private static List<String> parseConditionOperators(String oracleSource) {
        String conditionBody = syntaxBody(oracleSource, "Cond");
        Set<String> conditionOperators = new TreeSet<>();
        for (String alternative : conditionBody.split("\\|")) {
            List<String> literals = leadingLiterals(alternative);
            if (!literals.isEmpty()) {
                conditionOperators.add(String.join(" ", literals));
            }
        }

        return new ArrayList<>(conditionOperators);
    }

    private static List<String> parseRootKeywords(String oracleSource) {
        Set<String> rootKeywords = new TreeSet<>();
        rootKeywords.addAll(leadingLiterals(syntaxBody(oracleSource, "Package")));
        rootKeywords.addAll(leadingLiterals(syntaxBody(oracleSource, "Import")));
        rootKeywords.addAll(leadingLiterals(syntaxBody(oracleSource, "Assert")));

        String declarationBody = syntaxBody(oracleSource, "Decl");
        for (String alternative : declarationBody.split("\\|")) {
            rootKeywords.addAll(leadingLiterals(alternative));
        }

        return new ArrayList<>(rootKeywords);
    }

    private static List<String> parseConnectorKeywords(String oracleSource) {
        Set<String> connectorKeywords = new TreeSet<>();
        collectRecursiveConnectorKeywords(syntaxBody(oracleSource, "Predicate"), "Predicate", connectorKeywords);
        collectRecursiveConnectorKeywords(syntaxBody(oracleSource, "Cond"), "Cond", connectorKeywords);

        return new ArrayList<>(connectorKeywords);
    }

    private static String syntaxBody(String oracleSource, String syntaxName) {
        Pattern syntaxPattern = Pattern.compile(String.format(SYNTAX_BODY_PATTERN.pattern(), syntaxName));
        Matcher syntaxMatcher = syntaxPattern.matcher(oracleSource);
        if (!syntaxMatcher.find()) {
            return "";
        }

        return syntaxMatcher.group(1);
    }

    private static void collectRecursiveConnectorKeywords(String syntaxBody, String syntaxName, Set<String> connectorKeywords) {
        Pattern connectorPattern = Pattern.compile(
            "\\b" + Pattern.quote(syntaxName) + "\\b[^\\r\\n\"]*\"((?:\\\\.|[^\"])*)\"[^\\r\\n\"]*\\b" + Pattern.quote(syntaxName) + "\\b"
        );
        Matcher connectorMatcher = connectorPattern.matcher(syntaxBody);
        while (connectorMatcher.find()) {
            connectorKeywords.add(unescape(connectorMatcher.group(1)));
        }
    }

    private static List<String> leadingLiterals(String alternative) {
        String normalizedAlternative = alternative.stripLeading()
            .replaceFirst("^[A-Za-z_][A-Za-z0-9_-]*:\\s*", "");
        List<String> literals = new ArrayList<>();
        Matcher literalMatcher = RASCAL_LITERAL_PATTERN.matcher(normalizedAlternative);
        int expectedStart = firstMeaningfulIndex(normalizedAlternative);
        while (literalMatcher.find()) {
            String between = normalizedAlternative.substring(expectedStart, literalMatcher.start()).trim();
            if (!between.isEmpty() && !between.equals(">") && !between.equals("left")) {
                break;
            }

            literals.add(unescape(literalMatcher.group(1)));
            expectedStart = literalMatcher.end();
        }

        return literals;
    }

    private static int firstMeaningfulIndex(String alternative) {
        String trimmedAlternative = alternative.trim();
        if (trimmedAlternative.startsWith(">")) {
            return alternative.indexOf('>') + 1;
        }

        return alternative.length() - alternative.stripLeading().length();
    }

    private static void collectReservedKeywords(String oracleSource, Set<String> keywords) {
        Matcher reservedMatcher = RESERVED_PATTERN.matcher(oracleSource);
        if (!reservedMatcher.find()) {
            return;
        }

        Matcher literalMatcher = RASCAL_LITERAL_PATTERN.matcher(reservedMatcher.group(1));
        while (literalMatcher.find()) {
            keywords.add(unescape(literalMatcher.group(1)));
        }
    }

    private static List<String> parseLocales(String localeSource) {
        Set<String> locales = new TreeSet<>();
        Matcher localeMatcher = LOCALE_PATTERN.matcher(localeSource);
        while (localeMatcher.find()) {
            locales.add(localeMatcher.group(1));
        }

        return new ArrayList<>(locales);
    }

    private static String normalizeType(String type) {
        return type.trim().replaceAll("\\s+", "");
    }

    private static String unescape(String value) {
        StringBuilder builder = new StringBuilder(value.length());
        boolean escaped = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (!escaped) {
                if (current == '\\') {
                    escaped = true;
                } else {
                    builder.append(current);
                }
                continue;
            }

            switch (current) {
                case 'b':
                    builder.append('\b');
                    break;
                case 'n':
                    builder.append('\n');
                    break;
                case 'r':
                    builder.append('\r');
                    break;
                case 'f':
                    builder.append('\f');
                    break;
                case 't':
                    builder.append('\t');
                    break;
                case '\\':
                    builder.append('\\');
                    break;
                case '"':
                    builder.append('"');
                    break;
                default:
                    builder.append(current);
                    break;
            }
            escaped = false;
        }

        return builder.toString();
    }
}
