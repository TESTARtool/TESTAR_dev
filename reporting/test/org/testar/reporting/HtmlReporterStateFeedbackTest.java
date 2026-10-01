package org.testar.reporting;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;

public class HtmlReporterStateFeedbackTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void addsEscapedStateFeedbackToReport() throws Exception {
        File reportFile = temporaryFolder.newFile("state.html");
        State state = mock(State.class, invocation -> {
            if ("get".equals(invocation.getMethod().getName()) && invocation.getArguments().length == 2) {
                if (Tags.StateFeedback.equals(invocation.getArgument(0))) {
                    return "<page source timeout>";
                }
                return invocation.getArgument(1);
            }
            return RETURNS_DEFAULTS.answer(invocation);
        });

        HtmlReporter reporter = new HtmlReporter(reportFile.getAbsolutePath());
        reporter.addState(state);

        String html = new String(Files.readAllBytes(reportFile.toPath()), StandardCharsets.UTF_8);
        assertTrue(html.contains("State Feedback:</strong> &lt;page source timeout&gt;"));
        assertFalse(html.contains("<page source timeout>"));
    }
}
