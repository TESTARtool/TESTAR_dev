/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.stub;

import org.apache.commons.lang3.NotImplementedException;
import org.testar.core.Assert;
import org.testar.core.Drag;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.core.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class WidgetStub extends TaggableBase implements Widget {

    private static final long serialVersionUID = 1828072828801243863L;

    Map<Tag<?>, Object> tags = Util.newHashMap();

    private final List<Widget> widgets = new ArrayList<>();

    private Widget parent = null;
    private State root = null;

    @Override
    public State root() {
        if (root != null) {
            return root;
        }
        if (parent != null) {
            return parent.root();
        }
        if (this instanceof State) {
            return (State) this;
        }
        return null;
    }

    public void setRoot(State root) {
        this.root = root;
    }

    @Override
    public Widget parent() {
        return parent;
    }
    public void setParent(Widget parent) {
        this.parent = parent;
    }

    @Override
    public Widget child(int i) {
        return widgets.get(i);
    }

    @Override
    public int childCount() {
        return widgets.size();
    }

    @Override
    public void remove() {
        throw new NotImplementedException("NotImplementedException");
    }

    @Override
    public void moveTo(Widget p, int idx) {
        throw new NotImplementedException("NotImplementedException");
    }

    @Override
    public Widget addChild() {
        throw new NotImplementedException("NotImplementedException");
    }

    public Widget addChild(Widget widget) {
        widgets.add(widget);
        return this;
    }

    @Override
    public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
        throw new NotImplementedException("NotImplementedException");
    }

    @Override
    public String getRepresentation(String tab) {
        StringBuffer repr = new StringBuffer();
        repr.append(tab + "WIDGET = " + this.get(Tags.ConcreteID) + ", " +
                this.get(Tags.Abstract_R_ID) + ", " +
                this.get(Tags.Abstract_R_T_ID) + ", " +
                this.get(Tags.Abstract_R_T_P_ID) + "\n");
        return repr.toString();
    }

    @Override
    public String toString(Tag<?>... tags) {
        return Util.widgetDesc(this, tags);
    }

    @Override
    public void remove(Tag<?> tag) {
        throw new NotImplementedException("NotImplementedException");
    }

    Iterable<Tag<?>> tags(final Widget w) {
        Assert.notNull(w);

        final Set<Tag<?>> queryTags = new HashSet<Tag<?>>();
        queryTags.addAll(tags.keySet());
        queryTags.addAll(Tags.tagSet());

        Iterable<Tag<?>> ret = new Iterable<Tag<?>>() {
            public Iterator<Tag<?>> iterator() {
                return new Iterator<Tag<?>>() {
                    Iterator<Tag<?>> i = queryTags.iterator();
                    Widget target = w;
                    Tag<?> next;

                    private Tag<?> fetchNext() {
                        if (next == null) {
                            while (i.hasNext()) {
                                next = i.next();
                                if (target.get(next, null) != null) {
                                    return next;
                                }
                            }
                            next = null;
                        }
                        return next;
                    }

                    public boolean hasNext() {
                        return fetchNext() != null;
                    }

                    public Tag<?> next() {
                        Tag<?> ret = fetchNext();
                        if (ret == null) {
                            throw new NoSuchElementException();
                        }
                        next = null;
                        return ret;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        return ret;
    }

}
