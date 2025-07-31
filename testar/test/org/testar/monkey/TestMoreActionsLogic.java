package org.testar.monkey;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;

public class TestMoreActionsLogic {

	@Test
	public void test_continue_more_actions() {
		DefaultProtocol protocol = new DefaultProtocol();

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.StopGenerationOnFault, true));
		tags.add(Pair.from(ConfigTags.SequenceLength, 5));
		tags.add(Pair.from(ConfigTags.MaxTime, 31536000.0));
		protocol.settings = new Settings(tags, new Properties());

		protocol.actionCount = 1;
		protocol.lastSequenceActionNumber = 1;
		protocol.startTime = Util.time();

		StateStub state = new StateStub();
		state.set(Tags.OracleVerdict, new Verdict(Verdict.Severity.OK, "State is OK"));
		state.set(Tags.IsRunning, true);
		state.set(Tags.NotResponding, false);

		// State is OK and we have more actions to execute
		Assert.isTrue(protocol.moreActions(state));
	}

	@Test
	public void test_stop_max_time_reached() {
		DefaultProtocol protocol = new DefaultProtocol();

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.StopGenerationOnFault, true));
		tags.add(Pair.from(ConfigTags.SequenceLength, 5));
		tags.add(Pair.from(ConfigTags.MaxTime, 60.0));
		protocol.settings = new Settings(tags, new Properties());

		protocol.actionCount = 1;
		protocol.lastSequenceActionNumber = 1;
		protocol.startTime = Util.time() - 70.0;

		StateStub state = new StateStub();
		state.set(Tags.OracleVerdict, new Verdict(Verdict.Severity.OK, "State is OK"));
		state.set(Tags.IsRunning, true);
		state.set(Tags.NotResponding, false);

		// State is OK but we stop because the max time was reached
		Assert.isTrue(!protocol.moreActions(state));
	}

	@Test
	public void test_stop_more_actions_due_verdict() {
		DefaultProtocol protocol = new DefaultProtocol();

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.StopGenerationOnFault, true));
		tags.add(Pair.from(ConfigTags.SequenceLength, 5));
		tags.add(Pair.from(ConfigTags.MaxTime, 31536000.0));
		protocol.settings = new Settings(tags, new Properties());

		protocol.actionCount = 1;
		protocol.lastSequenceActionNumber = 1;
		protocol.startTime = Util.time();

		StateStub state = new StateStub();
		state.set(Tags.OracleVerdict, new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "State contains suspicious message"));
		state.set(Tags.IsRunning, true);
		state.set(Tags.NotResponding, false);

		// State contains an error and we stop on fault
		Assert.isTrue(!protocol.moreActions(state));
	}

	@Test
	public void test_more_actions_do_not_stop_fault() {
		DefaultProtocol protocol = new DefaultProtocol();

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.StopGenerationOnFault, false));
		tags.add(Pair.from(ConfigTags.SequenceLength, 5));
		tags.add(Pair.from(ConfigTags.MaxTime, 31536000.0));
		protocol.settings = new Settings(tags, new Properties());

		protocol.actionCount = 1;
		protocol.lastSequenceActionNumber = 1;
		protocol.startTime = Util.time();

		StateStub state = new StateStub();
		state.set(Tags.OracleVerdict, new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "State contains suspicious message"));
		state.set(Tags.IsRunning, true);
		state.set(Tags.NotResponding, false);

		// State contains an error but we continue testing because we do not stop on fault
		Assert.isTrue(protocol.moreActions(state));
	}

	@Test
	public void test_stop_not_running() {
		DefaultProtocol protocol = new DefaultProtocol();

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.StopGenerationOnFault, true));
		tags.add(Pair.from(ConfigTags.SequenceLength, 5));
		tags.add(Pair.from(ConfigTags.MaxTime, 31536000.0));
		protocol.settings = new Settings(tags, new Properties());

		protocol.actionCount = 1;
		protocol.lastSequenceActionNumber = 1;
		protocol.startTime = Util.time();

		StateStub state = new StateStub();
		state.set(Tags.OracleVerdict, new Verdict(Verdict.Severity.OK, "State is OK"));
		state.set(Tags.IsRunning, false);
		state.set(Tags.NotResponding, false);

		// State is OK but we stop because the system is not running
		Assert.isTrue(!protocol.moreActions(state));
	}

	@Test
	public void test_stop_not_responding() {
		DefaultProtocol protocol = new DefaultProtocol();

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.StopGenerationOnFault, true));
		tags.add(Pair.from(ConfigTags.SequenceLength, 5));
		tags.add(Pair.from(ConfigTags.MaxTime, 31536000.0));
		protocol.settings = new Settings(tags, new Properties());

		protocol.actionCount = 1;
		protocol.lastSequenceActionNumber = 1;
		protocol.startTime = Util.time();

		StateStub state = new StateStub();
		state.set(Tags.OracleVerdict, new Verdict(Verdict.Severity.OK, "State is OK"));
		state.set(Tags.IsRunning, true);
		state.set(Tags.NotResponding, true);

		// State is OK but we stop because the system is not responding
		Assert.isTrue(!protocol.moreActions(state));
	}

}
