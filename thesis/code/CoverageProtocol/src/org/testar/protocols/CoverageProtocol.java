package org.testar.protocols;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.fruit.monkey.ConfigTags.OutputDir;
import static org.fruit.monkey.ConfigTags.SequenceLength;
import static org.fruit.monkey.ConfigTags.Sequences;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testar.coverage.CoverageChart;
import org.testar.coverage.CoverageCounter;
import org.testar.coverage.CoverageData;
import org.testar.coverage.CoverageReader;
import org.testar.coverage.RemoteExecutor;

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;

public abstract class CoverageProtocol extends ClickFilterLayerProtocol {
    private static Logger logger = LoggerFactory.getLogger(CoverageProtocol.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private int defaultPort = 22;
    private Map<Integer, CoverageData> testCoverageData;
    private CoverageReader coverageReader;

    private int numberOfActions;
    private Timer timer;
    private int minute = -1;

    // Test settings
    private String testarSetup;
    private String testRun;

    private TimerTask coverageTimerTask;

    /**
     * Called once during the life time of TESTAR This method can be used to perform
     * initial setup work
     *
     * @param settings the current TESTAR settings as specified by the user.
     */
    protected void initialize(Settings settings) {
        super.initialize(settings);

        testarSetup = settings.get(ConfigTags.TestarSetup);
        testRun = settings.get(ConfigTags.TestarTestRun);

        logger.info("Start sequence: {}", sequenceCount());


        if (mode() == Modes.Generate) {
            // Collect coverage data
            testCoverageData = new HashMap<>();

            // Reset coverage data
            int coveragePort = defaultPort;
            try {
                coveragePort = Integer.valueOf(settings.get(ConfigTags.CoverageServerPort));
            } catch (NumberFormatException e) {
                logger.error("Failed to determine coverage server port -> use default 22: " + e.getMessage());
            }
            coverageReader = new CoverageReader(settings.get(ConfigTags.CoverageServerUsername),
                    settings.get(ConfigTags.CoverageServerHostname), coveragePort,
                    settings.get(ConfigTags.CoverageServerKeyFile));
            try {
                coverageReader.resetCoverageData(settings.get(ConfigTags.CoverageCommandReset));
            } catch (IOException e) {
                logger.error("Failed to reset coverage data: " + e.getMessage(), e);
            }
            // Restart SUT
            RemoteExecutor remoteExecutor = null;
            try {
                int sutPort = defaultPort;
                try {
                    sutPort = Integer.valueOf(settings.get(ConfigTags.SutServerPort));
                } catch (NumberFormatException e) {
                    logger.error("Failed to determine sut server port -> use default 22: " + e.getMessage());
                }
                remoteExecutor = new RemoteExecutor(settings.get(ConfigTags.SutServerHostname), sutPort,
                        settings.get(ConfigTags.SutServerUsername), null, settings.get(ConfigTags.SutServerKeyFile));
                remoteExecutor.connectWithKeys();
                String result = remoteExecutor.execCommand(settings.get(ConfigTags.SutServerRestart));
                logger.debug("Command result:\n{}", result);

            } catch (IOException e) {
                logger.error("Failed to restart SUT on " + settings.get(ConfigTags.SutServerHostname) + ": "
                        + e.getMessage(), e);
            } finally {
                if (remoteExecutor != null) {
                    remoteExecutor.disconnect();
                }
            }

            // Schedule every minute retrieval of coverage data
            String startDate = sdf.format(getStartDate());
            settings.get(Sequences);
            Path coverageDataFile = Paths
                    .get(settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_coverage" + "_s"
                            + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + startDate + ".txt");

            numberOfActions = 0;
            timer = new Timer();
            coverageTimerTask = new TimerTask() {
                public void run() {
                    minute++;
                    if (mode() == Modes.Quit || !moreSequences()) {
                        logger.info("TIMER: Finish protocol timer.");
                        timer.cancel();
                        return;
                    }
                    logger.info("TIMER: Retrieve performance data");
                    // Retrieve performance data
                    String cpu = "";
                    String memory = "";
                    RemoteExecutor remoteExecutor = null;
                    try {
                        int sutPort = defaultPort;
                        try {
                            sutPort = Integer.valueOf(settings.get(ConfigTags.SutServerPort));
                        } catch (NumberFormatException e) {
                            logger.error("Failed to determine sut server port -> use default 22: " + e.getMessage());
                        }
                        remoteExecutor = new RemoteExecutor(settings.get(ConfigTags.SutServerHostname), sutPort,
                                settings.get(ConfigTags.SutServerUsername), null, settings.get(ConfigTags.SutServerKeyFile));
                        remoteExecutor.connectWithKeys();
                        String result = remoteExecutor.execCommand(settings.get(ConfigTags.SutServerPerformance));
                        logger.debug("Command result:{}", result);
                        String[] performanceFields = result.split(",");
                        if (performanceFields.length == 2) {
                            cpu = performanceFields[0];
                            memory = performanceFields[1].replaceAll("(\r|\n)", "");
                        } else {
                            logger.error("Failed to retrieve performance data. Unknown result.");
                        }
                            

                    } catch (IOException e) {
                        logger.error("Failed to restart SUT on " + settings.get(ConfigTags.SutServerHostname) + ": "
                                + e.getMessage(), e);
                    } finally {
                        if (remoteExecutor != null) {
                            remoteExecutor.disconnect();
                        }
                    }
                    logger.info("TIMER: Retrieve coverage data");
                    try (OutputStream out = new BufferedOutputStream(
                            Files.newOutputStream(coverageDataFile, CREATE, APPEND))) {
                        // Dump the coverage data
                        coverageReader.dumpCoverageData(settings.get(ConfigTags.CoverageCommandDump));
                        String xmlFilename = String.format("/extra/tests/%s/%s/report_%s_%03d.xml", testarSetup,
                                testRun, testRun, minute);
                        String reportCommand = settings.get(ConfigTags.CoverageCommandXmlReport) + xmlFilename;
                        // Retrieve the coverage data
                        CoverageData coverageData = coverageReader.retrieveCoverageData(sequenceCount(), numberOfActions,
                                reportCommand, xmlFilename);
                        // Store the coverage data - internal memory and on disk
                        if (coverageData != null) {
                            testCoverageData.put(minute, coverageData);
                            // Get branch and line coverage, the other ones are also available here and
                            // could also be used if needed
                            int branchCoverage = coverageData.get(CoverageCounter.BRANCH).getCoveragePercentage();
                            int lineCoverage = coverageData.get(CoverageCounter.LINE).getCoveragePercentage();
                            String data = String.format("%d,%d,%d,%d,%d,%s,%s\n", minute, sequenceCount(), numberOfActions,
                                    branchCoverage, lineCoverage, cpu, memory);
                            // Write data
                            out.write(data.getBytes());
                        } else {
                            String data = String.format("%d,%d,%d,,%s,%s\n", minute, sequenceCount(), numberOfActions, cpu, memory);
                            // Write data
                            out.write(data.getBytes());
                        }
                    } catch (IOException e) {
                        logger.error("Failed to handle coverage data: " + e.getMessage(), e);
                        // Can't do anything here
                    }

                    logger.info("TIMER: log memory usage");
                    logMemoryUsage();
                }
            };
            timer.scheduleAtFixedRate(coverageTimerTask, 0, 60 * 1000);
        }
    }

    @Override
    public void closeTestSession() {
        logger.info("Close test session - {} - {}", testarSetup, testRun);
        super.closeTestSession();

        if (mode() == Modes.Generate) {
            logger.info("Cancel timer - {} - {}", testarSetup, testRun);
            coverageTimerTask.cancel();
            timer.cancel();
            // Create an HTML report of the coverage data for analysing purposes
            String htmlDir = String.format("/extra/tests/%s/%s/report_html_%s", testarSetup, testRun, testRun);
            String reportCommand = settings.get(ConfigTags.CoverageCommandHtmlReport) + htmlDir;
            try {
                coverageReader.executeRemoteCommand(reportCommand);
            } catch (IOException e) {
                logger.error("Failed to create HTML report: " + e.getMessage(), e);
            }
            // Create a chart of the data
            CoverageChart coverageChart = new CoverageChart();
            // Create chart title
            String title = testarSetup + " " + testRun;
            // Create chart subtitle
            long maxTime = Math.round(settings.get(ConfigTags.MaxTime)); // in seconds
            String hours = (maxTime/3600 > 0 ? String.format("%d", maxTime/3600) + " hours": "");
            String mins = (maxTime%3600 > 0 ? (!hours.isEmpty() ? " and ": "") + String.format("%d", (maxTime%3600)/60) + " minutes": "");
            String subTitle = settings.get(Sequences) + " sequences, " + settings.get(SequenceLength) 
                    + " actions or "  + hours + mins;
            // Create char filename
            String startDate = sdf.format(getStartDate());
            String chartFilename = settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_actionselect" + "_s"
                    + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + startDate + ".html";
            coverageChart.createChart(title, subTitle, testCoverageData, chartFilename);
        }
    }

    protected Action selectAction(State state, Set<Action> actions, boolean hasOwnSelect) {
        if (mode() == Modes.Generate) {
            numberOfActions++;
        }
        if (hasOwnSelect) {
            return null;
        } else {
            return super.selectAction(state, actions);
        }
    }

    /**
     * Gets the number of actions executed.
     *
     * @return the number of actions
     */
    public int getNumberOfActions() {
        return numberOfActions;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public abstract Date getStartDate();

    /**
     * Log memory usage.
     */
    private void logMemoryUsage() {
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        logger.info("Used memory is megabytes: {}MB ({})", bytesToMegabytes(memory), memory);
    }

    /**
     * Convert bytes to megabytes.
     *
     * @param bytes the bytes
     * @return the long
     */
    private long bytesToMegabytes(long bytes) {
        return bytes / (1024L * 1024L);
    }
}
