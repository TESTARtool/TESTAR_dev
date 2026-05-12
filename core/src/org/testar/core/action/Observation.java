/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

public class Observation extends TaggableBase implements Action {

    private static final long serialVersionUID = 8622084462407313716L;

    public static final String OBSERVE_DESC = "Observe the SUT";

    public Observation() {
        this.set(Tags.Desc, OBSERVE_DESC);
        this.set(Tags.Role, ActionRoles.ObserveAction);
    }

    public void run(SUT system, State state, double duration) {
        Util.pause(duration);
    }

    public String toString() {
        return OBSERVE_DESC;
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
