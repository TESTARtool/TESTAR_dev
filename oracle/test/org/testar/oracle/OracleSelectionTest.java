package org.testar.oracle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.config.TestarDirectories;

public class OracleSelectionTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String previousSettingsDir;
    private String previousSelectedSse;

    @Before
    public void setup() throws Exception {
        previousSettingsDir = TestarDirectories.getSettingsDir();
        previousSelectedSse = TestarDirectories.getSelectedSse();

        File settingsRoot = temporaryFolder.newFolder("settings");
        TestarDirectories.setSettingsDir(settingsRoot.getAbsolutePath() + File.separator);
        TestarDirectories.setSelectedSse("webdriver_test");

        File javaOraclesDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        assertTrue(javaOraclesDir.mkdirs());
        writeWorkspaceOracle(javaOraclesDir);
    }

    @After
    public void teardown() {
        TestarDirectories.setSettingsDir(previousSettingsDir);
        TestarDirectories.setSelectedSse(previousSelectedSse);
    }

    @Test
    public void availableExtendedOraclesIncludesWorkspaceJavaOracles() {
        List<String> oracleNames = OracleSelection.getAvailableExtendedOracles();

        assertTrue(oracleNames.contains("WorkspaceJavaOracle"));
    }

    @Test
    public void loadExtendedOraclesResolvesWorkspaceJavaOracles() {
        List<Oracle> oracles = OracleSelection.loadExtendedOracles("WorkspaceJavaOracle");

        assertEquals(1, oracles.size());
        assertEquals("WorkspaceJavaOracle", oracles.get(0).getClass().getSimpleName());
    }

    @Test
    public void loadExtendedOraclesResolvesSelectedWorkspaceOraclesInOrder() throws Exception {
        File javaOraclesDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        writeWorkspaceOracle(javaOraclesDir, "SecondWorkspaceOracle");

        List<Oracle> oracles = OracleSelection.loadExtendedOracles("WorkspaceJavaOracle,SecondWorkspaceOracle");

        assertEquals(2, oracles.size());
        assertEquals("WorkspaceJavaOracle", oracles.get(0).getClass().getSimpleName());
        assertEquals("SecondWorkspaceOracle", oracles.get(1).getClass().getSimpleName());
    }

    @Test
    public void duplicateWorkspaceOracleClassNamesAreRejected() throws Exception {
        File javaOraclesDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        writeWorkspaceOracle(javaOraclesDir, "first", "DuplicateOracle");
        writeWorkspaceOracle(javaOraclesDir, "second", "DuplicateOracle");

        try {
            OracleSelection.loadExtendedOracles("DuplicateOracle");
            fail("Expected duplicate workspace oracle names to be rejected.");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Duplicate workspace oracle class name(s): DuplicateOracle"));
            assertFalse(e.getMessage().isBlank());
        }
    }

    @Test
    public void packagedWorkspaceOracleIsNotRecompiledWhenClassFilesAreCurrent() throws Exception {
        File javaOraclesDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        writeRootSourcePackagedOracle(javaOraclesDir);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previousOut = System.out;

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            OracleSelection.getAvailableExtendedOracles();
            output.reset();

            OracleSelection.getAvailableExtendedOracles();
        } finally {
            System.setOut(previousOut);
        }

        assertFalse(output.toString(StandardCharsets.UTF_8).contains("Compiling added or modified external oracles"));
    }

    @Test
    public void loadExtendedOraclesPrintsWorkspaceLoadingProgressWhenSourcesAreCurrent() {
        OracleSelection.loadExtendedOracles("WorkspaceJavaOracle");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previousOut = System.out;

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            OracleSelection.loadExtendedOracles("WorkspaceJavaOracle");
        } finally {
            System.setOut(previousOut);
        }

        String outputText = output.toString(StandardCharsets.UTF_8);
        assertTrue(outputText.contains("Loading workspace Java oracles from:"));
        assertTrue(outputText.contains("Loaded workspace Java oracles: 1"));
        assertFalse(outputText.contains("Compiling added or modified external oracles"));
    }

    private void writeWorkspaceOracle(File javaOraclesDir) throws Exception {
        writeWorkspaceOracle(javaOraclesDir, "WorkspaceJavaOracle");
    }

    private void writeWorkspaceOracle(File javaOraclesDir, String className) throws Exception {
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
                "    public String getMessage() {",
                "        return \"" + className + "\";",
                "    }",
                "",
                "    @Override",
                "    public List<Verdict> getVerdicts(State state) {",
                "        markAsNonVacuous();",
                "        return Collections.singletonList(Verdict.OK);",
                "    }",
                "}",
                ""
        );

        Files.writeString(
                new File(javaOraclesDir, className + ".java").toPath(),
                source,
                StandardCharsets.UTF_8
        );
    }

    private void writeWorkspaceOracle(File javaOraclesDir, String packageName, String className) throws Exception {
        File packageDir = new File(javaOraclesDir, packageName);
        assertTrue(packageDir.mkdirs());

        String source = String.join(System.lineSeparator(),
                "package " + packageName + ";",
                "",
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

        Files.writeString(
                new File(packageDir, className + ".java").toPath(),
                source,
                StandardCharsets.UTF_8
        );
    }

    private void writeRootSourcePackagedOracle(File javaOraclesDir) throws Exception {
        String source = String.join(System.lineSeparator(),
                "package dsl_generated.packaged_workspace_oracle;",
                "",
                "import java.util.Collections;",
                "import java.util.List;",
                "",
                "import org.testar.core.state.State;",
                "import org.testar.core.verdict.Verdict;",
                "import org.testar.oracle.Oracle;",
                "",
                "public class PackagedWorkspaceOracle {",
                "",
                "    public static class PackagedInnerOracle implements Oracle {",
                "",
                "        @Override",
                "        public List<Verdict> getVerdicts(State state) {",
                "            markAsNonVacuous();",
                "            return Collections.singletonList(Verdict.OK);",
                "        }",
                "    }",
                "}",
                ""
        );

        Files.writeString(
                new File(javaOraclesDir, "PackagedWorkspaceOracle.java").toPath(),
                source,
                StandardCharsets.UTF_8
        );
    }
}
