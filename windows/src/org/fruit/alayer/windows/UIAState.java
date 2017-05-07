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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.WidgetIterator;
import org.fruit.alayer.exceptions.NoSuchTagException;

final class UIAState extends UIAWidget implements State {
	private static final long serialVersionUID = 7823095941981151363L;

	public UIAState(UIAElement root){
		super(null, null, root);
		this.root = this;
	}

	public Iterator<Widget> iterator() { return new WidgetIterator(this); }

	void remove(UIAWidget w){
		Assert.isTrue(this != w, "You cannot remove the root!");
		assert(w.parent != null);
		w.parent.children.remove(w);
		invalidate(w);
	}

	void invalidate(UIAWidget w){
		if(w.element != null)
			w.element.backRef = null;
		w.root = null;
		for(UIAWidget c : w.children)
			invalidate(c);
	}

	void setParent(UIAWidget w, Widget parent, int idx){
		Assert.notNull(parent);
		Assert.isTrue(parent instanceof UIAWidget);
		Assert.isTrue(w != this, "You cannot set the root's parent!");
		assert(w.parent != null);

		UIAWidget uiaParent = (UIAWidget) parent;
		Assert.isTrue(uiaParent.root == this);
		Assert.isTrue(!Util.isAncestorOf(w, parent), "The parent is a descendent of this widget!");

		w.parent.children.remove(w);
		uiaParent.children.add(idx, w);
		w.parent = uiaParent;
	}

	UIAWidget addChild(UIAWidget parent, UIAElement element){
		UIAWidget ret = new UIAWidget(this, parent, element);
		return ret;
	}

	void connect(UIAWidget parent, UIAWidget child){
		parent.children.add(child);
	}

	<T> T get(UIAWidget w, Tag<T> t){
		T ret = get(w, t, null);
		if(ret == null)
			throw new NoSuchTagException(t);
		return ret;
	}

	@SuppressWarnings("unchecked")
	<T> T get(UIAWidget w, Tag<T> t, T defaultValue) {
		Object ret = w.tags.get(t);

		if(ret != null){
			return (T)ret;
		}else if(w.element == null || w.tags.containsKey(t)){
			return defaultValue;
		}
		
		if(t.equals(Tags.Desc)){
			ret = w.element.name;
		}else if(t.equals(Tags.Role)){
			ret = UIARoles.fromTypeId(w.element.ctrlId);
		}else if(t.equals(Tags.HitTester)){
			ret = new UIAHitTester(w.element);
		}else if(t.equals(Tags.Shape)){	
			ret = w.element.rect;
		}else if(t.equals(Tags.Blocked)){
			ret = w.element.blocked;
		}else if(t.equals(Tags.Enabled)){
			ret = w.element.enabled;
		}else if(t.equals(Tags.Title)){
			ret = w.element.name;
		}else if(t.equals(Tags.ToolTipText)){
			ret = w.element.helpText;
		}else if(t.equals(Tags.PID)){
			ret = w == this ? ((UIARootElement)element).pid : null;
		}else if(t.equals(Tags.IsRunning)){
			ret = w == this ? ((UIARootElement)element).isRunning : null;
		}else if(t.equals(Tags.TimeStamp)){
			ret = w == this ? ((UIARootElement)element).timeStamp : null;
		}else if(t.equals(Tags.Foreground)){
			ret = w == this ? ((UIARootElement)element).isForeground : null;
		}else if(t.equals(Tags.HasStandardKeyboard)){
			ret = w == this ? ((UIARootElement)element).hasStandardKeyboard : null;	
		}else if(t.equals(Tags.HasStandardMouse)){
			ret = w == this ? ((UIARootElement)element).hasStandardMouse : null;				
		}else if(t.equals(UIATags.UIAName)){ 
			ret = w.element.name;
		}else if(t.equals(UIATags.UIAOrientation)){
			ret = w.element.orientation;
		// begin by urueda
		}else if(t.equals(Tags.ZIndex)){
			ret = w.element.zindex;
		}else if(t.equals(UIATags.UIAIsWindowModal)){
			ret = w.element.isModal;
		}else if(t.equals(UIATags.UIAScrollPattern)){
			ret = w.element.scrollPattern;
		//}else if(t.equals(UIATags.UIAScrollbarInfo)){
		//	ret = w.element.scrollbarInfo;
		//}else if(t.equals(UIATags.UIAScrollbarInfoH)){
		//	ret = w.element.scrollbarInfoH;
		//}else if(t.equals(UIATags.UIAScrollbarInfoV)){
		//	ret = w.element.scrollbarInfoV;
		}else if(t.equals(UIATags.UIAHorizontallyScrollable)){
			ret = w.element.hScroll;
		}else if(t.equals(UIATags.UIAVerticallyScrollable)){
			ret = w.element.vScroll;
		}else if(t.equals(UIATags.UIAScrollHorizontalViewSize)){
			ret = w.element.hScrollViewSize;
		}else if(t.equals(UIATags.UIAScrollVerticalViewSize)){
			ret = w.element.vScrollViewSize;
		}else if(t.equals(UIATags.UIAScrollHorizontalPercent)){
			ret = w.element.hScrollPercent;
		}else if(t.equals(UIATags.UIAScrollVerticalPercent)){
			ret = w.element.vScrollPercent;
		// end by urueda
		}else if(t.equals(UIATags.UIAHelpText)){
			ret = w.element.helpText;
		}else if(t.equals(UIATags.UIAClassName)){
			ret = w.element.className;
		}else if(t.equals(UIATags.UIAControlType)){
			ret = w.element.ctrlId;
		}else if(t.equals(UIATags.UIAFrameworkId)){
			ret = w.element.frameworkId;
		}else if(t.equals(UIATags.UIAHasKeyboardFocus)){
			ret = w.element.hasKeyboardFocus;
		}else if(t.equals(UIATags.UIAIsKeyboardFocusable)){
			ret = w.element.isKeyboardFocusable;
		}else if(t.equals(UIATags.UIAProviderDescription)){
			ret = w.element.providerDesc;
		}else if(t.equals(UIATags.UIAWindowInteractionState)){
			ret = w.element.wndInteractionState;
		}else if(t.equals(UIATags.UIAWindowVisualState)){
			ret = w.element.wndVisualState;
		}else if(t.equals(UIATags.UIAAutomationId)){
			ret = w.element.automationId;
		}
		
		cacheTag(w, t, ret);
		
		return (ret == null) ? defaultValue : (T)ret;
	}

