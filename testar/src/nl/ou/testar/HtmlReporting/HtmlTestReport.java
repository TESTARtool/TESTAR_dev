package nl.ou.testar.HtmlReporting;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.testar.OutputStructure;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

public class HtmlTestReport {

    private static final String OUTPUT_DATA_DIR = "output/Testar-Report/";
    private static final String HTML_INDEX_PATH = "report.html";
    private static final String HTML_JS_ISSUE_CHART_PATH = "js/issueMetricChart.js";
    private static final String HTML_JS_ORACLE_CHART_PATH = "js/oracleMetricChart.js";
    private static final String HTML_CSS_REPORT_PATH = "css/report.css";
    private String htmlIndex, htmlJsIssueChart, htmlJsOracleChart, htmlCssReport;

    private final String reportDir;

    public HtmlTestReport() {
        /// Create a file name with the format: $outputDir/$dateTime_$sutName-report.html
        this.reportDir = String.format(
                "%s%s%s_%s-report%s",
                OutputStructure.htmlOutputDir,
                File.separator,
                OutputStructure.startInnerLoopDateString,
                OutputStructure.executedSUTname,
                File.separator
        );

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

        if(!jsDirectory.exists())
            jsDirectory.mkdirs();

        if(!cssDirectory.exists())
            cssDirectory.mkdirs();
    }

    private static String readFileAsString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(OUTPUT_DATA_DIR + filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }

        return contentBuilder.toString();
    }

    @FunctionalInterface
    private interface saveFile<T> {
        void apply(T location, T content);
    }

    private void saveReport() {
        // Make sure that the directories exists before we write to it.
        createReportDirs();

        // Create a lambda for saving data to files
        saveFile<String> saveFile = (location, content) -> {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(location));
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // Save the template files
        saveFile.apply(this.reportDir + HTML_INDEX_PATH, this.htmlIndex);
        saveFile.apply(this.reportDir + HTML_JS_ISSUE_CHART_PATH, this.htmlJsIssueChart);
        saveFile.apply(this.reportDir + HTML_JS_ORACLE_CHART_PATH, this.htmlJsOracleChart);
        saveFile.apply(this.reportDir + HTML_CSS_REPORT_PATH, this.htmlCssReport);
    }

    public void addState(State state) {
    }

    public void addActions(Set<Action> actions) {
    }

    public void addSelectedAction(State state, Action action) {
    }

    public void addTestVerdict(Verdict verdict) {
        this.saveReport();
    }
}
