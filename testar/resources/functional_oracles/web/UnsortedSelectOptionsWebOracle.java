/**
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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
 *
 */

package functional_oracles.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import com.google.common.collect.Comparators;

public class UnsortedSelectOptionsWebOracle {

	public static Verdict getVerdict(State state) {
		Verdict unsortedSelectElementVerdict = Verdict.OK;

		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return [...document.getElementById('%s').options].map(o => o.value)", elementId);
				@SuppressWarnings("unchecked")
				ArrayList<String> selectOptionsList = (ArrayList<String>) WdDriver.executeScript(query);

				// Now that we have collected all the array list of the option values verify that is sorted 
				if(!isSorted(selectOptionsList)) {

					String verdictMsg = String.format("Detected a Select web element with unsorted elements! Role: %s , Path: %s , WebId: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

					return new Verdict(Verdict.SEVERITY_WARNING_UNSORTED, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return unsortedSelectElementVerdict;
	}

	private static boolean isSorted(List<String> listOfStrings) {
		return Comparators.isInOrder(listOfStrings, Comparator.<String> naturalOrder());
	}

}