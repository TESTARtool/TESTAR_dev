/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testar.core.Pair;
import org.testar.core.alayer.Roles;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.Visualizer;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.visualizers.RegionsVisualizer;
import org.testar.oracle.Oracle;

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
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		for (Widget w : state) {
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				List<Pair<Widget, String>> rowElementsDescription = new ArrayList<>();
				extractAllRowDescriptionsFromTable(w, rowElementsDescription);

				List<List<Pair<Widget, String>>> duplicatedDescriptions = 
						rowElementsDescription.stream()
						.collect(Collectors.groupingBy(Pair::right))
						.entrySet().stream()
						.filter(e -> e.getValue().size() > 1)
						.map(e -> e.getValue())
						.collect(Collectors.toList());

				// If the list of duplicated descriptions contains a matching prepare the verdict
				if(!duplicatedDescriptions.isEmpty()) {
					for (List<Pair<Widget, String>> duplicatedWidgets : duplicatedDescriptions) {
						Pair<Widget, String> duplicatedWidget = duplicatedWidgets.get(0);
						// Ignore empty rows
						if (!duplicatedWidget.right().replaceAll("_","").isEmpty()) {
							String verdictMsg = String.format(
									"Detected duplicated row in a Table for the widget: %s ",
									duplicatedWidget.right()
									);
							List<Widget> widgets = duplicatedWidgets.stream()
									.map(Pair::left)
									.collect(Collectors.toList());
							Visualizer visualizer = new RegionsVisualizer(
									getRedPen(),
									getWidgetRegions(widgets),
									"Invariant Fault",
									0.5, 0.5);
							verdicts.add(new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer));
						}
					}
				}
			}
		}

		if (!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}

	private void extractAllRowDescriptionsFromTable(Widget w, List<Pair<Widget, String>> rowElementsDescription) {
		if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTR)) {
			rowElementsDescription.add(new Pair<Widget, String>(w, obtainWidgetTreeDescription(w)));
		}

		// Iterate through the form element widgets
		for (int i = 0; i < w.childCount(); i++) {
			// If the children of the table are not sub-tables
			if(!w.child(i).get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				extractAllRowDescriptionsFromTable(w.child(i), rowElementsDescription);
			}
		}
	}

	private String obtainWidgetTreeDescription(Widget w) {
		String widgetDesc = w.get(WdTags.WebTextContent, "");

		// Iterate through the form element widgets
		for (int i = 0; i < w.childCount(); i++) {
			widgetDesc = widgetDesc + "_" + obtainWidgetTreeDescription(w.child(i));
		}

		return widgetDesc;
	}

}
