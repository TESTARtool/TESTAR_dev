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

package org.testar.monkey.alayer.android;

import org.testar.monkey.Drag;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidWidget implements Widget, Serializable {
	private static final long serialVersionUID = -1757924936831611142L;
	
	AndroidState root;
	AndroidWidget parent;
	Map<Tag<?>, Object> tags = new HashMap<>();
	List<AndroidWidget> children = new ArrayList<>();
	public AndroidElement element;

	protected AndroidWidget(AndroidState root, AndroidWidget parent, AndroidElement element) {
		this.parent = parent;
		this.element = element;
		this.root = root;

		if (parent != null) {
			root.connect(parent, this);
		}
	}

	final public void moveTo(Widget p, int idx) {
		root.setParent(this, p, idx);
	}

	public final AndroidWidget addChild() {
		return root.addChild(this, null);
	}

	public final AndroidState root() {
		return root;
	}

	public final AndroidWidget parent() {
		return root.getParent(this);
	}

	public final AndroidWidget child(int i) {
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

	public String getRepresentation(String tab) {
		return "COMPLETE: AndroidWidget getRepresentation";
	}

	@Override
	//TODO: Extend this such that when I log I get some more useful information.
	public String toString(Tag<?>... tags) {
		return Util.widgetDesc(this, tags);
	}

	@Override
	public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
		// TODO Auto-generated method stub
		return null;
	}
}
