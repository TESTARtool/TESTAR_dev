/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.core.alayer.Role;
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

public class WebInvariantTextAreaWithoutLength implements Oracle {

	private final List<Role> roles;

	public WebInvariantTextAreaWithoutLength() {
		this(List.of(WdRoles.WdTEXTAREA));
	}

	public WebInvariantTextAreaWithoutLength(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<>();

		for (Widget w : state) {
			if (roles.contains(w.get(Tags.Role, Roles.Widget)) && w.get(WdTags.WebMaxLength, -1) == 0) {
				String verdictMsg = String.format(
						"Detected TextArea widget %s with 0 max length!",
						getDescriptionOfWidgets(Collections.singletonList(w), WdTags.WebOuterHTML)
						);
				Visualizer visualizer = new RegionsVisualizer(
						getRedPen(),
						getWidgetRegions(Collections.singletonList(w)),
						"Invariant Fault",
						0.5, 0.5);

				verdicts.add(new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer));
			}
		}

		if (!verdicts.isEmpty()) {
			return verdicts;
		}
		return Collections.singletonList(Verdict.OK);
	}
}
