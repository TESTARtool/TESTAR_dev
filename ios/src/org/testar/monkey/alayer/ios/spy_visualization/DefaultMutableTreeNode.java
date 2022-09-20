package org.testar.monkey.alayer.ios.spy_visualization;

import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.ios.enums.IOSTags;

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
            return widget.get(IOSTags.iosClassName);
        } catch(Exception e) {
            return this.userObject == null ? "" : this.userObject.toString();
        }

    }
}