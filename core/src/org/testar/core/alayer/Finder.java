/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.io.Serializable;

import org.testar.core.state.Widget;
import org.testar.core.exceptions.WidgetNotFoundException;

/**
 * A Finder's task is to find a particular widget within a widget tree. It starts it's search from a location within the tree
 * (typically the root). Finder's are abstract representations of widgets and implement a particular search strategy, e.g.
 * "find the widget with the title 'Save'" or "the widget which is the 3rd child of another widget of type 'Canvas'".
 * 
 * Finders must be serializable and are often used to implement actions, e.g. "click on the widget with title 'Save'".
 */
public interface Finder extends Serializable {
    
    /**
     * Apply the search strategy implemented by this finder and start the search from start.
     * @param start the node from where to start the search
     * @return a non-null reference to the located widget.
     * @throws WidgetNotFoundException if no widget has been found
     */
    Widget apply(Widget start) throws WidgetNotFoundException;
    
    /**
     * Retrieves cached widget, if caching was activated. 
     * @return The widget, or 'null'.
     */
    Widget getCachedWidget();
}
