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

import es.upv.staq.testar.ActionStatus;
import org.fruit.UnProc;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import java.io.File;
import java.util.Set;

public abstract class AbstractProtocol implements UnProc<Settings>	{

	protected Settings settings;
	protected Settings settings(){ return settings; }

	//TODO think about creating pre- and post- methods, for example preSelectAction(), postSelectAction()
	//abstract methods for TESTAR flow:
	protected abstract void beginSequence(SUT system, State state);
	protected abstract void finishSequence(File recordedSequence);
	protected abstract void initialize(Settings settings);
	protected abstract SUT startSystem() throws SystemStartException;
	protected abstract void stopSystem(SUT system);
	protected abstract State getState(SUT system) throws StateBuildException;
	protected abstract Verdict getVerdict(State state);
	protected abstract Set<Action> deriveActions(SUT system, State state) throws ActionBuildException;
	protected abstract Canvas buildCanvas();
	protected abstract boolean moreActions(State state);
	protected abstract boolean moreSequences();
	protected abstract void processListeners(SUT system);
	protected abstract void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus);
	protected abstract boolean waitAdhocTestEventLoop(State state, ActionStatus actionStatus);
	protected abstract boolean waitAutomaticAction(SUT system, State state, Taggable fragment, ActionStatus actionStatus);
	protected abstract Action mapUserEvent(State state);
	protected abstract boolean runAction(Canvas cv, SUT system, State state, Taggable fragment);
	protected abstract void runGenerate(SUT system);
	protected abstract void runSpy(SUT system);
	protected abstract void replay();
	protected abstract void detectLoopMode(SUT system);
	protected abstract void quitSUT(SUT system);

	/**
	 * This is used for synchronizing the loops of TESTAR between automated and manual (and between two automated loops)
	 *
	 * Action execution listeners override.
	 *
	 * @param system
	 * @param state
	 * @param action
	 */
	protected abstract void actionExecuted(SUT system, State state, Action action);

}
