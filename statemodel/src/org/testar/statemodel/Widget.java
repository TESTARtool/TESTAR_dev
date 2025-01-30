/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel;

import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

import java.util.ArrayList;
import java.util.List;

public class Widget {

    // a set of attributes and values
    private TaggableBase attributes;

    // a unique string identifier for this widget
    private String id;

    // a list of child widgets for this widget
    List<Widget> children;

    // the widget's parent, if not the root element
    Widget parent;

    // for performance reasons, we store the root widget, so we do not have to climb up the widget tree
    // each time we need to access it
    ConcreteState rootWidget;

    public Widget(String id) {
        this.id = id;
        attributes = new TaggableBase();
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
     * This method adds a custom attribute to the concrete class in the form of a tag and its value
     * @param attribute
     * @param value
     */
    public void addAttribute(Tag attribute, Object value) {
        try {
            attributes.set(attribute, value);
        } catch (Exception e) {//TODO what kind of exception?
            System.out.println("Problem adding value for tag " + attribute.name() + " to abstract state");
        }
    }

    /**
     * This method returns the `attributes` that have been added to this concrete state
     * @return
     */
    public TaggableBase getAttributes() {
        return attributes;
    }

    /**
     * This method adds a child to this widget
     * @param child
     */
    public void addChild(Widget child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Returns the list of this widget's children.
     * @return list of child widgets.
     */
    public List<Widget> getChildren() {
        return children;
    }

    /**
     * Method returns this widget's parent.
     * @return
     */
    public Widget getParent() {
        return parent;
    }

    /**
     * Method sets this widget's parent.
     * @param parent
     */
    public void setParent(Widget parent) {
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
