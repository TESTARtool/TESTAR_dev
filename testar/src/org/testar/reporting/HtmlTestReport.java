package org.testar.reporting;

import nl.ou.testar.TestReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.OutputStructure;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

public class HtmlTestReport implements TestReport {

    /**
     * Directory that is used to store our template in
     */
    private static final String OUTPUT_DATA_DIR = "output/Testar-Report/";
    /**
     * Name of our report file
     */
    private static final String HTML_INDEX_PATH = "report.html";
    /**
     * Location of our pagination js file
     */
    private static final String HTML_JS_PAGINATE_TABLE_PATH = "js/paginateTable.js";
    /**
     * Location of our Issue Chart.js file
     */
    private static final String HTML_JS_ISSUE_CHART_PATH = "js/issueMetricChart.js";
    /**
     * Location of our Oracle Chart.js file
     */
    private static final String HTML_JS_ORACLE_CHART_PATH = "js/oracleMetricChart.js";
    /**
     * Location of our CSS file
     */
    private static final String HTML_CSS_REPORT_PATH = "css/report.css";
    /**
     * Content of the index template
     */
    private String htmlIndex;
    /**
     * Content of the pagination js template
     */
    private String htmlJsPaginateTable;
    /**
     * Content of our Issue chart.js template
     */
    private String htmlJsIssueChart;
    /**
     * Content of our Oracle chart.js template
     */
    private String htmlJsOracleChart;
    /**
     * Content of our css template
     */
    private String htmlCssReport;

    /**
     * Number of sequences executed
     */
    private int sequences = 0;
    /**
     * Number of total actions executed
     */
    private int actions = 0;
    /**
     * Total number of non-severe issues found
     */
    private int nonSevereIssues = 0;
    /**
     * Current number of non-severe issues found
     */
    private int curNonSevereIssues = 0;
    /**
     * Total number of severe issues found
     */
    private int severeIssues = 0;
    /**
     * Current number of severe issues found
     */
    private int curSevereIssues = 0;

    /**
     * Location where we should store our report in
     */
    private final String reportDir;

    private static final Logger LOGGER = LogManager.getLogger();

    private class TestAction {
        private final String description;
        private final String name;
        private final int id;
        private final String status;
        private final String screenshot;
        private final String start;

        private TestAction(String description, String name, int id, String status, String screenshot, String start) {
            this.description = description;
            this.name = name;
            this.id = id;
            this.status = status;
            this.screenshot = screenshot;
            this.start = start;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public String getScreenshot() {
            return screenshot;
        }

        public String getStart() {
            return start;
        }
    }

    private class TestIteration {
        private final int nonSevereIssues;
        private final int severeIssues;
        private final boolean hitOracle;

        public TestIteration(int nonSevereIssues, int severeIssues) {
            this.nonSevereIssues = nonSevereIssues;
            this.severeIssues = severeIssues;
            this.hitOracle = severeIssues > 0;
        }

        public int getNonSevereIssues() {
            return this.nonSevereIssues;
        }

        public int getSevereIssues() {
            return this.severeIssues;
        }

        public int getIssues() {
            return this.severeIssues + this.nonSevereIssues;
        }

        public boolean hasHitOracle() {
            return hitOracle;
        }
    }

    private ArrayList<TestIteration> iterations;
    private ArrayList<TestAction> curActions;

