/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.scriptless.mode;

import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.action.Action;
import org.testar.core.alayer.Pen;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.util.Util;
import org.testar.plugin.NativeLinker;
import org.testar.scriptless.ComposedProtocol;

import java.util.Set;

/**
 * Spy-mode loop.
 */
public class SpyMode {

    public void runSpyLoop(ComposedProtocol protocol) {
        // Register native events listener
        protocol.initializeTestSession();

        // Create or detect the SUT & build canvas representation
        SUT system = protocol.startSystem();
        protocol.runtimeContext().setCanvas(NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT));

        while (protocol.runtimeContext().mode() == TestarMode.Spy && system.isRunning()) {
            State state = protocol.getState(system);
            protocol.runtimeContext().canvas().begin();

            Set<Action> actions = protocol.deriveActions(system, state);

            // When we have the state and actions, clear and draw new visualizers
            Util.clear(protocol.runtimeContext().canvas());

            // in Spy-mode, always visualize the widget info under the mouse cursor
            protocol.visualizationListener().visualizeState(state);

            // in Spy-mode, always visualize the green dots:
            protocol.visualizationListener().visualizeActions(state, actions);

            // in some OS mode like WebDriver, paint all visualizers
            protocol.runtimeContext().canvas().paintBatch();

            protocol.runtimeContext().canvas().end();

            int refreshMs = (int) (protocol.runtimeContext().settings().get(ConfigTags.RefreshSpyCanvas, 0.5) * 1000);
            synchronized (this) {
                try {
                    this.wait(refreshMs);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }

        // If user closes the SUT while in Spy-mode, TESTAR will close (or go back to SettingsDialog)
        if (!system.isRunning()) {
            protocol.runtimeContext().setMode(TestarMode.Quit);
        }

        Util.clear(protocol.runtimeContext().canvas());
        protocol.runtimeContext().canvas().end();

        // Stop and close the SUT
        protocol.stopSystem(system);
    }
}
