/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.ios;

import org.testar.monkey.Assert;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.ios.enums.IOSMapping;
import org.testar.monkey.alayer.ios.enums.IOSRoles;
import org.testar.monkey.alayer.ios.enums.IOSTags;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class IOSState extends IOSWidget implements State {
	private static final long serialVersionUID = 6164678210625030830L;

	public IOSState(IOSRootElement root) {
		super(null, null, root);
		this.root = this;
	}

	public Iterator<Widget> iterator() {
		Iterator<Widget> iterator = new WidgetIterator(this);
		// If root element is null, disable iterating
		if (this.element == null) {
			iterator.next();
		}
		return iterator;
	}

	public void remove(IOSWidget w) {
		Assert.isTrue(this != w, "You cannot remove the root!");
		assert (w.parent != null);
		w.parent.children.remove(w);
		invalidate(w);
	}

	public void invalidate(IOSWidget w) {
		if (w.element != null) {
			w.element.backRef = null;
		}
		w.root = null;
		for (IOSWidget c : w.children) {
			invalidate(c);
		}
	}

	public void setParent(IOSWidget w, Widget parent, int idx) {
		Assert.notNull(parent);
		Assert.isTrue(parent instanceof IOSWidget);
		Assert.isTrue(w != this, "You cannot set the root's parent!");
		assert (w.parent != null);

		IOSWidget webParent = (IOSWidget) parent;
		Assert.isTrue(webParent.root == this);
		Assert.isTrue(!Util.isAncestorOf(w, parent), "The parent is a descendent of this widget!");

		w.parent.children.remove(w);
		webParent.children.add(idx, w);
		w.parent = webParent;
	}

	IOSWidget addChild(IOSWidget parent, IOSElement element) {
		IOSWidget ret = new IOSWidget(this, parent, element);
		return ret;
	}

	void connect(IOSWidget parent, IOSWidget child) {
		parent.children.add(child);
	}

	public <T> T get(IOSWidget w, Tag<T> t) {
		T ret = get(w, t, null);
		if (ret == null) {
			throw new NoSuchTagException(t);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(IOSWidget w, Tag<T> t, T defaultValue) {

		Tag<T> stateManagementTag = IOSMapping.getMappedStateTag(t);
		if (stateManagementTag != null) {
			t = stateManagementTag;
		}

		Object ret = w.tags.get(t);

		if (ret != null) {
			return (T)ret;
		}

		else if (w.element == null || w.tags.containsKey(t)) {
			return defaultValue;
		}

		/**
		 * Generic SUT Tags
		 */
		if (t.equals(Tags.Desc)) {
			ret = w.element.className;
		}
		else if (t.equals(Tags.Role)) {
			ret = IOSRoles.iosWidget;
		}
		else if (t.equals(Tags.HitTester)) {
			ret = new IOSHitTester(w.element);
		}
		else if (t.equals(Tags.Shape)) {
			ret = w.element.rect;
		}
		else if (t.equals(Tags.Blocked)) {
			ret = w.element.blocked;
		}
		else if (t.equals(Tags.Enabled)) {
			ret = w.element.enabled;
		}
		else if (t.equals(Tags.Title)) {
			ret = w.element.text;
		}
		else if (t.equals(Tags.PID)) {
			ret = (long) -1;
		}
		else if (t.equals(Tags.HWND)) {
			ret = w.element.root.windowsHandle;
		}
		else if (t.equals(Tags.IsRunning)) {
			ret = true;
		}
		else if (t.equals(Tags.TimeStamp)) {
			//ret = w == this ? ((IOSRootElement) element).timeStamp : null;
		}
		else if (t.equals(Tags.Foreground)) {
			ret = true;
		}
		else if (t.equals(Tags.ZIndex)) {
			ret = w.element.zindex;
		}
		/**
		 * Specific IOS Tags
		 */
		else if (t.equals(IOSTags.iosEnabled)) {
			ret = w.element.enabled;
		}
		else if (t.equals(IOSTags.iosBounds)) {
			ret = w.element.bounds;
		}
		else if (t.equals(IOSTags.iosNodeIndex)) {
			ret = w.element.nodeIndex;
		}
		else if (t.equals(IOSTags.iosText)) {
			ret = w.element.text;
		}
//		else if (t.equals(IOSTags.iosResourceId)) {
//			ret = w.element.resourceId;
//		}
		else if (t.equals(IOSTags.iosClassName)) {
			ret = w.element.className;
		}
//		else if (t.equals(IOSTags.iosPackageName)) {
//			ret = w.element.packageName;
//		}
//		else if (t.equals(IOSTags.iosCheckable)) {
//			ret = w.element.checkable;
//		}
//		else if (t.equals(IOSTags.iosChecked)) {
//			ret = w.element.checked;
//		}
//		else if (t.equals(IOSTags.iosClickable)) {
//			ret = w.element.clickable;
//		}
//		else if (t.equals(IOSTags.iosFocusable)) {
//			ret = w.element.focusable;
//		}
//		else if (t.equals(IOSTags.iosFocused)) {
//			ret = w.element.focused;
//		}
//		else if (t.equals(IOSTags.iosScrollable)) {
//			ret = w.element.scrollable;
//		}
//		else if (t.equals(IOSTags.iosLongClickable)) {
//			ret = w.element.longclicklable;
//		}
//		else if (t.equals(IOSTags.iosPassword)) {
//			ret = w.element.clickable;
//		}
//		else if (t.equals(IOSTags.iosSelected)) {
//			ret = w.element.selected;
//		}
		else if (t.equals(IOSTags.iosAccessibilityId)) {
			ret = w.element.accessibilityId;
		}
		else if (t.equals(IOSTags.iosVisible)) {
			ret = w.element.visible;
		}
		else if (t.equals(IOSTags.iosX)) {
			ret = w.element.x;
		}
		else if (t.equals(IOSTags.iosY)) {
			ret = w.element.y;
		}
		else if (t.equals(IOSTags.iosWidth)) {
			ret = w.element.width;
		}
		else if (t.equals(IOSTags.iosHeight)) {
			ret = w.element.height;
		}
		else if (t.equals(IOSTags.iosLabel)) {
			ret = w.element.label;
		}

		cacheTag(w, t, ret);

		return (ret == null) ? defaultValue : (T) ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T cacheTag(IOSWidget w, Tag<T> t, Object value) {
		w.tags.put(t, value);
		return (T) value;
	}

	public <T> void setTag(IOSWidget w, Tag<T> t, T value) {
		Assert.notNull(value);
		w.tags.put(t, value);
	}

	public <T> void remove(IOSWidget w, Tag<T> t) {
		Assert.notNull(w, t);
		w.tags.put(t, null);
	}

	public IOSWidget getChild(IOSWidget w, int idx) {
		return w.children.get(idx);
	}

	public int childCount(IOSWidget w) {
		return w.children.size();
	}

	public IOSWidget getParent(IOSWidget w) {
		return w.parent;
	}

	Iterable<Tag<?>> tags(final IOSWidget w) {
		Assert.notNull(w);

		// compile a query set
		final Set<Tag<?>> queryTags = new HashSet<Tag<?>>();
		queryTags.addAll(tags.keySet());
		queryTags.addAll(Tags.tagSet());
		queryTags.addAll(IOSTags.tagSet());

		Iterable<Tag<?>> ret = new Iterable<Tag<?>>() {
			public Iterator<Tag<?>> iterator() {
				return new Iterator<Tag<?>>() {
					Iterator<Tag<?>> i = queryTags.iterator();
					IOSWidget target = w;
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

	public String toString() {
		return Util.treeDesc(this, 2, IOSTags.iosClassName, Tags.Title, IOSTags.iosAccessibilityId, IOSTags.iosX, IOSTags.iosY, IOSTags.iosWidth, IOSTags.iosHeight, IOSTags.iosBounds);
	}
}
