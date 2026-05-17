package org.testar.monkey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestSuspiciousTagsVerdict {

    private static DefaultProtocol defaultProtocol;

    private static StateStub state;
    private static WidgetStub widget;

    @Before
    public void test_setup() {
        state = new StateStub();
        widget = new WidgetStub();

        widget.setParent(state);
        state.addChild(widget);

        // Prepare the suspicious tags settings
        List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
        tags.add(Pair.from(ConfigTags.SuspiciousTags, ".*none.*|.*[eE]xcep[ct]ion.*|.*error.*"));
        tags.add(Pair.from(ConfigTags.TagsForSuspiciousOracle, Arrays.asList("None", "Title", "ValuePattern")));

        defaultProtocol = new DefaultProtocol();
        defaultProtocol.settings = new Settings(tags, new Properties());

        // State is running properly
        state.set(Tags.IsRunning, true);
        state.set(Tags.NotResponding, false);
    }

    @Test
    public void test_verdict_ok_valid_tag_values() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "");
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertEquals(Verdict.OK, verdicts.get(0));
    }

    @Test
    public void test_verdict_ok_empty_tags() {
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertEquals(Verdict.OK, verdicts.get(0));
    }

    @Test
    public void test_verdict_suspicious_title_exception() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "This is an Exception in the SUT");
        widget.set(Tags.ValuePattern, "");
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertTrue(verdicts.get(0).verdictSeverityTitle().equals("SUSPICIOUS_TAG"));
        assertTrue(verdicts.get(0).severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue());
        assertTrue(verdicts.get(0).info().contains("Discovered suspicious widget 'Title' : 'This is an Exception in the SUT'."));
    }

    @Test
    public void test_verdict_suspicious_valuepattern_error() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "ValuePattern internal error");
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertTrue(verdicts.get(0).verdictSeverityTitle().equals("SUSPICIOUS_TAG"));
        assertTrue(verdicts.get(0).severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue());
        assertTrue(verdicts.get(0).info().contains("Discovered suspicious widget 'ValuePattern' : 'ValuePattern internal error'."));
    }

    @Test
    public void test_verdict_ignore_content_edit_elements() {
        widget.set(Tags.Role, UIARoles.UIAEdit);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "TESTAR writes an error message in the edit widget");
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertTrue(verdicts.get(0).equals(Verdict.OK));
        assertTrue(verdicts.get(0).severity() == Verdict.OK.severity());
    }

    @Test
    public void test_newline_and_cr_are_normalized() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Line1\nerror\rLine3");
        widget.set(Tags.ValuePattern, "");
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertTrue(verdicts.get(0).verdictSeverityTitle().equals("SUSPICIOUS_TAG"));
        assertTrue(verdicts.get(0).severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue());
        assertTrue(verdicts.get(0).info().contains("Discovered suspicious widget 'Title' : 'Line1 error Line3'."));
    }

    @Test
    public void test_case_sensitivity_lock_in() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "This contains ERROR in caps");
        widget.set(Tags.ValuePattern, "This contains ERROR in caps");
        List<Verdict> verdicts = defaultProtocol.getVerdicts(state);
        assertEquals(1, verdicts.size());
        assertTrue(verdicts.get(0).equals(Verdict.OK));
        assertTrue(verdicts.get(0).severity() == Verdict.OK.severity());
    }

}
