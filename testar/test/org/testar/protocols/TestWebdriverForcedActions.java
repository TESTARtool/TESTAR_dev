/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.CodingManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebdriverForcedActions {

	private static WebdriverProtocol webdriverProtocol;

	private static Map.Entry<String, String> acceptPolicyEntry =
			new AbstractMap.SimpleEntry<String, String>("id", "accept_policy");
	private static Map.Entry<String, String> rejectPolicyEntry =
			new AbstractMap.SimpleEntry<String, String>("class", "reject_policy");

	private static Map<String, String> attributesMap = Stream.of(acceptPolicyEntry, rejectPolicyEntry)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	private static StateStub state = new StateStub();
	private static WidgetStub widget = new WidgetStub();

	@BeforeClass
	public static void widgets_setup() {
		// Prepare the state and the widget with the attributes map
		state.addChild(widget);
		widget.setParent(state);
		widget.set(WdTags.WebAttributeMap, attributesMap);

		// Prepare the Tags that the action compiler uses to create an action
		widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
		widget.set(Tags.Role, Roles.Button);
		widget.set(Tags.Path, "[0,0,1]");

		// Build widget and state identifiers
		CodingManager.buildIDs(state);
	}

	@Before
	public void webdriver_protocol_setup() {
		// Before each test initialize a new webdriver protocol + policy attributes multimap
		webdriverProtocol = new WebdriverProtocol();
	}

	@Test
	public void test_matching_unique_policyAttribute() {
		webdriverProtocol.policyAttributes.put("id", "accept_policy");

		Set<Action> popupClickActions = webdriverProtocol.detectForcedPopupClick(state, new AnnotatingActionCompiler());
		assertTrue(popupClickActions.size() == 1);
		assertTrue(popupClickActions.iterator().next().get(Tags.OriginWidget).equals(widget));
	}

	@Test
	public void test_not_existing_unique_policyAttribute() {
		webdriverProtocol.policyAttributes.put("id", "accept_cookie");

		Set<Action> popupClickActions = webdriverProtocol.detectForcedPopupClick(state, new AnnotatingActionCompiler());
		assertTrue(popupClickActions.isEmpty());
	}

	@Test
	public void test_matching_one_of_mutiple_policyAttribute() {
		webdriverProtocol.policyAttributes.put("class", "not_exists_policy");
		webdriverProtocol.policyAttributes.put("class", "reject_policy");
		webdriverProtocol.policyAttributes.put("class", "this_neither");

		Set<Action> popupClickActions = webdriverProtocol.detectForcedPopupClick(state, new AnnotatingActionCompiler());
		assertTrue(popupClickActions.size() == 1);
		assertTrue(popupClickActions.iterator().next().get(Tags.OriginWidget).equals(widget));
	}

	@Test
	public void test_not_existing_of_mutiple_policyAttribute() {
		webdriverProtocol.policyAttributes.put("class", "not_exists_policy");
		webdriverProtocol.policyAttributes.put("class", "reject_cookie");
		webdriverProtocol.policyAttributes.put("class", "this_neither");

		Set<Action> popupClickActions = webdriverProtocol.detectForcedPopupClick(state, new AnnotatingActionCompiler());
		assertTrue(popupClickActions.isEmpty());
	}
}
