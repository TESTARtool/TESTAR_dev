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

        List<Path> files = Files.list(tempDir).toList();
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
            Assert.assertEquals(verdict.info(), "Suspicious Android logcat line(s) detected[1] AndroidRuntime: FATAL EXCEPTION: main");
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
            Assert.assertEquals(second.info(), "Suspicious Android logcat line(s) detected[1] MyTag: Exception happened");
        }
    }

    private Settings buildSettings(RuntimeControlsProtocol.Modes mode, String regex) {
        List<Pair<?, ?>> tags = new ArrayList<>();
        tags.add(Pair.from(ConfigTags.Mode, mode));
        tags.add(Pair.from(ConfigTags.LogOracleRegex, regex));
        return new Settings(tags, new Properties());
    }

}