    public HtmlTestReport() {
        /// Create a file name with the format: $outputDir/$dateTime_$sutName-report.html
        this.reportDir = String.format(
                "%s%sFull Report%s",
                OutputStructure.htmlOutputDir,
                File.separator,
                File.separator
        );

        this.iterations = new ArrayList<>();
        this.curActions = new ArrayList<>();

        try {
            // Read the report templates
            this.htmlIndex = readFileAsString(HTML_INDEX_PATH);
            this.htmlJsPaginateTable = readFileAsString(HTML_JS_PAGINATE_TABLE_PATH);
            this.htmlJsIssueChart = readFileAsString(HTML_JS_ISSUE_CHART_PATH);
            this.htmlJsOracleChart = readFileAsString(HTML_JS_ORACLE_CHART_PATH);
            this.htmlCssReport = readFileAsString(HTML_CSS_REPORT_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createReportDirs() {
        // Create report dirs if they don't exists already
        File jsDirectory = new File(this.reportDir + "js/");
        File cssDirectory = new File(this.reportDir + "css/");

        if (!jsDirectory.exists() && !jsDirectory.mkdirs())
            LOGGER.error("Failed to create JS report directory!");

        if (!cssDirectory.exists() && !cssDirectory.mkdirs())
            LOGGER.error("Failed to create CSS report directory!");
    }

    private static String readFileAsString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(OUTPUT_DATA_DIR + filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }

        return contentBuilder.toString();
    }

    @FunctionalInterface
    private interface SaveFile<T> {
        void apply(T location, T content);
    }

    private String getIterationsAsHtml() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.iterations.size(); i++) {
            TestIteration it = this.iterations.get(i);
            sb.append("<tr class=\"value\">\n");
            sb.append(
                    String.format(
                            "<td>%d</td> <td>%d</td> <td>%s</td> <td> <button id=\"%s\" class=\"show-details\" onclick=\"location.href='sequences/details-%d.html'\">Details</button> </td>%n",
                            i, it.getIssues(), it.hasHitOracle() ? "True" : "False", "iteration_id_" + i, i
                    )
            );
            sb.append("</tr>\n");
        }

        return sb.toString();
    }

    private String getIssuesPerSequenceAsJsonArray() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        boolean firstItem = true;
        for (TestIteration it : this.iterations) {
            if (firstItem) {
                firstItem = false;
            } else {
                sb.append(',');
            }
            sb.append(it.getIssues());
        }

        sb.append(']');
        return sb.toString();
    }

    private String getOraclesPerSequenceAsJsonArray() {
        String jsonArray = "[";

        jsonArray += this.severeIssues;
        jsonArray +=  ',';
        jsonArray += this.sequences - this.severeIssues;

        jsonArray += ']';
        return jsonArray;
    }

    private String parseIndexTemplate(
            final int actionsPerSequence,
            final int totalSequences,
            final String url
    ) {
        String title = url
                .replace("https://", "")
                .replace("http://", "")
                .replace("www2.", "")
                .replace("www.", "")
                .replace("\\", "")
                .replace("/", "");

        return this.htmlIndex
                .replace("#[sequences]", this.sequences + "/" + totalSequences)
                .replace("#[actions]", Integer.toString(this.actions))
                .replace("#[actions_per_sequence]", Integer.toString(actionsPerSequence))
                .replace("#[issues]", Integer.toString(this.nonSevereIssues))
                .replace("#[oracles]", Integer.toString(this.severeIssues))
                .replace(
                        "<a href=\"https://www.example.com\">#[url]</a>",
                        String.format("<a href=\"%s\">%s</a>", url, title)
                )
                .replace("#[iteration_trs]", this.getIterationsAsHtml())
                .replace("0//#[chart_iteration]", Integer.toString(this.sequences))
                .replace("[0]//#[chart_issues]", this.getIssuesPerSequenceAsJsonArray())
                .replace("[0]//#[chart_oracles]", this.getOraclesPerSequenceAsJsonArray());
    }

