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
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

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

            List<Verdict> firstInvokationVerdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, firstInvokationVerdicts.size());
            Verdict firstVerdict = firstInvokationVerdicts.get(0);
            Assert.assertEquals(Verdict.Severity.OK.getValue(), firstVerdict.severity(), 0.0);

            List<Verdict> secondInvokationVerdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, secondInvokationVerdicts.size());
            Verdict secondVerdict = secondInvokationVerdicts.get(0);
            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), secondVerdict.severity(), 0.0);
            Assert.assertEquals(secondVerdict.info(), "Suspicious Android logcat line(s) detected MyTag: Exception happened");
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
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

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

        String line1 = "02-09 08:59:33.844 17550 17575 E ViewRootImpl: Exception @1:207875, unable to find 3421 viewState";
        String line2 = "02-09 08:59:33.845 17550 17575 E ViewRootImpl: Exception @1:204868, unable to find 9008 viewState";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line1 + "\n" + line2);

            androidLogcatOracle.initialize();
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity(), 0.0);
            String expected = "Suspicious Android logcat line(s) detected "
                    + "ViewRootImpl: Exception @<num>:<num>, unable to find <num> viewState";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    @Test
    public void generateModeVerdict_KeepsHttpStatusCodes() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*Exception.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String line1 = "02-09 08:59:33.844 17550 17575 E ViewRootImpl: Exception, http status 404";
        String line2 = "02-09 08:59:33.845 17550 17575 E ViewRootImpl: Exception, http status 503";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line1 + "\n" + line2);

            androidLogcatOracle.initialize();
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

            Assert.assertEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity(), 0.0);
            String expected = "Suspicious Android logcat line(s) detected "
                    + "ViewRootImpl: Exception, http status 404"
                    + " | ViewRootImpl: Exception, http status 503";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    @Test
    public void generateModeVerdict_NormalizesDynamicAndroidPaths() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*Exception.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String line = "07-06 11:48:55.095 29813 29884 E BitmapFactory: Unable to decode file: java.io.FileNotFoundException: "
                + "/data/user/0/com.example.app/cache/sentry/83a56134754ad7e27d5f94754e5a842865257057/"
                + "replay_053d15c452f042f9a7049bb22a6860ca/1783338523682.jpg: open failed: ENOENT (No such file or directory)";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line);

            androidLogcatOracle.initialize();
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

            String expected = "Suspicious Android logcat line(s) detected "
                    + "BitmapFactory: Unable to decode file: java.io.FileNotFoundException: "
                    + "/data/user/<num>/<package>/cache/<path>/<id>/replay_<id>/<file>.jpg: "
                    + "open failed: ENOENT (No such file or directory)";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    @Test
    public void generateModeVerdict_DeduplicatesDifferentDynamicAndroidPaths() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*Exception.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String line1 = "07-06 11:48:55.095 29813 29884 E BitmapFactory: Unable to decode file: java.io.FileNotFoundException: "
                + "/data/user/0/com.example.app/cache/sentry/83a56134754ad7e27d5f94754e5a842865257057/"
                + "replay_053d15c452f042f9a7049bb22a6860ca/1783338523682.jpg: open failed: ENOENT (No such file or directory)";
        String line2 = "07-06 11:48:56.095 29813 29884 E BitmapFactory: Unable to decode file: java.io.FileNotFoundException: "
                + "/data/user/0/com.example.app/cache/sentry/9f3d44b21234ad7e27d5f94754e5a842812345678/"
                + "replay_77aa22bb33cc44dd55ee66ff77889900/1888888888888.jpg: open failed: ENOENT (No such file or directory)";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line1 + "\n" + line2);

            androidLogcatOracle.initialize();
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

            String expected = "Suspicious Android logcat line(s) detected "
                    + "BitmapFactory: Unable to decode file: java.io.FileNotFoundException: "
                    + "/data/user/<num>/<package>/cache/<path>/<id>/replay_<id>/<file>.jpg: "
                    + "open failed: ENOENT (No such file or directory)";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    @Test
    public void generateModeVerdict_NormalizesDifferentAndroidPrivateStorageRoots() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*error.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String line1 = "07-06 11:48:55.095 29813 29884 E SQLite: error opening db /data/data/com.example.app/databases/550e8400-e29b-41d4-a716-446655440000.db";
        String line2 = "07-06 11:48:56.095 29813 29884 E SQLite: error opening db /data/data/com.other.app/databases/123e4567-e89b-12d3-a456-426614174000.db";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line1 + "\n" + line2);

            androidLogcatOracle.initialize();
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

            String expected = "Suspicious Android logcat line(s) detected "
                    + "SQLite: error opening db /data/data/<package>/databases/<file>.db";
            Assert.assertEquals(expected, verdict.info());
        }
    }

    @Test
    public void generateModeVerdict_NormalizesJavaObjectIdentitySuffixes() {
        OutputStructure.logsOutputDir = Path.of("target").toString();
        OutputStructure.startInnerLoopDateString = "YYYY-MM-DD_hh-mm-ss";
        OutputStructure.executedSUTname = "test-sut";

        Settings settings = buildSettings(RuntimeControlsProtocol.Modes.Generate, "(?i)(.*Integrations.*)");
        AndroidLogcatOracle androidLogcatOracle = new AndroidLogcatOracle(settings);
        State state = Mockito.mock(State.class);

        String line1 = "07-06 11:48:55.095 29813 29884 E RNSentry: Native Integrations "
                + "'[io.sentry.UncaughtExceptionHandlerIntegration@6d8322a, "
                + "io.sentry.android.replay.ReplayIntegration@5c9d785]'";
        String line2 = "07-06 11:48:56.095 29813 29884 E RNSentry: Native Integrations "
                + "'[io.sentry.UncaughtExceptionHandlerIntegration@123abcd, "
                + "io.sentry.android.replay.ReplayIntegration@8de45f6]'";

        try (MockedStatic<AndroidAppiumFramework> mocked = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            mocked.when(AndroidAppiumFramework::getAppPackageFromCapabilitiesOrCurrent).thenReturn("org.testar.app");
            mocked.when(() -> AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app"))
                    .thenReturn(line1 + "\n" + line2);

            androidLogcatOracle.initialize();
            List<Verdict> verdicts = androidLogcatOracle.getVerdicts(state);
            Assert.assertEquals(1, verdicts.size());
            Verdict verdict = verdicts.get(0);

            String expected = "Suspicious Android logcat line(s) detected "
                    + "RNSentry: Native Integrations '[io.sentry.UncaughtExceptionHandlerIntegration@<id>, "
                    + "io.sentry.android.replay.ReplayIntegration@<id>]'";
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
