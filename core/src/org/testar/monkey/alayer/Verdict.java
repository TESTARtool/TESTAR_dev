/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.monkey.alayer;

import java.io.Serializable;
import java.util.Arrays;

import org.testar.monkey.Assert;
import org.testar.monkey.Util;

/**
 * A Verdict is the outcome of a test oracle. It determines whether an <code>SUT</code>'s state is erroneous and if so
 * provides a short explanation and a visualization of what is wrong.
 */
public final class Verdict implements Serializable {
	private static final long serialVersionUID = 3517681535425699094L;

	/**
	 * Enum representing different levels of severity for a test verdict.
	 */
	public enum Severity {
		/** PASS (0.0 - 0.099) **/

		// Test sequence completed without other PASS, WARNING, or FAIL, severity
		OK(0.0, "OK"),
		// LLM believes test goal has been accomplished
		LLM_COMPLETE(0.04, "LLM_COMPLETE"),
		// Test goal complete based on conditions
		CONDITION_COMPLETE(0.05, "CONDITION_COMPLETE"),

		/** WARNING (0.1 - 0.499) **/

		// Default WARNING verdict
		WARNING(0.1, "WARNING"),

		// WARNING GROUP: DATA
		WARNING_DATA_CONFIGURATION_SETTING_NOT_WORKING(0.10000002, "WARNING_DATA_CONFIGURATION_SETTING_NOT_WORKING"),
		WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED(0.10000003, "WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED"),
		WARNING_DATA_DATA_MAPPING_FAULT(0.10000004, "WARNING_DATA_DATA_MAPPING_FAULT"),
		WARNING_DATA_FILTERING_FAULT(0.10000005, "WARNING_DATA_FILTERING_FAULT"),
		WARNING_DATA_FUNCTIONAL_DOMAIN_SPECIFIC_FAULT(0.10000006, "WARNING_DATA_FUNCTIONAL_DOMAIN_SPECIFIC_FAULT"),
		WARNING_DATA_INPUT_VALIDATION_FAULT(0.10000007, "WARNING_DATA_INPUT_VALIDATION_FAULT"),
		WARNING_DATA_SORTING_FAULT(0.10000008, "WARNING_DATA_SORTING_FAULT"),
		WARNING_DATA_VISUAL_DOCUMENT_GENERATION_OR_CONVERSION_FAULT(0.10000009, "WARNING_DATA_VISUAL_DOCUMENT_GENERATION_OR_CONVERSION_FAULT"),

		// WARNING GROUP: RESOURCE
		WARNING_RESOURCE_PERFORMANCE_ISSUE(0.10000010, "WARNING_RESOURCE_PERFORMANCE_ISSUE"),
		WARNING_RESOURCE_NOT_FOUND_FAULT(0.10000011, "WARNING_RESOURCE_NOT_FOUND_FAULT"),

		// WARNING GROUP: UI
		WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE(0.10000012, "WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE"),
		WARNING_UI_FLOW_FAULT(0.10000013, "WARNING_UI_FLOW_FAULT"),
		WARNING_UI_ITEM_VISIBILITY_FAULT(0.10000014, "WARNING_UI_ITEM_VISIBILITY_FAULT"),
		WARNING_UI_ITEM_WRONG_VALUE_FAULT(0.10000015, "WARNING_UI_ITEM_WRONG_VALUE_FAULT"),
		WARNING_UI_TRIMMED_SECURITY_FAULT(0.10000016, "WARNING_UI_TRIMMED_SECURITY_FAULT"),
		WARNING_UI_VISUAL_OR_RENDERING_FAULT(0.10000017, "WARNING_UI_VISUAL_OR_RENDERING_FAULT"),

		// SUSPICIOUS GROUP: EXCEPTION
		SUSPICIOUS_ALERT(0.19999998, "SUSPICIOUS_ALERT"),

		// WARNING GROUP: WEB INVARIANT
		WARNING_WEB_INVARIANT_FAULT(0.2, "WARNING_WEB_INVARIANT_FAULT"),

		// WARNING GROUP: ACCESSIBILITY
		WARNING_ACCESSIBILITY_FAULT(0.4, "WARNING_ACCESSIBILITY_FAULT"),

		/** FAIL (0.5 - 1.0) **/

		UNREPLAYABLE(0.5, "UNREPLAYABLE"), // Sequence not replayable
		SUSPICIOUS_TAG(0.8, "SUSPICIOUS_TAG"), // Suspicious tag
		SUSPICIOUS_PROCESS(0.87, "SUSPICIOUS_PROCESS"), // Suspicious message in the process standard output/error
		SUSPICIOUS_LOG(0.89, "SUSPICIOUS_LOG"), // Suspicious message in log file or command output (LogOracle)
		NOT_RESPONDING(0.99999990, "NOT_RESPONDING"), // Unresponsive
		UNEXPECTEDCLOSE(0.99999999, "UNEXPECTEDCLOSE"), // Crash? Unexpected close?
		FAIL(1.0, "FAIL");

