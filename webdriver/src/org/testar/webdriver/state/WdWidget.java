/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import org.testar.core.Drag;
import org.testar.core.util.Util;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;
import org.testar.webdriver.alayer.WdCanvasDimensions;
import org.testar.webdriver.tag.WdTags;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WdWidget implements Widget, Serializable {
  private static final long serialVersionUID = 1194546788417258853L;

  WdState root;
  WdWidget parent;
  Map<Tag<?>, Object> tags = Util.newHashMap();
  List<WdWidget> children = new ArrayList<>();
  public WdElement element;

  public WdWidget(WdState root, WdWidget parent, WdElement element) {
    this.parent = parent;
    this.element = element;
    this.root = root;

    if (parent != null) {
      root.connect(parent, this);
    }
  }

  public String getAttribute(String key) {
    return element.attributeMap.get(key);
  }

  final public void moveTo(Widget p, int idx) {
    root.setParent(this, p, idx);
  }

  public final WdWidget addChild() {
    return root.addChild(this, null);
  }

  public final WdState root() {
    return root;
  }

  public final WdWidget parent() {
    return root.getParent(this);
  }

  public final WdWidget child(int i) {
    return root.getChild(this, i);
  }

  public final void remove() {
    root.remove(this);
  }

  public final int childCount() {
    return root.childCount(this);
  }

  public final <T> T get(Tag<T> tag) {
    return root.get(this, tag);
  }

  public final <T> void set(Tag<T> tag, T value) {
    root.setTag(this, tag, value);
  }

  public final <T> T get(Tag<T> tag, T defaultValue) {
    return root.get(this, tag, defaultValue);
  }

  public final Iterable<Tag<?>> tags() {
    return root.tags(this);
  }

  public final void remove(Tag<?> tag) {
    root.remove(this, tag);
  }

  private double[] calculateScrollDragPoints(int dragC, double fixedPoint, double fragment) { // returns relative points
    double dragP = 0.0;
    double[] dragPoints = new double[dragC];
    for (int i = 0; i < dragC; i++) {
      if (Math.abs(fixedPoint - dragP) < fragment) {
        dragP += fragment;
      }
      dragPoints[i] = dragP;
      dragP += fragment;
    }
    dragPoints[dragC - 1] -= 5;
    return dragPoints;
  }

  private Drag[] getDrags(Shape shape,
                          // true = horizontal, false = vertical
                          boolean scrollOrientation,
                          double viewSize, double scrollPercent,
                          double scrollArrowSize, double scrollThick) {

    final int scrollX = WdCanvasDimensions.getScrollX();
    final int scrollY = WdCanvasDimensions.getScrollY();
    final int innerHeight = WdCanvasDimensions.getInnerHeight();
    final int innerWidth = WdCanvasDimensions.getInnerWidth();
      
    boolean shouldRecompute = (scrollPercent <= 0.0 || scrollPercent > 100.0);
    if (shouldRecompute) {
      double scrollOffset = scrollOrientation ? scrollX : scrollY;
      double innerSize    = scrollOrientation ? innerWidth : innerHeight;

      // viewSize = percentage of document visible (0-100), so...
      double docSize = innerSize * (100.0 / viewSize); // total page height
      double maxScroll = Math.max(1.0, docSize - innerSize);

      scrollPercent = 100.0 * scrollOffset / maxScroll;
      scrollPercent = Math.max(0.0, Math.min(100.0, scrollPercent));
    }

    // Translate shape to page space coordinates
    final double shapePageX = shape.x() + scrollX;
    final double shapePageY = shape.y() + scrollY;

    // system dependent
    double scrollableSize = (scrollOrientation ? shape.width() : shape.height()) - scrollArrowSize * 2;
    double fromX;
    double fromY;

    // horizontal
    if (scrollOrientation) {
      fromX = shapePageX + scrollArrowSize +
              scrollableSize * scrollPercent / 100.0 +
              (scrollPercent < 50.0 ? scrollThick / 2 : -3 * scrollThick / 2);
      fromY = shapePageY + shape.height() - scrollThick / 2;
    }
    // vertical
    else {
      fromX = shapePageX + shape.width() - scrollThick / 2;
      fromY = shapePageY + scrollArrowSize +
              scrollableSize * scrollPercent / 100.0 +
              (scrollPercent < 50.0 ? scrollThick / 2 : -3 * scrollThick / 2);
    }

    int dragC = (int) Math.ceil(100.0 / viewSize) - 1;
    if (dragC < 1) {
      return null;
    }

    double[] dragPoints = calculateScrollDragPoints(dragC,
        scrollOrientation ? fromX - shapePageX : fromY - shapePageY,
        scrollableSize / (double) dragC);

    Drag[] drags = new Drag[dragC];
    for (int i = 0; i < dragC; i++) {
      double toX = scrollOrientation ? shapePageX + scrollArrowSize + dragPoints[i] : fromX;
      double toY = scrollOrientation ? fromY : shapePageY + scrollArrowSize + dragPoints[i];

      drags[i] = new Drag(fromX, fromY, toX, toY);
    }
    
    return drags;
  }

  @Override
  public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
    boolean hScroll = get(WdTags.WebHorizontallyScrollable, Boolean.FALSE);
    boolean vScroll = get(WdTags.WebVerticallyScrollable, Boolean.FALSE);
    if (!hScroll && !vScroll) {
      return null;
    }

    Drag[] hDrags = null;
    if (hScroll) {
      double hViewSize = get(WdTags.WebScrollHorizontalViewSize, Double.MIN_VALUE);
      if (hViewSize > 0) {
        double hScrollPercent = get(WdTags.WebScrollHorizontalPercent, -1.0);
        Shape shape = get(Tags.Shape, null);
        if (shape != null) {
          hDrags = getDrags(shape, true, hViewSize, hScrollPercent, scrollArrowSize, scrollThick);
        }
      }
    }

    Drag[] vDrags = null;
    if (vScroll) {
      double vViewSize = get(WdTags.WebScrollVerticalViewSize, Double.MIN_VALUE);
      if (vViewSize > 0) {
        double vScrollPercent = get(WdTags.WebScrollVerticalPercent, -1.0);
        Shape shape = get(Tags.Shape, null);
        if (shape != null) {
          vDrags = getDrags(shape, false, vViewSize, vScrollPercent, scrollArrowSize, scrollThick);
        }
      }
    }

    return Util.join(hDrags, vDrags);
  }

  /**
   * @param tab A tabulator for indentation.
   * @return Computes a string representation of the widget properties.
   */
  private String getPropertiesRepresentation(String tab) {
    StringBuilder pr = new StringBuilder();
    Role role = this.get(Tags.Role, null);
    if (role != null) {
      pr.append(tab).append("ROLE = ").append(role.toString()).append("\n");
    }
    String title = this.get(Tags.Title, null);
    if (title != null) {
      pr.append(tab).append("TITLE = ").append(title).append("\n");
    }
    Shape shape = this.get(Tags.Shape, null);
    if (shape != null) {
      pr.append(tab).append("SHAPE = ").append(shape.toString()).append("\n");
    }
    pr.append(tab).append("CHILDREN = ").append(this.childCount()).append("\n");
    pr.append(tab).append("PATH = ").append(this.get(Tags.Path)).append("\n");

    // missing any critical property for unique representation?

    return pr.toString();
  }

  /**
   * @param tab tabulator for indentation.
   * @return Computes a string representation for the widget.
   */
  public String getRepresentation(String tab) {
    return tab + "WIDGET = " + this.get(Tags.ConcreteID) + ", " +
           this.get(Tags.Abstract_R_ID) + ", " +
           this.get(Tags.Abstract_R_T_ID) + ", " +
           this.get(Tags.Abstract_R_T_P_ID) + "\n" +
           getPropertiesRepresentation(tab);
  }

  @Override
  public String toString(Tag<?>... tags) {
    return Util.widgetDesc(this, tags);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
	  // I'm having issues serializing some webdriver Tags...
	  Map<Tag<?>, Object> serializableTags = Util.newHashMap();
	  for (Map.Entry<Tag<?>, Object> entry : tags.entrySet()) {
		  if (entry.getKey() instanceof Serializable && entry.getValue() instanceof Serializable) {
			  serializableTags.put(entry.getKey(), entry.getValue());
		  }
	  }
	  // Serialize fields one by one
	  try {
		  out.writeObject(root);
		  out.writeObject(parent);
		  out.writeObject(serializableTags);
		  out.writeObject(children);
		  out.writeObject(element);
	  } catch (IOException e) {
		  // Handle or propagate the exception as needed
		  throw e;
	  }
  }

  @SuppressWarnings("unchecked")
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	  try {
		  // Read fields one by one
		  root = (WdState) in.readObject();
		  parent = (WdWidget) in.readObject();

		  Map<Tag<?>, Object> serializableTags = (Map<Tag<?>, Object>) in.readObject();
		  tags = serializableTags;

		  children = (List<WdWidget>) in.readObject();
		  element = (WdElement) in.readObject();
	  } catch (IOException | ClassNotFoundException e) {
		  // Handle or propagate the exception as needed
		  throw e;
	  }
  }
}
