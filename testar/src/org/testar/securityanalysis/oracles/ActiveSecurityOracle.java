/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.securityanalysis.oracles;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Set;

public abstract class ActiveSecurityOracle extends BaseSecurityOracle {
    protected RemoteWebDriver webDriver;

    public ActiveSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver){
        super(securityResultWriter);
        this.webDriver = webDriver;
    }

    /** Enables oracle to add actions to action pool **/
    public Set<Action> getActions(State state)
    {
        /** Add actions **/
        return new HashSet<>();
    }

    /** Enables oracle to select action **/
    public Set<Action> preSelect(Set<Action> actions)
    {
        return actions;
    }

    /** Lets the oracle know what action is executed for potential followup actions **/
    public void actionSelected(Action action)
    {
    }

    //region helpers
    protected boolean isAtBrowserCanvas(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return false;
        }

        // Widget must be completely visible on viewport for screenshots
        return widget.get(WdTags.WebIsFullOnScreen, false);
    }

    protected boolean isTypeable(Widget widget) {
        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
            if (role.equals(WdRoles.WdINPUT)) {
                String type = ((WdWidget) widget).element.type;
                return WdRoles.typeableInputTypes().contains(type.toLowerCase());
            }
            return true;
        }

        return false;
    }
    //endregion
}
