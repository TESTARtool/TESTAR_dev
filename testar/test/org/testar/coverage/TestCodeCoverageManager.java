package org.testar.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.settings.Settings;

public class TestCodeCoverageManager {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testCoverageConstructorCreatesDirectory() {
		// Assign the outerLoopOutputDir to the tempFolder path
		OutputStructure.outerLoopOutputDir = tempFolder.getRoot().getPath();

		// Prepare the settings required by the CodeCoverageManager constructor
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.JacocoCoverage, false));
		Settings settings = new Settings(tags, new Properties());

		String coverageDirectory = OutputStructure.outerLoopOutputDir + File.separator + "coverage";
		Assert.assertFalse(new File(coverageDirectory).exists());

		new CodeCoverageManager(settings);

		Assert.assertTrue(new File(coverageDirectory).exists());
	}

}
