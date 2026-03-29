/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

/**
 * The 'No Operation' action.
 */
public class NOP extends TaggableBase implements Action {

    private static final long serialVersionUID = 8622084462407313716L;

    public static final String NOP_ID = "No Operation";

    public NOP() {
        this.set(Tags.Desc, NOP_ID);
        this.set(Tags.Role, ActionRoles.NOPAction);
    }

    public void run(SUT system, State state, double duration) {
        Util.pause(duration);
    }

    public String toString() {
        return NOP_ID;
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
