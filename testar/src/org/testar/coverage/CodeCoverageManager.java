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

package org.testar.coverage;

import java.io.File;
import java.util.ArrayList;

import org.testar.OutputStructure;
import org.testar.coverage.jacoco.JacocoCoverage;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

public class CodeCoverageManager implements CodeCoverage {

	private ArrayList<CodeCoverage> coverageExtractors;

	public CodeCoverageManager(Settings settings) {
		// Create a file directory to store the coverage file results
		String outputCoveragePath = OutputStructure.outerLoopOutputDir + File.separator + "coverage";
		File outputCoverageDir = new File(outputCoveragePath);
		if(!outputCoverageDir.exists())
			outputCoverageDir.mkdirs();

		coverageExtractors = new ArrayList<>();

		if(settings.get(ConfigTags.JacocoCoverage, false)) {
			coverageExtractors.add(new JacocoCoverage(settings, outputCoveragePath));
		}
	}

	@Override
	public void getSequenceCoverage() {
		for(CodeCoverage coverageExtractor : coverageExtractors)
			coverageExtractor.getSequenceCoverage();
	}

	@Override
	public void getActionCoverage(String actionCount) {
		for(CodeCoverage coverageExtractor : coverageExtractors)
			coverageExtractor.getActionCoverage(actionCount);
	}

}
