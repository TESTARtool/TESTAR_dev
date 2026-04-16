/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.scriptless.mode;

import org.testar.OutputStructure;
import org.testar.config.TestarMode;
import org.testar.core.action.Action;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.ComposedProtocol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class GenerateMode {

    private boolean exceptionThrown = false;

    public void runGenerateOuterLoop(ComposedProtocol protocol) {
        protocol.initializeTestSession();
        protocol.runtimeContext().setSequenceCount(1);

        while (protocol.runtimeContext().mode() != TestarMode.Quit && protocol.stopCriteriaTestSession()) {
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
					protocol.runtimeContext().stateModelManager().notifyNewStateReached(initialState, Collections.emptySet());
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
                protocol.runtimeContext().stateModelManager().notifyTestSequenceInterruptedBySystem(stackTrace.toString());
                exceptionThrown = true;
                Verdict unexpectedCloseVerdict = new Verdict(Verdict.Severity.UNEXPECTEDCLOSE, "System is offline! Closed Unexpectedly! I assume it crashed!");
                finishGeneratedSequence(protocol, system, Arrays.asList(unexpectedCloseVerdict));
            }
        }

        if (protocol.runtimeContext().mode() == TestarMode.Quit && !exceptionThrown) {
            protocol.runtimeContext().stateModelManager().notifyTestSequenceInterruptedByUser();
        }

        protocol.runtimeContext().stateModelManager().notifyTestingEnded();
        protocol.runtimeContext().setMode(TestarMode.Quit);
    }

    private List<Verdict> runGenerateInnerLoop(ComposedProtocol protocol, SUT system, State state) throws Exception {
        Set<Action> actions = protocol.deriveActions(system, state);
        protocol.runtimeContext().sessionReportingManager().addActions(actions);
        protocol.runtimeContext().stateModelManager().notifyNewStateReached(state, actions);

        while (protocol.runtimeContext().mode() != TestarMode.Quit && protocol.stopCriteriaTestSequence(state)) {
            LogSerialiser.log("Obtained system state in inner loop of TESTAR...\n", LogSerialiser.LogLevel.Debug);
            protocol.runtimeContext().canvas().begin();
            Util.clear(protocol.runtimeContext().canvas());

            Action action = protocol.selectAction(state, actions);

            //Showing the actions if visualization is on:
			if(protocol.runtimeContext().isVisualizationEnabled()) {
                protocol.visualizationListener().visualizeActions(state, actions);
                protocol.visualizationListener().visualizeSelectedAction(state, action);
            }

            protocol.runtimeContext().stateModelManager().notifyActionExecution(action);
            protocol.executeAction(system, state, action);
            protocol.runtimeContext().setActionCount(protocol.runtimeContext().actionCount() + 1);

            Util.clear(protocol.runtimeContext().canvas());
            protocol.runtimeContext().canvas().end();

            state = protocol.getState(system);
            protocol.getVerdicts(system, state);
            actions = protocol.deriveActions(system, state);
            protocol.runtimeContext().sessionReportingManager().addActions(actions);
            protocol.runtimeContext().stateModelManager().notifyNewStateReached(state, actions);
        }

        return state.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
    }

    private void finishGeneratedSequence(ComposedProtocol protocol, SUT system, List<Verdict> finalVerdicts) {
        protocol.runtimeContext().stateModelManager().notifyTestSequenceStopped();

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
