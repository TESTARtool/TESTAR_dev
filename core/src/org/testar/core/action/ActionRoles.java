/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.alayer.Role;

public final class ActionRoles {

    private ActionRoles() {
    }

    public static final Role
            Action = Role.from("Action"),
            NOPAction = Role.from("NOPAction", Action),
            MouseAction = Role.from("MouseAction", Action),
            KeyboardAction = Role.from("KeyboardAction", Action),
            CompoundAction = Role.from("CompoundAction", Action),
            MouseMove = Role.from("MouseMove", MouseAction),
            MouseDown = Role.from("MouseDown", MouseAction),
            KeyDown = Role.from("KeyDown", KeyboardAction),
            MouseUp = Role.from("MouseUp", MouseAction),
            KeyUp = Role.from("KeyUp", KeyboardAction),
            HitKey = Role.from("HitKey", KeyDown, KeyUp),
            HitESC = Role.from("HitESC", HitKey),
            HitShortcutKey = Role.from("HitShortcutKey", KeyDown, KeyUp),
            Click = Role.from("Click", MouseDown, MouseUp),
            LeftClick = Role.from("LeftClick", Click),
            RightClick = Role.from("RightClick", Click),
            DoubleClick = Role.from("DoubleClick", Click),
            LDoubleClick = Role.from("LDoubleClick", LeftClick, DoubleClick),
            RDoubleClick = Role.from("RDoubleClick", RightClick, DoubleClick),
            ClickAt = Role.from("ClickAt", Click, MouseMove),
            LeftClickAt = Role.from("LeftClickAt", ClickAt, LeftClick),
            RightClickAt = Role.from("RightClickAt", ClickAt, RightClick),
            DoubleClickAt = Role.from("DoubleClickAt", ClickAt, DoubleClick),
            LDoubleClickAt = Role.from("LDoubleClickAt", DoubleClickAt, LeftClick),
            RDoubleClickAt = Role.from("RDoubleClickAt", DoubleClickAt, RightClick),
            Type = Role.from("Type", HitKey),
            Paste = Role.from("Paste", HitKey),
            ClickTypeInto = Role.from("ClickTypeInto", ClickAt, Type),
            DropDown = Role.from("DropDown", Click, KeyDown),
            PasteTextInto = Role.from("PasteTextInto", ClickAt, Paste),
            Drag = Role.from("Drag", MouseDown, MouseUp, MouseMove),
            LeftDrag = Role.from("LeftDrag", Drag);
}