    public void saveActions() {
        File sequenceDirectory = new File(this.reportDir + "sequences/");

        if (!sequenceDirectory.exists() && !sequenceDirectory.mkdirs())
            LOGGER.error("Failed to create sequences directory!");

        StringBuilder tableBuilder = new StringBuilder();

        tableBuilder.append("<html> <head> <title>Sequence report ")
                .append(this.sequences)
                .append("</title> ")
                .append("<link href=\"../css/report.css\" rel=\"stylesheet\"/>")
                .append("</head> <body>\n");

        tableBuilder.append("<table id=\"sequenceOverviewTable\">\n");

        tableBuilder.append("<thead>\n")
                .append("                <tr>\n")
                .append("                    <th id=\"action-desc\">Description</th>\n")
                .append("                    <th id=\"action-name\">Name</th>\n")
                .append("                    <th id=\"action-id\">Action ID</th>\n")
                .append("                    <th id=\"action-status\">Status</th>\n")
                .append("                    <th id=\"action-screenshot\">Screenshot</th>\n")
                .append("                    <th id=\"action-start\">Start</th>\n")
                .append("                </tr>\n")
                .append("            </thead>\n")
                .append("            <tbody>\n");

        final String TD_OPEN = "<td>";
        final String TD_CLOSE = "</td>";
        this.curActions.forEach(it -> {
            tableBuilder.append("<tr>");

            tableBuilder.append(TD_OPEN).append(it.getDescription()).append(TD_CLOSE); // Desc
            tableBuilder.append(TD_OPEN).append(it.getName()).append(TD_CLOSE); // Name
            tableBuilder.append(TD_OPEN).append(it.getId()).append(TD_CLOSE); // Id
            tableBuilder.append(TD_OPEN).append(it.getStatus()).append(TD_CLOSE); // Status
            tableBuilder.append(TD_OPEN).append(it.getScreenshot()).append(TD_CLOSE); // Screenshot
            tableBuilder.append(TD_OPEN).append(it.getStart()).append(TD_CLOSE); // Start

            tableBuilder.append("</tr>");
        });
        tableBuilder.append("            </tbody>\n" + "</table>\n</body></html>\n");

        try (
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(
                                this.reportDir + "sequences/details-" + this.sequences + ".html"
                        ))
        ) {
            writer.write(tableBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveReport(
            final int actionsPerSequence,
            final int totalSequences,
            final String url
    ) {
        // Make sure that the directories exists before we write to it.
        createReportDirs();

        // Create a lambda for saving data to files
        SaveFile<String> saveFile = (location, content) -> {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(location));
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // Save the template files
        saveFile.apply(this.reportDir + HTML_INDEX_PATH, this.parseIndexTemplate(actionsPerSequence, totalSequences, url));
        saveFile.apply(this.reportDir + HTML_JS_PAGINATE_TABLE_PATH, this.htmlJsPaginateTable);
        saveFile.apply(this.reportDir + HTML_JS_ISSUE_CHART_PATH, this.htmlJsIssueChart);
        saveFile.apply(this.reportDir + HTML_JS_ORACLE_CHART_PATH, this.htmlJsOracleChart);
        saveFile.apply(this.reportDir + HTML_CSS_REPORT_PATH, this.htmlCssReport);
    }

    public void addState(State state) {
        /*
         * Currently not used but may be useful in the future for extra reporting. This is also used in the original report
         */
    }

    public void addActions(Set<Action> actions) {
        /*
         * Currently not used but may be useful in the future for extra reporting. This is also used in the original report
         */
    }

    private void addAction(Action action, State state) {
        Date date = new Date(state.get(Tags.TimeStamp));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        this.curActions.add(
                new TestAction(
                        action.toString(),
                        action.toShortString(),
                        this.curActions.size(),
                        state.get(Tags.OracleVerdict).verdictSeverityTitle(),
                        "<img src=\"../../../../." + state.get(Tags.ScreenshotPath) + "\" />",
                        df.format(date)
                )
        );
    }

    public void addSelectedAction(State state, Action action) {
        this.addAction(action, state);
        this.actions++;
    }

    public void addTestVerdict(Verdict verdict, Action action, State state) { // Even non-severe issues cancel the test? e.g. suspicious titles?
        if (verdict.severity() >= Verdict.SEVERITY_NOT_RESPONDING && verdict.severity() <= Verdict.SEVERITY_MAX) {
            this.severeIssues++;
            this.curSevereIssues++;
        } else if (verdict.severity() >= Verdict.SEVERITY_WARNING) {
            this.nonSevereIssues++;
            this.curNonSevereIssues++;
        }
        if(action!=null) {
            this.addAction(action, state); // Last action is not provided to addSelectedAction
        }
        this.iterations.add(
                new TestIteration(
                        this.curNonSevereIssues,
                        this.curSevereIssues)
        );
        this.curSevereIssues = 0;
        this.curNonSevereIssues = 0;
        this.saveActions();
        this.curActions = new ArrayList<>();
        this.sequences++;
    }

    @Override
    public void setTargetState(Action action, State state) {
        // STUB
    }
}
