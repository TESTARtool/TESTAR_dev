/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.reporting;

import java.awt.GraphicsEnvironment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.io.PrintStream;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.settings.Settings;
import org.testar.core.action.Action;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.serialisation.ScreenshotSerialiser;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.RuntimePathsUtil;
import org.testar.core.verdict.Verdict;
import org.testar.plugin.screenshot.ScreenshotProviderFactory;
import org.testar.reporting.ReportManager;
import org.testar.reporting.Reporting;

/**
 * Manages per-session screenshots and report generation for CLI sessions.
 */
public final class SessionReportingManager {

    private Reporting reporting;
    private final AtomicBoolean finished;

    private SessionReportingManager(Reporting reporting) {
        this.reporting = reporting;
        this.finished = new AtomicBoolean(false);
    }

    public static SessionReportingManager create() {
        return new SessionReportingManager(null);
    }

    public static SessionReportingManager start(Settings settings, String target) {
        configureOutputStructure(settings, target);
        startScreenshotSerialiser();
        return new SessionReportingManager(new ReportManager(settings));
    }

    public void bindReporting(Reporting reporting) {
        this.reporting = reporting;
    }

    public void prepareGeneratedSequenceOutput(Settings settings) {
        String sequenceCountDir = "_sequence_" + OutputStructure.sequenceInnerLoopCount;
        String generatedSequence = OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir;
        String logFileName = OutputStructure.logsOutputDir
                + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir + ".log";
        String screenshotsDirectory = OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir;

        try {
            LogSerialiser.start(
                    new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(logFileName), true))),
                    settings.get(ConfigTags.LogLevel)
            );
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }

        ScreenshotSerialiser.start(OutputStructure.screenshotsOutputDir, screenshotsDirectory);
    }

    public void prepareState(State state) {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        AWTCanvas screenshot = ScreenshotProviderFactory.current().getStateshotBinary(state);
        if (screenshot == null) {
            return;
        }

        String screenshotPath = ScreenshotSerialiser.saveStateshot(
                state.get(Tags.ConcreteID, "NoConcreteIdAvailable"),
                screenshot
        );
        state.set(Tags.ScreenshotPath, screenshotPath);
    }

    public void addState(State state) {
        if (reporting != null) {
            reporting.addState(state);
        }
    }

    public void addActions(Set<Action> actions) {
        if (reporting != null) {
            reporting.addActions(actions);
        }
    }

    public void addSelectedAction(State state, Action action) {
        // Add the selected action information to the reports
        ScreenshotProviderFactory.current().getActionshot(state, action);
        if (reporting != null) {
            reporting.addSelectedAction(state, action);
        }

        // Add the selected action information to the trace logs
        String[] actionRepresentation = Action.getActionRepresentation(state, action, "\t");

        LogSerialiser.log(
                String.format(
                        "Selected Action: %s\n%s",
                        "\n @Action ConcreteID = " + action.get(Tags.ConcreteID, "ConcreteID not available")
                                + " AbstractID = " + action.get(Tags.AbstractID, "AbstractID not available") + "\n"
                                + " @State ConcreteID = " + state.get(Tags.ConcreteID, "ConcreteID not available")
                                + " AbstractID = " + state.get(Tags.AbstractID, "AbstractID not available") + "\n",
                        actionRepresentation[0]
                ) + "\n",
                LogSerialiser.LogLevel.Info
        );
    }

    public void addTestVerdicts(List<Verdict> verdicts) {
        if (reporting != null) {
            reporting.addTestVerdicts(verdicts);
        }
    }

    public void finishReport() {
        if (reporting != null) {
            reporting.finishReport();
            reporting = null;
        }
    }

    public void endSequenceOutput() {
        ScreenshotSerialiser.exit();
        LogSerialiser.flush();
        LogSerialiser.finish();
        LogSerialiser.exit();
    }

    public void abortSequenceOutput(Exception exception) {
        ScreenshotSerialiser.finish();
        ScreenshotSerialiser.exit();
        LogSerialiser.log(
                "Exception <" + exception.getMessage() + "> has been caught\n",
                LogSerialiser.LogLevel.Critical
        );
        LogSerialiser.flush();
        LogSerialiser.finish();
        LogSerialiser.exit();
    }

    public void finish() {
        finish(Collections.singletonList(Verdict.OK));
    }

    public void finish(List<Verdict> verdicts) {
        if (!finished.compareAndSet(false, true)) {
            return;
        }

        addTestVerdicts(verdicts);
        finishReport();
        ScreenshotSerialiser.exit();
    }

    private static void configureOutputStructure(Settings settings, String target) {
        Path outputPath = RuntimePathsUtil.resolveAgainstTestarHome(settings.get(ConfigTags.OutputDir, "./output"));
        TestarDirectories.setOutputDir(outputPath.toString());
        OutputStructure.calculateOuterLoopDateString();
        OutputStructure.sequenceInnerLoopCount = 1;
        OutputStructure.calculateInnerLoopDateString();
        OutputStructure.createOutputSUTname(settings);

        if (OutputStructure.executedSUTname == null || OutputStructure.executedSUTname.isEmpty()) {
            OutputStructure.executedSUTname = sanitizeTarget(target);
        }

        OutputStructure.createOutputFolders();
    }

    private static void startScreenshotSerialiser() {
        String screenshotsDirectory = OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + "_sequence_" + OutputStructure.sequenceInnerLoopCount;
        ScreenshotSerialiser.start(OutputStructure.screenshotsOutputDir, screenshotsDirectory);
    }

    private static String sanitizeTarget(String target) {
        String sanitizedTarget = target == null ? "" : target.trim();
        if (sanitizedTarget.isEmpty()) {
            return "unknown";
        }

        int lastSeparatorIndex = Math.max(
                sanitizedTarget.lastIndexOf('/'),
                sanitizedTarget.lastIndexOf(File.separatorChar)
        );
        if (lastSeparatorIndex >= 0 && lastSeparatorIndex < sanitizedTarget.length() - 1) {
            sanitizedTarget = sanitizedTarget.substring(lastSeparatorIndex + 1);
        }

        sanitizedTarget = sanitizedTarget.replaceAll("[^A-Za-z0-9]", "_");
        if (sanitizedTarget.isEmpty()) {
            return "unknown";
        }
        return sanitizedTarget;
    }
}
