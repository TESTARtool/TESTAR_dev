package org.testar.monkey;

import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.reporting.DummyReportManager;
import org.testar.reporting.ReportManager;
import org.testar.settings.Settings;

public class TestDefaultProtocolReporting {

    @Test
    public void testDefaultDummyReporting() {
        DefaultProtocol protocol = new DefaultProtocol();
        Assert.assertTrue(protocol.reportManager != null);
        Assert.assertTrue(protocol.reportManager instanceof DummyReportManager);
    }

    @Test
    public void testSpyModeDummyReporting() {
        DefaultProtocol protocol = new DefaultProtocol();
        protocol.settings = new Settings();
        protocol.settings.set(ConfigTags.Mode, Modes.Spy);
        protocol.preSequencePreparations();
        Assert.assertTrue(protocol.reportManager != null);
        Assert.assertTrue(protocol.reportManager instanceof DummyReportManager);
    }

    @Test
    public void testGenerateModeReportManager() {
        DefaultProtocol protocol = new DefaultProtocol();
        protocol.settings = new Settings();
        protocol.settings.set(ConfigTags.Mode, Modes.Generate);
        protocol.settings.set(ConfigTags.ReportInHTML, false);
        protocol.settings.set(ConfigTags.ReportInPlainText, false);
        protocol.preSequencePreparations();
        Assert.assertTrue(protocol.reportManager instanceof ReportManager);
    }

    @Test
    public void testReplayModeReportManager() {
        DefaultProtocol protocol = new DefaultProtocol();
        protocol.settings = new Settings();
        protocol.settings.set(ConfigTags.Mode, Modes.Replay);
        protocol.settings.set(ConfigTags.ReportInHTML, false);
        protocol.settings.set(ConfigTags.ReportInPlainText, false);
        protocol.preSequencePreparations();
        Assert.assertTrue(protocol.reportManager instanceof ReportManager);
    }

}
