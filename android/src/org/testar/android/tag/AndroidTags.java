/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.tag;

import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tag;
import org.testar.core.tag.TagsBase;

public class AndroidTags extends TagsBase {
	
	private AndroidTags() {}
	
	public static final Tag<Boolean> AndroidEnabled = from("AndroidEnabled", Boolean.class);
	
	public static final Tag<Rect> AndroidBounds = from("AndroidBounds", Rect.class);
	
	public static final Tag<Integer> AndroidNodeIndex = from("AndroidNodeIndex", Integer.class);
	
	public static final Tag<String> AndroidText = from("AndroidText", String.class);

	public static final Tag<String> AndroidHint = from("AndroidHint", String.class);

	public static final Tag<String> AndroidResourceId = from("AndroidResourceId", String.class);
	
	public static final Tag<String> AndroidClassName = from("AndroidClassName", String.class);
	
	public static final Tag<String> AndroidPackageName = from("AndroidPackageName", String.class);
	
	public static final Tag<Boolean> AndroidCheckable = from("AndroidCheckable", Boolean.class);
	
	public static final Tag<Boolean> AndroidChecked = from("AndroidChecked", Boolean.class);
	
	public static final Tag<Boolean> AndroidClickable = from("AndroidClickable", Boolean.class);
	
	public static final Tag<Boolean> AndroidFocusable = from("AndroidFocusable", Boolean.class);
	
	public static final Tag<Boolean> AndroidFocused = from("AndroidFocused", Boolean.class);
	
	public static final Tag<Boolean> AndroidScrollable = from("AndroidScrollable", Boolean.class);
	
	public static final Tag<Boolean> AndroidLongClickable = from("AndroidLongClickable", Boolean.class);
	
	public static final Tag<Boolean> AndroidPassword = from("AndroidPassword", Boolean.class);

	public static final Tag<Boolean> AndroidSelected = from("AndroidSelected", Boolean.class);

	public static final Tag<String> AndroidAccessibilityId = from("AndroidAccessibilityId", String.class);

	public static final Tag<Boolean> AndroidDisplayed = from("AndroidDisplayed", Boolean.class);
	
	public static final Tag<String> AndroidXpath = from("AndroidXpath", String.class);

	public static final Tag<String> AndroidAbstractActionId = from("AndroidAbstractActionId", String.class);

	public static final Tag<String> AndroidActivity = from("AndroidActivity", String.class);
}
