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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

public class WebInvariantDuplicateMenuItems implements Oracle {

	public WebInvariantDuplicateMenuItems() {}

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
		List<Widget> menuWidgetsWithDuplicates = new ArrayList<>();

		for (Widget w : state) {
			// Check for UL elements with at least two children
			if (w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdUL) && w.childCount() > 1) {
				ArrayList<String> selectOptionsTextsList = new ArrayList<>();

				// Gather the text of each LI child
				for (int i = 0; i < w.childCount(); i++) {
					String itemText = w.child(i).get(WdTags.WebTextContent);

					// Only consider LI elements with non-empty text
					if (w.child(i).get(Tags.Role, Roles.Widget).equals(WdRoles.WdLI) && !itemText.isEmpty()) {
						selectOptionsTextsList.add(itemText);
					}
				}

				// Find duplicates in the list of item texts
				Set<String> duplicatesTexts = findDuplicates(selectOptionsTextsList);

				// If duplicates are found, prepare the verdict message
				if (duplicatesTexts.size() > 0) {
					menuWidgetsWithDuplicates.add(w);
				}
			}
		}

		// If exists one or more incorrect widgets
		if (!menuWidgetsWithDuplicates.isEmpty()) {

			String verdictMsg = String.format(
					"Detected a Unnumbered List (UL) web menu %s with duplicate option elements!",
					getDescriptionOfWidgets(menuWidgetsWithDuplicates, WdTags.WebId)
					);

			Visualizer visualizer = new RegionsVisualizer(
					getRedPen(),
					getWidgetRegions(menuWidgetsWithDuplicates),
					"Invariant Fault",
					0.5, 0.5);

			return new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer);
		}

		return Verdict.OK;
	}

	// Helper method to find duplicates in a list
	private static Set<String> findDuplicates(List<String> list) {
		Set<String> set = new HashSet<>();
		Set<String> duplicates = new HashSet<>();
		for (String s : list) {
			// If the item is already in the set, it is a duplicate
			if (set.contains(s)) {
				duplicates.add(s);
			}
			// Add item to the set
			set.add(s);
		}
		return duplicates;
	}
}
