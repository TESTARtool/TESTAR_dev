/***************************************************************************************************
 *
 * Copyright (c) 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 Open Universiteit - www.ou.nl
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

package org.testar.coverage.jacoco;

import java.io.File;

import org.testar.OutputStructure;
import org.testar.coverage.CodeCoverage;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

public class JacocoCoverage implements CodeCoverage {

	private String outputJacocoCoveragePath;
	private MBeanClient mbeanClient;
	private JacocoReportCSV jacocoReportCSV;
	private MergeJacocoFiles mergeJacocoFiles;

	public JacocoCoverage(Settings settings, String outputCoveragePath) {
		// Create a file directory to store the jacoco coverage file results
		outputJacocoCoveragePath = outputCoveragePath + File.separator + "jacoco";
		File outputJacocoCoverageDir = new File(outputJacocoCoveragePath);
		if(!outputJacocoCoverageDir.exists())
			outputJacocoCoverageDir.mkdirs();

		// Initialize the MBeanClient that connects to the Jacoco agent port to extract coverage
		mbeanClient = new MBeanClient(settings.get(ConfigTags.JacocoCoverageIpAddress), settings.get(ConfigTags.JacocoCoveragePort));

		// Initialize the CSV reporter
		jacocoReportCSV = new JacocoReportCSV(settings);

		// Initialize MergeJacocoFiles if user wants to track accumulative Jacoco coverage
		if(settings.get(ConfigTags.JacocoCoverageAccumulate, false)) {
			mergeJacocoFiles = new MergeJacocoFiles();
		}
	}

	@Override
	public void getSequenceCoverage() {
		// Create the default SUT + sequence name + sequence number to extract the jacoco coverage exec file
		String destJacocoSequenceFileName = outputJacocoCoveragePath + File.separator 
				+ OutputStructure.startOuterLoopDateString + "_" + OutputStructure.executedSUTname 
				+ "_sequence_" + OutputStructure.sequenceInnerLoopCount;

		String jacocoExecSequenceCoverage = mbeanClient.dumpJacocoReport(destJacocoSequenceFileName + ".exec");

		// Once the exec file is created, prepare the CSV results
		jacocoReportCSV.generateCSVresults(jacocoExecSequenceCoverage, destJacocoSequenceFileName + ".csv");
	}

	@Override
	public void getActionCoverage(String actionCount) {
		// Create the default SUT + sequence name + sequence number + action number to extract the jacoco coverage exec file
		String destJacocoActionFileName = outputJacocoCoveragePath + File.separator 
				+ OutputStructure.startOuterLoopDateString + "_" + OutputStructure.executedSUTname 
				+ "_sequence_" + OutputStructure.sequenceInnerLoopCount 
				+ "_action_" + actionCount;

		String jacocoExecActionCoverage = mbeanClient.dumpJacocoReport(destJacocoActionFileName + ".exec");

		// Once the exec file is created, prepare the CSV results
		jacocoReportCSV.generateCSVresults(jacocoExecActionCoverage, destJacocoActionFileName + ".csv");

		// Compute the accumulative Jacoco coverage if enabled by user
		if(mergeJacocoFiles != null) {
			mergeJacocoFiles.testarExecuteMojo(jacocoReportCSV, jacocoExecActionCoverage);
			// Write an additional accumulative ratio coverage file
			jacocoReportCSV.writeAccumulativeCoverage(outputJacocoCoveragePath, actionCount);
		}
	}
}
