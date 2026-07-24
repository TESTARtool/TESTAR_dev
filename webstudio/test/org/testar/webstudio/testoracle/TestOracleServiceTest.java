package org.testar.webstudio.testoracle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.oracle.OracleSelection;
import org.testar.rascal.DslOracleMetadata;
import org.testar.webstudio.api.dto.TestOracleInventoryDto;
import org.testar.webstudio.api.dto.TestOracleItemDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.workspace.WorkspaceService;

public class TestOracleServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void dslMetadataExposesRascalOracleEditorMetadata() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        DslOracleMetadata metadata = testOracleService.dslMetadata();

        Assert.assertTrue(metadata.keywords().contains("assert"));
        Assert.assertTrue(metadata.widgetTypes().contains("static_text"));
        Assert.assertTrue(metadata.fieldNames().contains("visible"));
        Assert.assertTrue(metadata.conditionOperators().contains("has nonempty"));
    }

    @Test
    public void inventoryExposesWorkspaceJavaAndDslOracles() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        TestOracleInventoryDto inventory = testOracleService.inventory("webdriver_generic");
        Map<String, TestOracleItemDto> itemsByKey = inventory.items().stream()
            .collect(Collectors.toMap(item -> item.origin() + ":" + item.name(), item -> item));

        Assert.assertEquals("webdriver_generic", inventory.workspaceName());
        Assert.assertTrue(inventory.activeOracles().contains("WorkspaceJavaOracle"));

        TestOracleItemDto workspaceOracle = itemsByKey.get("WORKSPACE_JAVA:WorkspaceJavaOracle");
        Assert.assertNotNull(workspaceOracle);
        Assert.assertTrue(workspaceOracle.active());
        Assert.assertTrue(workspaceOracle.editable());
        Assert.assertEquals("WorkspaceJavaOracle.java", workspaceOracle.path());

        TestOracleItemDto dslOracle = itemsByKey.get("DSL_SOURCE:login.testar");
        Assert.assertNotNull(dslOracle);
        Assert.assertFalse(dslOracle.active());
        Assert.assertTrue(dslOracle.editable());
        Assert.assertEquals("flows/login.testar", dslOracle.path());
    }

    @Test
    public void inventoryMarksWorkspaceJavaOracleThatOverridesBuiltInName() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");
        String builtInOracleName = OracleSelection.getAvailableBuiltInOracles().stream()
            .findFirst()
            .orElse("");
        Assume.assumeFalse("No built-in oracle classes are available for override detection.", builtInOracleName.isBlank());

        Path workspaceJavaDir = workspaceDirectory("webdriver_generic").resolve("oracles").resolve("java");
        writeWorkspaceOracle(workspaceJavaDir, builtInOracleName);

        TestOracleInventoryDto inventory = testOracleService.inventory("webdriver_generic");
        TestOracleItemDto overridingOracle = inventory.items().stream()
            .filter(item -> "WORKSPACE_JAVA".equals(item.origin()))
            .filter(item -> builtInOracleName.equals(item.name()))
            .findFirst()
            .orElseThrow();

        Assert.assertTrue(overridingOracle.overridesBuiltIn());
    }

    @Test
    public void dslFileOperationsStayInsideWorkspaceDslRoot() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        WorkspaceFileDto createdFile = testOracleService.createDslFile("webdriver_generic", "generated/new_rule.testar");
        Assert.assertEquals("new_rule.testar", createdFile.name());
        Assert.assertEquals("generated/new_rule.testar", createdFile.location());
        Assert.assertEquals("dsl-oracle", createdFile.category());

        WorkspaceFileDto savedFile = testOracleService.saveDslFile(
            "webdriver_generic",
            "generated/new_rule.testar",
            "oracle content"
        );
        Assert.assertEquals("oracle content", savedFile.content());

        WorkspaceFileDto readFile = testOracleService.readDslFile("webdriver_generic", "generated/new_rule.testar");
        Assert.assertEquals("oracle content", readFile.content());

        TestOracleInventoryDto inventory = testOracleService.deleteDslFile("webdriver_generic", "generated/new_rule.testar");
        boolean deletedFileStillListed = inventory.items().stream()
            .anyMatch(item -> "generated/new_rule.testar".equals(item.path()));

        Assert.assertFalse(deletedFileStillListed);
    }

    @Test
    public void dslFileOperationsRejectNonTestarExtension() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        try {
            testOracleService.createDslFile("webdriver_generic", "notes.txt");
            Assert.fail("Expected non-.testar DSL oracle files to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains(".testar"));
        }
    }

    @Test
    public void dslFileOperationsRejectPathTraversal() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        try {
            testOracleService.createDslFile("webdriver_generic", "../escape.testar");
            Assert.fail("Expected path traversal to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Invalid DSL oracle path"));
        }
    }

    @Test
    public void javaFileOperationsStayInsideWorkspaceJavaRoot() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        WorkspaceFileDto createdFile = testOracleService.createJavaFile("webdriver_generic", "generated/NewOracle.java");
        Assert.assertEquals("NewOracle.java", createdFile.name());
        Assert.assertEquals("generated/NewOracle.java", createdFile.location());
        Assert.assertEquals("java-oracle", createdFile.category());
        Assert.assertTrue(readSettings("webdriver_generic").contains("NewOracle"));

        WorkspaceFileDto savedFile = testOracleService.saveJavaFile(
            "webdriver_generic",
            "generated/NewOracle.java",
            "java oracle content"
        );
        Assert.assertEquals("java oracle content", savedFile.content());

        WorkspaceFileDto readFile = testOracleService.readJavaFile("webdriver_generic", "generated/NewOracle.java");
        Assert.assertEquals("java oracle content", readFile.content());

        TestOracleInventoryDto inventory = testOracleService.deleteJavaFile("webdriver_generic", "generated/NewOracle.java");
        boolean deletedFileStillListed = inventory.items().stream()
            .anyMatch(item -> "generated/NewOracle.java".equals(item.path()));

        Assert.assertFalse(deletedFileStillListed);
    }

    @Test
    public void javaFileOperationsRejectNonJavaExtension() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        try {
            testOracleService.createJavaFile("webdriver_generic", "notes.txt");
            Assert.fail("Expected non-.java oracle files to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains(".java"));
        }
    }

    @Test
    public void compileJavaFileReportsSuccessAndDiagnostics() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");
        String validSource = String.join(System.lineSeparator(),
            "import java.util.Collections;",
            "import java.util.List;",
            "",
            "import org.testar.core.state.State;",
            "import org.testar.core.verdict.Verdict;",
            "import org.testar.oracle.Oracle;",
            "",
            "public class CompiledOracle implements Oracle {",
            "    @Override",
            "    public List<Verdict> getVerdicts(State state) {",
            "        markAsNonVacuous();",
            "        return Collections.singletonList(Verdict.OK);",
            "    }",
            "}",
            ""
        );

        var result = testOracleService.compileJavaFile("webdriver_generic", "CompiledOracle.java", validSource);

        Assert.assertTrue(result.message(), result.success());
        Assert.assertEquals("oracle-source", result.scope());
        Assert.assertTrue(result.diagnostics().isEmpty());
        Assert.assertTrue(readSettings("webdriver_generic").contains("CompiledOracle"));
        Assert.assertTrue(Files.isRegularFile(
            workspaceDirectory("webdriver_generic")
                .resolve("oracles")
                .resolve("compiled")
                .resolve("CompiledOracle.class")
        ));
    }

    @Test
    public void compileJavaFileReportsCompilationErrors() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        var result = testOracleService.compileJavaFile(
            "webdriver_generic",
            "BrokenOracle.java",
            "public class BrokenOracle { syntax error }"
        );

        Assert.assertFalse(result.success());
        Assert.assertEquals("oracle-source", result.scope());
        Assert.assertFalse(result.diagnostics().isEmpty());
    }

    @Test
    public void validateDslFileReportsInvalidDslContent() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");

        var result = testOracleService.validateDslFile(
            "webdriver_generic",
            "broken.testar",
            "this is not valid testar dsl"
        );

        Assert.assertFalse(result.success());
        Assert.assertFalse(result.diagnostics().isEmpty());
    }

    @Test
    public void generateJavaFromDslFileWritesGeneratedJavaInWorkspaceJavaRoot() throws Exception {
        TestOracleService testOracleService = createServiceWithWorkspace("webdriver_generic");
        String dslSource = "assert button \"Submit\" is enabled \"button Submit must be enabled\".";

        var result = testOracleService.generateJavaFromDslFile(
            "webdriver_generic",
            "generated/runtime_oracles.testar",
            dslSource
        );

        Assert.assertTrue(result.message(), result.success());
        Assert.assertEquals("generated/runtime_oracles.java", result.generatedJavaPath());
        Assert.assertTrue(readSettings("webdriver_generic").contains("ButtonSubmitMustBeEnabled"));
        String generatedJava = Files.readString(
            workspaceDirectory("webdriver_generic")
                .resolve("oracles")
                .resolve("java")
                .resolve("generated")
                .resolve("runtime_oracles.java"),
            StandardCharsets.UTF_8
        );
        Assert.assertTrue(generatedJava.contains("Verdict.Severity.DSL_INFRACTION"));
        Assert.assertFalse(generatedJava.contains("Verdict.Severity.FAIL"));
        Assert.assertTrue(Files.isRegularFile(
            workspaceDirectory("webdriver_generic")
                .resolve("oracles")
                .resolve("java")
                .resolve("generated")
                .resolve("runtime_oracles.java")
        ));
    }

    private TestOracleService createServiceWithWorkspace(String workspaceName) throws Exception {
        Path settingsRoot = temporaryFolder.newFolder("settings").toPath();
        Path cliSettingsRoot = temporaryFolder.newFolder("cli-settings").toPath();
        Path workspaceDirectory = Files.createDirectories(settingsRoot.resolve(workspaceName));

        Files.writeString(
            workspaceDirectory.resolve("test.settings"),
            "ExtendedOracles = WorkspaceJavaOracle\n",
            StandardCharsets.UTF_8
        );

        Path javaDir = Files.createDirectories(workspaceDirectory.resolve("oracles").resolve("java"));
        writeWorkspaceOracle(javaDir, "WorkspaceJavaOracle");

        Path dslDir = Files.createDirectories(workspaceDirectory.resolve("oracles").resolve("dsl").resolve("flows"));
        Files.writeString(dslDir.resolve("login.testar"), "package generated;\n", StandardCharsets.UTF_8);

        return new TestOracleService(new WorkspaceService(settingsRoot, cliSettingsRoot));
    }

    private Path workspaceDirectory(String workspaceName) {
        return temporaryFolder.getRoot().toPath().resolve("settings").resolve(workspaceName);
    }

    private String readSettings(String workspaceName) throws Exception {
        return Files.readString(workspaceDirectory(workspaceName).resolve("test.settings"), StandardCharsets.UTF_8);
    }

    private void writeWorkspaceOracle(Path javaDir, String className) throws Exception {
        String source = String.join(System.lineSeparator(),
            "import java.util.Collections;",
            "import java.util.List;",
            "",
            "import org.testar.core.state.State;",
            "import org.testar.core.verdict.Verdict;",
            "import org.testar.oracle.Oracle;",
            "",
            "public class " + className + " implements Oracle {",
            "",
            "    @Override",
            "    public List<Verdict> getVerdicts(State state) {",
            "        markAsNonVacuous();",
            "        return Collections.singletonList(Verdict.OK);",
            "    }",
            "}",
            ""
        );

        Files.writeString(javaDir.resolve(className + ".java"), source, StandardCharsets.UTF_8);
    }
}
