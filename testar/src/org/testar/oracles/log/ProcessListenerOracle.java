/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.DefaultProtocol;
import org.testar.monkey.Util;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.oracles.Oracle;
import org.testar.settings.Settings;

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
		if(settings.get(ConfigTags.Mode).equals(Modes.Spy)) {
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
	public Verdict getVerdict(State state) {
		if (!processListenerEnabled) {
			return Verdict.OK;
		}

		try {
			// Read one line from each stream (non-blocking)
			Verdict standardErrorVerdict = checkStream(standardErrorReader, "_StdErr.log", "StdErr");
			Verdict standardOutputVerdict = checkStream(standardOutputReader, "_StdOut.log", "StdOut");

			if (standardErrorVerdict.severity() != Verdict.Severity.OK.getValue())
				return standardErrorVerdict;

			if (standardOutputVerdict.severity() != Verdict.Severity.OK.getValue())
				return standardOutputVerdict;

		} catch (IOException e) {
			System.err.println("Unable to read the SUT process buffer");
			return Verdict.OK;
		}

		return Verdict.OK;
	}

	private Verdict checkStream(BufferedReader reader, String logSuffix, String streamLabel) throws IOException {
		String line;
		while ((line = reader.ready() ? reader.readLine() : null) != null) {
			String actionId = "unknown";
			if (DefaultProtocol.lastExecutedAction != null) {
				actionId = DefaultProtocol.lastExecutedAction.get(Tags.ConcreteID, "");
			}

			Matcher oracleMatch = processOracles.matcher(line);
			Matcher logMatch = processLogs.matcher(line);

			if (oracleMatch.matches()) {
				Verdict verdict = new Verdict(Verdict.Severity.SUSPICIOUS_PROCESS,
						"Process Listener suspicious process: '" + line + "' on Action: '" + actionId + "'.");
				logLine(line, logSuffix, actionId, streamLabel);
				return verdict;
			} else if (logMatch.matches()) {
				logLine(line, logSuffix, actionId, streamLabel);
			}
		}
		return Verdict.OK;
	}

	private void logLine(String line, String suffix, String actionId, String stream) {
		String timestamp = Util.dateString(DATE_FORMAT);
		String logFileName = logProcessListenerName + suffix;

		try (PrintWriter writer = new PrintWriter(new FileWriter(logFileName, true))) {
			System.out.println("SUT Log " + stream + ": " + line);
			writer.println(timestamp + " on Action: " + actionId + " SUT " + stream + ": " + line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
