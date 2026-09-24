package org.testar.scriptless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.verdict.VerdictProcessing;
import org.testar.core.Pair;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.capability.StopCriteriaCapability;
import org.testar.stub.StateStub;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class StopCriteriaCapabilityTest {

    @Test
    public void continuesWhenStateIsHealthyAndLimitsAreNotReached() {
        RuntimeContext context = context(true, 5, 31536000.0);
        context.setActionCount(1);
        context.setStartTime(Util.time());

        assertTrue(new StopCriteriaCapability().stopTestSequence(context, state(true, false, Verdict.OK)));
    }

    @Test
    public void stopsWhenMaximumTimeIsReached() {
        RuntimeContext context = context(true, 5, 60.0);
        context.setActionCount(1);
        context.setStartTime(Util.time() - 70.0);

        assertFalse(new StopCriteriaCapability().stopTestSequence(context, state(true, false, Verdict.OK)));
    }

    @Test
    public void stopsWhenSequenceLengthIsExceeded() {
        RuntimeContext context = context(true, 5, 31536000.0);
        context.setActionCount(6);
        context.setStartTime(Util.time());

        assertFalse(new StopCriteriaCapability().stopTestSequence(context, state(true, false, Verdict.OK)));
    }

    @Test
    public void stopsOnFaultWhenConfiguredToStopOnFault() {
        RuntimeContext context = context(true, 5, 31536000.0);
        context.setActionCount(1);
        context.setStartTime(Util.time());

        assertFalse(new StopCriteriaCapability().stopTestSequence(
                context,
                state(true, false, new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "suspicious"))));
    }

    @Test
    public void continuesOnFaultWhenStopOnFaultIsDisabled() {
        RuntimeContext context = context(false, 5, 31536000.0);
        context.setActionCount(1);
        context.setStartTime(Util.time());

        assertTrue(new StopCriteriaCapability().stopTestSequence(
                context,
                state(true, false, new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "suspicious"))));
    }

    @Test
    public void stopsWhenSystemIsNotRunningOrNotResponding() {
        RuntimeContext context = context(true, 5, 31536000.0);
        context.setActionCount(1);
        context.setStartTime(Util.time());

        assertFalse(new StopCriteriaCapability().stopTestSequence(context, state(false, false, Verdict.OK)));
        assertFalse(new StopCriteriaCapability().stopTestSequence(context, state(true, true, Verdict.OK)));
    }

    private RuntimeContext context(boolean stopOnFault, int sequenceLength, double maxTime) {
        List<Pair<?, ?>> tags = new ArrayList<>();
        tags.add(Pair.from(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER));
        tags.add(Pair.from(ConfigTags.SUTConnectorValue, "https://example.org"));
        tags.add(Pair.from(ConfigTags.StopGenerationOnFault, stopOnFault));
        tags.add(Pair.from(ConfigTags.SequenceLength, sequenceLength));
        tags.add(Pair.from(ConfigTags.MaxTime, maxTime));
        Settings settings = new Settings(tags, new Properties());

        RuntimeContext context = new RuntimeContext();
        context.setSettings(settings);
        context.setVerdictProcessing(new VerdictProcessing(settings));
        return context;
    }

    private State state(boolean running, boolean notResponding, Verdict verdict) {
        StateStub state = new StateStub();
        state.set(Tags.IsRunning, running);
        state.set(Tags.NotResponding, notResponding);
        state.set(Tags.OracleVerdicts, Collections.singletonList(verdict));
        return state;
    }
}
