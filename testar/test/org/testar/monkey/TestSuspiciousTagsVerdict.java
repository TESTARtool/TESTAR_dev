package org.testar.monkey;

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
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.equals(Verdict.OK));
        assertTrue(verdict.severity() == Verdict.OK.severity());
    }

    @Test
    public void test_verdict_ok_empty_tags() {
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.equals(Verdict.OK));
        assertTrue(verdict.severity() == Verdict.OK.severity());
    }

    @Test
    public void test_verdict_suspicious_title_exception() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "This is an Exception in the SUT");
        widget.set(Tags.ValuePattern, "");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.verdictSeverityTitle().equals("SUSPICIOUS_TAG"));
        assertTrue(verdict.severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue());
        assertTrue(verdict.info().contains("Discovered suspicious widget 'Title' : 'This is an Exception in the SUT'."));
    }

    @Test
    public void test_verdict_suspicious_valuepattern_error() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "ValuePattern internal error");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.verdictSeverityTitle().equals("SUSPICIOUS_TAG"));
        assertTrue(verdict.severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue());
        assertTrue(verdict.info().contains("Discovered suspicious widget 'ValuePattern' : 'ValuePattern internal error'."));
    }

    @Test
    public void test_verdict_ignore_content_edit_elements() {
        widget.set(Tags.Role, UIARoles.UIAEdit);
        widget.set(Tags.Title, "Everything is fine");
        widget.set(Tags.ValuePattern, "TESTAR writes an error message in the edit widget");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.equals(Verdict.OK));
        assertTrue(verdict.severity() == Verdict.OK.severity());
    }

    @Test
    public void test_newline_and_cr_are_normalized() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "Line1\nerror\rLine3");
        widget.set(Tags.ValuePattern, "");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.verdictSeverityTitle().equals("SUSPICIOUS_TAG"));
        assertTrue(verdict.severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue());
        assertTrue(verdict.info().contains("Discovered suspicious widget 'Title' : 'Line1 error Line3'."));
    }

    @Test
    public void test_case_sensitivity_lock_in() {
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "This contains ERROR in caps");
        widget.set(Tags.ValuePattern, "This contains ERROR in caps");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.equals(Verdict.OK));
        assertTrue(verdict.severity() == Verdict.OK.severity());
    }

    @Test
    public void test_ignore_valid_exception_message() {
        defaultProtocol.settings.set(ConfigTags.IgnoredSuspiciousTags, Arrays.asList("msg.DisplayExceptionMessage", "User fix this error"));
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.ValuePattern, "Showing msg.DisplayExceptionMessage popup");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.equals(Verdict.OK));
        assertTrue(verdict.severity() == Verdict.OK.severity());
    }

    @Test
    public void test_ignore_valid_error_message() {
        defaultProtocol.settings.set(ConfigTags.IgnoredSuspiciousTags, Arrays.asList("msg.DisplayExceptionMessage", "User fix this error"));
        widget.set(Tags.Role, Roles.Text);
        widget.set(Tags.Title, "User fix this error: Add username value");
        Verdict verdict = defaultProtocol.getVerdict(state);
        assertTrue(verdict.equals(Verdict.OK));
        assertTrue(verdict.severity() == Verdict.OK.severity());
    }

}
