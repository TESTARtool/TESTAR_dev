/**
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.fruit.alayer.webdriver;

import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fruit.alayer.webdriver.enums.WdTags.*;

public class WdWidget implements Widget, Serializable {
  private static final long serialVersionUID = 1194546788417258853L;

  WdState root;
  WdWidget parent;
  Map<Tag<?>, Object> tags = new HashMap<>();
  List<WdWidget> children = new ArrayList<>();
  public WdElement element;

  protected WdWidget(WdState root, WdWidget parent, WdElement element) {
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
    // system dependent
    double scrollableSize = (scrollOrientation ? shape.width() : shape.height()) - scrollArrowSize * 2;
    double fromX;
    double fromY;

    // horizontal
    if (scrollOrientation) {
      fromX = shape.x() + scrollArrowSize +
              scrollableSize * scrollPercent / 100.0 +
              (scrollPercent < 50.0 ? scrollThick / 2 : -3 * scrollThick / 2);
      fromY = shape.y() + shape.height() - scrollThick / 2;
    }
    // vertical
    else {
      fromX = shape.x() + shape.width() - scrollThick / 2;
      fromY = shape.y() + scrollArrowSize +
              scrollableSize * scrollPercent / 100.0 +
              (scrollPercent < 50.0 ? scrollThick / 2 : -3 * scrollThick / 2);
    }

    int dragC = (int) Math.ceil(100.0 / viewSize) - 1;
    if (dragC < 1) {
      return null;
    }
    double[] emptyDragPoints = calculateScrollDragPoints(dragC,
        scrollOrientation ? fromX - shape.x() : fromY - shape.y(),
        scrollableSize / (double) dragC);

    Drag[] drags = new Drag[dragC];
    for (int i = 0; i < dragC; i++) {
      double toX = scrollOrientation ? shape.x() + scrollArrowSize + emptyDragPoints[i] : fromX;
      double toY = scrollOrientation ? fromY : shape.y() + scrollArrowSize + emptyDragPoints[i];

      drags[i] = new Drag(fromX, fromY, toX, toY);
    }
    return drags;
  }

  @Override
  public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
    boolean hScroll = get(WebHorizontallyScrollable, Boolean.FALSE);
    boolean vScroll = get(WebVerticallyScrollable, Boolean.FALSE);
    if (!hScroll && !vScroll) {
      return null;
    }

    Drag[] hDrags = null;
    if (hScroll) {
      double hViewSize = get(WebScrollHorizontalViewSize, Double.MIN_VALUE);
      if (hViewSize > 0) {
        double hScrollPercent = get(WebScrollHorizontalPercent, -1.0);
        Shape shape = get(Tags.Shape, null);
        if (shape != null) {
          hDrags = getDrags(shape, true, hViewSize, hScrollPercent, scrollArrowSize, scrollThick);
        }
      }
    }

    Drag[] vDrags = null;
    if (vScroll) {
      double vViewSize = get(WebScrollVerticalViewSize, Double.MIN_VALUE);
      if (vViewSize > 0) {
        double vScrollPercent = get(WebScrollVerticalPercent, -1.0);
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
    return Util.treeDesc(this, 2, tags);
  }
}
