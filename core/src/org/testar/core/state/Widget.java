/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.state;

import java.io.Serializable;

import org.testar.core.Drag;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Taggable;

/**
 * A Widget is usually a control element of an <code>SUT</code>.
 * Widgets have exactly one parent and can have several children.
 * They are attached to a <code>State</code> and form a Widget Tree.
 * In fact a <code>State</code> is a Widget itself and is the root
 * of the Widget Tree.
 * 
 * @see State
 */
public interface Widget extends Taggable, Serializable {

    State root();
    Widget parent();
    Widget child(int i);
    int childCount();
    void remove();
    void moveTo(Widget p, int idx);
    Widget addChild();

    /**
     * For scrollable widgets, compute drag segments of scrolling options.
     * @param scrollArrowSize The size of scrolling arrows.
     * @param scrollThick The scroller thickness.
     * @return 'null' for non-scrollable widgets or a set of drags, from (x1,y1) to (x2,y2), otherwise.
     * @author: urueda
     */
    Drag[] scrollDrags(double scrollArrowSize, double scrollThick);

    /**
     * @param tab tabulator for indentation.
     * @return Computes a string representation for the widget.
     * @author urueda
     */
    public String getRepresentation(String tab);

    public abstract String toString(Tag<?>... tags);
}
