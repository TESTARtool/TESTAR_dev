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
	<T> T get(UIAWidget widget, Tag<T> tag, T defaultValue) {
		// first we check if it is a state management tag that has been mapped to an automation tag
		Tag<T> stateManagementTag = UIAMapping.getMappedStateTag(tag);
		if (stateManagementTag != null) {
			tag = stateManagementTag;
		}

		Object returnObject = widget.tags.get(tag);

		if(returnObject != null){
			return (T)returnObject;
		}else if(widget.uiaElement == null || widget.tags.containsKey(tag)){
			return defaultValue;
		}

		// check the automation element for the tag
		returnObject = widget.uiaElement.get(tag, null);
		if (returnObject != null) {
			return (T)returnObject;
		}
		
		if(tag.equals(Tags.Desc)){
			returnObject = widget.uiaElement.name;
		}else if(tag.equals(Tags.Role)){
			returnObject = UIARoles.fromTypeId(widget.uiaElement.ctrlId);
		}else if(tag.equals(Tags.HitTester)){
			returnObject = new UIAHitTester(widget.uiaElement);
		}else if(tag.equals(Tags.Shape)){
			returnObject = widget.uiaElement.rect;
		}else if(tag.equals(Tags.Blocked)){
			returnObject = widget.uiaElement.blocked;
		}else if(tag.equals(Tags.Enabled)){
			returnObject = widget.uiaElement.enabled;
		}else if(tag.equals(Tags.Title)){
			returnObject = widget.uiaElement.name;
		}else if (tag.equals(Tags.ValuePattern)) {
			returnObject = widget.uiaElement.valuePattern;
		}else if(tag.equals(Tags.ToolTipText)){
			returnObject = widget.uiaElement.helpText;
		}else if(tag.equals(Tags.PID)){
			returnObject = widget == this ? ((UIARootElement) uiaElement).pid : null;
		}else if(tag.equals(Tags.IsRunning)){
			returnObject = widget == this ? ((UIARootElement) uiaElement).isRunning : null;
		}else if(tag.equals(Tags.TimeStamp)){
			returnObject = widget == this ? ((UIARootElement) uiaElement).timeStamp : null;
		}else if(tag.equals(Tags.Foreground)){
			returnObject = widget == this ? ((UIARootElement) uiaElement).isForeground : null;
		}else if(tag.equals(Tags.HasStandardKeyboard)){
			returnObject = widget == this ? ((UIARootElement) uiaElement).hasStandardKeyboard : null;
		}else if(tag.equals(Tags.HasStandardMouse)){
			returnObject = widget == this ? ((UIARootElement) uiaElement).hasStandardMouse : null;
		}else if(tag.equals(UIATags.UIAName)){
			returnObject = widget.uiaElement.name;
		}else if(tag.equals(UIATags.UIAOrientation)){
			returnObject = widget.uiaElement.orientation;
		// begin by urueda
		}else if(tag.equals(Tags.ZIndex)){
			returnObject = widget.uiaElement.zindex;
		}else if(tag.equals(UIATags.UIAIsWindowModal)){
			returnObject = widget.uiaElement.isModal;
		}else if(tag.equals(UIATags.UIAIsTopmostWindow)){
			returnObject = widget.uiaElement.isTopmostWnd;
		}else if(tag.equals(UIATags.UIAIsContentElement)){
			returnObject = widget.uiaElement.isContentElement;
		}else if(tag.equals(UIATags.UIAIsControlElement)){
			returnObject = widget.uiaElement.isControlElement;
		}else if(tag.equals(UIATags.UIAIsScrollPatternAvailable)){
			returnObject = widget.uiaElement.scrollPattern;
		//}else if(t.equals(UIATags.UIAScrollbarInfo)){
		//	ret = w.uiaElement.scrollbarInfo;
		//}else if(t.equals(UIATags.UIAScrollbarInfoH)){
		//	ret = w.uiaElement.scrollbarInfoH;
		//}else if(t.equals(UIATags.UIAScrollbarInfoV)){
		//	ret = w.uiaElement.scrollbarInfoV;
		}else if(tag.equals(UIATags.UIAHorizontallyScrollable)){
			returnObject = widget.uiaElement.hScroll;
		}else if(tag.equals(UIATags.UIAVerticallyScrollable)){
			returnObject = widget.uiaElement.vScroll;
		}else if(tag.equals(UIATags.UIAScrollHorizontalViewSize)){
			returnObject = widget.uiaElement.hScrollViewSize;
		}else if(tag.equals(UIATags.UIAScrollVerticalViewSize)){
			returnObject = widget.uiaElement.vScrollViewSize;
		}else if(tag.equals(UIATags.UIAScrollHorizontalPercent)){
			returnObject = widget.uiaElement.hScrollPercent;
		}else if(tag.equals(UIATags.UIAScrollVerticalPercent)){
			returnObject = widget.uiaElement.vScrollPercent;
		// end by urueda
		}else if(tag.equals(UIATags.UIAHelpText)){
			returnObject = widget.uiaElement.helpText;
		}else if(tag.equals(UIATags.UIAClassName)){
			returnObject = widget.uiaElement.className;
		}else if(tag.equals(UIATags.UIAControlType)){
			returnObject = widget.uiaElement.ctrlId;
		}else if(tag.equals(UIATags.UIACulture)){
			returnObject = widget.uiaElement.culture;
		}else if(tag.equals(UIATags.UIAFrameworkId)){
			returnObject = widget.uiaElement.frameworkId;
		}else if(tag.equals(UIATags.UIAHasKeyboardFocus)){
			returnObject = widget.uiaElement.hasKeyboardFocus;
		}else if(tag.equals(UIATags.UIAIsKeyboardFocusable)){
			returnObject = widget.uiaElement.isKeyboardFocusable;
		}else if(tag.equals(UIATags.UIAProviderDescription)){
			returnObject = widget.uiaElement.providerDesc;
		}else if(tag.equals(UIATags.UIAWindowInteractionState)){
			returnObject = widget.uiaElement.wndInteractionState;
		}else if(tag.equals(UIATags.UIAWindowVisualState)){
			returnObject = widget.uiaElement.wndVisualState;
		}else if(tag.equals(UIATags.UIAAutomationId)){
			returnObject = widget.uiaElement.automationId;
		}else if(tag.equals(UIATags.UIAAcceleratorKey)){
			returnObject = widget.uiaElement.acceleratorKey;
		}else if(tag.equals(UIATags.UIAAccessKey)){
			returnObject = widget.uiaElement.accessKey;
		}
		
		cacheTag(widget, tag, returnObject);
		
		return (returnObject == null) ? defaultValue : (T)returnObject;
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
