/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.devices.MouseButtons;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

/**
 * An action which presses a given Button on the StandardMouse of an SUT.
 */
public final class MouseDown extends TaggableBase implements Action {

    private static final long serialVersionUID = 259015065012204913L;
    private final MouseButtons btn;

    public MouseDown(MouseButtons btn) {
        Assert.notNull(btn);
        this.btn = btn;
    }

    public String toString() {
        return "Press Mouse Button " + btn;
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.MouseDown.name())) {
                return "Mouse button pressed";
            }
        }
        return toString();
    }

    public void run(SUT system, State state, double duration) {
        try {
            Assert.notNull(system);
            Util.pause(duration);
            system.get(Tags.StandardMouse).press(btn);
        } catch (NoSuchTagException tue) {
            throw new ActionFailedException(tue);
        }
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
        //return "(" + btn.toString() + ")";
        return "";
    }
}
