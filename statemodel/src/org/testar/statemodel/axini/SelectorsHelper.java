package org.testar.statemodel.axini;

import java.util.*;

public class SelectorsHelper {

	public static String extractTextFromDesc(String desc) {
		if (desc == null) return "";

		int first = desc.indexOf('\'');
		if (first == -1) return "";

		int last = desc.lastIndexOf('\'');
		if (last <= first) return "";

		return desc.substring(first + 1, last);
	}

	// Returns the original css if it's appropriate; otherwise returns "".
	// For comma-separated lists, removes the ugly ones; if none remain, returns "".
	public static String isAppropiatteCssSelector(String css) {
		if (css == null) return "";
		String[] parts = css.split(",");
		List<String> kept = new ArrayList<>();

		for (String raw : parts) {
			String s = raw.trim();
			if (s.isEmpty()) continue;

			if (!isUglySelector(s)) {
				kept.add(s);
			}
		}

		return kept.isEmpty() ? "" : String.join(", ", kept);
	}

	// ---- Heuristics ---------------------------------------------------------
	// "Ugly" if any of the following:
	// 1) Only :root and :nth-child(...) hops (no tags, ids, classes, attrs).
	// 2) Many nth-child segments (>=4) and no stable hooks (#, ., [data-*, [aria-*, [name=, [id=]).
	// 3) Very deep (>8 simple steps) AND nth-child makes up the majority.
	private static boolean isUglySelector(String s) {
		// 1) Only :root + :nth-child(...) hops (allow >, +, ~, spaces as combinators)
		if (s.matches("^\\s*:root(?:\\s*[>+~\\s]\\s*:nth-child\\(\\d+\\))+\\s*$")) {
			return true;
		}

		int nthCount = countOccurrences(s, ":nth-child(");
		int steps = countSimpleSteps(s);
		boolean hasStableHook = hasStableHook(s);

		// 2) Lots of nth-child and no stable hook
		if (nthCount >= 4 && !hasStableHook) {
			return true;
		}

		// 3) Too deep and mostly nth-child
		double nthRatio = steps == 0 ? 0.0 : (double) nthCount / (double) steps;
		if (steps > 8 && nthRatio >= 0.6 && !hasStableHook) {
			return true;
		}

		return false;
	}

	// Count approximate "simple steps" by splitting on combinators.
	// This is a rough depth measure, good enough for our heuristic.
	private static int countSimpleSteps(String s) {
		// Normalize whitespace around combinators
		String normalized = s.replaceAll("\\s*(>|\\+|~)\\s*", " $1 ");
		// Split on combinators and whitespace sequences between simple selectors
		String[] chunks = normalized.trim().split("\\s+|>|\\+|~");
		int count = 0;
		for (String c : chunks) {
			if (!c.isEmpty()) count++;
		}
		return count;
	}

	private static int countOccurrences(String s, String needle) {
		int idx = 0, cnt = 0;
		while ((idx = s.indexOf(needle, idx)) != -1) {
			cnt++;
			idx += needle.length();
		}
		return cnt;
	}

	private static boolean hasStableHook(String s) {
		// Any id (#...), class (....), or useful attributes like data-*, aria-*, name=, id=
		// Also treat explicit tag names as a (weak) hook:
		boolean hasIdOrClass = s.matches(".*(#|\\.).*");
		boolean hasUsefulAttr = s.matches(".*\\[\\s*(data-[^=\\]]+|aria-[^=\\]]+|name|id)\\s*(=|\\]).*");
		boolean hasTag = s.matches(".*(^|[\\s>+~])([a-zA-Z][a-zA-Z0-9_-]*)(?=([\\s#.\\[:>+~]|$)).*");
		return hasIdOrClass || hasUsefulAttr || hasTag;
	}

}
