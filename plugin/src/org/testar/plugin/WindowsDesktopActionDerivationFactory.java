/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Role;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.action.DefaultActionDerivationService;
import org.testar.engine.action.DefaultDesktopWidgetActionDeriver;
import org.testar.engine.action.EscFallbackActionDeriver;
import org.testar.engine.action.ForegroundActionDeriver;
import org.testar.engine.action.KillProcessesActionDeriver;
import org.testar.engine.action.StateActionDeriver;
import org.testar.engine.action.policy.EnabledWidgetFilterPolicy;
import org.testar.engine.action.policy.PredicateTypeablePolicy;
import org.testar.engine.action.policy.RoleBasedClickablePolicy;
import org.testar.engine.action.policy.UnblockedWidgetFilterPolicy;
import org.testar.windows.alayer.UIARoles;
import org.testar.windows.tag.UIATags;

/**
 * Builds the default Windows desktop derive-action service used by modular
 * clients such as CLI. It replaces the older TESTAR-only NativeLinker lookups
 * for clickable/typeable desktop behavior.
 */
public final class WindowsDesktopActionDerivationFactory {

    private static final String DEFAULT_TEXT_INPUT = "TESTAR";

    private WindowsDesktopActionDerivationFactory() {
    }

    public static DefaultActionDerivationService create(String processesToKillRegex) {
        List<org.testar.engine.action.ActionDeriver> forcedDerivers =
                hasProcessKillRegex(processesToKillRegex)
                        ? List.of(
                                new KillProcessesActionDeriver(processesToKillRegex),
                                new ForegroundActionDeriver()
                        )
                        : List.of(new ForegroundActionDeriver());

        return DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(new RoleBasedClickablePolicy(getClickableRoles())),
                Collections.singletonList(new PredicateTypeablePolicy(WindowsDesktopActionDerivationFactory::isTypeable)),
                Collections.singletonList(widget -> widget.scrollDrags(36.0, 16.0) != null),
                List.of(new EnabledWidgetFilterPolicy(), new UnblockedWidgetFilterPolicy()),
                forcedDerivers,
                Collections.singletonList(new StateActionDeriver(
                        new DefaultDesktopWidgetActionDeriver(widget -> DEFAULT_TEXT_INPUT)
                )),
                Collections.singletonList(new EscFallbackActionDeriver())
        );
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

    public static boolean isTypeable(Widget widget) {
        Role role = widget.get(Tags.Role, null);
        if (role == null) {
            return false;
        }
        if (!role.isA(UIARoles.UIAEdit) && !role.isA(UIARoles.UIADocument)) {
            return false;
        }
        if (!widget.get(UIATags.UIAIsKeyboardFocusable, false)) {
            return false;
        }
        return widget.get(UIATags.UIAIsValuePatternAvailable, false)
                && !widget.get(UIATags.UIAValueIsReadOnly, false);
    }

    private static boolean hasProcessKillRegex(String processKillRegex) {
        return processKillRegex != null
                && !processKillRegex.isBlank()
                && !"(?!x)x".equals(processKillRegex);
    }
}
