/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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


package org.testar.verdicts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fruit.Util;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Roles;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.visualizers.ShapeVisualizer;

public class GeneralVerdictItem {

	String suspiciousListName;
	Pattern suspiciousPattern;
	
	public GeneralVerdictItem(String suspiciousListName, Set<String> tagsName, Pattern suspiciousPattern) {
		this.suspiciousListName = suspiciousListName;
		this.suspiciousPattern = suspiciousPattern;
		
		for(String tag : tagsName) {
			setGeneralStringVerdictTags(tag);
		}
	}
	
	private static Map<Tag<String>, Boolean> generalStringVerdictTags = new HashMap<>();

	static {
		generalStringVerdictTags.put(Tags.Title, false);
		generalStringVerdictTags.put(Tags.ValuePattern, false);
		generalStringVerdictTags.put(Tags.Text, false);
		generalStringVerdictTags.put(Tags.Desc, false);
	}

	public void setGeneralStringVerdictTags(String tagName) {
		for(Map.Entry<Tag<String>, Boolean> entry : generalStringVerdictTags.entrySet()) {
			if(entry.getKey().toString().equals(tagName)) {
				generalStringVerdictTags.put(entry.getKey(), true);
			}
		}
	}

	public Set<Tag<String>> getEnabledGeneralStringVerdictTags() {
		Set<Tag<String>> enabledVerdictTags = new HashSet<>();
		for(Map.Entry<Tag<String>, Boolean> entry : generalStringVerdictTags.entrySet()) {
			if(entry.getValue()) {
				enabledVerdictTags.add(entry.getKey());
			}
		}
		return enabledVerdictTags;
	}

	/*public boolean isGeneralStringVerdictTagEnabled(String tagName) {
		for(Map.Entry<Tag<String>, Boolean> entry : generalStringVerdictTags.entrySet()) {
			if(entry.getKey().toString().equals(tagName)) {
				return entry.getValue();
			}
		}
		return false;
	}*/

	public Verdict suspiciousStringValueMatcher(Widget w, Pen redPen) {

		for(Tag<String> t : getEnabledGeneralStringVerdictTags()) {

			Map<String, Matcher> suspiciousPatternMatchers = new WeakHashMap<>();
			Matcher m;

			if(t != null && !w.get(t,"").isEmpty()) {

				if(concreteTagValueToIgnore(t, w)) {
					continue;
				}

				m = suspiciousPatternMatchers.get(w.get(t,""));
				m = suspiciousPattern.matcher(w.get(t,""));
				suspiciousPatternMatchers.put(w.get(t,""), m);

				if (m.matches()) {
					Visualizer visualizer = Util.NullVisualizer;
					// visualize the problematic widget, by marking it with a red box
					if(w.get(Tags.Shape, null) != null) {
						visualizer = new ShapeVisualizer(redPen, w.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
					}

					return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE, 
							"Discovered suspicious widget '" + t.name() + "' : '" + w.get(t,"") + "'.", visualizer);
				}
			} 
		}

		return Verdict.OK;
	}

	/**
	 * Some Text Edit widgets have Tag Values that dynamically change based on typed text.
	 * Ignore these Specific Tags 
	 * 
	 * @param Tag
	 * @param Widget
	 * @return
	 */
	private boolean concreteTagValueToIgnore(Tag<?> t, Widget w) {
		//Ignore value ValuePattern for UIAEdit widgets
		if (t.name().equals("ValuePattern") && w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")) {
			return true;
		}
		return false;
	}
}
