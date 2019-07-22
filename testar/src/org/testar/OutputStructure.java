/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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


package org.testar;

import java.io.File;

import org.fruit.Util;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;

public class OutputStructure {

	private OutputStructure() {}

	public static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

	public static String startOuterLoopDateString;
	public static String startInnerLoopDateString;

	public static String executedSUTname;
	public static int sequenceInnerLoopCount;

	public static String outerLoopOutputDir;
	public static String sequencesOutputDir;
	public static String screenshotsOutputDir;
	public static String htmlOutputDir;
	public static String logsOutputDir;
	public static String debugLogsOutputDir;
	public static String processListenerDir;

	public static void calculateOuterLoopDateString() {
		String date = Util.dateString(OutputStructure.DATE_FORMAT);
		date = date + "s";
		date = date.substring(0, 16) + "m" + date.substring(17);
		date = date.substring(0, 13) + "h" + date.substring(14);
		startOuterLoopDateString = date;
	}

	public static void calculateInnerLoopDateString() {
		String date = Util.dateString(OutputStructure.DATE_FORMAT);
		date = date + "s";
		date = date.substring(0, 16) + "m" + date.substring(17);
		date = date.substring(0, 13) + "h" + date.substring(14);
		startInnerLoopDateString = date;
	}

	public static void createOutputSUTname(Settings settings) {
		executedSUTname = "";

		if(settings.get(ConfigTags.ApplicationName,"").equals("")) {

			String sutConnectorValue = settings.get(ConfigTags.SUTConnectorValue);

			sutConnectorValue = sutConnectorValue.replace("/", File.separator);

			try {
				if (sutConnectorValue.contains("http") && sutConnectorValue.contains("www.")) {
					int indexWWW = sutConnectorValue.indexOf("www.")+4;
					int indexEnd = sutConnectorValue.indexOf(".", indexWWW);
					String domain = sutConnectorValue.substring(indexWWW, indexEnd);
					executedSUTname = domain;
				}
				else if (sutConnectorValue.contains(".exe")) {
					int startSUT = sutConnectorValue.lastIndexOf(File.separator)+1;
					int endSUT = sutConnectorValue.indexOf(".exe");
					String sutName = sutConnectorValue.substring(startSUT, endSUT);
					executedSUTname = sutName;
				}
				else if (sutConnectorValue.contains(".jar")) {
					int startSUT = sutConnectorValue.lastIndexOf(File.separator)+1;
					int endSUT = sutConnectorValue.indexOf(".jar");
					String sutName = sutConnectorValue.substring(startSUT, endSUT);
					executedSUTname = sutName;
				}
			}catch(Exception e) {
				System.out.println("Error: This run generation will be stored with empty name");
			}
			
		}else {
			executedSUTname = settings.get(ConfigTags.ApplicationName,"");
		}
		
		String version = settings.get(ConfigTags.ApplicationVersion,"");
		if(!version.isEmpty())
			executedSUTname += "_" + version;

	}

	public static void createOutputFolders() {

		outerLoopOutputDir = Main.outputDir + File.separator + startOuterLoopDateString + "_" + executedSUTname;
		File runDir = new File(outerLoopOutputDir);
		runDir.mkdirs();

		//Check if main output folder was created correctly, if not use unknown name with timestamp
		if(!runDir.exists()) {
			runDir = new File(Main.outputDir + File.separator + startOuterLoopDateString + "_unknown");
			runDir.mkdirs();
		}

		sequencesOutputDir = outerLoopOutputDir + File.separator + "sequences";
		File seqDir = new File(sequencesOutputDir);
		if(!seqDir.exists())
			seqDir.mkdirs();

		screenshotsOutputDir = outerLoopOutputDir + File.separator + "scrshots";
		File scrnDir = new File(screenshotsOutputDir);
		if(!scrnDir.exists())
			scrnDir.mkdirs();

		htmlOutputDir = outerLoopOutputDir + File.separator + "HTMLreports";
		File htmlDir = new File(htmlOutputDir);
		if(!htmlDir.exists())
			htmlDir.mkdirs();

		logsOutputDir = outerLoopOutputDir + File.separator + "logs";
		File logsDir = new File(logsOutputDir);
		if(!logsDir.exists())
			logsDir.mkdirs();

		debugLogsOutputDir = logsOutputDir + File.separator + "debug";
		File logsDebugDir = new File(debugLogsOutputDir);
		if(!logsDebugDir.exists())
			logsDebugDir.mkdirs();

		processListenerDir = logsOutputDir + File.separator + "processListener";
		File procListDir = new File(processListenerDir);
		if(!procListDir.exists())
			procListDir.mkdirs();
	}

}
