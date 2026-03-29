/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import java.util.Collections;

import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.devices.ProcessHandle;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

/**
 * An action that kills a process by PID or by Name.
 */
public class KillProcess extends TaggableBase implements Action {

    private static final long serialVersionUID = -1777427445519403935L;
    final String name;
    final Long pid;
    final double waitTime;

    public static KillProcess byName(String name, double timeToWaitForProcessToAppear) {
        return new KillProcess(name, null, timeToWaitForProcessToAppear);
    }

    public static KillProcess byPID(long pid, double timeToWaitForProcessToAppear) {
        return new KillProcess(null, pid, timeToWaitForProcessToAppear);
    }

    private KillProcess(String name, Long pid, double waitTime) {
        Assert.isTrue(!(name == null && pid == null) && waitTime >= 0);
        this.name = name;
        this.pid = pid;
        this.waitTime = waitTime;
    }

    public void run(SUT system, State state, double duration) throws ActionFailedException {
        Assert.notNull(system);
        Assert.isTrue(duration >= 0);
        
        double start = Util.time();
        Util.pause(waitTime);

        for (ProcessHandle ph : Util.makeIterable(system.get(Tags.ProcessHandles,
                Collections.<ProcessHandle>emptyList().iterator()))) {
            if ((pid != null && ph.pid() == pid) || (name != null && name.equals(ph.name()))) {
                ph.kill();
                return;
            }
        }

        Util.pause(duration - (Util.time() - start));
    }

    public String toString() {
        return "KillProcess";
    }

    @Override
    public String toString(Role... discardParameters) {
        return toString();
    }

    @Override
    public String toShortString() {
        Role r = get(Tags.Role, null);
        if (r != null) {
            return r.toString();
        } else {
            return toString();
        }
    }

    @Override
    public String toParametersString() {
        return "";
    }
}
