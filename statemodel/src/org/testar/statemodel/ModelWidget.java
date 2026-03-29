/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ModelWidget extends TaggableEntity {

    // a unique string identifier for this widget
    private final String id;

    // a list of child widgets for this widget
    private final List<ModelWidget> children;

    // the widget's parent, if not the root element
    private ModelWidget parent;

    // for performance reasons, we store the root widget, so we do not have to climb up the widget tree
    // each time we need to access it
    private ConcreteState rootWidget;

    public ModelWidget(String id) {
        super();
        this.id = Objects.requireNonNull(id, "ModelWidget ID cannot be null");
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("ModelWidget ID cannot be empty or blank");
        }
        children = new ArrayList<>();
    }

    /**
     * This method returns the widget id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * This method adds a child to this widget
     * @param child
     */
    public void addChild(ModelWidget child) {
        Objects.requireNonNull(child, "Child widget cannot be null");
        children.add(child);
        child.setParent(this);
    }

    /**
     * Returns the list of this widget's children.
     * @return list of child widgets.
     */
    public List<ModelWidget> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Method returns this widget's parent.
     * @return
     */
    public ModelWidget getParent() {
        return parent;
    }

    /**
     * Method sets this widget's parent.
     * @param parent
     */
    public void setParent(ModelWidget parent) {
        this.parent = parent;
    }

    /**
     * Method returns the root widget for this widget.
     * @return
     */
    public ConcreteState getRootWidget() {
        return rootWidget;
    }

    /**
     * Method sets the root widget for this widget.
     * @param rootWidget
     */
    public void setRootWidget(ConcreteState rootWidget) {
        this.rootWidget = rootWidget;
    }
}
