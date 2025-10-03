package org.testar.reporting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;

public class TestReportManager {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private static StateStub state;
	private static Set<Action> derivedActions;
	private static Action selectedAction;

	private static Verdict fooVerdict = new Verdict(Verdict.Severity.FAIL, "Foo", "This is a Foo verdict description");
	private static Verdict booVerdict = new Verdict(Verdict.Severity.FAIL, "Boo", "This is a Boo verdict description");
	private static Verdict finalVerdict = fooVerdict.join(booVerdict);

	@Before
	public void setUp() throws IOException {
		tempFolder.newFolder();

		state = new StateStub();
		state.set(Tags.AbstractID, "stateAbstractID");
		state.set(Tags.ConcreteID, "stateConcreteID");
		state.set(WdTags.WebHref, "https://testar.org");
		state.set(Tags.ScreenshotPath, "imgPath");
		state.set(Tags.StateRenderTime, 1.2);
		derivedActions = new HashSet<>();

		Action typeAction = new Type("typeAction");
		typeAction.set(Tags.AbstractID, "typeActionAbstractID");
		typeAction.set(Tags.ConcreteID, "typeActionConcreteID");
		typeAction.set(Tags.Desc, "typeActionDescription");
		derivedActions.add(typeAction);
		selectedAction = typeAction;

		Action pasteAction = new Type("pasteAction");
		pasteAction.set(Tags.AbstractID, "pasteActionAbstractID");
		pasteAction.set(Tags.ConcreteID, "pasteActionConcreteID");
		pasteAction.set(Tags.Desc, "pasteActionDescription");
		derivedActions.add(pasteAction);

		Action emptyTagsAction = new Type("emptyTagsAction");
		derivedActions.add(emptyTagsAction);
	}

