/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testar.core.Drag;
import org.testar.core.util.Util;
import org.testar.windows.tag.UIATags;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Shape;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;

public class UIAWidget implements Widget, Serializable {
    private static final long serialVersionUID = 8840515358018797073L;
    UIAState root;
    UIAWidget parent;
    Map<Tag<?>, Object> tags = Util.newHashMap();
    List<UIAWidget> children = new ArrayList<UIAWidget>();
    UIAElement uiaElement;
        
    public UIAWidget(UIAState root, UIAWidget parent, UIAElement uiaElement){
        this.parent = parent;
        this.uiaElement = uiaElement;
        this.root = root;
        
        if(parent != null)
            root.connect(parent, this);
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.defaultWriteObject();
    }

    
    final boolean valid(){ return root != null; }
    final void check(){ if(root == null) throw new IllegalStateException("UIAState root is null! Probably SUT stop running..."); }
    
    final public void moveTo(Widget p, int idx) { /*check();*/ root.setParent(this, p, idx); }
    public final UIAWidget addChild() { /*check();*/ return root.addChild(this, null); }
    public final UIAState root() { return root; }
    public final UIAWidget parent() { /*check();*/ return root.getParent(this); }
    public final UIAWidget child(int i) { /*check();*/ return root.getChild(this, i); }
    public final void remove() { /*check();*/ root.remove(this); }
    public final int childCount() { /*check();*/ return root.childCount(this); }

    public final <T> T get(Tag<T> tag) { /*check;*/ return root.get(this, tag); }
    public final <T> void set(Tag<T> tag, T value) { /*check;*/ root.setTag(this, tag, value); }
    public final <T> T get(Tag<T> tag, T defaultValue) { /*check;*/ return root.get(this, tag, defaultValue); }
    public final Iterable<Tag<?>> tags() { /*check;*/ return root.tags(this); }
    public final void remove(Tag<?> tag) { /*check;*/ root.remove(this, tag); }

    // by urueda (scrolls helper)
    private double[] calculateScrollDragPoints(int dragC, double fixedPoint, double fragment){ // returns relative points
        double dragP = 0.0;
        double[] dragPoints = new double[dragC];
        for (int i=0; i<dragC; i++){
            if (Math.abs(fixedPoint - dragP) < fragment)
                dragP += fragment;
            dragPoints[i] = dragP;
            dragP += fragment;
        }
        dragPoints[dragC-1] -= 5;
        return dragPoints;
    }
    
    // by urueda (scrolls helper)
    private Drag[] getDrags(Shape shape,
            boolean scrollOrientation, // true = horizontal, false = vertical
            double viewSize, double scrollPercent,
            double scrollArrowSize, double scrollThick){ // system dependent
        double scrollableSize = (scrollOrientation ? shape.width() : shape.height()) - scrollArrowSize*2;
        double fixedH = 0.0, fixedV = 0.0;
        if (scrollOrientation){ // horizontal
            fixedH = shape.x() + scrollArrowSize +
                    scrollableSize*scrollPercent/100.0 +
                    (scrollPercent < 50.0 ? scrollThick/2 : -3*scrollThick/2);
            fixedV = shape.y() + shape.height() - scrollThick/2;
        } else{ // vertical
            fixedH = shape.x() + shape.width() - scrollThick/2;
            fixedV = shape.y() + scrollArrowSize +
                    scrollableSize*scrollPercent/100.0 +
                    (scrollPercent < 50.0 ? scrollThick/2 : -3*scrollThick/2);
        }
        int dragC = (int)Math.ceil(100.0 / viewSize) - 1;
        if (dragC < 1)
            return null;
        double[] emptyDragPoints = calculateScrollDragPoints(dragC,
                scrollOrientation ? fixedH-shape.x() : fixedV-shape.y(), 
                        scrollableSize/(double)dragC);
        Drag[] drags = new Drag[dragC];
        for (int i=0; i<dragC; i++){
            drags[i] = new Drag(
                fixedH,
                fixedV,
                scrollOrientation ? shape.x() + scrollArrowSize + emptyDragPoints[i] : fixedH,
                scrollOrientation ? fixedV : shape.y() + scrollArrowSize + emptyDragPoints[i]
            );
        }
        return drags;
    }
        
    // by urueda
    @Override
    public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {        
        boolean hasScroll = get(UIATags.UIAIsScrollPatternAvailable, null);
        if (!hasScroll)
            return null;

        Drag[] hDrags = null, vDrags = null;
        boolean hScroll = get(UIATags.UIAHorizontallyScrollable, Boolean.FALSE);
        if (hScroll){
            double hViewSize = get(UIATags.UIAScrollHorizontalViewSize, Double.MIN_VALUE);
            if (hViewSize > 0){
                double hScrollPercent = get(UIATags.UIAScrollHorizontalPercent, -1.0);
                Shape shape = get(Tags.Shape, null);
                if (shape != null){
                    hDrags = getDrags(shape,true,hViewSize,hScrollPercent,scrollArrowSize,scrollThick);
                }
            }
        }
        boolean vScroll = get(UIATags.UIAVerticallyScrollable, Boolean.FALSE);
        if (vScroll){
            double vViewSize = get(UIATags.UIAScrollVerticalViewSize, Double.MIN_VALUE);
            if (vViewSize > 0){
                double vScrollPercent = get(UIATags.UIAScrollVerticalPercent, -1.0);
                Shape shape = get(Tags.Shape, null);
                if (shape != null){
                    vDrags = getDrags(shape,false,vViewSize,vScrollPercent,scrollArrowSize,scrollThick);
                }
            }
        }
                
        return Util.join(hDrags,vDrags);
    }
    
    /**
     * @param tab A tabulator for indentation.
     * @return Computes a string representation of the widget properties.
     * @author urueda
     */
    private String getPropertiesRepresentation(String tab){
        StringBuffer pr = new StringBuffer();
        Role role = this.get(Tags.Role, null);
        if (role != null)
            pr.append(tab + "ROLE = " + role.toString() + "\n");
        String title = this.get(Tags.Title, null);
        if (title != null)
            pr.append(tab + "TITLE = " + title + "\n");
        Shape shape = this.get(Tags.Shape, null);
        if (shape != null)
            pr.append(tab + "SHAPE = " + shape.toString() + "\n");
        pr.append(tab + "CHILDREN = " + this.childCount() + "\n");
        pr.append(tab + "PATH = " + this.get(Tags.Path) + "\n");
        
        // missing any critical property for unique representation?
        
        return pr.toString();
    }
    
    /**
     * @param tab tabulator for indentation.
     * @return Computes a string representation for the widget.
     * @author urueda
     */
    public String getRepresentation(String tab){
        StringBuffer repr = new StringBuffer();
        repr.append(tab + "WIDGET = " + this.get(Tags.ConcreteID) + ", " +
                this.get(Tags.Abstract_R_ID) + ", " +
                this.get(Tags.Abstract_R_T_ID) + ", " +
                this.get(Tags.Abstract_R_T_P_ID) + "\n");
        repr.append(getPropertiesRepresentation(tab));
        return repr.toString();
    }
    
    @Override
    public String toString(Tag<?>... tags){
        return Util.widgetDesc(this, tags);
    }
        
}
