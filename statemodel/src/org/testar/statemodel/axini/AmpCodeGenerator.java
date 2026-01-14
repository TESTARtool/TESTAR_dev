package org.testar.statemodel.axini;

import org.testar.statemodel.axini.model.ActionDefinition;
import org.testar.statemodel.axini.model.BehaviorDefinition;
import org.testar.statemodel.axini.model.BehaviorOperation;
import org.testar.statemodel.axini.model.Constraint;
import org.testar.statemodel.axini.model.InitialVariable;
import org.testar.statemodel.axini.model.ProcessDefinition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AmpCodeGenerator {

	public static String generate(ProcessDefinition process) {
		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("timeout 10.0\n");
		sb.append("external 'extern'\n\n");

		// 1. Render the Action function definitions
		for (ActionDefinition action : process.getActions()) {
			sb.append(renderActionDefinition(action)).append("\n\n");
		}

		// 2. Process wrapper
		sb.append("process('").append(process.getName()).append("') {\n\n");

		// 3. Channel and stimulus definition (this is adapter dependent)
		sb.append("  channel('extern') {\n")
		.append("    stimulus 'visit', '_url' => :string\n")
		.append("    stimulus 'click', 'css' => :string, 'text' => :string\n")
		.append("    stimulus 'fill_in', 'selector' => :string, 'value' => :string\n")
		.append("    response 'page_title', '_title' => :string, '_url' => :string\n")
		.append("  }\n\n");

		// 4. Initial URL launch behavior
		sb.append("  behavior('launch') {\n")
		.append("    receive 'visit',\n")
		.append("    constraint: \"_url == initial_url\"\n")
		.append("    send 'page_title',\n")
		.append("    constraint: %(_title == \"")
		.append(process.getInitialVariables().getPage()).append("\")\n")
		.append("  }\n");

		// 5. Render all the Behavior definitions
		for (BehaviorDefinition behavior : process.getBehaviors()) {
			sb.append("\n").append(renderBehavior(process.getBehaviors(), behavior));
		}

		// 6. Add Initial variables
		InitialVariable var = process.getInitialVariables();
		sb.append("\n  var 'initial_url'")
		.append(", :string")
		.append(", ").append(var.getUrl());

		// 7. Add entry call and entry behave_as
		sb.append("\n\n  ").append(process.getEntryCall());
		sb.append("\n\n  ").append(process.getEntryBehave());

		sb.append("\n\n}\n"); // end process
		return sb.toString();
	}

	private static String renderActionDefinition(ActionDefinition action) {
		return String.format("def %s()\n  receive '%s', constraint: %%(%s)\n  send '%s', constraint: %%(%s)\nend",
				action.getName(),
				action.getReceivedStimulus(),
				formatActionConstraints(action.getReceiveConstraints()),
				action.getSentResponse(),
				formatActionConstraints(action.getSendConstraints())
				);
	}

	private static String formatActionConstraints(List<Constraint> constraints) {
		return constraints.stream()
				.map(c -> c.getField() + " == " + c.getExpression())
				.collect(Collectors.joining(" && "));
	}

	// TESTAR exploration may produce a partial/disconnected model
	// only render behave_as calls to behaviors that exist in the infered model
	private static String renderBehavior(List<BehaviorDefinition> existingBehaviors, BehaviorDefinition behavior) {
		StringBuilder sb = new StringBuilder();

		// Build a fast lookup of existing behavior names
		Set<String> existingNames = java.util.Collections.emptySet();
		if (existingBehaviors != null) {
			existingNames = existingBehaviors.stream()
					.map(BehaviorDefinition::getName)
					.filter(java.util.Objects::nonNull)
					.map(String::trim)
					.collect(java.util.stream.Collectors.toSet());
		}

		sb.append("  behavior('").append(behavior.getName()).append("'");
		if (behavior.isNonTerminating()) {
			sb.append(", :non_terminating");
		}
		sb.append(") {\n    repeat {\n");

		// Null-safe operations list
		java.util.List<BehaviorOperation> ops = java.util.Collections.emptyList();
		if (behavior.getOperations() != null) {
			ops = behavior.getOperations();
		}

		for (BehaviorOperation op : ops) {
			if (op == null) {
				continue;
			}

			String next = op.getNextBehavior();
			if (next == null) {
				continue;
			}

			String normalized = next.trim();
			if (normalized.isEmpty()) {
				continue;
			}

			// Include only if nextBehavior targets an existing behavior name
			if (existingNames.contains(normalized)) {
				String action = op.getActionCall() == null ? "" : op.getActionCall();
				sb.append("      o { ")
				.append(action)
				.append("; behave_as '")
				.append(normalized)
				.append("' }\n");
			}
		}

		sb.append("    }\n  }\n");
		return sb.toString();
	}

}
