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

import org.fruit.monkey.Main;

public class OutputStructure {
	
	private OutputStructure() {}

	public static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

	public static String startRunDateString;

	public static String startInnerLoopDateString;

	public static String sutProcessName;

	public static String runOutputDir;
	public static String processListenerDir;

	public static int sequenceCount;

	public static void createOutputFolders() {

		runOutputDir = Main.outputDir + File.separator + startRunDateString+"_"+sutProcessName;

		File runDir = new File(runOutputDir);
		if(!runDir.exists())
			runDir.mkdirs();

		File htmlDir = new File(runOutputDir + File.separator +"HTMLreports");
		if(!htmlDir.exists())
			htmlDir.mkdirs();

		File logsDir = new File(runOutputDir + File.separator +"logs");
		if(!logsDir.exists())
			logsDir.mkdirs();

		File logsDebugDir = new File(logsDir+ File.separator + "debug");
		if(!logsDebugDir.exists())
			logsDebugDir.mkdirs();

		processListenerDir = logsDir+ File.separator + "processListener";
		File procListDir = new File(processListenerDir);
		if(!procListDir.exists())
			procListDir.mkdirs();

	}

}
