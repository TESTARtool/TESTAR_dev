/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

public final class Roles {

    private Roles() {
    }

    public static final Role

            Widget = Role.from("Widget"),
            Invalid = Role.from("Invalid"),
            Control = Role.from("Control", Widget),
            Expander = Role.from("Expander", Control),
            Hider = Role.from("Hider", Control),
            Button = Role.from("Button", Control),
            StateButton = Role.from("StateButton", Button),
            ToggleButton = Role.from("ToggleButton", StateButton),
            Item = Role.from("Item", Control),
            ItemContainer = Role.from("ItemContainer", Control),
            Text = Role.from("Text", Control),
            Decoration = Role.from("Decoration", Control),
            Slider = Role.from("Slider", Control),
            Dialog = Role.from("Dialog", Control),
            MessageBox = Role.from("MessageBox", Dialog),
            DragSource = Role.from("DragSource", Control),
            DropTarget = Role.from("DropTarget", Control),
            SUT = Role.from("SUT"),
            System = Role.from("System", Widget),
            Process = Role.from("Process", System);
}
