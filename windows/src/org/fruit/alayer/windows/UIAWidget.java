/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

import static org.fruit.alayer.windows.UIATags.UIAHorizontallyScrollable;
import static org.fruit.alayer.windows.UIATags.UIAScrollHorizontalPercent;
import static org.fruit.alayer.windows.UIATags.UIAScrollHorizontalViewSize;
import static org.fruit.alayer.windows.UIATags.UIAScrollPattern;
import static org.fruit.alayer.windows.UIATags.UIAScrollVerticalPercent;
import static org.fruit.alayer.windows.UIATags.UIAScrollVerticalViewSize;
import static org.fruit.alayer.windows.UIATags.UIAVerticallyScrollable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.Role;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

class UIAWidget implements Widget, Serializable {
	private static final long serialVersionUID = 8840515358018797073L;
	UIAState root;
	UIAWidget parent;
	Map<Tag<?>, Object> tags = Util.newHashMap();
	ArrayList<UIAWidget> children = new ArrayList<UIAWidget>();
	UIAElement element;
		
	protected UIAWidget(UIAState root, UIAWidget parent, UIAElement element){
		this.parent = parent;
		this.element = element;
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

	public final <T> T get(Tag<T> t) { /*check;*/ return root.get(this, t); }
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
		boolean hasScroll = get(UIAScrollPattern, null);
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
	 * @param A tabulator for indentation.
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