		private final double value;
		private final String title;

		Severity(double value, String title) {
			this.value = value;
			this.title = title;
		}

		public double getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}

		/**
		 * Retrieves the title corresponding to a given severity value.
		 * If the severity does not match any predefined values, "FAILURE" is returned.
		 *
		 * @param severity The severity value to look up.
		 * @return The corresponding title for the given severity.
		 */
		public static String getTitleBySeverity(double severity) {
			return Arrays.stream(values())
					.filter(s -> s.value == severity)
					.map(Severity::getTitle)
					.findFirst()
					.orElse("FAILURE");
		}
	}

	private final String info; // Short Verdict info that can be used as identifier
	private final double severity;
	private final Visualizer visualizer;

	private String description = ""; // Optional complete Verdict description with extended context

	public static final Verdict OK = new Verdict(Severity.OK, "No problem detected.", Util.NullVisualizer);
	public static final Verdict FAIL = new Verdict(Severity.FAIL, "SUT failed.", Util.NullVisualizer);

	public Verdict(Severity severity, String info) {
		this(severity, info, Util.NullVisualizer);
	}

	public Verdict(Severity severity, String info, Visualizer visualizer) {
		Assert.isTrue(severity.getValue() >= Severity.OK.getValue() && severity.getValue() <= Severity.FAIL.getValue());
		Assert.notNull(info, visualizer);
		this.severity = severity.getValue();
		this.info = info;
		this.visualizer = visualizer;
	}

	/**
	 * Returns the likelihood of the state being erroneous (value within interval [0, 1]).
	 *
	 * @return A value within [0, 1] representing the error likelihood.
	 */
	public double severity() {
		return severity;
	}

	/**
	 * Returns a short informative message about whether the state is erroneous and, if so, what part of it.
	 *
	 * @return A short informative message of the issue.
	 */
	public String info() {
		return info;
	}

	/**
	 * Set an optional complete description about the context of the state issue.
	 */
	public void setDescription(String description) {
		this.description = (description == null) ? "" : description;
	}

	/**
	 * Returns an optional complete description about the context of the state issue.
	 *
	 * @return An optional complete description of the issue, or an empty string if no provided.
	 */
	public String description() {
		return description;
	}

	/**
	 * Retrieves the title associated with this verdict's severity.
	 * The title represents a human-readable categorization of the severity.
	 *
	 * @return The title corresponding to this verdict's severity.
	 */
	public String verdictSeverityTitle() {
		return Severity.getTitleBySeverity(severity);
	}

	/**
	 * This visualizer should visualize the part of the state where the problem occurred.
	 * For example: If there is a suspicious control element, like f.e. a critical message box
	 * than this should be framed or pointed to with a big red arrow.
	 * 
	 * @return the visualizer which is guaranteed to be non-null
	 */
	public Visualizer visualizer() {
		return visualizer;
	}

	@Override
	public String toString() {
		return "severity: " + severity + " info: " + info;
	}

	/**
	 * Retrieves the verdict result of joining two verdicts.
	 * @param verdict A verdict to join with current verdict.
	 * 
	 * @return A new verdict that is the result of joining the current verdict with the provided verdict.
	 */
	public Verdict join(Verdict verdict) {
		Severity joinedSeverity = Arrays.stream(Severity.values())
				.filter(s -> s.getValue() == Math.max(this.severity, verdict.severity()))
				.findFirst()
				.orElse(Severity.FAIL);

		String joinedInfo = this.info.contains(verdict.info()) ? this.info
				: (this.severity == Severity.OK.getValue() ? "" : this.info + "\n") + verdict.info();

		Visualizer joinedVisualizer = (this.severity >= verdict.severity()) ? this.visualizer() : verdict.visualizer();

		Verdict jointVerdict = new Verdict(joinedSeverity, joinedInfo, joinedVisualizer);
		jointVerdict.setDescription(concatWithNewline(this.description(), verdict.description()));

		return jointVerdict;
	}

	private String concatWithNewline(String a, String b) {
		String a1 = a == null ? "" : a.trim();
		String b1 = b == null ? "" : b.trim();
		if (a1.isEmpty()) return b1;
		if (b1.isEmpty()) return a1;
		return a1 + "\n" + b1;
	}

	/**
	 * Determines if two Verdict objects are equal.
	 *
	 * @param o The object to compare with.
	 * @return True if both verdicts have the same severity, info, and visualizer.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Verdict)) return false;
		Verdict other = (Verdict) o;
		// Use the severity, info, and visualizer for Verdict equality comparison
		// Note: description intentionally NOT part of equality comparison
		return this.severity == other.severity
				&& this.info.equals(other.info)
				&& this.visualizer.equals(other.visualizer);
	}

}