/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.spy;

import org.testar.android.tag.AndroidTags;
import org.testar.core.state.Widget;

public class DefaultMutableTreeNode extends javax.swing.tree.DefaultMutableTreeNode {

	public DefaultMutableTreeNode() {
        super();
    }

    public DefaultMutableTreeNode(Object userObject) {
        super(userObject);
    }

    public DefaultMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Override
    public String toString() {
        try {
            Widget widget = (Widget)this.getUserObject();
            return widget.get(AndroidTags.AndroidClassName);
        } catch(Exception e) {
            return this.userObject == null ? "" : this.userObject.toString();
        }

    }
}