	@Test
	public void testReportSettingsDisabled() {
		// Prepare settings to do not create any report
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ReportInHTML, false));
		tags.add(Pair.from(ConfigTags.ReportInPlainText, false));
		Settings settings = new Settings(tags, new Properties());

		createReportManager(settings);

		// Use File.listFiles to get all files and then filter out directories
		File[] files = tempFolder.getRoot().listFiles(File::isFile);
		// Assert that there are no files
		Assert.assertTrue(files == null || files.length == 0);
	}

	@Test
	public void testHtmlReport() {
		// Prepare settings to only create an HTML report
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ReportInHTML, true));
		tags.add(Pair.from(ConfigTags.ReportInPlainText, false));
		Settings settings = new Settings(tags, new Properties());

		// Prepare a custom output directory to create the HTML report
		OutputStructure.screenshotsOutputDir = "screenshots";
		OutputStructure.htmlOutputDir = tempFolder.getRoot().getAbsolutePath();
		OutputStructure.startInnerLoopDateString = "Date";
		OutputStructure.executedSUTname = "testHtmlReport";
		OutputStructure.sequenceInnerLoopCount = 1;

		ReportManager reportManager = createReportManager(settings);

		// Verify the html report file was created with the state and actions information
		File htmlReportFile = new File(reportManager.getReportFileName().concat("_FAIL.html"));
		System.out.println("testHtmlReport: " + htmlReportFile.getPath());
		Assert.assertTrue(htmlReportFile.exists());

		// Verify state information
		Assert.assertTrue(fileContains("<title>TESTAR execution sequence report</title>", htmlReportFile));
		Assert.assertTrue(fileContains("<h1>TESTAR execution sequence report for sequence 1</h1>", htmlReportFile));
		Assert.assertTrue(fileContains("<h4>AbstractID=stateAbstractID || ConcreteID=stateConcreteID</h4>", htmlReportFile));
		Assert.assertTrue(fileContains("<h4>State Render Time: 1.2 ms</h4>", htmlReportFile));
		Assert.assertTrue(fileContains("<a href='https://testar.org' target='_blank'>https://testar.org</a>", htmlReportFile));
		// Verify derived actions information
		Assert.assertTrue(fileContains("<button type='button' class='collapsible'>Click to view the set of derived actions:</button>", htmlReportFile));
		Assert.assertTrue(fileContains("<b>typeActionDescription</b>", htmlReportFile));
		Assert.assertTrue(fileContains("<b>pasteActionDescription</b>", htmlReportFile));
		Assert.assertTrue(fileContains("<b>NoActionDescriptionAvailable</b>", htmlReportFile));
		// Verify selected action information
		Assert.assertTrue(fileContains("<h4>AbstractID=typeActionAbstractID || ConcreteID=typeActionConcreteID || typeActionDescription</h4>", htmlReportFile));
		// Verify verdict information
		Assert.assertTrue(fileContains("<h2>Test verdict for this sequence: FooBoo</h2>", htmlReportFile));
		Assert.assertTrue(fileContains("This is a Foo verdict description", htmlReportFile));
		Assert.assertTrue(fileContains("This is a Boo verdict description", htmlReportFile));

		// Verify the plain txt report was not created
		File txtReportFile = new File(reportManager.getReportFileName().concat("_FAIL.txt"));
		Assert.assertTrue(!txtReportFile.exists());
	}

	@Test
	public void testPlainReport() {
		// Prepare settings to only create an plain txt report
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ReportInHTML, false));
		tags.add(Pair.from(ConfigTags.ReportInPlainText, true));
		Settings settings = new Settings(tags, new Properties());

		// Prepare a custom output directory to create the plain txt report
		OutputStructure.screenshotsOutputDir = "screenshots";
		OutputStructure.htmlOutputDir = tempFolder.getRoot().getAbsolutePath();
		OutputStructure.startInnerLoopDateString = "Date";
		OutputStructure.executedSUTname = "testTxtReport";
		OutputStructure.sequenceInnerLoopCount = 1;

		ReportManager reportManager = createReportManager(settings);

		// Verify the html report was not created
		File htmlReportFile = new File(reportManager.getReportFileName().concat("_FAIL.html"));
		Assert.assertTrue(!htmlReportFile.exists());

		// Verify the txt report file was created with the state and actions information
		File txtReportFile = new File(reportManager.getReportFileName().concat("_FAIL.txt"));
		Assert.assertTrue(txtReportFile.exists());

		// Verify state information
		Assert.assertTrue(fileContains("TESTAR execution sequence report", txtReportFile));
		Assert.assertTrue(fileContains("TESTAR execution sequence report for sequence 1", txtReportFile));
		Assert.assertTrue(fileContains("AbstractID=stateAbstractID", txtReportFile));
		Assert.assertTrue(fileContains("ConcreteID=stateConcreteID", txtReportFile));
		// Verify derived actions information
		Assert.assertTrue(fileContains("Set of actions:", txtReportFile));
		Assert.assertTrue(fileContains("typeActionDescription", txtReportFile));
		Assert.assertTrue(fileContains("pasteActionDescription", txtReportFile));
		Assert.assertTrue(fileContains("NoActionDescriptionAvailable", txtReportFile));
		// Verify selected action information
		Assert.assertTrue(fileContains("ConcreteID=typeActionConcreteID || typeActionDescription", txtReportFile));
		// Verify verdict information
		Assert.assertTrue(fileContains("Test verdict for this sequence: FooBoo", txtReportFile));
		Assert.assertTrue(fileContains("This is a Foo verdict description", txtReportFile));
		Assert.assertTrue(fileContains("This is a Boo verdict description", txtReportFile));
	}

	@Test
	public void testHtmlReportWithoutStateScreenshot() {
		// Prepare settings to only create an HTML report
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ReportInHTML, true));
		tags.add(Pair.from(ConfigTags.ReportInPlainText, false));
		Settings settings = new Settings(tags, new Properties());

		// Create a state without IDs, without web URL, and without ScreenshotPath
		state = new StateStub();

		// Prepare a custom output directory to create the HTML report
		OutputStructure.screenshotsOutputDir = "screenshots";
		OutputStructure.htmlOutputDir = tempFolder.getRoot().getAbsolutePath();
		OutputStructure.startInnerLoopDateString = "Date";
		OutputStructure.executedSUTname = "testHtmlReportWithoutScreenshot";
		OutputStructure.sequenceInnerLoopCount = 1;

		ReportManager reportManager = createReportManager(settings);

		// Verify the html report file was created with the state and actions information
		File htmlReportFile = new File(reportManager.getReportFileName().concat("_FAIL.html"));
		System.out.println("testHtmlReportWithoutScreenshot: " + htmlReportFile.getPath());
		Assert.assertTrue(htmlReportFile.exists());

		// Verify state information
		Assert.assertTrue(fileContains("<title>TESTAR execution sequence report</title>", htmlReportFile));
		Assert.assertTrue(fileContains("<h1>TESTAR execution sequence report for sequence 1</h1>", htmlReportFile));
		Assert.assertTrue(fileContains("<h4>AbstractID=NoAbstractIdAvailable || ConcreteID=NoConcreteIdAvailable</h4>", htmlReportFile));
		Assert.assertFalse(fileContains("<a href='https://testar.org' target='_blank'>https://testar.org</a>", htmlReportFile));
		Assert.assertTrue(fileContains("<img src=\"NoScreenshotPathAvailable", htmlReportFile));
	}

	private ReportManager createReportManager(Settings settings) {
		ReportManager reportManager = new ReportManager(false, settings);
		reportManager.addState(state);
		reportManager.addActions(derivedActions);
		reportManager.addSelectedAction(state, selectedAction);
		reportManager.addTestVerdict(finalVerdict);
		reportManager.finishReport();
		return reportManager;
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
