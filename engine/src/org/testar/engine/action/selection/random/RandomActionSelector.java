/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.selection.random;

import org.testar.core.action.Action;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.state.State;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomActionSelector implements ActionSelectorService {

	public RandomActionSelector() {
	}

	@Override
	public Action selectAction(State state, Set<Action> actions) {
		// Convert the Set to an ArrayList for easier indexing
		ArrayList<Action> actionList = new ArrayList<>(actions);
		// Generate a random index within the bounds of the ArrayList
		int randomIndex = new Random().nextInt(actionList.size());
		// Retrieve the Action at the generated index and return it
		return actionList.get(randomIndex);
	}

}
