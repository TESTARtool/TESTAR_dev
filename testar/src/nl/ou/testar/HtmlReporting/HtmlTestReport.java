package nl.ou.testar.HtmlReporting;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.testar.OutputStructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

public class HtmlTestReport {

    /**
     * Directory that is used to store our template in
     */
    private static final String OUTPUT_DATA_DIR = "output/Testar-Report/";
    /**
     * Name of our report file
     */
    private static final String HTML_INDEX_PATH = "report.html";
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
     * Number of non-severe issues found
     */
    private int nonSevereIssues = 0;
    /**
     * Number of severe issues found
     */
    private int severeIssues = 0;

    /**
     * Location where we should store our report in
     */
    private final String reportDir;

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

    public HtmlTestReport() {
        /// Create a file name with the format: $outputDir/$dateTime_$sutName-report.html
        this.reportDir = String.format(
                "%s%sFull Report%s",
                OutputStructure.htmlOutputDir,
                File.separator,
                File.separator
        );

        this.iterations = new ArrayList<>();

        try {
            // Read the report templates
            this.htmlIndex = readFileAsString(HTML_INDEX_PATH);
            this.htmlJsIssueChart = readFileAsString(HTML_JS_ISSUE_CHART_PATH);
            this.htmlJsOracleChart = readFileAsString(HTML_JS_ORACLE_CHART_PATH);
            this.htmlCssReport = readFileAsString(HTML_CSS_REPORT_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createReportDirs() {
        // Create report dirs if they don't exists already
        File jsDirectory, cssDirectory;
        jsDirectory = new File(this.reportDir + "js/");
        cssDirectory = new File(this.reportDir + "css/");

        if (!jsDirectory.exists())
            if (!jsDirectory.mkdirs())
                System.err.println("Failed to create JS report directory!");

        if (!cssDirectory.exists())
            if (!cssDirectory.mkdirs())
                System.err.println("Failed to create CSS report directory!");
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
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < this.iterations.size(); i++) {
            TestIteration it = this.iterations.get(i);
            sb.append("<tr class=\"value\">\n");
            sb.append(
                    String.format(
                            "<td>%d</td> <td>%d</td> <td>%s</td> <td> <button id=\"%s\" class=\"show-details\">Details</button> </td>\n",
                            i, it.getIssues(), it.hasHitOracle() ? "True" : "False", "iteration_id_" + i
                    )
            );
            sb.append("</tr>\n");
        }

        return sb.toString();
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
                .replace("#[iteration_trs]", this.getIterationsAsHtml());
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
        saveFile.apply(this.reportDir + HTML_JS_ISSUE_CHART_PATH, this.htmlJsIssueChart);
        saveFile.apply(this.reportDir + HTML_JS_ORACLE_CHART_PATH, this.htmlJsOracleChart);
        saveFile.apply(this.reportDir + HTML_CSS_REPORT_PATH, this.htmlCssReport);
    }

    public void addState(State state) {
    }

    public void addActions(Set<Action> actions) {
    }

    public void addSelectedAction(State state, Action action) {
        this.actions++;
    }

    public void addTestVerdict(Verdict verdict) {
        if (verdict.severity() >= Verdict.SEVERITY_NOT_RESPONDING && verdict.severity() <= Verdict.SEVERITY_MAX) {
            this.severeIssues++;
        } else if (verdict.severity() >= Verdict.SEVERITY_WARNING) {
            this.nonSevereIssues++;
        }
        this.sequences++;

        this.iterations.add(
                new TestIteration(
                        this.nonSevereIssues,
                        this.severeIssues
                )
        );
    }
}
