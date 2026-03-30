/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.policy;

import java.util.Collection;
import java.util.List;

import org.testar.core.action.policy.ClickablePolicy;
import org.testar.core.alayer.Role;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.windows.alayer.UIARoles;

/**
 * Default Windows/UIA clickable-role policy.
 */
public final class WindowsClickablePolicy implements ClickablePolicy {

    @Override
    public boolean isClickable(Widget widget) {
        Role role = widget.get(Tags.Role, null);
        return role != null && getClickableRoles().stream().anyMatch(role::isA);
    }

    public static Collection<Role> getClickableRoles() {
        return List.of(
                UIARoles.UIAButton,
                UIARoles.UIACheckBox,
                UIARoles.UIAComboBox,
                UIARoles.UIAHyperlink,
                UIARoles.UIAListItem,
                UIARoles.UIAMenuItem,
                UIARoles.UIARadioButton,
                UIARoles.UIASlider,
                UIARoles.UIASplitButton,
                UIARoles.UIATabItem,
                UIARoles.UIATreeItem
        );
    }
}
