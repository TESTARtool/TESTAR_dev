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

package org.testar.oracles.web.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;

/**
 * Test Oracle that checks for web image elements (<img>) that do not have alternative text. 
 * Missing alternative text can make content inaccessible for users relying on screen readers. 
 */
public class WebAccessibilityImagesAltOracle implements Oracle {

	public WebAccessibilityImagesAltOracle() {}

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
		List<Widget> incorrectWidgets = new ArrayList<>();

		// Check if some widget of the state
		for(Widget widget : state) {
			//  Is a widget image (<img>) and if it lacks alternative text
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdIMG)
					&& (widget.get(WdTags.WebAlt, null) == null || widget.get(WdTags.WebAlt, "").isBlank())) {
				// If so, save it as incorrect widget
				incorrectWidgets.add(widget);
			}
		}

		// If exists one or more incorrect widgets
		if(!incorrectWidgets.isEmpty()) {
			// Create and return a WARNING_ACCESSIBILITY_FAULT verdict
			String verdictMsg = String.format(
					"Detected web image widgets '%s' without alternative text!", 
					getDescriptionOfWidgets(incorrectWidgets, WdTags.WebOuterHTML)
					);
			Visualizer visualizer = new RegionsVisualizer(
					getRedPen(), 
					getWidgetRegions(incorrectWidgets), 
					"Accessibility Fault", 
					0.5, 0.5);
			return new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, verdictMsg, visualizer);
		} else {
			return Verdict.OK;
		}

	}

}
