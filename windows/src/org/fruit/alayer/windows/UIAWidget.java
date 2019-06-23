/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.Role;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import static org.fruit.alayer.windows.UIATags.*;

class UIAWidget implements Widget, Serializable {
	private static final long serialVersionUID = 8840515358018797073L;
	UIAState root;
	UIAWidget parent;
	Map<Tag<?>, Object> tags = Util.newHashMap();
	List<UIAWidget> children = new ArrayList<UIAWidget>();
	UIAElement uiaElement;
		
	protected UIAWidget(UIAState root, UIAWidget parent, UIAElement uiaElement){
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
	final void check(){ if(root == null) throw new IllegalStateException(); }
	
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
		boolean hasScroll = get(UIAIsScrollPatternAvailable, null);
		if (!hasScroll)
			return null;

		Drag[] hDrags = null, vDrags = null;
		boolean hScroll = get(UIAHorizontallyScrollable, Boolean.FALSE);
		if (hScroll){
			double hViewSize = get(UIAScrollHorizontalViewSize, Double.MIN_VALUE);
			if (hViewSize > 0){
				double hScrollPercent = get(UIAScrollHorizontalPercent, -1.0);
				Shape shape = get(Tags.Shape, null);
				if (shape != null){
					hDrags = getDrags(shape,true,hViewSize,hScrollPercent,scrollArrowSize,scrollThick);
				}
			}
		}
		boolean vScroll = get(UIAVerticallyScrollable, Boolean.FALSE);
		if (vScroll){
			double vViewSize = get(UIAScrollVerticalViewSize, Double.MIN_VALUE);
			if (vViewSize > 0){
				double vScrollPercent = get(UIAScrollVerticalPercent, -1.0);
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
	
	// by urueda
	@Override
	public String toString(Tag<?>... tags){
		return Util.treeDesc(this, 2, tags);
	}
		
}
