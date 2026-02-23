package org.testar.oracles.log;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.settings.Settings;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestAndroidLogcatOracle {

    @Test
    public void spyMode_doesNothing() throws Exception {
        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Spy, "(?i)(.*exception.*|.*error.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            androidLogcatOracle.initialize();
            mocked.verify(AndroidAppiumFramework::clearLogcat, Mockito.times(0));
        }
    }

    @Test
    public void generateMode_CreatesFileAndClearsLogcat() throws Exception {
        Path tempDir = Files.createTempDirectory("android-logcat");
        OutputStructure.logsOutputDir = tempDir.toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*exception.*|.*error.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            androidLogcatOracle.initialize();
            mocked.verify(AndroidAppiumFramework::clearLogcat, Mockito.times(1));
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(tempDir)) {
            files = stream.sorted().collect(Collectors.toList());
        }

        Assert.assertEquals(1, files.size());
        String content = Files.readString(files.get(0), StandardCharsets.UTF_8);
        Assert.assertTrue(content.contains("TESTAR Android logcat"));
    }

    @Test
    public void generateModeVerdict_DetectsRegexAndReturnsSuspiciousLog() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*exception.*|.*error.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(
                        "02-09 08:59:33.844 17550 17575 E AndroidRuntime: FATAL EXCEPTION: main\n" +
                        "02-09 08:59:33.845 17550 17575 I Tag: just info"
                    );

            androidLogcatOracle.initialize();
            Verdict verdict = androidLogcatOracle.getVerdict(state);

            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity(), 0.0);
            Assert.assertEquals(verdict.info(), "Suspicious Android logcat line(s) detected AndroidRuntime: FATAL EXCEPTION: main");
            mocked.verify(AndroidAppiumFramework::clearLogcat, Mockito.times(2));
        }
    }

    @Test
    public void generateModeVerdict_ProcessesOnlyNewLinesAcrossCalls() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*exception.*|.*error.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn("02-09 08:00:00.000 100 100 I MyTag: first line")
                    .thenReturn(
                        "02-09 08:00:00.000 100 100 I MyTag: first line\n" +
                        "02-09 08:00:01.000 100 100 E MyTag: Exception happened"
                    );

            androidLogcatOracle.initialize();

            Verdict first = androidLogcatOracle.getVerdict(state);
            Assert.assertEquals(Verdict.Severity.OK.getValue(), first.severity(), 0.0);

            Verdict second = androidLogcatOracle.getVerdict(state);
            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), second.severity(), 0.0);
            Assert.assertEquals(second.info(), "Suspicious Android logcat line(s) detected MyTag: Exception happened");
        }
    }

    @Test
    public void generateModeVerdict_DeduplicatesAndOrdersMatches() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*exception.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String lineA = "02-09 08:59:33.844 17550 17575 E ViewRootImpl: Accessibility content change on non-UI thread. Future Android versions will throw an exception.";
        String lineB = "02-09 08:59:33.845 17550 17575 E ViewRootImpl: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(lineB + "\n" + lineA + "\n" + lineB);

            androidLogcatOracle.initialize();
            Verdict verdict = androidLogcatOracle.getVerdict(state);

            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity(), 0.0);
            String expected = "Suspicious Android logcat line(s) detected "
                    + "ViewRootImpl: Accessibility content change on non-UI thread. Future Android versions will throw an exception."
                    + " | ViewRootImpl: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    @Test
    public void generateModeVerdict_DeduplicatesNumbersInMatches() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*Exception.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String line1 = "02-09 08:59:33.844 17550 17575 E ViewRootImpl: Exxception @1:207875, unable to find 3421 viewState";
        String line2 = "02-09 08:59:33.845 17550 17575 E ViewRootImpl: Exception @1:204868, unable to find 9008 viewState";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line1 + "\n" + line2);

            androidLogcatOracle.initialize();
            Verdict verdict = androidLogcatOracle.getVerdict(state);

            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity(), 0.0);
            String expected = "Suspicious Android logcat line(s) detected "
                    + "ViewRootImpl: Exception @<num>:<num>, unable to find <num> viewState";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    private Settings buildSettings(RuntimeControlsProtocol.Modes mode, String regex) {
        List<Pair<?, ?>> tags = new ArrayList<>();
        tags.add(Pair.from(ConfigTags.Mode, mode));
        tags.add(Pair.from(ConfigTags.LogOracleRegex, regex));
        return new Settings(tags, new Properties());
    }

}
