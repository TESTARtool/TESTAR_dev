package org.testar.statemodel.axini;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AmpCodeGenerator {

	public static String generate(ProcessDefinition process) {
		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("timeout 10.0\n");
		sb.append("external 'extern'\n\n");

		// 1. Action function definitions
		for (ActionDefinition action : process.getActions()) {
			sb.append(generateFunction(action)).append("\n\n");
		}

		// 2. Process wrapper
		sb.append("process('").append(process.getName()).append("') {\n\n");

		// 3. Channel definition
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

		// 5. Behavior definitions
		for (BehaviorDefinition behavior : process.getBehaviors()) {
			//sb.append("\n").append(generateBehavior(behavior)); // This can generate a non terminating behave call
			sb.append("\n").append(generateBehavior(process.getBehaviors(), behavior)); // This removes non terminating behave calls
		}

		// 6. Initial variables
		InitialVariable var = process.getInitialVariables();
		sb.append("\n  var '").append(var.getName())
		.append("', ").append(var.getType())
		.append(", ").append(var.getValue());

		// 7. Entry calls
		for (String call : process.getEntryCalls()) {
			sb.append("\n\n  ").append(call);
		}

		sb.append("\n\n}\n"); // end process
		return sb.toString();
	}

	private static String generateFunction(ActionDefinition action) {
		return String.format("def %s()\n  receive '%s', constraint: %%(%s)\n  send '%s', constraint: %%(%s)\nend",
				action.getName(),
				action.getReceivedStimulus(),
				formatConstraints(action.getReceiveConstraints()),
				action.getSentResponse(),
				formatConstraints(action.getSendConstraints())
				);
	}

	private static String formatConstraints(List<Constraint> constraints) {
		return constraints.stream()
				.map(c -> c.getField() + " == " + c.getExpression())
				.collect(Collectors.joining(" && "));
	}

	private static String generateBehavior(BehaviorDefinition behavior) {
		StringBuilder sb = new StringBuilder();

		sb.append("  behavior('").append(behavior.getName()).append("'");
		if (behavior.isNonTerminating()) {
			sb.append(", :non_terminating");
		}
		sb.append(") {\n    repeat {\n");

		for (BehaviorOperation op : behavior.getOperations()) {
			sb.append("      o { ").append(op.getActionCall())
			.append("; behave_as '").append(op.getNextBehavior()).append("' }\n");
		}

		sb.append("    }\n  }\n");
		return sb.toString();
	}

	private static String generateBehavior(List<BehaviorDefinition> existingBehaviors, BehaviorDefinition behavior) {
		StringBuilder sb = new StringBuilder();

		// Build a fast lookup of existing behavior names
		Set<String> existingNames = existingBehaviors == null
				? java.util.Collections.emptySet()
						: existingBehaviors.stream()
						.map(BehaviorDefinition::getName)
						.filter(java.util.Objects::nonNull)
						.map(String::trim)
						.collect(java.util.stream.Collectors.toSet());

				sb.append("  behavior('").append(behavior.getName()).append("'");
				if (behavior.isNonTerminating()) {
					sb.append(", :non_terminating");
				}
				sb.append(") {\n    repeat {\n");

				// Null-safe operations list
				java.util.List<BehaviorOperation> ops =
						behavior.getOperations() == null ? java.util.Collections.emptyList() : behavior.getOperations();

						for (BehaviorOperation op : ops) {
							if (op == null) continue;

							String next = op.getNextBehavior();
							if (next == null) continue;

							String normalized = next.trim();
							if (normalized.isEmpty()) continue;

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
