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

package org.testar.monkey.alayer.android.enums;

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TagsBase;

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
