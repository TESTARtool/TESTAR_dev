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

package org.testar.protocols;

import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.BeforeClass;

public class GetWidgetMatchingTag {

	private static GenericUtilsProtocol protocol;

	@BeforeClass
	public static void setup() {
		// To avoid issues with java awt robot, we only execute this unit tests in windows environments.
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
		protocol = new GenericUtilsProtocol();
	}

	@Test
	public void find_widget_by_WdTag() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(WdTags.WebName, "one_web_name");

		// Find the widget by the WdTag value
		Widget equalsMatchWebWidget = protocol.getWidgetWithMatchingTag(WdTags.WebName, "one_web_name", state);
		Widget containsMatchWebWidget = protocol.getWidgetWithMatchingTag(WdTags.WebName, "web_na", state);
		assertEquals(widget, equalsMatchWebWidget);
		assertEquals(widget, containsMatchWebWidget);

		// Do not find any widget if the WdTag value is not correct
		assertNull(protocol.getWidgetWithMatchingTag(WdTags.WebName, "bad_web_name", state));
	}

	@Test
	public void find_widget_by_UIATag() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(UIATags.UIAClassName, "one_class_name");

		// Find the widget by the UIATag value
		Widget equalsMatchUIAWidget = protocol.getWidgetWithMatchingTag(UIATags.UIAClassName, "one_class_name", state);
		Widget containsMatchUIAWidget = protocol.getWidgetWithMatchingTag(UIATags.UIAClassName, "_class_n", state);
		assertEquals(widget, equalsMatchUIAWidget);
		assertEquals(widget, containsMatchUIAWidget);

		// Do not find any widget if the UIATag value is not correct
		assertNull(protocol.getWidgetWithMatchingTag(UIATags.UIAClassName, "bad_class_name", state));
	}

	@Test
	public void find_widget_by_TagName() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(WdTags.WebName, "one_web_name");

		// Find the widget by the WdTag value
		Widget equalsMatchWebWidget = protocol.getWidgetWithMatchingTag("WebName", "one_web_name", state);
		Widget containsMatchWebWidget = protocol.getWidgetWithMatchingTag("WebName", "web_na", state);
		assertEquals(widget, equalsMatchWebWidget);
		assertEquals(widget, containsMatchWebWidget);

		// Do not find any widget if the Tag value is not correct
		assertNull(protocol.getWidgetWithMatchingTag("WebName", "bad_web_name", state));
		// Do not find any widget because the Tag Name is not correct
		assertNull(protocol.getWidgetWithMatchingTag("WebBadTag", "one_web_name", state));
	}

	@Test
	public void state_without_childrens() {
		StateStub state = new StateStub();

		// Do not find any widget (correct or incorrect) because state has no children
		assertNull(protocol.getWidgetWithMatchingTag(UIATags.UIAClassName, "name", state));
		assertNull(protocol.getWidgetWithMatchingTag(WdTags.WebName, "name", state));
		assertNull(protocol.getWidgetWithMatchingTag("WebName", "name", state));
		assertNull(protocol.getWidgetWithMatchingTag("WebBadTag", "name", state));
	}

}
