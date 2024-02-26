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

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GetWidgetMatchingTags {

	private static GenericUtilsProtocol protocol;

	@BeforeClass
	public static void setup() {
		// To avoid issues with java awt robot, we only execute this unit tests in windows environments.
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
		protocol = new GenericUtilsProtocol();
	}

	@Test
	public void find_widget_by_matching_WdTag_and_UIATag() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(WdTags.WebName, "one_web_name");
		widget.set(UIATags.UIAClassName, "one_class_name");

		// Find the widget by the tags values
		Map<String, String> tagValues = new HashMap<String, String>();
		tagValues.put("WebName", "one_web_name");
		tagValues.put("UIAClassName", "one_class_name");

		Widget equalsMatchAWidget = protocol.getWidgetWithMatchingTags(tagValues, state);
		assertEquals(widget, equalsMatchAWidget);
	}

	@Test
	public void not_matching_WdTag_and_UIATag() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(WdTags.WebName, "one_web_name");
		widget.set(UIATags.UIAClassName, "one_class_name");

		// Do not find any widget if any tag value is not correct
		Map<String, String> tagValuesNotCorrect1 = new HashMap<String, String>();
		tagValuesNotCorrect1.put("WebName", "wrong_web_name");
		tagValuesNotCorrect1.put("UIAClassName", "one_class_name");

		Map<String, String> tagValuesNotCorrect2 = new HashMap<String, String>();
		tagValuesNotCorrect2.put("WebName", "one_web_name");
		tagValuesNotCorrect2.put("UIAClassName", "wrong_class_name");

		Map<String, String> tagValuesNotCorrect3 = new HashMap<String, String>();
		tagValuesNotCorrect3.put("WebName", "wrong_web_name");
		tagValuesNotCorrect3.put("UIAClassName", "wrong_class_name");

		Map<String, String> tagValuesNotCorrect4 = new HashMap<String, String>();
		tagValuesNotCorrect4.put("WdTags.WebName", "one_web_name");
		tagValuesNotCorrect4.put("UIAClassName", "one_class_name");

		Map<String, String> tagValuesNotCorrect5 = new HashMap<String, String>();
		tagValuesNotCorrect5.put("WebName", "one_web_name");
		tagValuesNotCorrect5.put("UIATags.UIAClassName", "one_class_name");

		Map<String, String> tagValuesNotCorrect6 = new HashMap<String, String>();
		tagValuesNotCorrect6.put("WdTags.WebName", "one_web_name");
		tagValuesNotCorrect6.put("UIATags.UIAClassName", "one_class_name");

		assertNull(protocol.getWidgetWithMatchingTags(tagValuesNotCorrect1, state));
		assertNull(protocol.getWidgetWithMatchingTags(tagValuesNotCorrect2, state));
		assertNull(protocol.getWidgetWithMatchingTags(tagValuesNotCorrect3, state));
		assertNull(protocol.getWidgetWithMatchingTags(tagValuesNotCorrect4, state));
		assertNull(protocol.getWidgetWithMatchingTags(tagValuesNotCorrect5, state));
		assertNull(protocol.getWidgetWithMatchingTags(tagValuesNotCorrect6, state));
	}

	@Test
	public void state_without_children() {
		StateStub state = new StateStub();

		// Do not find any widget (correct or incorrect) because state has no children
		Map<String, String> tagValues1 = new HashMap<String, String>();
		tagValues1.put("WebName", "name");
		tagValues1.put("UIAClassName", "name");

		assertNull(protocol.getWidgetWithMatchingTags(tagValues1, state));
	}

}
