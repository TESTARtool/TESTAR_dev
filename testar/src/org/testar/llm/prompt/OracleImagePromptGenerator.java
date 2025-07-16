/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.llm.prompt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class OracleImagePromptGenerator implements IPromptOracleGenerator {

	protected static final Logger logger = LogManager.getLogger();

	/**
	 * Creates a new oracle prompt generator that uses state images.
	 */
	public OracleImagePromptGenerator() {}

	@Override
	public String generateOraclePrompt(State state, String appName, String currentTestGoal, String previousTestGoal) {

		StringBuilder builder = new StringBuilder();
		builder.append(String.format("We are testing the \"%s\" web application. ", appName));

		if(StringUtils.isEmpty(previousTestGoal)) {
			builder.append(String.format("The objective of the test is: %s. ", currentTestGoal));
		} else {
			builder.append(String.format("The following objective was previously achieved: %s. ", previousTestGoal));
			builder.append(String.format("The current objective of the test is: %s. ", currentTestGoal));
		}

		String pageTitle = state.get(WdTags.WebTitle, "");
		builder.append(String.format("We are currently on the following page: %s. ", pageTitle));

		builder.append("An image of the current state is attached. ");

		builder.append("Is the test objective met in this state?");

		return builder.toString();
	}

}
