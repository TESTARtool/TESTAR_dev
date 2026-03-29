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
 * An action which releases a given Button on the StandardMouse of an SUT.
 */
public final class MouseUp extends TaggableBase implements Action {

    private static final long serialVersionUID = -3335028037420381947L;
    private final MouseButtons btn;

    public MouseUp(MouseButtons btn) {
        Assert.notNull(btn);
        this.btn = btn;
    }

    public String toString() {
        return "Release Mouse Button " + btn;
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.MouseUp.name())) {
                return "Mouse button released";
            }
        }
        return toString();
    }

    public void run(SUT system, State state, double duration) {
        try {
            Assert.notNull(system);
            Util.pause(duration);
            system.get(Tags.StandardMouse).release(btn);
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
