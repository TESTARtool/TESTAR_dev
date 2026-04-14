/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.capability.ScriptlessCapabilities;
import org.testar.core.exceptions.ActionBuildException;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStartException;

import org.testar.config.settings.Settings;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This is the abstract flow of TESTAR (generate mode):
 *
 * - InitializeSettings (TESTAR settings)
 * - InitializeTestSession (before starting the first sequence)
 * - OUTER LOOP:
 * 		StartTestSequence (before each sequence, for example starting WebDriver, JaCoCo or another process for sequence)
 * 		StartSystem
 *      GetState
 * 		BeginSequence (starting "script" on the GUI of the SUT, for example login)
 * 		INNER LOOP
 * 			GetState
 * 			GetVerdicts
 * 			StopCriteria (moreActions/moreSequences/time?)
 * 			DeriveActions
 * 			SelectAction
 * 			ExecuteAction
 * 		StopSUT
 * 		FinishTestSequence (after each sequence)
 * - CloseTestSession (after finishing the last sequence)
 *
 */
public abstract class AbstractProtocol implements Consumer<Settings> {

	protected Settings settings;
	public final Settings settings() { return settings; }

    protected ScriptlessCapabilities scriptlessCapabilities;
	public final ScriptlessCapabilities scriptlessCapabilities() { return scriptlessCapabilities; }

    protected final RuntimeContext runtimeContext = new RuntimeContext();
	public final RuntimeContext runtimeContext() { return runtimeContext; }

	/**
	 * Initialize is run as the first thing to initialize TESTAR with the given settings
	 *
	 * @param settings
	 */
	protected abstract void initializeSettings(Settings settings);

	/**
	 * This method is called before the first test sequence, allowing for example setting up the test environment
	 */
	protected abstract void initializeTestSession();

	/**
	 * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
	 */
	protected abstract void startTestSequence();

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 *      seconds until they have finished loading)
	 *
	 * @return  a started SUT, ready to be tested.
	 * @throws SystemStartException
	 */
	protected abstract SUT startSystem() throws SystemStartException;

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 *
	 * @param system
	 * @param state
	 */
	protected abstract void beginSequence(SUT system, State state);

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * @param system
	 * @return the current state of the SUT with attached oracle.
	 * @throws StateBuildException
	 */
	protected abstract State getState(SUT system) throws StateBuildException;

	/**
	 * The getVerdicts methods implements the online state oracles that
	 * examine the SUT's current state and returns oracle verdicts.
	 *
	 * @return list of oracle verdicts, which determine whether the state is erroneous and why.
	 */
	protected abstract List<Verdict> getVerdicts(SUT system, State state);

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	protected abstract Set<Action> deriveActions(SUT system, State state) throws ActionBuildException;

	/**
	 * Select one of the available actions using the action selection algorithm of your choice (e.g. at random)
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action
	 */
	protected abstract Action selectAction(State state, Set<Action> actions);

	/**
	 * Execute the selected action.
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	protected abstract boolean executeAction(SUT system, State state, Action action);

	/**
	 * StopCriteria for a sequence:
	 *
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You can stop deriving more actions after:
	 * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 *
	 * @return  if <code>true</code> continue generation, else stop
	 */
	protected abstract boolean moreActions(State state);

	/**
	 * StopCriteria for a test session:
	 *
	 * TESTAR uses this method to determine when to stop the entire test sequence
	 * You could stop the test after:
	 * - a specified amount of sequences, which is specified through the Sequences setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 *
	 * @return  if <code>true</code> continue test, else stop
	 */
	protected abstract boolean moreSequences();

	/**
	 * This methods stops the SUT
	 *
	 * @param system
	 */
	protected abstract void stopSystem(SUT system);

	/**
	 * This method is called after each sequence, to allow for example checking the coverage of the sequence
	 */
	protected abstract void finishTestSequence(List<Verdict> verdicts);

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	protected abstract void closeTestSession();

}
