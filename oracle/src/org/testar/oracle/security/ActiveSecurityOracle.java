/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.action.Action;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.alayer.Shape;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.plugin.NativeLinker;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.state.WdWidget;
import org.testar.webdriver.tag.WdTags;

import java.util.HashSet;
import java.util.Set;

public abstract class ActiveSecurityOracle extends BaseSecurityOracle {

	public enum ActiveOracle {
		SQL_INJECTION("SqlInjectionSecurityOracle"), 
		XSS_INJECTION("XssSecurityOracle"), 
		TOKEN_INVALIDATION("TokenInvalidationSecurityOracle"); 

		private final String oracle;

		ActiveOracle(String oracle) {
			this.oracle = oracle;
		}

		public String getOracle() {
			return oracle;
		}
	}

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
}
