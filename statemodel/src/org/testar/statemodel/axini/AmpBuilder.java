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

			String css = action.getSelector();
			String desc = action.getDesc();

			boolean isType = action.getInputText() != null && !action.getInputText().isBlank();
			String functionName = generateDescFunctionName(desc, target.getTitle());
			actionToFunctionName.put(actionId, functionName);

			// If this is a new function, create it
			if (!createdFunctions.contains(functionName)) {

				List<Constraint> receiveConstraints = new ArrayList<>();

				String stimulus = "click";
				if (isType) {
					stimulus = "fill_in";
					// %(selector == "[onclick='setCookie\(true\)']")
					receiveConstraints.add(new Constraint("selector", "\"" + css + "\""));
					receiveConstraints.add(new Constraint("value", "\"" + action.getInputText() + "\""));
				} else {
					// %(css == "" && text == "Show gifts")
					receiveConstraints.add(new Constraint("css", "\"" + SelectorsHelper.isAppropiatteCssSelector(css) + "\""));
					receiveConstraints.add(new Constraint("text", "\"" + SelectorsHelper.extractTextFromDesc(desc) + "\""));
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

			// Prepare the state behavior abstraction identifier
			String sourceBehaviorId = source.getTitle() + " - " + source.getId();
			String targetBehaviorId = target.getTitle() + " - " + target.getId();

			// Add operation to the corresponding source state behavior
			behaviorOps
			.computeIfAbsent(sourceBehaviorId, k -> new LinkedHashSet<>())
			.add(new BehaviorOperation(functionName + "()", targetBehaviorId));
		}

		// Build BehaviorDefinitions from operations
		for (Map.Entry<String, Set<BehaviorOperation>> entry : behaviorOps.entrySet()) {
			BehaviorDefinition behavior = new BehaviorDefinition(entry.getKey(), true);
			for (BehaviorOperation op : entry.getValue()) {
				behavior.addOperation(op);
			}
			process.addBehavior(behavior);
		}

		// Prepare the initial state behavior abstraction identifier
		String initialBehavior = guiModel.getInitialPage() + " - " + guiModel.getInitialIdentifier();

		// Initial values and entry points
		process.setInitialVariables(new InitialVariable(
				"initial_url", 
				":string", 
				"\"" + guiModel.getInitialUrl() + "\"",
				guiModel.getInitialPage(), 
				initialBehavior));
		process.addEntryCall("call 'launch'");
		process.addEntryCall("behave_as '" + initialBehavior + "'");

		return process;
	}

	private static String generateDescFunctionName(String desc, String targetTitle) {
		String sanitizedDesc = sanitize(desc);
		String sanitizedTarget = sanitize(targetTitle);
		return sanitizedDesc + "_to_" + sanitizedTarget;
	}

	private static String generateCssFunctionName(String selector, String targetTitle, boolean isTypeAction) {
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
