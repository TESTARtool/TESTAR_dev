/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.coverage;

import java.io.File;
import java.util.ArrayList;

import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.coverage.jacoco.JacocoCoverage;

public class CodeCoverageManager implements CodeCoverage {

	private ArrayList<CodeCoverage> coverageExtractors;

	public CodeCoverageManager(Settings settings) {
		// Create a file directory to store the coverage file results
		String outputCoveragePath = OutputStructure.outerLoopOutputDir + File.separator + "coverage";
		File outputCoverageDir = new File(outputCoveragePath);
		if(!outputCoverageDir.exists() && !outputCoverageDir.mkdirs()) {
			System.err.println("ERROR: Failed to create coverage output directory: <" + outputCoverageDir.getAbsolutePath() + ">");
		}

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
