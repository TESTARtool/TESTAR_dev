package org.testar.webstudio.api;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;
import org.testar.webstudio.api.dto.RegexValidationResultDto;
import org.testar.webstudio.workspace.WorkspaceService;

public class WorkspaceControllerRegexValidationTest {

    private final WorkspaceController controller = new WorkspaceController(
            new WorkspaceService(Path.of("workspaces"))
    );

    @Test
    public void acceptsValidRegularExpression() {
        RegexValidationResultDto result = controller.validateRegex("notepad.*");

        Assert.assertTrue(result.valid());
    }

    @Test
    public void reportsInvalidRegularExpression() {
        RegexValidationResultDto result = controller.validateRegex("[notepad");

        Assert.assertFalse(result.valid());
        Assert.assertNotNull(result.errorIndex());
    }
}
