package nl.ou.testar.StateModel.automation;

import com.opencsv.CSVWriter;
import es.upv.staq.testar.StateManagementTags;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static java.lang.System.exit;

public class SqlManager {

    private final String database = "testar";
    private final String user = "testar";
    private final String password = "testar";
    private String connectionString;

    public SqlManager() {
        connectionString = "jdbc:mysql://localhost:3306/" + database;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(connectionString, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }
        return null;
    }

    public void initDatabase() {
        try {
            Connection connection = this.getConnection();
            String insertQuery = "INSERT INTO widget (widget_config_name, widget_description, use_in_abstraction) VALUES(?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            StateManagementTags.getAllTags().forEach(tag -> {
                if (StateManagementTags.getTagGroup(tag) == StateManagementTags.Group.General) {
                    try {
                        insertStatement.setString(1, StateManagementTags.getSettingsStringFromTag(tag));
                        insertStatement.setString(2, tag.name());
                        insertStatement.setInt(3, 1);
                        insertStatement.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            });

            // init application
            String insertApplicationQuery = "INSERT INTO application(application_name, application_version) VALUES (?, ?)";
            PreparedStatement applicationStatement = connection.prepareStatement(insertApplicationQuery);
            applicationStatement.setString(1, "Notepad");
            applicationStatement.setString(2, "1.0.0");
            applicationStatement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initTest1() {
        try {
            Connection connection = this.getConnection();
            // fetch the application
            String fetchApplicationQuery = "SELECT * FROM application WHERE application_name = 'Notepad'";
            Statement appStatement = connection.createStatement();
            ResultSet resultSet1 = appStatement.executeQuery(fetchApplicationQuery);

            resultSet1.first();
            int applicationId = resultSet1.getInt("application_id");

            // now fetch the widgets
            String fetchWidgetQuery = "SELECT * from widget WHERE use_in_abstraction = 1";
            Statement widgetStatement = connection.createStatement();
            ResultSet resultSet2 = widgetStatement.executeQuery(fetchWidgetQuery);

            // in test 1, we create tests with just a single widget
            String testRunInsertQuery = "INSERT INTO automated_test_run(application_id, configured_sequences, configured_steps, reset_data_store_before_run) " +
                    "VALUES(?, ?, ?, ?)";
            PreparedStatement preparedStatement1 = connection.prepareStatement(testRunInsertQuery, Statement.RETURN_GENERATED_KEYS);

            String widgetAttachQuery = "INSERT INTO test_run_widget(test_run_id, widget_id) VALUES(?, ?)";
            PreparedStatement widgetAttachStatement = connection.prepareStatement(widgetAttachQuery);
            while (resultSet2.next()) {
                preparedStatement1.setInt(1, applicationId);
                preparedStatement1.setInt(2, 4);
                preparedStatement1.setInt(3, 50);
                preparedStatement1.setInt(4, 0);
                preparedStatement1.execute();

                // fetch the generated test run id
                ResultSet keys = preparedStatement1.getGeneratedKeys();
                keys.next();
                int testRunId = keys.getInt(1);

                // now attach the widget to the test run
                widgetAttachStatement.setInt(1, testRunId);
                widgetAttachStatement.setInt(2, resultSet2.getInt("widget_id"));
                widgetAttachStatement.execute();

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initTest2() {
        try {
            Connection connection = this.getConnection();
            // fetch the application
            String fetchApplicationQuery = "SELECT * FROM application WHERE application_name = 'Notepad'";
            Statement appStatement = connection.createStatement();
            ResultSet resultSet1 = appStatement.executeQuery(fetchApplicationQuery);
            resultSet1.first();
            int applicationId = resultSet1.getInt("application_id");


            // now fetch the widgets
            String fetchWidgetQuery = "SELECT * from widget WHERE use_in_abstraction = 1";
            Statement widgetStatement = connection.createStatement();
            ResultSet resultSet2 = widgetStatement.executeQuery(fetchWidgetQuery);

            // in test 1, we create tests with just a single widget
            String testRunInsertQuery = "INSERT INTO automated_test_run(application_id, configured_sequences, configured_steps, reset_data_store_before_run) " +
                    "VALUES(?, ?, ?, ?)";
            PreparedStatement preparedStatement1 = connection.prepareStatement(testRunInsertQuery, Statement.RETURN_GENERATED_KEYS);

            String widgetAttachQuery = "INSERT INTO test_run_widget(test_run_id, widget_id) VALUES(?, ?)";
            PreparedStatement widgetAttachStatement = connection.prepareStatement(widgetAttachQuery);

            // in the second test, we make combinations of widgets
            List<Integer> widgetIds = new ArrayList<>();
            while (resultSet2.next()) {
                widgetIds.add(resultSet2.getInt("widget_id"));
            }

            // now create the combinations
            List<int[]> combinations = generateCombinations(widgetIds, 2);
            System.out.println("Number of combinations: " + combinations.size());

            // for each combination, we insert a test run
            for (int[] combination : combinations) {
                preparedStatement1.setInt(1, applicationId);
                preparedStatement1.setInt(2, 4);
                preparedStatement1.setInt(3, 50);
                preparedStatement1.setInt(4, 0);
                preparedStatement1.execute();

                // fetch the generated test run id
                ResultSet keys = preparedStatement1.getGeneratedKeys();
                keys.next();
                int testRunId = keys.getInt(1);

                for (int widgetId: combination) {
                    widgetAttachStatement.setInt(1, testRunId);
                    widgetAttachStatement.setInt(2, widgetId);
                    widgetAttachStatement.execute();
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TestRun> getTestRuns() {
        List<TestRun> testRuns = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            // fetch the test runs
            String fetchRunQuery = "SELECT * FROM automated_test_run WHERE starting_time_ms IS NULL";
            Statement fetchRunStatement = connection.createStatement();
            ResultSet resultSet1 = fetchRunStatement.executeQuery(fetchRunQuery);

            while (resultSet1.next()) {
                int testRunId = resultSet1.getInt("test_run_id");
                int nrOfSequences = resultSet1.getInt("configured_sequences");
                int nrOfSteps = resultSet1.getInt("configured_steps");
                int resetBeforeRunInt = resultSet1.getInt("reset_data_store_before_run");
                boolean resetBeforeRun = resetBeforeRunInt == 1;

                // fetch the widgets for this run
                String fetchWidgetQuery = "SELECT w.* FROM test_run_widget trw JOIN widget w on trw.widget_id = w.widget_id WHERE test_run_id = " + testRunId;
                Statement fetchWidgetStatement = connection.createStatement();
                ResultSet resultSet2 = fetchWidgetStatement.executeQuery(fetchWidgetQuery);
                Set<String> widgets = new HashSet<>();
                while (resultSet2.next()) {

                    testRuns.add(new TestRun(testRunId, nrOfSequences, nrOfSteps, resetBeforeRun, widgets));
                    widgets.add(resultSet2.getString("widget_config_name"));
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }

        return testRuns;
    }

    public void saveTestStats(TestRun testRun) {
        try {
            String insertQuery = "UPDATE automated_test_run " +
                    "SET starting_time_ms     = ?," +
                    "    ending_time_ms       = ?," +
                    "    deterministic_model  = ?," +
                    "    nr_of_steps_executed = ?," +
                    " exception_thrown = ?," +
                    " exception_message = ?," +
                    " stack_trace = ?" +
                    " WHERE test_run_id = ?";
            Connection connection = getConnection();
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            insertStatement.setLong(1, testRun.getStartingMS());
            insertStatement.setLong(2, testRun.getEndingMS());
            insertStatement.setInt(3, testRun.isModelIsDeterministic() ? 1 : 0);
            insertStatement.setInt(4, testRun.getNrOfStepsExecuted());
            insertStatement.setInt(5, testRun.isExceptionThrown() ? 1 : 0);
            insertStatement.setString(6, testRun.getExceptionMessage());
            insertStatement.setString(7, testRun.getTrackTrace());
            insertStatement.setInt(8, testRun.getTestRunId());
            insertStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(1);
        }

    }

    private List<int[]> generateCombinations(List<Integer> input, int r) {
        List<int[]> combinations = new ArrayList<>();
        helper(combinations, new int[r], 0, input.size() -1, 0, input);
        return combinations;
    }

    private void helper(List<int[]> combinations, int[] data, int start, int end, int index, List<Integer> input) {
        if (index == data.length) {
            int[] combination = data.clone();
            combinations.add(combination);
        } else if (start <= end) {
            data[index] = input.get(start);
            helper(combinations, data, start + 1, end, index + 1, input);
            helper(combinations, data, start + 1, end, index, input);
        }
    }

    public void exportTestResultsToFile(String directoryName, boolean quoteExportData) {
        File tempDir = new File("c:\\temp");
        if (tempDir.exists() && !tempDir.isDirectory()) {
            System.out.println("The configured temp directory is not a directory atm. Exiting TESTAR");
            exit(1);
        }

        if (!tempDir.exists()) {
            System.out.println("Creating temporary directory: " + tempDir.getAbsolutePath());
            boolean success = tempDir.mkdir();
            if (!success) {
                System.out.println("Could not create temporary directory. Exiting TESTAR");
                exit(1);
            }
            System.out.println("Successfully created temporary directory.");
        }

        // create the subdirectory
        File exportDir = new File(tempDir, directoryName);
        if (!exportDir.exists()) {
            System.out.println("Creating output directory: " + exportDir.getAbsolutePath());
            boolean success = exportDir.mkdir();
            if (!success) {
                System.out.println("Unable to create output directory. Exiting TESTAR");
                exit(1);
            }
        }

        // we want to export the automated_test_run and the test_run_widget tables
        Connection connection = this.getConnection();
        String atrQuery = "SELECT * FROM automated_test_run";
        try {
            // first the automated_test_run table
            Statement statement1 = connection.createStatement();
            ResultSet resultSet1 = statement1.executeQuery(atrQuery);

            // open a file writer
            File atrFile = new File(exportDir, "automated_test_run");
            try (FileWriter fileWriter = new FileWriter(atrFile.getAbsoluteFile())) {
                try (CSVWriter csvWriter = new CSVWriter(fileWriter, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
                    csvWriter.writeAll(resultSet1, true, false, quoteExportData);
                }
            }

            // then the test_run_widget_table
            String trwQuery = "SELECT * FROM test_run_widget";
            Statement statement2 = connection.createStatement();
            ResultSet resultSet2 = statement2.executeQuery(trwQuery);
            File trwFile = new File(exportDir, "test_run_widget");
            try (FileWriter fileWriter = new FileWriter(trwFile.getAbsoluteFile())) {
                try (CSVWriter csvWriter = new CSVWriter(fileWriter, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
                    csvWriter.writeAll(resultSet2, true, false, quoteExportData);
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Could not retrieve data. Exiting TESTAR");
            exit(1);
        }

        System.out.println("Succesfully exported test run data.");
    }

}