	@SuppressWarnings("unchecked")
	<T> T cacheTag(UIAWidget w, Tag<T> t, Object value){
		w.tags.put(t, value);
		return (T)value;
	}

	<T> void setTag(UIAWidget w, Tag<T> t, T value){
		Assert.notNull(value);
		w.tags.put(t, value);
	}

	<T> void remove(UIAWidget w, Tag<T> t){
		Assert.notNull(w, t);
		w.tags.put(t, null);
	}

	UIAWidget getChild(UIAWidget w, int idx){ return w.children.get(idx); }
	int childCount(UIAWidget w){ return w.children.size(); }
	UIAWidget getParent(UIAWidget w){ return w.parent; }


	Iterable<Tag<?>> tags(final UIAWidget w){
		Assert.notNull(w);

		// compile a query set
		final Set<Tag<?>> queryTags = new HashSet<Tag<?>>();
		queryTags.addAll(tags.keySet());
		queryTags.addAll(Tags.tagSet());
		queryTags.addAll(UIATags.tagSet());

		Iterable<Tag<?>> ret = new Iterable<Tag<?>>(){
			public Iterator<Tag<?>> iterator() {
				return new Iterator<Tag<?>>(){
					Iterator<Tag<?>> i = queryTags.iterator();
					UIAWidget target = w;
					Tag<?> next;

					private Tag<?> fetchNext(){
						if(next == null){
							while(i.hasNext()){
								next = i.next();
								if(target.get(next, null) != null)
									return next;
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
						if(ret == null)
							throw new NoSuchElementException();
						next = null;
						return ret;
					}

					public void remove() { throw new UnsupportedOperationException(); }

				};
			}

		};
		return ret;
	}

	//public String toString(){ return Util.treeDesc(this, 2, Tags.Desc); }
	public String toString(){ return Util.treeDesc(this, 2, Tags.Role, Tags.Title); }// by urueda

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}
}