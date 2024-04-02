/***************************************************************************************************
 *
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
 *******************************************************************************************************/

package org.testar.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.testar.StateManagementTags;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

public class SettingsVerification {

	/**
	 * Verify the settings provided by the user. 
	 * 
	 * @param settings
	 */
	public static void verifySettings(Settings settings) {
		verifyStateModelSettings(settings);
		verifyRegularExpressionSettings(settings);
		escapeSpecialCharactersInFileWritingSettings(settings);
	}

	/**
	 * This method will check if the provided settings for the concrete and abstract state models are valid.
	 */
	private static void verifyStateModelSettings(Settings settings) {
		// verify the concrete and abstract state settings
		// the values provided should be valid state management tags
		Set<String> allowedStateAttributes = StateManagementTags.getAllTags().stream().map(StateManagementTags::getSettingsStringFromTag).collect(Collectors.toSet());

		// track the state management tags that are valid and invalid
		Set<String> validStateSet = new HashSet<>();
		Set<String> invalidStateSet = new HashSet<>();

		try {
			List<String> configAbstractStateAttributes = settings.get(ConfigTags.AbstractStateAttributes);
			for (String abstractStateAttribute : configAbstractStateAttributes) {
				if (allowedStateAttributes.contains(abstractStateAttribute)) {
					validStateSet.add(abstractStateAttribute);
				} else {
					invalidStateSet.add(abstractStateAttribute);
				}
			}

			// Inform user if invalid AbstractStateAttributes configuration detected
			if (!invalidStateSet.isEmpty()) {
				System.err.println("*** WARNING: Ignoring invalid AbstractStateAttributes configured! " + invalidStateSet);
			}

			// Set the valid AbstractStateAttributes configured by users
			settings.set(ConfigTags.AbstractStateAttributes, new ArrayList<>(validStateSet));

			// But, if AbstractStateAttributes do not exist due to invalid or empty value
			// Inform users that the abstract state properties need to have at least 1 valid value
			// Then reset to the default abstract state property
			if ((settings.get(ConfigTags.AbstractStateAttributes)).isEmpty()) {
				String msg = "*** WARNING: AbstractStateAttributes test.settings is empty or invalid! \n"
						+ "Please configure at least 1 valid abstract state attribute or leave the key out of the settings file \n"
						+ "Reseting AbstractStateAttributes test.settings to: [WidgetControlType]";
				System.err.println(msg);

				settings.set(ConfigTags.AbstractStateAttributes, Collections.singletonList("WidgetControlType"));
			}

		} catch (NoSuchTagException ex) {
			// no need to do anything, nothing to verify
		}
	}

	/**
	 * Verify if filter and oracles regular expressions settings are valid. 
	 * 
	 * @param settings
	 * @return
	 */
	private static void verifyRegularExpressionSettings(Settings settings) {
		StringBuilder invalidExpressions = new StringBuilder();

		List<Tag<String>> regularExpressionTags = Arrays.asList(
				ConfigTags.ProcessesToKillDuringTest,
				ConfigTags.ClickFilter,
				ConfigTags.SuspiciousTags,
				ConfigTags.SuspiciousProcessOutput,
				ConfigTags.ProcessLogs,
				ConfigTags.LogOracleRegex,
				ConfigTags.WebConsoleErrorPattern,
				ConfigTags.WebConsoleWarningPattern
				);

		for(Tag<String> tag : regularExpressionTags) {
			try {
				Pattern.compile(settings.get(tag, ""));
			} catch (PatternSyntaxException exception) {
				invalidExpressions.append(System.getProperty("line.separator"));
				invalidExpressions.append("Error! Your " + tag.name() + " setting is not a valid regular expression! " + settings.get(tag));
			}
		}

		if(!invalidExpressions.toString().isEmpty()) {
			invalidExpressions.append(System.getProperty("line.separator"));
			invalidExpressions.append("Settings Initialization Error! An invalid regular expression was detected in the TESTAR settings.");
			invalidExpressions.append(System.getProperty("line.separator"));
			invalidExpressions.append("Settings Initialization Error! Please fix the expressions or remove all characters and start TESTAR again.");
			invalidExpressions.append(System.getProperty("line.separator"));
			throw new IllegalStateException(invalidExpressions.toString());
		}
	}

	/**
	 * Escape special characters in settings that are used to write directories or files. 
	 * 
	 * @param settings
	 * @return
	 */
	private static void escapeSpecialCharactersInFileWritingSettings(Settings settings) {
		List<Tag<String>> writeSystemTags = Arrays.asList(
				ConfigTags.ApplicationName,
				ConfigTags.ApplicationVersion
				);

		for(Tag<String> tag : writeSystemTags) {
			Pattern p = Pattern.compile("[\\\\/?:*\"|><]");
			Matcher m = p.matcher(settings.get(tag, ""));

			if(m.find()) {
				String value = settings.get(tag, "").replaceAll("[\\\\/?:*\"|><]", "_") + "_";
				System.out.println(String.format("Info: Replacing %s special characters from %s to %s", 
						tag.name(), settings.get(tag, ""), value));
				settings.set(tag, value);
			}
		}
	}

}
