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

package org.testar.monkey.alayer.ios.enums;

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TagsBase;

public class IOSTags extends TagsBase {
	
	private IOSTags() {}
	
	public static final Tag<Boolean> iosEnabled = from("iosEnabled", Boolean.class);
	
	public static final Tag<Rect> iosBounds = from("iosBounds", Rect.class);
	
	public static final Tag<Integer> iosNodeIndex = from("iosNodeIndex", Integer.class);
	
	public static final Tag<String> iosText = from("iosText", String.class);
	
	//public static final Tag<String> iosResourceId = from("iosResourceId", String.class);
	
	public static final Tag<String> iosClassName = from("iosClassName", String.class);
	
	//public static final Tag<String> iosPackageName = from("iosPackageName", String.class);
	
	//public static final Tag<Boolean> iosCheckable = from("iosCheckable", Boolean.class);
	
	//public static final Tag<Boolean> iosChecked = from("iosChecked", Boolean.class);
	
	//public static final Tag<Boolean> iosClickable = from("iosClickable", Boolean.class);
	
	//public static final Tag<Boolean> iosFocusable = from("iosFocusable", Boolean.class);
	
	//public static final Tag<Boolean> iosFocused = from("iosFocused", Boolean.class);
	
	//public static final Tag<Boolean> iosScrollable = from("iosScrollable", Boolean.class);
	
	//public static final Tag<Boolean> iosLongClickable = from("iosLongClickable", Boolean.class);
	
	//public static final Tag<Boolean> iosPassword = from("iosPassword", Boolean.class);
	
	//public static final Tag<Boolean> iosSelected = from("iosSelected", Boolean.class);

	public static final Tag<String> iosAccessibilityId = from("iosAccessibilityId", String.class);

	public static final Tag<String> iosXpath = from("iosXpath", String.class);

	public static final Tag<Boolean> iosVisible = from("iosVisible", Boolean.class);

	public static final Tag<Integer> iosX = from("iosX", Integer.class);

	public static final Tag<Integer> iosY = from("iosY", Integer.class);

	public static final Tag<Integer> iosWidth = from("iosWidth", Integer.class);

	public static final Tag<Integer> iosHeight = from("iosHeight", Integer.class);

	public static final Tag<String> iosLabel = from("iosLabel", String.class);

}
