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
 * An action which releases a given Key on the Keyboard.
 */
public final class KeyUp extends KeyAction {

    private static final long serialVersionUID = -7035337967443813849L;

    public KeyUp(KBKeys key) {
        super(key);
    }

    public String toString() {
        return "Release Key " + key;
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.KeyUp.name())) {
                return "Key released";
            }
        }
        return toString();
    }

    @Override
    protected void performKeyAction(SUT system, KBKeys key) {
        system.get(Tags.StandardKeyboard).release(key);
    }

    @Override
    protected void altNumpad(SUT system, String numpadCodes) { }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof KeyUp && this.key.equals(((KeyUp) o).key));
    }
}
