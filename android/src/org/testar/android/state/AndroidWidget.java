/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.state;

import org.testar.core.Drag;
import org.testar.core.util.Util;
import org.testar.core.tag.Tag;
import org.testar.core.state.Widget;

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

	public AndroidWidget(AndroidState root, AndroidWidget parent, AndroidElement element) {
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
