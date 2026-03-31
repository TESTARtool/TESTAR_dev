/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.llm.oracle;

import java.util.Locale;

public enum LlmVerdictDecision {
	CONTINUE,
	COMPLETED,
	INVALID,
	UNKNOWN;

	public static LlmVerdictDecision fromStatus(String status) {
		if (status == null || status.trim().isEmpty()) {
			return UNKNOWN;
		}

		String normalized = status.trim().toUpperCase(Locale.ROOT);
		switch (normalized) {
		case "CONTINUE":
			return CONTINUE;
		case "COMPLETED":
			return COMPLETED;
		case "INVALID":
			return INVALID;
		default:
			return UNKNOWN;
		}
	}
}
