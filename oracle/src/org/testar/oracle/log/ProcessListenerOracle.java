/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.core.util.Util;
import org.testar.config.TestarMode;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.config.settings.Settings;

public class ProcessListenerOracle implements Oracle {

	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private final SUT system;
	private final Settings settings;

	private boolean processListenerEnabled = false;

	private Pattern processOracles;
	private Pattern processLogs;
	private String logProcessListenerName;

	private BufferedReader standardErrorReader;
	private BufferedReader standardOutputReader;

	public ProcessListenerOracle(SUT system, Settings settings) {
		this.system = system;
		this.settings = settings;
	}

	@Override
	public void initialize() {
		//Disabled with Spy Mode, don't need for SUT exploration
		if(settings.get(ConfigTags.Mode).equals(TestarMode.Spy)) {
			System.out.println("INFO: Process Listeners feature is disabled in Spy Mode");
			return;
		}

		//Only for SUTs executed with command_line
		if(!settings.get(ConfigTags.SUTConnector).equals(Settings.SUT_CONNECTOR_CMDLINE)) {
			System.out.println("INFO: Process Listeners only allowed for Desktop Aplications invoked by SUTConnector: COMMAND_LINE");
			return;
		}

		//Disabled for browsers
		if(settings.get(ConfigTags.SUTConnector).equals(Settings.SUT_CONNECTOR_WEBDRIVER)) {
			System.out.println("INFO: Process Listeners only allowed for Desktop Aplications not working with web browsers");
			return;
		}

		// Initialize the processOracles using the regular expression SuspiciousProcessOutput from test settings file
		processOracles = Pattern.compile(settings.get(ConfigTags.SuspiciousProcessOutput), Pattern.UNICODE_CHARACTER_CLASS);
		// Initialize the ProcessLogs using the regular expression ProcessLogs from test settings file
		processLogs = Pattern.compile(settings.get(ConfigTags.ProcessLogs), Pattern.UNICODE_CHARACTER_CLASS);

		logProcessListenerName = OutputStructure.processListenerDir
				+ File.separator + OutputStructure.startInnerLoopDateString + "_"
				+ OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount;

		standardErrorReader = new BufferedReader(new InputStreamReader(system.get(Tags.StdErr)));
		standardOutputReader = new BufferedReader(new InputStreamReader(system.get(Tags.StdOut)));

		processListenerEnabled = true;
		System.out.println("Process Listener enabled correctly");
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		if (!processListenerEnabled) {
			return Collections.singletonList(Verdict.OK);
		}

		try {
			// Read one line from each stream (non-blocking)
			Verdict standardErrorVerdict = checkStream(standardErrorReader, "_StdErr.log", "StdErr");
			Verdict standardOutputVerdict = checkStream(standardOutputReader, "_StdOut.log", "StdOut");

			if (standardErrorVerdict.severity() != Verdict.Severity.OK.getValue())
				return Collections.singletonList(standardErrorVerdict);

			if (standardOutputVerdict.severity() != Verdict.Severity.OK.getValue())
				return Collections.singletonList(standardOutputVerdict);

		} catch (IOException e) {
			System.err.println("Unable to read the SUT process buffer");
			return Collections.singletonList(Verdict.OK);
		}

		return Collections.singletonList(Verdict.OK);
	}

	private Verdict checkStream(BufferedReader reader, String logSuffix, String streamLabel) throws IOException {
		String line;
		while ((line = reader.ready() ? reader.readLine() : null) != null) {
			Matcher oracleMatch = processOracles.matcher(line);
			Matcher logMatch = processLogs.matcher(line);

			if (oracleMatch.matches()) {
				Verdict verdict = new Verdict(Verdict.Severity.SUSPICIOUS_PROCESS,
						"Process Listener suspicious process: '" + line + "'.");
				logLine(line, logSuffix, streamLabel);
				return verdict;
			} else if (logMatch.matches()) {
				logLine(line, logSuffix, streamLabel);
			}
		}
		return Verdict.OK;
	}

	private void logLine(String line, String suffix, String stream) {
		String timestamp = Util.dateString(DATE_FORMAT);
		String logFileName = logProcessListenerName + suffix;

		try (PrintWriter writer = new PrintWriter(new FileWriter(logFileName, true))) {
			System.out.println("SUT Log " + stream + ": " + line);
			writer.println(timestamp + " SUT " + stream + ": " + line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
