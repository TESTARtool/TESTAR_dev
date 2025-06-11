/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles.web.invariants;

import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;

public class WebInvariantEmptySelectItems implements Oracle {

	private final List<Role> roles;

	public WebInvariantEmptySelectItems() {
		this(List.of(WdRoles.WdSELECT));
	}

	public WebInvariantEmptySelectItems(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public String getMessage() {
		return "";
	}

	@Override
	public Verdict getVerdict(State state) {
		List<Widget> emptySelectWidgets = new ArrayList<>();

		for (Widget w : state) {
			if (roles.contains(w.get(Tags.Role, Roles.Widget)) && !w.get(WdTags.WebId, "").isEmpty()) {
				try {
					String elementId = w.get(WdTags.WebId);
					String query = String.format("return document.getElementById('%s').length", elementId);
					Long selectItemsLength = (Long) WdDriver.executeScript(query);

					// If the select contains 0 or 1 item
					if (selectItemsLength != null && selectItemsLength.intValue() <= 1) {
						emptySelectWidgets.add(w);
					}
				} catch (Exception e) {
					// Ignore webdriver execute script errors
				}
			}
		}

		// If exists one or more incorrect widgets
		if (!emptySelectWidgets.isEmpty()) {

			String verdictMsg = String.format(
					"Detected Select widgets %s with empty or only one item!",
					getDescriptionOfWidgets(emptySelectWidgets, WdTags.WebId)
					);

			Visualizer visualizer = new RegionsVisualizer(
					getRedPen(),
					getWidgetRegions(emptySelectWidgets),
					"Invariant Fault",
					0.5, 0.5);

			return new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer);
		}

		return Verdict.OK;
	}
}
