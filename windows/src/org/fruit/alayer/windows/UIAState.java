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
		if(w.uiaElement != null)
			w.uiaElement.backRef = null;
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
		}else if(w.uiaElement == null || w.tags.containsKey(t)){
			return defaultValue;
		}
		
		if(t.equals(Tags.Desc)){
			ret = w.uiaElement.name;
		}else if(t.equals(Tags.Role)){
			ret = UIARoles.fromTypeId(w.uiaElement.ctrlId);
		}else if(t.equals(Tags.HitTester)){
			ret = new UIAHitTester(w.uiaElement);
		}else if(t.equals(Tags.Shape)){	
			ret = w.uiaElement.rect;
		}else if(t.equals(Tags.Blocked)){
			ret = w.uiaElement.blocked;
		}else if(t.equals(Tags.Enabled)){
			ret = w.uiaElement.enabled;
		}else if(t.equals(Tags.Title)){
			ret = w.uiaElement.name;
		}else if (t.equals(Tags.ValuePattern)) {
			ret = w.uiaElement.valuePattern;
		}else if(t.equals(Tags.ToolTipText)){
			ret = w.uiaElement.helpText;
		}else if(t.equals(Tags.PID)){
			ret = w == this ? ((UIARootElement) uiaElement).pid : null;
		}else if(t.equals(Tags.IsRunning)){
			ret = w == this ? ((UIARootElement) uiaElement).isRunning : null;
		}else if(t.equals(Tags.TimeStamp)){
			ret = w == this ? ((UIARootElement) uiaElement).timeStamp : null;
		}else if(t.equals(Tags.Foreground)){
			ret = w == this ? ((UIARootElement) uiaElement).isForeground : null;
		}else if(t.equals(Tags.HasStandardKeyboard)){
			ret = w == this ? ((UIARootElement) uiaElement).hasStandardKeyboard : null;
		}else if(t.equals(Tags.HasStandardMouse)){
			ret = w == this ? ((UIARootElement) uiaElement).hasStandardMouse : null;
		}else if(t.equals(UIATags.UIAName)){ 
			ret = w.uiaElement.name;
		}else if(t.equals(UIATags.UIAOrientation)){
			ret = w.uiaElement.orientation;
		// begin by urueda
		}else if(t.equals(Tags.ZIndex)){
			ret = w.uiaElement.zindex;
		}else if(t.equals(UIATags.UIAIsWindowModal)){
			ret = w.uiaElement.isModal;
		}else if(t.equals(UIATags.UIAIsTopmostWindow)){
			ret = w.uiaElement.isTopmostWnd;
		}else if(t.equals(UIATags.UIAIsContentElement)){
			ret = w.uiaElement.isContentElement;
		}else if(t.equals(UIATags.UIAIsControlElement)){
			ret = w.uiaElement.isControlElement;
		}else if(t.equals(UIATags.UIAScrollPattern)){
			ret = w.uiaElement.scrollPattern;
		//}else if(t.equals(UIATags.UIAScrollbarInfo)){
		//	ret = w.uiaElement.scrollbarInfo;
		//}else if(t.equals(UIATags.UIAScrollbarInfoH)){
		//	ret = w.uiaElement.scrollbarInfoH;
		//}else if(t.equals(UIATags.UIAScrollbarInfoV)){
		//	ret = w.uiaElement.scrollbarInfoV;
		}else if(t.equals(UIATags.UIAHorizontallyScrollable)){
			ret = w.uiaElement.hScroll;
		}else if(t.equals(UIATags.UIAVerticallyScrollable)){
			ret = w.uiaElement.vScroll;
		}else if(t.equals(UIATags.UIAScrollHorizontalViewSize)){
			ret = w.uiaElement.hScrollViewSize;
		}else if(t.equals(UIATags.UIAScrollVerticalViewSize)){
			ret = w.uiaElement.vScrollViewSize;
		}else if(t.equals(UIATags.UIAScrollHorizontalPercent)){
			ret = w.uiaElement.hScrollPercent;
		}else if(t.equals(UIATags.UIAScrollVerticalPercent)){
			ret = w.uiaElement.vScrollPercent;
		// end by urueda
		}else if(t.equals(UIATags.UIAHelpText)){
			ret = w.uiaElement.helpText;
		}else if(t.equals(UIATags.UIAClassName)){
			ret = w.uiaElement.className;
		}else if(t.equals(UIATags.UIAControlType)){
			ret = w.uiaElement.ctrlId;
		}else if(t.equals(UIATags.UIACulture)){
			ret = w.uiaElement.culture;
		}else if(t.equals(UIATags.UIAFrameworkId)){
			ret = w.uiaElement.frameworkId;
		}else if(t.equals(UIATags.UIAHasKeyboardFocus)){
			ret = w.uiaElement.hasKeyboardFocus;
		}else if(t.equals(UIATags.UIAIsKeyboardFocusable)){
			ret = w.uiaElement.isKeyboardFocusable;
		}else if(t.equals(UIATags.UIAProviderDescription)){
			ret = w.uiaElement.providerDesc;
		}else if(t.equals(UIATags.UIAWindowInteractionState)){
			ret = w.uiaElement.wndInteractionState;
		}else if(t.equals(UIATags.UIAWindowVisualState)){
			ret = w.uiaElement.wndVisualState;
		}else if(t.equals(UIATags.UIAAutomationId)){
			ret = w.uiaElement.automationId;
		}else if(t.equals(UIATags.UIAAcceleratorKey)){
			ret = w.uiaElement.acceleratorKey;
		}else if(t.equals(UIATags.UIAAccessKey)){
			ret = w.uiaElement.accessKey;
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


	Iterable<Tag<?>> tags(final UIAWidget widget){
		Assert.notNull(widget);

		// compile a query set
		final Set<Tag<?>> queryTags = new HashSet<Tag<?>>();
		queryTags.addAll(tags.keySet());  // the tags that have been set on this widget (state is also a widget)
		queryTags.addAll(Tags.tagSet()); // the tags defined in org.fruit.alayer.Tags
		queryTags.addAll(UIATags.tagSet()); // the tags defined in org.fruit.alayer.windows.UIATags

		Iterable<Tag<?>> returnTags = new Iterable<Tag<?>>(){
			public Iterator<Tag<?>> iterator() {
				return new Iterator<Tag<?>>(){
					Iterator<Tag<?>> tagIterator = queryTags.iterator();
					UIAWidget targetWidget = widget;
					Tag<?> nextTag;

					private Tag<?> fetchNext(){
						if(nextTag == null){
							while(tagIterator.hasNext()){
								nextTag = tagIterator.next();
								if(targetWidget.get(nextTag, null) != null)
									return nextTag;
							}
							nextTag = null;
						}
						return nextTag;
					}

					public boolean hasNext() {
						return fetchNext() != null;
					}

					public Tag<?> next() {
						Tag<?> returnTag = fetchNext();
						if(returnTag == null)
							throw new NoSuchElementException();
						nextTag = null;
						return returnTag;
					}

					public void remove() { throw new UnsupportedOperationException(); }
				};
			}
		};
		return returnTags;
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
