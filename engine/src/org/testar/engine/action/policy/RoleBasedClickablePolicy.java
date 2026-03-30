/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.policy;

import java.util.Collection;
import java.util.Collections;

import org.testar.core.Assert;
import org.testar.core.action.policy.ClickablePolicy;
import org.testar.core.alayer.Role;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

public final class RoleBasedClickablePolicy implements ClickablePolicy {

    private final Collection<Role> clickableRoles;

    public RoleBasedClickablePolicy(Collection<Role> clickableRoles) {
        this.clickableRoles = Collections.unmodifiableCollection(Assert.notNull(clickableRoles));
    }

    @Override
    public boolean isClickable(Widget widget) {
        Role role = widget.get(Tags.Role, null);
        return role != null && Role.isOneOf(role, clickableRoles);
    }
}
