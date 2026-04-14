/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.scriptless;

import org.testar.OutputStructure;
import org.testar.config.TestarMode;
import org.testar.core.action.Action;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Generate-mode loop.
 */
public class GenerateMode {

    private boolean exceptionThrown = false;

    public void runGenerateOuterLoop(ComposedProtocol protocol) {
        protocol.initializeTestSession();
        protocol.runtimeContext().setSequenceCount(1);

        while (protocol.mode() != TestarMode.Quit && protocol.moreSequences()) {
            exceptionThrown = false;
            SUT system = null;

            synchronized (this) {
                OutputStructure.calculateInnerLoopDateString();
                OutputStructure.sequenceInnerLoopCount++;
            }

            try {
                protocol.startTestSequence();

                system = protocol.startSystem();

                LogSerialiser.log("Obtaining system initial state before beginSequence...\n", LogSerialiser.LogLevel.Debug);
                State initialState = protocol.getState(system);
                List<Verdict> initialVerdicts = protocol.getVerdicts(system, initialState);

                if (!Verdict.helperAreAllVerdictsOK(initialVerdicts)) {
                    // If failure exists in the initial state
                    // Save initial state information in the state model before finishing
					protocol.stateModelManager.notifyNewStateReached(initialState, Collections.emptySet());
                    // Finish the test sequence and state model only with the initial state verdicts
                    finishGeneratedSequence(protocol, system, initialVerdicts);
                } else {
                    LogSerialiser.log("Invoking begin sequence in the initial state...\n", LogSerialiser.LogLevel.Debug);
                    // beginSequence() - a script to interact with GUI, for example login screen
                    protocol.beginSequence(system, initialState);
                    // starting the INNER LOOP with the updated state after SUT modification
                    List<Verdict> finalSequenceVerdicts = runGenerateInnerLoop(protocol, system, protocol.getState(system));
                    // Finish the test sequence and state model with the final state verdicts
                    finishGeneratedSequence(protocol, system, finalSequenceVerdicts);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
                String message = "Thread: name=" + Thread.currentThread().getName() + ",id=" + Thread.currentThread().getId() + ", TESTAR throws exception";
                System.out.println(message);
                StringJoiner stackTrace = new StringJoiner(System.lineSeparator());
                stackTrace.add(message);
                Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).forEach(stackTrace::add);
                protocol.stateModelManager.notifyTestSequenceInterruptedBySystem(stackTrace.toString());
                exceptionThrown = true;
                protocol.emergencyTerminateTestSequence(system, exception);
            }
        }

        if (protocol.mode() == TestarMode.Quit && !exceptionThrown) {
            protocol.stateModelManager.notifyTestSequenceInterruptedByUser();
        }

        protocol.stateModelManager.notifyTestingEnded();
        protocol.setMode(TestarMode.Quit);
    }

    private List<Verdict> runGenerateInnerLoop(ComposedProtocol protocol, SUT system, State state) throws Exception {
        Set<Action> actions = protocol.deriveActions(system, state);
        protocol.runtimeServices().sessionReportingManager().addActions(actions);
        protocol.stateModelManager.notifyNewStateReached(state, actions);

        while (protocol.mode() != TestarMode.Quit && protocol.moreActions(state)) {
            LogSerialiser.log("Obtained system state in inner loop of TESTAR...\n", LogSerialiser.LogLevel.Debug);
            protocol.runtimeContext().canvas().begin();
            Util.clear(protocol.runtimeContext().canvas());

            Action action = protocol.selectAction(state, actions);

            //Showing the actions if visualization is on:
			if(protocol.isVisualizationEnabled()) {
                protocol.visualizeActions(protocol.runtimeContext().canvas(), state, actions);
                protocol.visualizeSelectedAction(protocol.runtimeContext().canvas(), state, action, protocol.settings());
            }

            protocol.stateModelManager.notifyActionExecution(action);
            protocol.executeAction(system, state, action);
            protocol.runtimeContext().setActionCount(protocol.runtimeContext().actionCount() + 1);

            Util.clear(protocol.runtimeContext().canvas());
            protocol.runtimeContext().canvas().end();

            state = protocol.getState(system);
            actions = protocol.deriveActions(system, state);
            protocol.runtimeServices().sessionReportingManager().addActions(actions);
            protocol.stateModelManager.notifyNewStateReached(state, actions);
        }

        return state.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
    }

    private void finishGeneratedSequence(ComposedProtocol protocol, SUT system, List<Verdict> finalVerdicts) {
        protocol.stateModelManager.notifyTestSequenceStopped();

        if (!Verdict.helperAreAllVerdictsOK(finalVerdicts)) {
            LogSerialiser.log("Sequence contained faults!\n", LogSerialiser.LogLevel.Critical);
        }

        protocol.finishTestSequence(finalVerdicts);
        LogSerialiser.log("End of test sequence - shutting down the SUT...\n", LogSerialiser.LogLevel.Info);
        protocol.stopSystem(system);
        LogSerialiser.log("... SUT has been shut down!\n", LogSerialiser.LogLevel.Debug);
        protocol.runtimeContext().setSequenceCount(protocol.runtimeContext().sequenceCount() + 1);
    }
}
