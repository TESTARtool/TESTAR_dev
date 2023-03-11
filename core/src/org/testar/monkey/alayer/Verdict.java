/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
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
import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.Assert;
import org.testar.monkey.Util;

/**
 * A Verdict is the outcome of a test oracle. It determines whether an <code>SUT</code>'s state is erroneous and if so
 * provides a short explanation and a visualization of what is wrong. 
 */
public final class Verdict implements Serializable {
	private static final long serialVersionUID = 3517681535425699094L;

	//public static final Verdict OK = new Verdict(0.0, "No problem detected.", Util.NullVisualizer);

	// Verdict severities
	// PASS
	public static final double SEVERITY_MIN = 0.0;

	// WARNING
	public static final double SEVERITY_WARNING =              		0.00000001; // must be less than FAULT THRESHOLD @test.settings
	public static final double SEVERITY_WARNING_DECIMALS =     		0.00000002;
	public static final double SEVERITY_WARNING_TABLE_ROWS =   		0.00000003;
	public static final double SEVERITY_WARNING_ORPHAN_ITEM =  		0.00000004;
	public static final double SEVERITY_WARNING_UNSORTED =     		0.00000005;
	public static final double SEVERITY_WARNING_NON_WRITABLE = 		0.00000006;
	public static final double SEVERITY_WARNING_MANY_ITEMS =   		0.00000007;
	public static final double SEVERITY_WARNING_DUPLICATE_ITEMS =	0.00000008;

	// SUSPICIOUS_TITLE
	public static final double SEVERITY_SUSPICIOUS_ALERT = 0.19999998; // suspicious alert
	public static final double SEVERITY_SUSPICIOUS_TITLE = 0.19999999; // suspicious title

	// CRASH
	public static final double SEVERITY_NOT_RESPONDING =   0.99999990; // unresponsive
	public static final double SEVERITY_NOT_RUNNING =	   0.99999999; // crash? unexpected close?
	public static final double SEVERITY_MAX = 1.0;

	public static final double SEVERITY_OK = 			   SEVERITY_MIN;
	public static final double SEVERITY_FAIL =	   		   SEVERITY_MAX;

	public static final double SEVERITY_UNREPLAYABLE = 0.1;

	public static final Verdict OK = new Verdict(SEVERITY_OK, "No problem detected.", Util.NullVisualizer);
	public static final Verdict FAIL = new Verdict(SEVERITY_FAIL, "SUT failed.", Util.NullVisualizer);


	private final String info;
	private final double severity;
	private final Visualizer visualizer;

	private final List<Rect> visualtHighlights;
	public List<Rect> getVisualtHighlights(){
		return visualtHighlights;
	}

	private String description = "";
	public void addDescription(String description) {
		this.description = this.description + description;
	}
	public String getDescription() {
		return this.description;
	}

	public Verdict(double severity, String info){
		this(severity, info, Util.NullVisualizer, new ArrayList<>());
	}

	public Verdict(double severity, String info, List<Rect> visualtHighlights){
		this(severity, info, Util.NullVisualizer, visualtHighlights);
	}

	public Verdict(double severity, String info, Visualizer visualizer){
		this(severity, info, visualizer, new ArrayList<>());
	}

	public Verdict(double severity, String info, Visualizer visualizer, List<Rect> visualtHighlights){
		//Assert.isTrue(severity >= 0 && severity <= 1.0);
		Assert.isTrue(severity >= SEVERITY_MIN && severity <= SEVERITY_MAX);
		Assert.notNull(info, visualizer, visualtHighlights);
		this.severity = severity;
		this.info = info;
		this.visualizer = visualizer;
		this.visualtHighlights = visualtHighlights;
	}

	/**
	 * returns the likelihood of the state to be erroneous (value within interval [0, 1])
	 * @return value within [0, 1]
	 */
	public double severity(){ return severity; }

	/**
	 * returns a short description about whether the state is erroneous and if so, what part of it
	 * @return
	 */
	public String info(){ return info; }

	public String verdictSeverityTitle() {
		if(severity == Verdict.SEVERITY_MIN) return "OK";

		if(severity == Verdict.SEVERITY_WARNING) return "WARNING";
		if(severity == Verdict.SEVERITY_WARNING_DECIMALS) return "SEVERITY_WARNING_DECIMALS";
		if(severity == Verdict.SEVERITY_WARNING_TABLE_ROWS) return "SEVERITY_WARNING_TABLE_ROWS";
		if(severity == Verdict.SEVERITY_WARNING_ORPHAN_ITEM) return "SEVERITY_WARNING_ORPHAN_ITEM";
		if(severity == Verdict.SEVERITY_WARNING_UNSORTED) return "SEVERITY_WARNING_UNSORTED";
		if(severity == Verdict.SEVERITY_WARNING_NON_WRITABLE) return "SEVERITY_WARNING_NON_WRITABLE";
		if(severity == Verdict.SEVERITY_WARNING_MANY_ITEMS) return "SEVERITY_WARNING_MANY_ITEMS";
		if(severity == Verdict.SEVERITY_WARNING_DUPLICATE_ITEMS) return "SEVERITY_WARNING_DUPLICATE_ITEMS";
		
		if(severity == Verdict.SEVERITY_SUSPICIOUS_ALERT) return "SUSPICIOUS_ALERT";
		if(severity == Verdict.SEVERITY_SUSPICIOUS_TITLE) return "SUSPICIOUS_TITLE";

		if(severity == Verdict.SEVERITY_NOT_RESPONDING) return "NOT_RESPONDING";
		if(severity == Verdict.SEVERITY_NOT_RUNNING) return "NOT_RUNNING";

		if(severity == Verdict.SEVERITY_UNREPLAYABLE) return "NOT_REPLAYABLE";

		return "ERROR";
	}

	/**
	 * This visualizer should visualize the part of the state where the problem occurred.
	 * For example: If there is a suspicious control element, like f.e. a critical message box
	 * than this should be framed or pointed to with a big red arrow. 
	 * @return the visualizer which is guaranteed to be non-null
	 */
	public Visualizer visualizer(){ return visualizer; }

	public String toString(){ return "severity: " + severity + " info: " + info; }

	/**
	 * Retrieves the verdict result of joining two verdicts.
	 * @param verdict A verdict to join with current verdict.
	 * @return A new verdict that is the result of joining the current verdict with the provided verdict.
	 */
	public Verdict join(Verdict verdict){
		List<Rect> jointHighlights = new ArrayList<>();
		jointHighlights.addAll(this.visualtHighlights);
		jointHighlights.addAll(verdict.getVisualtHighlights());

		Verdict jointVerdict = new Verdict(Math.max(this.severity, verdict.severity()),
				(this.info.contains(verdict.info) ? this.info : (this.severity == SEVERITY_OK ? "" : this.info + "\n") + verdict.info()),
				jointHighlights);

		jointVerdict.addDescription(verdict.getDescription());

		return jointVerdict;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Verdict) {
			Verdict other = (Verdict)o;
			return this.severity == other.severity
					&& this.info.equals(other.info)
					&& this.visualizer.equals(other.visualizer);
		}
		return false;
	}

}
