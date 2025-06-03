package org.testar.statemodel.axini;

import java.util.*;

public class AmpBuilder {

	public static ProcessDefinition buildFrom(GuiStateModel guiModel) {
		ProcessDefinition process = new ProcessDefinition("testar");

		Map<String, GuiState> stateMap = new HashMap<>();
		Map<String, GuiAction> actionMap = new HashMap<>();
		Map<String, String> actionToFunctionName = new HashMap<>();
		Map<String, Set<BehaviorOperation>> behaviorOps = new HashMap<>();
		Set<String> createdFunctions = new HashSet<>();

		// Map states and actions by ID
		for (GuiState state : guiModel.getStates()) {
			stateMap.put(state.getId(), state);
		}
		for (GuiAction action : guiModel.getActions()) {
			actionMap.put(action.getId(), action);
		}

		// Generate ActionDefinitions and collect BehaviorOperations
		for (GuiTransition transition : guiModel.getTransitions()) {
			String sourceId = transition.getSourceStateId();
			String targetId = transition.getTargetStateId();
			String actionId = transition.getActionId();

			GuiState source = stateMap.get(sourceId);
			GuiState target = stateMap.get(targetId);
			GuiAction action = actionMap.get(actionId);

			if (source == null || target == null || action == null) continue;

			String selector = action.getSelector();

			boolean isType = action.getInputText() != null && !action.getInputText().isBlank();
			String functionName = generateFunctionName(selector, target.getTitle(), isType);
			actionToFunctionName.put(actionId, functionName);

			// If this is a new function, create it
			if (!createdFunctions.contains(functionName)) {

				List<Constraint> receiveConstraints = new ArrayList<>();
				receiveConstraints.add(new Constraint("selector", "\"" + selector + "\""));

				String stimulus = "click_link";
				if (isType) {
					stimulus = "fill_in"; 
					receiveConstraints.add(new Constraint("value", "\"" + action.getInputText() + "\""));
				}

				ActionDefinition def = new ActionDefinition(
						functionName,
						stimulus,
						receiveConstraints,
						"page_title",
						List.of(new Constraint("_title", "\"" + target.getTitle() + "\""))
						);

				process.addAction(def);
				createdFunctions.add(functionName);
			}

			// Add operation to the corresponding source state behavior
			behaviorOps
			.computeIfAbsent(source.getTitle(), k -> new LinkedHashSet<>())
			.add(new BehaviorOperation(functionName + "()", target.getTitle()));
		}

		// Build BehaviorDefinitions from operations
		for (Map.Entry<String, Set<BehaviorOperation>> entry : behaviorOps.entrySet()) {
			BehaviorDefinition behavior = new BehaviorDefinition(entry.getKey(), true);
			for (BehaviorOperation op : entry.getValue()) {
				behavior.addOperation(op);
			}
			process.addBehavior(behavior);
		}

		// Initial values and entry points
		process.addInitialVariable(new InitialVariable("initial_url", ":string", "\"" + guiModel.getInitialUrl() + "\""));
		process.addEntryCall("call 'launch'");
		process.addEntryCall("behave_as '" + guiModel.getInitialPage() + "'");

		return process;
	}

	private static String generateFunctionName(String selector, String targetTitle, boolean isTypeAction) {
		String prefix = isTypeAction ? "Type" : "Click";
		String sanitizedSelector = sanitize(selector);
		String sanitizedTarget = sanitize(targetTitle);
		return prefix + "_" + sanitizedSelector + "_to_" + sanitizedTarget;
	}

	private static String sanitize(String input) {
		return input
				.replaceAll("[^a-zA-Z0-9]", "_")     // replace non-alphanumerics with _
				.replaceAll("_+", "_")               // collapse multiple _ into one
				.replaceAll("^_+", "")               // remove leading underscores
				.replaceAll("_+$", "");              // remove trailing underscores
	}

}
