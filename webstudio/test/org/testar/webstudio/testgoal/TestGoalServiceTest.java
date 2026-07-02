package org.testar.webstudio.testgoal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.webstudio.api.dto.TestGoalFileDto;
import org.testar.webstudio.api.dto.TestGoalNodeDto;

public class TestGoalServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createsReadsSavesAndDeletesYamlGoalInsideManagedRoot() throws IOException {
        TestGoalService service = new TestGoalService(temporaryFolder.newFolder("testar-home").toPath());

        TestGoalFileDto created = service.createFile("webdriver_generic", "web/parabank/functional/login.yaml");
        Assert.assertEquals("web/parabank/functional/login.yaml", created.path());
        Assert.assertTrue(created.executable());
        Assert.assertTrue(created.content().contains("new-test-goal"));

        TestGoalFileDto saved = service.saveFile("webdriver_generic", "web/parabank/functional/login.yaml", "version: 1\n");
        Assert.assertEquals("version: 1\n", saved.content());

        TestGoalFileDto read = service.readFile("webdriver_generic", "web/parabank/functional/login.yaml");
        Assert.assertEquals("version: 1\n", read.content());

        service.delete("webdriver_generic", "web/parabank/functional/login.yaml");
        Assert.assertFalse(Files.exists(service.testGoalsRoot("webdriver_generic").resolve("web/parabank/functional/login.yaml")));
    }

    @Test
    public void marksYamlFilesExecutableAndReferenceFilesNonExecutable() throws IOException {
        TestGoalService service = new TestGoalService(temporaryFolder.newFolder("testar-home").toPath());
        service.saveFile("webdriver_generic", "web/parabank/functional/login.yaml", "version: 1\n");
        service.saveFile("webdriver_generic", "web/parabank/README.md", "# Notes\n");

        TestGoalNodeDto tree = service.tree("webdriver_generic");
        TestGoalNodeDto webFolder = tree.children().get(0);
        TestGoalNodeDto parabankFolder = webFolder.children().get(0);
        TestGoalNodeDto functionalFolder = parabankFolder.children().get(0);
        TestGoalNodeDto readmeFile = parabankFolder.children().get(1);
        TestGoalNodeDto yamlFile = functionalFolder.children().get(0);

        Assert.assertTrue(yamlFile.executable());
        Assert.assertFalse(readmeFile.executable());
    }

    @Test
    public void rejectsTraversalOutsideManagedRoot() throws IOException {
        TestGoalService service = new TestGoalService(temporaryFolder.newFolder("testar-home").toPath());
        Path externalFile = temporaryFolder.newFile("external.yaml").toPath();

        try {
            service.saveFile("webdriver_generic", "../external.yaml", "version: 1\n");
            Assert.fail("Expected traversal path to be rejected");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Invalid test goal path"));
        }

        Assert.assertEquals("", Files.readString(externalFile));
    }

    @Test
    public void rejectsDeletingManagedRoot() throws IOException {
        TestGoalService service = new TestGoalService(temporaryFolder.newFolder("testar-home").toPath());

        try {
            service.delete("webdriver_generic", "");
            Assert.fail("Expected managed root deletion to be rejected");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Cannot delete the test goals root folder"));
        }

        Assert.assertTrue(Files.isDirectory(service.testGoalsRoot("webdriver_generic")));
    }

    @Test
    public void storesTestGoalsInsideSelectedWorkspace() throws IOException {
        Path testarHome = temporaryFolder.newFolder("testar-home").toPath();
        TestGoalService service = new TestGoalService(testarHome);

        service.saveFile("webdriver_generic", "login.yaml", "version: 1\n");
        service.saveFile("windows_generic", "desktop.yaml", "version: 1\n");

        Assert.assertTrue(Files.isRegularFile(testarHome.resolve("settings/webdriver_generic/test_goals/login.yaml")));
        Assert.assertTrue(Files.isRegularFile(testarHome.resolve("settings/windows_generic/test_goals/desktop.yaml")));
        Assert.assertFalse(Files.exists(testarHome.resolve(".agents/test_goals/login.yaml")));
        Assert.assertEquals(1, service.tree("webdriver_generic").children().size());
        Assert.assertEquals("login.yaml", service.tree("webdriver_generic").children().get(0).name());
        Assert.assertEquals("desktop.yaml", service.tree("windows_generic").children().get(0).name());
    }

    @Test
    public void rejectsUnsafeWorkspaceNames() throws IOException {
        TestGoalService service = new TestGoalService(temporaryFolder.newFolder("testar-home").toPath());

        try {
            service.tree("../webdriver_generic");
            Assert.fail("Expected unsafe workspace name to be rejected");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Invalid workspace name"));
        }
    }
}
