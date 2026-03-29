/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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
