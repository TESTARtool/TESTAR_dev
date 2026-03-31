/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.oracle;

public class LlmVerdict {
	private Boolean match;
	private String status;
	private String info;

	public LlmVerdict(Boolean match, String info) {
		this(match, "", info);
	}

	public LlmVerdict(Boolean match, String status, String info) {
		this.match = match;
		this.status = status;
		this.info = info;
	}

	public Boolean match() {
		return match;
	}

	public String getStatus() {
		return status;
	}

	public String getInfo() {
		return info;
	}

	public LlmVerdictDecision getDecision() {
		LlmVerdictDecision fromStatus = LlmVerdictDecision.fromStatus(status);
		// Return a (CONTINUE | COMPLETED | INVALID) detected LLM Oracle decision
		if (fromStatus != LlmVerdictDecision.UNKNOWN) {
			return fromStatus;
		}
		// Return a true/false detected LLM Oracle decision
		if (Boolean.TRUE.equals(match)) {
			return LlmVerdictDecision.COMPLETED;
		}
		if (Boolean.FALSE.equals(match)) {
			return LlmVerdictDecision.CONTINUE;
		}
		return LlmVerdictDecision.UNKNOWN;
	}
}
