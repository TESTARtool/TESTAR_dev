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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class AlertSuspiciousMessageWebOracle {

	public static Verdict getVerdict(State state, String pattern, Action lastExecutedAction) {
		Verdict alertVerdict = Verdict.OK;

		if(!WdDriver.alertMessage.isEmpty()) {
			Matcher matcher = Pattern.compile(pattern).matcher(WdDriver.alertMessage);
			if (matcher.find()) {
				// The widget to remark is the state by default
				Widget w = state;
				// But if the alert was prompt by executing an action in a widget, remark this widget
				if(lastExecutedAction != null  && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
					w = lastExecutedAction.get(Tags.OriginWidget);
				}

				String verdictMsg = String.format("Detected an alert with a suspicious message %s ! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
						WdDriver.alertMessage, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

				alertVerdict = alertVerdict.join(new Verdict(Verdict.SEVERITY_SUSPICIOUS_ALERT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return alertVerdict;
	}

}