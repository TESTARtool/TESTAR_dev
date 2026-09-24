package org.testar.engine.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Pair;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class DefaultOracleEvaluationServiceTest {

    private static final String SUSPICIOUS_TAG_PATTERN = ".*none.*|.*[eE]xcep[ct]ion.*|.*error.*";

    private StateStub state;
    private WidgetStub widget;
    private DefaultOracleEvaluationService service;

    @Before
    public void setUp() {
        state = new StateStub();
        widget = new WidgetStub();
        widget.setParent(state);
        state.addChild(widget);
        state.set(Tags.IsRunning, true);
        state.set(Tags.NotResponding, false);

        List<Pair<?, ?>> tags = new ArrayList<>();
        tags.add(Pair.from(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER));
        tags.add(Pair.from(ConfigTags.SUTConnectorValue, "https://example.org"));
        tags.add(Pair.from(ConfigTags.SuspiciousTags, SUSPICIOUS_TAG_PATTERN));
        tags.add(Pair.from(ConfigTags.TagsForSuspiciousOracle, Arrays.asList("None", "Title", "ValuePattern")));
        service = new DefaultOracleEvaluationService(new Settings(tags, new Properties()));
    }

    @Test
    public void returnsOkWhenNoConfiguredTagIsSuspicious() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "");

        List<Verdict> verdicts = service.getVerdicts(null, state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.OK, verdicts.get(0));
    }

    @Test
    public void returnsOkWhenConfiguredTagsAreAbsent() {
        List<Verdict> verdicts = service.getVerdicts(null, state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.OK, verdicts.get(0));
    }

    @Test
    public void reportsSuspiciousTitleAndNormalizesLineBreaks() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Line1\nerror\rLine3");

        List<Verdict> verdicts = service.getVerdicts(null, state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.SUSPICIOUS_TAG.getValue(), verdicts.get(0).severity(), 0.0);
        assertTrue(verdicts.get(0).info().contains("Line1 error Line3"));
    }

    @Test
    public void ignoresValuePatternOnEditWidgets() {
        widget.set(Tags.Role, Role.from("UIAEdit"));
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "TESTAR writes an error message in the edit widget");

        List<Verdict> verdicts = service.getVerdicts(null, state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.OK, verdicts.get(0));
    }

    @Test
    public void reportsSuspiciousValuePatternOnNonEditWidgets() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "ValuePattern internal error");

        List<Verdict> verdicts = service.getVerdicts(null, state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.SUSPICIOUS_TAG.getValue(), verdicts.get(0).severity(), 0.0);
        assertTrue(verdicts.get(0).info().contains("ValuePattern internal error"));
    }

    @Test
    public void keepsSuspiciousTagMatchingCaseSensitive() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "This contains ERROR in caps");
        widget.set(Tags.ValuePattern, "This contains ERROR in caps");

        List<Verdict> verdicts = service.getVerdicts(null, state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.OK, verdicts.get(0));
    }
}
