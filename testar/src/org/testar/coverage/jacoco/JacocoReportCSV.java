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
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

public class JacocoReportCSV {

	private String jacocoClassesPath;

	public JacocoReportCSV(Settings settings) {
		jacocoClassesPath = settings.get(ConfigTags.JacocoCoverageClasses);
	}

	public void generateCSVresults(String jacocoExecFile, String outputCSV) {
		try {
			// Load the jacoco.exec file
			ExecFileLoader loader = new ExecFileLoader();
			loader.load(new File(jacocoExecFile));

			// Analyze the coverage data
			CoverageBuilder coverageBuilder = new CoverageBuilder();
			Analyzer analyzer = new Analyzer(loader.getExecutionDataStore(), coverageBuilder);

			// Analyze all classes
			analyzeAllClasses(jacocoClassesPath, analyzer);

			// Initialize total counters
			int totalInstructionMissed = 0;
			int totalInstructionCovered = 0;
			int totalBranchMissed = 0;
			int totalBranchCovered = 0;
			int totalLineMissed = 0;
			int totalLineCovered = 0;
			int totalComplexityMissed = 0;
			int totalComplexityCovered = 0;
			int totalMethodMissed = 0;
			int totalMethodCovered = 0;

			// Write to CSV
			try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputCSV), CSVFormat.DEFAULT)) {
				printer.printRecord("CLASS", 
						"INSTRUCTION_MISSED", "INSTRUCTION_COVERED", "INSTRUCTION_COVERED_RATIO",
						"BRANCH_MISSED", "BRANCH_COVERED", "BRANCH_COVERED_RATIO",
						"LINE_MISSED", "LINE_COVERED", "LINE_COVERED_RATIO",
						"COMPLEXITY_MISSED", "COMPLEXITY_COVERED", "COMPLEXITY_COVERED_RATIO",
						"METHOD_MISSED", "METHOD_COVERED",  "METHOD_COVERED_RATIO");
				for (IClassCoverage cc : coverageBuilder.getClasses()) {
					printer.printRecord(
							cc.getName(), // Class name

							cc.getInstructionCounter().getMissedCount(), //  INSTRUCTION_MISSED
							cc.getInstructionCounter().getCoveredCount(), // INSTRUCTION_COVERED
							formatPercentage(cc.getInstructionCounter().getCoveredRatio()), // INSTRUCTION_COVERED_RATIO

							cc.getBranchCounter().getMissedCount(), // BRANCH_MISSED
							cc.getBranchCounter().getCoveredCount(), // BRANCH_COVERED
							formatPercentage(cc.getBranchCounter().getCoveredRatio()), // BRANCH_COVERED_RATIO

							cc.getLineCounter().getMissedCount(), // LINE_MISSED
							cc.getLineCounter().getCoveredCount(), // LINE_COVERED
							formatPercentage(cc.getLineCounter().getCoveredRatio()), // LINE_COVERED_RATIO

							cc.getComplexityCounter().getMissedCount(), // COMPLEXITY_MISSED
							cc.getComplexityCounter().getCoveredCount(), // COMPLEXITY_COVERED
							formatPercentage(cc.getComplexityCounter().getCoveredRatio()), // COMPLEXITY_COVERED_RATIO

							cc.getMethodCounter().getMissedCount(), // METHOD_MISSED
							cc.getMethodCounter().getCoveredCount(), // METHOD_COVERED
							formatPercentage(cc.getMethodCounter().getCoveredRatio()) // METHOD_COVERED_RATIO
							);

					// Sum up totals
					totalInstructionMissed += cc.getInstructionCounter().getMissedCount();
					totalInstructionCovered += cc.getInstructionCounter().getCoveredCount();
					totalBranchMissed += cc.getBranchCounter().getMissedCount();
					totalBranchCovered += cc.getBranchCounter().getCoveredCount();
					totalLineMissed += cc.getLineCounter().getMissedCount();
					totalLineCovered += cc.getLineCounter().getCoveredCount();
					totalComplexityMissed += cc.getComplexityCounter().getMissedCount();
					totalComplexityCovered += cc.getComplexityCounter().getCoveredCount();
					totalMethodMissed += cc.getMethodCounter().getMissedCount();
					totalMethodCovered += cc.getMethodCounter().getCoveredCount();
				}

				// Print totals
				printer.printRecord("TOTAL",
						totalInstructionMissed, totalInstructionCovered,
						formatPercentage((double)totalInstructionCovered / (double)(totalInstructionMissed + totalInstructionCovered)),

						totalBranchMissed, totalBranchCovered,
						formatPercentage((double)totalBranchCovered / (double)(totalBranchMissed + totalBranchCovered)),

						totalLineMissed, totalLineCovered,
						formatPercentage((double)totalLineCovered / (double)(totalLineMissed + totalLineCovered)),

						totalComplexityMissed, totalComplexityCovered,
						formatPercentage((double)totalComplexityCovered / (double)(totalComplexityMissed + totalComplexityCovered)),

						totalMethodMissed, totalMethodCovered, 
						formatPercentage((double)totalMethodCovered / (double)(totalMethodMissed + totalMethodCovered))
						);

			}

			System.out.println("JacocoReportCSV generated the CSV report: " + outputCSV);

		} catch (IOException e) {
			System.err.println("JacocoReportCSV was not able to create the CSV report " + jacocoClassesPath + " with the exec file " + jacocoExecFile);
			e.printStackTrace();
		}
	}

	private void analyzeAllClasses(String classesDirPath, Analyzer analyzer) {
		File classesDir = new File(classesDirPath);
		if (classesDir.isDirectory()) {
			File[] files = classesDir.listFiles();
			if (files != null) {
				for (File file : files) {
					try {
						if (file.isDirectory()) {
							analyzeAllClasses(file.getPath(), analyzer);
						} else if (file.getName().endsWith(".class") || file.getName().endsWith(".jar")) {
							if (file.getName().endsWith(".jar")) {
								try (ZipFile zipFile = new ZipFile(file)) {
									zipFile.stream()
									.filter(entry -> entry.getName().endsWith(".class"))
									.forEach(entry -> analyzeZipEntry(zipFile, entry, analyzer));
								}
							} else {
								analyzer.analyzeAll(file);
							}
						}
					} catch (Exception e) {
						System.err.println("Error analyzing file " + file.getPath() + ": " + e.getMessage());
					}
				}
			}
		}
	}

	private void analyzeZipEntry(ZipFile zipFile, ZipEntry entry, Analyzer analyzer) {
		try {
			analyzer.analyzeClass(zipFile.getInputStream(entry), entry.getName());
		} catch (IOException e) {
			System.err.println("Error analyzing zip entry " + entry.getName() + ": " + e.getMessage());
		}
	}

	private String formatPercentage(double value) {
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.getDefault());
		symbols.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.00", symbols);
		return df.format(value * 100.0) + "%";
	}

}
