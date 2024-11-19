package org.testar.coverage.jacoco;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.OutputStructure;
import org.testar.coverage.CodeCoverageManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.settings.Settings;

public class TestJacocoCoverage {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testJacocoCoverageDirectory() {
		// Assign the outerLoopOutputDir to the tempFolder path
		OutputStructure.outerLoopOutputDir = tempFolder.getRoot().getPath();
		// Fake jacoco classes path
		String classesPath = OutputStructure.outerLoopOutputDir + File.separator + "classes_path";
		new File(classesPath).mkdir();

		// Prepare the settings required by the JacocoCoverage constructor
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.JacocoCoverage, true));
		tags.add(Pair.from(ConfigTags.JacocoCoverageIpAddress, "localhost"));
		tags.add(Pair.from(ConfigTags.JacocoCoveragePort, 5000));
		tags.add(Pair.from(ConfigTags.JacocoCoverageClasses, classesPath));
		Settings settings = new Settings(tags, new Properties());

		String jacocoDirectory = OutputStructure.outerLoopOutputDir 
				+ File.separator + "coverage" 
				+ File.separator + "jacoco";

		Assert.assertFalse(new File(jacocoDirectory).exists());

		new CodeCoverageManager(settings);

		Assert.assertTrue(new File(jacocoDirectory).exists());
	}

	@Test
	public void testWarningInformationJacocoSettings() {
		// Redirect the console System-output to a variable
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(errContent));	

		// Prepare the settings required by the JacocoCoverage constructor
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.JacocoCoverage, true));
		tags.add(Pair.from(ConfigTags.JacocoCoverageIpAddress, "my_ip"));
		tags.add(Pair.from(ConfigTags.JacocoCoveragePort, 9999999));
		tags.add(Pair.from(ConfigTags.JacocoCoverageClasses, "path/suts"));
		new Settings(tags, new Properties());

		Assert.assertTrue(errContent.toString().contains("Invalid JacocoCoverageIpAddress: my_ip"));
		Assert.assertTrue(errContent.toString().contains("Invalid JacocoCoveragePort: 9999999"));
		Assert.assertTrue(errContent.toString().contains("Invalid JacocoCoverageClasses: path/suts"));
	}

}
