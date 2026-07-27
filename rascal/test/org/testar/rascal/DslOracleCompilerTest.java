package org.testar.rascal;

import org.junit.Assert;
import org.junit.Test;

public class DslOracleCompilerTest {

    @Test
    public void validateReportsSemanticDiagnosticsFromRascalJson() {
        DslOracleCompiler compiler = new DslOracleCompiler();

        DslOracleOperationResult result = compiler.validate(
            "broken.testar",
            String.join(System.lineSeparator(),
                "assert for all table",
                "  it.backgroundColor spell checks in en_GB",
                "  \"DSL: Spell checking for English table headers\"."
            )
        );

        DslOracleDiagnostic diagnostic = result.diagnostics().stream()
            .filter(item -> item.message().contains("undefined field"))
            .findFirst()
            .orElseThrow();

        Assert.assertFalse(result.success());
        Assert.assertEquals("ERROR", diagnostic.severity());
        Assert.assertTrue(diagnostic.line() > 0);
        Assert.assertTrue(diagnostic.column() > 0);
        Assert.assertTrue(diagnostic.endLine() > 0);
        Assert.assertTrue(diagnostic.endColumn() > diagnostic.column());
    }

    @Test
    public void validateReportsNoDiagnosticsForValidDsl() {
        DslOracleCompiler compiler = new DslOracleCompiler();

        DslOracleOperationResult result = compiler.validate(
            "valid.testar",
            "assert button \"Submit\" is enabled \"Button Submit must be enabled\"."
        );

        Assert.assertTrue(result.message(), result.success());
        Assert.assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    public void validateNormalizesParseErrors() {
        DslOracleCompiler compiler = new DslOracleCompiler();

        DslOracleOperationResult result = compiler.validate(
            "broken_keyword.testar",
            String.join(System.lineSeparator(),
                "package dsl_generated.parabank_invariants;",
                "",
                "asser static_text \"Welcome John Demo\" is visible",
                "\"DSL: Welcome John Demo is visible\"."
            )
        );

        DslOracleDiagnostic diagnostic = result.diagnostics().get(0);

        Assert.assertFalse(result.success());
        Assert.assertEquals("ERROR", diagnostic.severity());
        Assert.assertEquals("DSL parse error. Check the syntax near this position.", diagnostic.message());
        Assert.assertFalse(diagnostic.message().contains("ParseError"));
        Assert.assertFalse(diagnostic.message().contains("TODO"));
        Assert.assertEquals(3, diagnostic.line());
    }
}
