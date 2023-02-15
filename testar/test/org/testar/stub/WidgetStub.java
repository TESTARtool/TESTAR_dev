/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.stub;

import org.apache.commons.lang.NotImplementedException;
import org.testar.monkey.Assert;
import org.testar.monkey.Drag;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.windows.UIATags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class WidgetStub extends TaggableBase implements Widget {
	private static final long serialVersionUID = 1828072828801243863L;

	Map<Tag<?>, Object> tags = Util.newHashMap();

	private final List<Widget> widgets = new ArrayList<>();

	private Widget parent = null;

	@Override
	public State root() {
		return (State)this;
	}

	@Override
	public Widget parent() {
		return parent;
	}
	public void setParent(Widget parent) {
		this.parent = parent;
	}

	@Override
	public Widget child(int i) {
		return widgets.get(i);
	}

	@Override
	public int childCount() {
		return widgets.size();
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

	@Override
	public void moveTo(Widget p, int idx) {
		throw new NotImplementedException();
	}

	@Override
	public Widget addChild() {
		throw new NotImplementedException();
	}

	public Widget addChild(Widget widget) {
		widgets.add(widget);
		return this;
	}

	@Override
	public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
		throw new NotImplementedException();
	}

	@Override
	public String getRepresentation(String tab){
		StringBuffer repr = new StringBuffer();
		repr.append(tab + "WIDGET = " + this.get(Tags.ConcreteID) + ", " +
				this.get(Tags.Abstract_R_ID) + ", " +
				this.get(Tags.Abstract_R_T_ID) + ", " +
				this.get(Tags.Abstract_R_T_P_ID) + "\n");
		return repr.toString();
	}

	@Override
	public String toString(Tag<?>... tags){
		return Util.treeDesc(this, 2, tags);
	}

	@Override
	public void remove(Tag<?> tag) {
		throw new NotImplementedException();
	}

	Iterable<Tag<?>> tags(final Widget w) {
		Assert.notNull(w);

		final Set<Tag<?>> queryTags = new HashSet<Tag<?>>();
		queryTags.addAll(tags.keySet());
		queryTags.addAll(Tags.tagSet());
		queryTags.addAll(UIATags.tagSet());
		queryTags.addAll(WdTags.tagSet());

		Iterable<Tag<?>> ret = new Iterable<Tag<?>>() {
			public Iterator<Tag<?>> iterator() {
				return new Iterator<Tag<?>>() {
					Iterator<Tag<?>> i = queryTags.iterator();
					Widget target = w;
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

}
