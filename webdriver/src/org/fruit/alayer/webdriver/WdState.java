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

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public final class WdState extends WdWidget implements State {
	private static final long serialVersionUID = 661696260972010052L;

	public WdState(WdElement root) {
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

	public void remove(WdWidget w) {
		Assert.isTrue(this != w, "You cannot remove the root!");
		assert (w.parent != null);
		w.parent.children.remove(w);
		invalidate(w);
	}

	public void invalidate(WdWidget w) {
		if (w.element != null) {
			w.element.backRef = null;
		}
		w.root = null;
		for (WdWidget c : w.children) {
			invalidate(c);
		}
	}

	public void setParent(WdWidget w, Widget parent, int idx) {
		Assert.notNull(parent);
		Assert.isTrue(parent instanceof WdWidget);
		Assert.isTrue(w != this, "You cannot set the root's parent!");
		assert (w.parent != null);

		WdWidget webParent = (WdWidget) parent;
		Assert.isTrue(webParent.root == this);
		Assert.isTrue(!Util.isAncestorOf(w, parent), "The parent is a descendent of this widget!");

		w.parent.children.remove(w);
		webParent.children.add(idx, w);
		w.parent = webParent;
	}

	WdWidget addChild(WdWidget parent, WdElement element) {
		WdWidget ret = new WdWidget(this, parent, element);
		return ret;
	}

	void connect(WdWidget parent, WdWidget child) {
		parent.children.add(child);
	}

	public <T> T get(WdWidget w, Tag<T> t) {
		T ret = get(w, t, null);
		if (ret == null) {
			throw new NoSuchTagException(t);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(WdWidget w, Tag<T> t, T defaultValue) {
		Object ret = w.tags.get(t);

		if (ret != null) {
			return (T) ret;
		}
		else if (w.element == null || w.tags.containsKey(t)) {
			return defaultValue;
		}

		if (t.equals(Tags.Desc)) {
			ret = w.element.name;
		}
		else if (t.equals(Tags.Role)) {
			ret = WdRoles.fromTypeId(w.element.tagName);
		}
		else if (t.equals(Tags.HitTester)) {
			ret = new WdHitTester(w.element);
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
			ret = w.element.name;
		}
		else if (t.equals(Tags.ValuePattern)) {
			ret = w.element.valuePattern;
		}
		else if (t.equals(Tags.ToolTipText)) {
			ret = w.element.helpText;
		}
		else if (t.equals(Tags.PID)) {
			ret = w == this ? ((WdRootElement) element).pid : null;
		}
		else if (t.equals(Tags.IsRunning)) {
			ret = w == this ? ((WdRootElement) element).isRunning : null;
		}
		else if (t.equals(Tags.TimeStamp)) {
			ret = w == this ? ((WdRootElement) element).timeStamp : null;
		}
		else if (t.equals(Tags.Foreground)) {
			ret = w == this ? ((WdRootElement) element).isForeground : null;
		}
		else if (t.equals(Tags.HasStandardKeyboard)) {
			ret = w == this ? ((WdRootElement) element).hasStandardKeyboard : null;
		}
		else if (t.equals(Tags.HasStandardMouse)) {
			ret = w == this ? ((WdRootElement) element).hasStandardMouse : null;
		}
		else if (t.equals(WdTags.WebName)) {
			ret = w.element.name;
		}
		else if (t.equals(WdTags.WebOrientation)) {
			ret = (long)0;
		}
		else if (t.equals(WdTags.WebCssClasses)) {
			ret = w.element.cssClasses.toString();
		}
		else if (t.equals(Tags.ZIndex)) {
			ret = w.element.zindex;
		}
		else if (t.equals(WdTags.WebIsWindowModal)) {
			ret = w.element.isModal;
		}
		else if (t.equals(WdTags.WebIsTopmostWindow)) {
			ret = true;
		}
		else if (t.equals(WdTags.WebIsContentElement)) {
			ret = w.element.isContentElement;
		}
		else if (t.equals(WdTags.WebIsControlElement)) {
			ret = w.element.isControlElement;
		}
		else if (t.equals(WdTags.WebScrollPattern)) {
			ret = w.element.scrollPattern;
		}
		else if (t.equals(WdTags.WebHorizontallyScrollable)) {
			ret = w.element.hScroll;
		}
		else if (t.equals(WdTags.WebVerticallyScrollable)) {
			ret = w.element.vScroll;
		}
		else if (t.equals(WdTags.WebScrollHorizontalViewSize)) {
			ret = w.element.hScrollViewSize;
		}
		else if (t.equals(WdTags.WebScrollVerticalViewSize)) {
			ret = w.element.vScrollViewSize;
		}
		else if (t.equals(WdTags.WebScrollHorizontalPercent)) {
			ret = w.element.hScrollPercent;
		}
		else if (t.equals(WdTags.WebScrollVerticalPercent)) {
			ret = w.element.vScrollPercent;
		}
		else if (t.equals(WdTags.WebHelpText)) {
			ret = w.element.helpText;
		}
		else if (t.equals(WdTags.WebTagName)) {
			ret = w.element.tagName;
		}
		else if (t.equals(WdTags.WebControlType)) {
			ret = null;
		}
		else if (t.equals(WdTags.WebCulture)) {
			ret = w.element.culture;
		}
		else if (t.equals(WdTags.WebFrameworkId)) {
			ret = null;
		}
		else if (t.equals(WdTags.WebHasKeyboardFocus)) {
			ret = w.element.hasKeyboardFocus;
		}
		else if (t.equals(WdTags.WebIsFullOnScreen)) {
			ret = w.element.isFullVisibleOnScreen;
		}
		else if (t.equals(WdTags.WebIsKeyboardFocusable)) {
			ret = w.element.isKeyboardFocusable;
		}
		else if (t.equals(WdTags.WebAcceleratorKey)) {
			ret = w.element.acceleratorKey;
		}
		else if (t.equals(WdTags.WebAccessKey)) {
			ret = w.element.accessKey;
		}
		else if (t.equals(WdTags.WebId)) {
			ret = w.element.id;
		}
		else if (t.equals(WdTags.WebTextContext)) {
			ret = w.element.textContent;
		}
		else if (t.equals(WdTags.WebTitle)) {
			ret = w.element.title;
		}
		else if (t.equals(WdTags.WebHref)) {
			ret = w.element.href;
		}
		else if (t.equals(WdTags.WebValue)) {
			ret = w.element.value;
		}
		else if (t.equals(WdTags.WebStyle)) {
			ret = w.element.style;
		}
		else if (t.equals(WdTags.WebTarget)) {
			ret = w.element.target;
		}
		else if (t.equals(WdTags.WebAlt)) {
			ret = w.element.alt;
		}
		else if (t.equals(WdTags.WebDisplay)) {
			ret = w.element.display;
		}
		else if (t.equals(WdTags.WebType)) {
			ret = w.element.type;
		}
		else if (t.equals(WdTags.WebZIndex)) {
			ret = w.element.zindex;
		}
		else if (t.equals(WdTags.WebIsEnabled)) {
			ret = w.element.enabled;
		}
		else if (t.equals(WdTags.WebIsBlocked)) {
			ret = w.element.blocked;
		}
		else if (t.equals(WdTags.WebIsClickable)) {
			ret = w.element.isClickable;
		}

		cacheTag(w, t, ret);

		return (ret == null) ? defaultValue : (T) ret;
	}

	@SuppressWarnings("unchecked")
	public <T> T cacheTag(WdWidget w, Tag<T> t, Object value) {
		w.tags.put(t, value);
		return (T) value;
	}

	public <T> void setTag(WdWidget w, Tag<T> t, T value) {
		Assert.notNull(value);
		w.tags.put(t, value);
	}

	public <T> void remove(WdWidget w, Tag<T> t) {
		Assert.notNull(w, t);
		w.tags.put(t, null);
	}

	public WdWidget getChild(WdWidget w, int idx) {
		return w.children.get(idx);
	}

	public int childCount(WdWidget w) {
		return w.children.size();
	}

	public WdWidget getParent(WdWidget w) {
		return w.parent;
	}

	Iterable<Tag<?>> tags(final WdWidget w) {
		Assert.notNull(w);

		// compile a query set
		final Set<Tag<?>> queryTags = new HashSet<Tag<?>>();
		queryTags.addAll(tags.keySet());
		queryTags.addAll(Tags.tagSet());
		queryTags.addAll(WdTags.tagSet());

		Iterable<Tag<?>> ret = new Iterable<Tag<?>>() {
			public Iterator<Tag<?>> iterator() {
				return new Iterator<Tag<?>>() {
					Iterator<Tag<?>> i = queryTags.iterator();
					WdWidget target = w;
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
		return Util.treeDesc(this, 2, Tags.Role, Tags.Title);
	}
}
