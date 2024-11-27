package org.testar.coverage.jacoco;

import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.settings.Settings;
import org.junit.Assert;
import org.junit.Rule;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;

public class TestJacocoReportCSV {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testGenerateJacocoReportCSV() throws Exception {
		// Mocking the CoverageBuilder behavior
		IClassCoverage mockClassCoverage = Mockito.mock(IClassCoverage.class);
		Collection<IClassCoverage> classCoverages = new ArrayList<>();
		classCoverages.add(mockClassCoverage);

		CoverageBuilder mockCoverageBuilder = Mockito.mock(CoverageBuilder.class);
		Mockito.when(mockCoverageBuilder.getClasses()).thenReturn(classCoverages);

		// Mock InstructionCounter
		ICounter mockInstructionCounter = Mockito.mock(ICounter.class);
		Mockito.when(mockInstructionCounter.getMissedCount()).thenReturn(10);
		Mockito.when(mockInstructionCounter.getCoveredCount()).thenReturn(90);
		Mockito.when(mockInstructionCounter.getCoveredRatio()).thenReturn(0.9);

		// Mock BranchCounter
		ICounter mockBranchCounter = Mockito.mock(ICounter.class);
		Mockito.when(mockBranchCounter.getMissedCount()).thenReturn(5);
		Mockito.when(mockBranchCounter.getCoveredCount()).thenReturn(50);
		Mockito.when(mockBranchCounter.getCoveredRatio()).thenReturn(0.91);

		// Mock LineCounter
		ICounter mockLineCounter = Mockito.mock(ICounter.class);
		Mockito.when(mockLineCounter.getMissedCount()).thenReturn(7);
		Mockito.when(mockLineCounter.getCoveredCount()).thenReturn(93);
		Mockito.when(mockLineCounter.getCoveredRatio()).thenReturn(0.93);

		// Mock ComplexityCounter
		ICounter mockComplexityCounter = Mockito.mock(ICounter.class);
		Mockito.when(mockComplexityCounter.getMissedCount()).thenReturn(2);
		Mockito.when(mockComplexityCounter.getCoveredCount()).thenReturn(98);
		Mockito.when(mockComplexityCounter.getCoveredRatio()).thenReturn(0.98);

		// Mock MethodCounter
		ICounter mockMethodCounter = Mockito.mock(ICounter.class);
		Mockito.when(mockMethodCounter.getMissedCount()).thenReturn(1);
		Mockito.when(mockMethodCounter.getCoveredCount()).thenReturn(99);
		Mockito.when(mockMethodCounter.getCoveredRatio()).thenReturn(0.99);

		// Mock the class coverage to return the mocked counters
		Mockito.when(mockClassCoverage.getInstructionCounter()).thenReturn(mockInstructionCounter);
		Mockito.when(mockClassCoverage.getBranchCounter()).thenReturn(mockBranchCounter);
		Mockito.when(mockClassCoverage.getLineCounter()).thenReturn(mockLineCounter);
		Mockito.when(mockClassCoverage.getComplexityCounter()).thenReturn(mockComplexityCounter);
		Mockito.when(mockClassCoverage.getMethodCounter()).thenReturn(mockMethodCounter);

		// Mock class name
		Mockito.when(mockClassCoverage.getName()).thenReturn("MockClass");

		// Assign the outerLoopOutputDir to the tempFolder path
		OutputStructure.outerLoopOutputDir = tempFolder.getRoot().getPath();
		// Fake Jacoco classes path
		String classesPath = OutputStructure.outerLoopOutputDir + File.separator + "classes_path";
		new File(classesPath).mkdir();

		// Prepare the settings required by the JacocoCoverage constructor
		List<Pair<?, ?>> tags = new ArrayList<>();
		tags.add(Pair.from(ConfigTags.JacocoCoverageClasses, classesPath));
		Settings settings = new Settings(tags, new Properties());

		// Spy on the JacocoReportCSV class to mock loadJacocoAnalysis
		JacocoReportCSV mockJacocoReportCSV = Mockito.spy(new JacocoReportCSV(settings));
		Mockito.doReturn(mockCoverageBuilder).when(mockJacocoReportCSV).loadJacocoAnalysis(any(String.class));

		// Generate the CSV report with mocked coverage data
		String outputCSVpath = tempFolder.getRoot().getPath() + File.separator + "output.csv";
		mockJacocoReportCSV.generateCSVresults("mockJacoco.exec", outputCSVpath);

		File outputCVSFile = new File(outputCSVpath);
		Assert.assertTrue(outputCVSFile.exists());
		Assert.assertTrue(fileContains("CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,INSTRUCTION_COVERED_RATIO,BRANCH_MISSED,BRANCH_COVERED,BRANCH_COVERED_RATIO,LINE_MISSED,LINE_COVERED,LINE_COVERED_RATIO,COMPLEXITY_MISSED,COMPLEXITY_COVERED,COMPLEXITY_COVERED_RATIO,METHOD_MISSED,METHOD_COVERED,METHOD_COVERED_RATIO", outputCVSFile));
		Assert.assertTrue(fileContains("MockClass,10,90,90.00,5,50,91.00,7,93,93.00,2,98,98.00,1,99,99.00", outputCVSFile));
		Assert.assertTrue(fileContains("TOTAL,10,90,90.00,5,50,90.91,7,93,93.00,2,98,98.00,1,99,99.00", outputCVSFile));
	}

	private boolean fileContains(String searchText, File file) {
		try (Scanner scanner = new Scanner(file)) {
			// Read the content of the file line by line
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				// Check if the line contains the specific text
				if (line.contains(searchText)) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

}
