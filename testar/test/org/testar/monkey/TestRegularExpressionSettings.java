package org.testar.monkey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.alayer.Tag;
import com.github.stefanbirkner.systemlambda.SystemLambda;

public class TestRegularExpressionSettings {

	private static String test_protocol = "webdriver_generic";
	private static File temp_settings = new File(Main.settingsDir);
	private static File temp_protocol = new File(Main.settingsDir + File.separator + test_protocol);
	private static List<File> initialOutputFiles = new ArrayList<>();

	@BeforeClass
	public static void initialDirectory() {
		// Create the protocol temp directory
		File protocolDir = new File(Main.testarDir + "resources" + File.separator + "settings" + File.separator + test_protocol);
		try {
			FileUtils.copyDirectory(protocolDir, temp_protocol);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File output = new File(Main.outputDir);
		if(output.exists()) {
			for (File seq : output.listFiles()) {
				initialOutputFiles.add(seq);
			}
		}
	}

	@AfterClass
	public static void cleanUp() {
		// Clean the settings temp directory
		try {
			FileUtils.deleteDirectory(temp_settings);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Delete all the sequence output directories that were created in these Unit tests
		File output = new File(Main.outputDir);
		if(output.exists()) {
			for (File seq : output.listFiles()) {
				if(!initialOutputFiles.contains(seq)) {
					try {
						FileUtils.deleteDirectory(seq);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if(initialOutputFiles.isEmpty()) {
				try {
					FileUtils.deleteDirectory(output);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String[] args = {"testar", "sse="+test_protocol, "ShowVisualSettingsDialogOnStartup=false", "AlwaysCompile=true", "Mode=Generate", "Sequences=0", "SequenceLength=0", "ClickFilter=.*.*"};
	private List<Tag<String>> regularExpressionTags = Arrays.asList(
			ConfigTags.ProcessesToKillDuringTest,
			ConfigTags.ClickFilter,
			ConfigTags.SuspiciousTags,
			ConfigTags.SuspiciousProcessOutput,
			ConfigTags.ProcessLogs,
			ConfigTags.LogOracleRegex,
			ConfigTags.WebConsoleErrorPattern,
			ConfigTags.WebConsoleWarningPattern
			);

	@Test
	public void validRegularExpressionSetting() {
		for(Tag<String> tag : regularExpressionTags) {
			args[args.length - 1] = tag + "=.*[aA].*|.*bc.*";
			String exceptionMessage = "";
			try {
				int status = SystemLambda.catchSystemExit(() -> {
					// We expect a System.exit(0) from TESTAR Main.stopTestar
					Main.main(args);
				});
				// Verify this is system exit 0
				Assert.isTrue(status == 0);
			} catch (Exception e) {
				exceptionMessage = e.getMessage();
			}
			// Because the pattern is correct, we should not get any exception messages
			System.out.println(exceptionMessage);
			Assert.isTrue(exceptionMessage.isEmpty());
		}
	}

	@Test
	public void invalidRegularExpressionSetting() {
		for(Tag<String> tag : regularExpressionTags) {
			args[args.length - 1] = tag + "=.**[aA].*|.*bc.*";
			String exceptionMessage = ""; 
			try {
				// When the regular expression is not valid, TESTAR throws an exception and does not execute System.exit(0)
				Main.main(args);
			} catch (IllegalStateException | IOException e) {
				exceptionMessage = e.getMessage();
			}
			// Because the pattern is NOT correct, we should get an exception message
			System.out.println(exceptionMessage);
			Assert.hasText(exceptionMessage);
			Assert.isTrue(exceptionMessage.contains(tag + " is not a valid regular expression"));
		}
	}

}
