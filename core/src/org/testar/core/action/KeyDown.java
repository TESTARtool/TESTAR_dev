/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.devices.KBKeys;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;

/**
 * An action which presses a given Key on the Keyboard.
 */
public final class KeyDown extends KeyAction {

    private static final long serialVersionUID = -462251384755779329L;

    public KeyDown(KBKeys key) {
        super(key);
    }

    public String toString() {
        return "Press Key " + key;
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.KeyDown.name())) {
                return "Key pressed";
            }
        }
        return toString();
    }

    @Override
    protected void performKeyAction(SUT system, KBKeys key) {
        system.get(Tags.StandardKeyboard).press(key);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof KeyDown && this.key.equals(((KeyDown) o).key));
    }
}
