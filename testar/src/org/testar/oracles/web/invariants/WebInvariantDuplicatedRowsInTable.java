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
import java.util.stream.Collectors;

import org.testar.monkey.Pair;
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
 * Test Oracle that checks duplicated rows in a table by concatenating all visible values with an _ underscore. 
 */
public class WebInvariantDuplicatedRowsInTable implements Oracle {

	public WebInvariantDuplicatedRowsInTable() {}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public Verdict getVerdict(State state) {
		List<Widget> incorrectWidgets = new ArrayList<>();

		for(Widget w : state) {
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				List<Pair<Widget, String>> rowElementsDescription = new ArrayList<>();
				extractAllRowDescriptionsFromTable(w, rowElementsDescription);

				List<Pair<Widget, String>> duplicatedDescriptions = 
						rowElementsDescription.stream()
						.collect(Collectors.groupingBy(Pair::right))
						.entrySet().stream()
						.filter(e -> e.getValue().size() > 1)
						.flatMap(e -> e.getValue().stream())
						.collect(Collectors.toList());

				// If the list of duplicated descriptions contains a matching prepare the verdict
				if(!duplicatedDescriptions.isEmpty()) {
					for(Pair<Widget, String> duplicatedWidget : duplicatedDescriptions) {
						// Ignore empty rows
						if (!duplicatedWidget.right().replaceAll("_","").isEmpty()) {

							String verdictMsg = String.format(
									"Detected a duplicated rows in a Table! Role: %s , WebId: %s, Description: %s", 
									duplicatedWidget.left().get(Tags.Role), 
									duplicatedWidget.left().get(WdTags.WebId, ""), 
									duplicatedWidget.right()
									);
							Visualizer visualizer = new RegionsVisualizer(
									getRedPen(), 
									getWidgetRegions(incorrectWidgets), 
									"Invariant Fault", 
									0.5, 0.5);

							return new Verdict(Verdict.Severity.WARNING, verdictMsg, visualizer);
						}
					}
				}
			}
		}

		return Verdict.OK;
	}

	private void extractAllRowDescriptionsFromTable(Widget w, List<Pair<Widget, String>> rowElementsDescription) {
		if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTR)) {
			rowElementsDescription.add(new Pair<Widget, String>(w, obtainWidgetTreeDescription(w)));
		}

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			// If the children of the table are not sub-tables
			if(!w.child(i).get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				extractAllRowDescriptionsFromTable(w.child(i), rowElementsDescription);
			}
		}
	}

	private String obtainWidgetTreeDescription(Widget w) {
		String widgetDesc = w.get(WdTags.WebTextContent, "");

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			widgetDesc = widgetDesc + "_" + obtainWidgetTreeDescription(w.child(i));
		}

		return widgetDesc;
	}

}
