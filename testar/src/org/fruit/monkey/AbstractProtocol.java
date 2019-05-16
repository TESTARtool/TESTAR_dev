/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.fruit.monkey;

import org.fruit.UnProc;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;

import java.util.Set;

/**
 * This is the abstract flow of TESTAR (generate mode):
 *
 * - Initialize TESTAR settings
 * - InitTestSession (before starting the first sequence)
 * - OUTER LOOP:
 * 		PreSequencePreparations (before each sequence, for example starting WebDriver, JaCoCo or another process for sequence)
 * 		StartSUT
 * 		BeginSequence (starting "script" on the GUI of the SUT, for example login)
 * 		INNER LOOP
 * 			GetState
 * 			GetVerdict
 * 			StopCriteria (moreActions/moreSequences/time?)
 * 			DeriveActions
 * 			SelectAction
 * 			ExecuteAction
 * 		FinishSequence (closing "script" on the GUI of the SUT, for example logout)
 * 		StopSUT
 * 		PostSequenceProcessing (after each sequence)
 * - CloseTestSession (after finishing the last sequence)
 *
 */
public abstract class AbstractProtocol implements UnProc<Settings>	{

	protected Settings settings;
	protected Settings settings(){ return settings; }

	/**
	 * Initialize is run as the first thing to initialize TESTAR with the given settings
	 *
	 * @param settings
	 */
	protected abstract void initialize(Settings settings);

	/**
	 * This method is called before the first test sequence, allowing for example setting up the test environment
	 */
	protected abstract void initTestSession();

	/**
	 * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
	 */
	protected abstract void preSequencePreparations();

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

	//TODO think about creating pre- and post- methods, for example preSelectAction(), postSelectAction()
	//abstract methods for TESTAR flow:

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
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	protected abstract Verdict getVerdict(State state);

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
	 * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
	 * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
	 */
	protected abstract void finishSequence();

	/**
	 * This methods stops the SUT
	 *
	 * @param system
	 */
	protected abstract void stopSystem(SUT system);

	/**
	 * This method is called after each sequence, to allow for example checking the coverage of the sequence
	 */
	protected abstract void postSequenceProcessing();

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	protected abstract void closeTestSession();

}
