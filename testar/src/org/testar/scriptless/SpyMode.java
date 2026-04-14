/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.scriptless;

import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.util.Util;

import java.util.Set;

/**
 * Spy-mode loop.
 */
public class SpyMode {

    public void runSpyLoop(ComposedProtocol protocol) {
        SUT system = protocol.startSystem();
        protocol.runtimeContext().setCanvas(protocol.buildCanvas());

        while (protocol.mode() == TestarMode.Spy && system.isRunning()) {
            State state = protocol.getState(system);
            protocol.runtimeContext().canvas().begin();

            Set<Action> actions = protocol.deriveActions(system, state);

            Util.clear(protocol.runtimeContext().canvas());
            protocol.visualizeState(protocol.runtimeContext().canvas(), state);
            protocol.visualizeActions(protocol.runtimeContext().canvas(), state, actions);
            protocol.runtimeContext().canvas().end();

            int refreshMs = (int) (protocol.settings().get(ConfigTags.RefreshSpyCanvas, 0.5) * 1000);
            synchronized (this) {
                try {
                    this.wait(refreshMs);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (!system.isRunning()) {
            protocol.setMode(TestarMode.Quit);
        }

        Util.clear(protocol.runtimeContext().canvas());
        protocol.runtimeContext().canvas().end();
        protocol.stopSystem(system);
    }
}
