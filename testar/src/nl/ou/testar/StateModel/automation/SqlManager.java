package nl.ou.testar.StateModel.automation;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import es.upv.staq.testar.StateManagementTags;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.exit;

public class SqlManager {

    public static final String TEMP_DIR = "c:\\temp";
    public static final String IMPORT_SUB_DIR = "results_import";
    public static final String AUTOMATED_TEST_RUN_CSV = "automated_test_run.csv";
    public static final String TEST_RUN_WIDGET_CSV = "test_run_widget.csv";
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

    public void initDatabase(String applicationName, String applicationVersion) {
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
            applicationName = applicationName.isEmpty() ? "Notepad" : applicationName;
            applicationVersion = applicationVersion.isEmpty() ? "1.0.0" : applicationVersion;
            applicationStatement.setString(1, applicationName);
            applicationStatement.setString(2, applicationVersion);
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

    public void initTest3(boolean clearOldResults) {
        Connection connection = getConnection();
        if (clearOldResults) {
            String query1 = "TRUNCATE TABLE test_run_widget";
            String query2 = "DELETE FROM automated_test_run";
            String query3 = "ALTER TABLE automated_test_run AUTO_INCREMENT = 1";
            String query4 = "ALTER TABLE test_run_widget AUTO_INCREMENT = 1";
            Stream.of(query1, query2, query3, query4).forEach(query -> {
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error occurred while cleaning old results. Exiting TESTAR.");
                    exit(1);
                }
            });
        }

        // fetch the application
        try {
            String fetchApplicationQuery = "SELECT * FROM application WHERE application_name = 'Notepad'";
            Statement appStatement = connection.createStatement();
            ResultSet resultSet1 = appStatement.executeQuery(fetchApplicationQuery);
            resultSet1.first();
            int applicationId = resultSet1.getInt("application_id");


            // update the widgets to a subset of useable combos
            String updateWidgetQuery1 = "UPDATE widget " +
                    "SET use_in_abstraction = 0";
            String updateWidgetQuery2 = "UPDATE widget " +
                    "SET use_in_abstraction = 1 " +
                    "WHERE" +
                    " widget_config_name IN ('WidgetTitle', 'WidgetHasKeyboardFocus', 'WidgetBoundary' );";
            Statement updateWidgetStatement = connection.createStatement();
            updateWidgetStatement.executeUpdate(updateWidgetQuery1);
            updateWidgetStatement.executeUpdate(updateWidgetQuery2);

            // in test 1, we create tests with just a single widget
            String testRunInsertQuery = "INSERT INTO automated_test_run(application_id, configured_sequences, configured_steps, reset_data_store_before_run) " +
                    "VALUES(?, ?, ?, ?)";
            PreparedStatement preparedStatement1 = connection.prepareStatement(testRunInsertQuery, Statement.RETURN_GENERATED_KEYS);

            String widgetAttachQuery = "INSERT INTO test_run_widget(test_run_id, widget_id) VALUES(?, ?)";
            PreparedStatement widgetAttachStatement = connection.prepareStatement(widgetAttachQuery);

            // now fetch the widgets
            String fetchWidgetQuery = "SELECT * from widget WHERE use_in_abstraction = 1";
            Statement widgetStatement = connection.createStatement();
            ResultSet resultSet2 = widgetStatement.executeQuery(fetchWidgetQuery);

            // in the second test, we make combinations of widgets
            List<Integer> widgetIds = new ArrayList<>();
            while (resultSet2.next()) {
                widgetIds.add(resultSet2.getInt("widget_id"));
            }

            // now create the combinations
            List<int[]> combinations = generateCombinations(widgetIds, 3);
            System.out.println("Number of combinations: " + combinations.size());

            // for each combination, we insert a test run
            for (int[] combination : combinations) {
                preparedStatement1.setInt(1, applicationId);
                preparedStatement1.setInt(2, 4);
                preparedStatement1.setInt(3, 100);
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

    public void initTest4(boolean clearOldResults) {
        Connection connection = getConnection();
        if (clearOldResults) {
            String query1 = "TRUNCATE TABLE test_run_widget";
            String query2 = "DELETE FROM automated_test_run";
            String query3 = "ALTER TABLE automated_test_run AUTO_INCREMENT = 1";
            String query4 = "ALTER TABLE test_run_widget AUTO_INCREMENT = 1";
            Stream.of(query1, query2, query3, query4).forEach(query -> {
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error occurred while cleaning old results. Exiting TESTAR.");
                    exit(1);
                }
            });
        }

        // fetch the application
        try {
            String fetchApplicationQuery = "SELECT * FROM application WHERE application_name = 'Notepad'";
            Statement appStatement = connection.createStatement();
            ResultSet resultSet1 = appStatement.executeQuery(fetchApplicationQuery);
            resultSet1.first();
            int applicationId = resultSet1.getInt("application_id");


            // update the widgets to a subset of useable combos
            String updateWidgetQuery1 = "UPDATE widget " +
                    "SET use_in_abstraction = 0";
            String updateWidgetQuery2 = "UPDATE widget " +
                    "SET use_in_abstraction = 1 " +
                    "WHERE" +
                    " widget_config_name IN ('WidgetTitle', 'WidgetHasKeyboardFocus', 'WidgetBoundary' );";
            Statement updateWidgetStatement = connection.createStatement();
            updateWidgetStatement.executeUpdate(updateWidgetQuery1);
            updateWidgetStatement.executeUpdate(updateWidgetQuery2);

            // in test 1, we create tests with just a single widget
            String testRunInsertQuery = "INSERT INTO automated_test_run(application_id, configured_sequences, configured_steps, reset_data_store_before_run) " +
                    "VALUES(?, ?, ?, ?)";
            PreparedStatement preparedStatement1 = connection.prepareStatement(testRunInsertQuery, Statement.RETURN_GENERATED_KEYS);

            String widgetAttachQuery = "INSERT INTO test_run_widget(test_run_id, widget_id) VALUES(?, ?)";
            PreparedStatement widgetAttachStatement = connection.prepareStatement(widgetAttachQuery);

            // now fetch the widgets
            String fetchWidgetQuery = "SELECT * from widget WHERE use_in_abstraction = 1";
            Statement widgetStatement = connection.createStatement();
            ResultSet resultSet2 = widgetStatement.executeQuery(fetchWidgetQuery);
            List<Integer> mainWidgetIds = new ArrayList<>();
            while (resultSet2.next()) {
                mainWidgetIds.add(resultSet2.getInt("widget_id"));
            }

            String alternateWidgetQuery = "SELECT * FROM widget WHERE use_in_abstraction = 0";
            Statement alternateWidgetStatement = connection.createStatement();
            ResultSet resultSet3 = alternateWidgetStatement.executeQuery(alternateWidgetQuery);

            // in this test, we take the 3 most succesful widgets and add all the combos of 2 from the other 16 to them
            List<Integer> widgetIds = new ArrayList<>();
            while (resultSet3.next()) {
                widgetIds.add(resultSet2.getInt("widget_id"));
            }

            // now create the combinations
            List<int[]> combinations = generateCombinations(widgetIds, 2);
            System.out.println("Number of combinations: " + combinations.size());
            // add the 3 main widgets to all the combinations
            List<int[]> mergedCombinations = new ArrayList<>();
            for (int[] combination : combinations) {
                IntStream stream1 = Arrays.stream(combination);
                IntStream stream2 = mainWidgetIds.stream().mapToInt(i -> i);
                IntStream combinedStream = IntStream.concat(stream1, stream2);
                mergedCombinations.add(combinedStream.toArray());
            }

            // for each combination, we insert a test run
            for (int[] combination : mergedCombinations) {
                preparedStatement1.setInt(1, applicationId);
                preparedStatement1.setInt(2, 4);
                preparedStatement1.setInt(3, 100);
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
                    widgets.add(resultSet2.getString("widget_config_name"));
                }
                testRuns.add(new TestRun(testRunId, nrOfSequences, nrOfSteps, resetBeforeRun, widgets));

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
        File tempDir = new File(TEMP_DIR);
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
            File atrFile = new File(exportDir, AUTOMATED_TEST_RUN_CSV);
            try (FileWriter fileWriter = new FileWriter(atrFile.getAbsoluteFile())) {
                try (CSVWriter csvWriter = new CSVWriter(fileWriter, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
                    csvWriter.writeAll(resultSet1, true, false, quoteExportData);
                }
            }

            // then the test_run_widget_table
            String trwQuery = "SELECT * FROM test_run_widget";
            Statement statement2 = connection.createStatement();
            ResultSet resultSet2 = statement2.executeQuery(trwQuery);
            File trwFile = new File(exportDir, TEST_RUN_WIDGET_CSV);
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

    public void importTestResults(boolean clearOldResults) {
        final List<Integer> testRunTotal = new ArrayList<>();
        final List<Integer> testRunWidgetTotal = new ArrayList<>();

        Connection connection = getConnection();
        if (clearOldResults) {
            String query1 = "TRUNCATE TABLE test_run_widget";
            String query2 = "DELETE FROM automated_test_run";
            String query3 = "ALTER TABLE automated_test_run AUTO_INCREMENT = 1";
            String query4 = "ALTER TABLE test_run_widget AUTO_INCREMENT = 1";
            Stream.of(query1, query2, query3, query4).forEach(query -> {
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error occurred while cleaning old results. Exiting TESTAR.");
                    exit(1);
                }
            });
        }

        // now, we look in the temp directory for an import folder
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists() || !tempDir.isDirectory()) {
            System.out.println("Temp directory does not exist. Exiting TESTAR.");
            exit(1);
        }

        File importDir = new File(tempDir, IMPORT_SUB_DIR);
        if (!importDir.exists() || !importDir.isDirectory()) {
            System.out.println("Import folder does not exist. Exiting TESTAR");
            exit(1);
        }

        // the import folder should contain sub folders that contain two files, one for the runs and one for the widgets attached to the runs
        Arrays.stream(Objects.requireNonNull(importDir.listFiles(File::isDirectory))).forEach(resultDir -> {
            System.out.println("Processing directory: " + resultDir.getAbsolutePath());
            File atrFile = new File(resultDir, AUTOMATED_TEST_RUN_CSV);
            File trwFile = new File(resultDir, TEST_RUN_WIDGET_CSV);
            if (!atrFile.exists() || !trwFile.exists()) {
                System.out.println("Missing import files. Exiting TESTAR");
                exit(1);
            }

            // now, clear the import tables
            String query5 = "TRUNCATE TABLE automated_test_run_import";
            String query6 = "TRUNCATE TABLE test_run_widget_import";
            Stream.of(query5, query6).forEach(query -> {
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error occurred while cleaning the import tables. Exiting TESTAR.");
                    exit(1);
                }
            });

            try {
                FileReader fileReader = new FileReader(atrFile);
                HeaderColumnNameMappingStrategy<AutomatedTestRunPojo> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
                mappingStrategy.setType(AutomatedTestRunPojo.class);

                // first we import the testruns
                CsvToBean<AutomatedTestRunPojo> csvToBean = new CsvToBeanBuilder<AutomatedTestRunPojo>(fileReader)
                        .withSeparator(';')
                        .withIgnoreLeadingWhiteSpace(true)
                        .withType(AutomatedTestRunPojo.class)
                        .withMappingStrategy(mappingStrategy)
                        .build();
                List<AutomatedTestRunPojo> atrs = csvToBean.parse();
                System.out.println("Parsed " + atrs.size() + " nr of test results");
                testRunTotal.add(atrs.size());

                String query = "INSERT INTO automated_test_run_import(test_run_id, application_id, configured_sequences, configured_steps, reset_data_store_before_run, starting_time_ms, ending_time_ms, nr_of_steps_executed, deterministic_model, exception_thrown, exception_message, stack_trace) " +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    PreparedStatement insertAtrStatement = connection.prepareStatement(query);

                    atrs.forEach(atr -> {
                        try {
                            insertAtrStatement.setInt(1, atr.getTestRunId());
                            insertAtrStatement.setInt(2, atr.getApplicationId());
                            insertAtrStatement.setInt(3, atr.getConfiguredSequences());
                            insertAtrStatement.setInt(4, atr.getConfiguredSteps());
                            insertAtrStatement.setInt(5, booleanStringToIntHelper(atr.getResetDataStoreBeforeRun()));
                            insertAtrStatement.setLong(6, Long.parseLong(atr.getStartingTimeMs()));
                            insertAtrStatement.setLong(7, Long.parseLong(atr.getEndingTimeMs()));
                            insertAtrStatement.setInt(8, atr.getNrOfStepsExecuted());
                            insertAtrStatement.setInt(9, booleanStringToIntHelper(atr.getDeterministicModel()));
                            insertAtrStatement.setInt(10, booleanStringToIntHelper(atr.getExceptionThrown()));
                            insertAtrStatement.setString(11, atr.getExceptionMessage());
                            insertAtrStatement.setString(12, atr.getStrackTrace());
                            insertAtrStatement.execute();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("An error occurred during test result db insertion. Exiting TESTAR");
                            exit(1);
                        }
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("An error occurred during test result db insertion. Exiting TESTAR");
                    exit(1);
                }

                // next we import the test_run_widget file
                fileReader = new FileReader(trwFile);
                HeaderColumnNameMappingStrategy<TestRunWidgetPojo> mappingStrategy2 = new HeaderColumnNameMappingStrategy<>();
                mappingStrategy2.setType(TestRunWidgetPojo.class);

                // first we import the testruns
                CsvToBean<TestRunWidgetPojo> csvToBean2 = new CsvToBeanBuilder<TestRunWidgetPojo>(fileReader)
                        .withSeparator(';')
                        .withIgnoreLeadingWhiteSpace(true)
                        .withType(TestRunWidgetPojo.class)
                        .withMappingStrategy(mappingStrategy2)
                        .build();
                List<TestRunWidgetPojo> atrs2 = csvToBean2.parse();
                System.out.println("Parsed " + atrs2.size() + " nr of test run widget combo's");
                testRunWidgetTotal.add(atrs2.size());

                query = "INSERT INTO test_run_widget_import(test_run_id, widget_id) VALUES(? , ?)";
                try {
                    PreparedStatement insertTrwStatement = connection.prepareStatement(query);
                    atrs2.forEach(atr -> {
                        try {
                            insertTrwStatement.setInt(1, atr.getTestRunId());
                            insertTrwStatement.setInt(2, atr.getWidgetId());
                            insertTrwStatement.execute();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("An error occurred during test result db insertion. Exiting TESTAR");
                            exit(1);
                        }

                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("An error occurred during test result db insertion. Exiting TESTAR");
                    exit(1);
                }

                mergeImport(connection);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("Imported a total of " + testRunTotal.stream().mapToInt(i -> i).sum() + " test runs");
            System.out.println("Imported a total of " + testRunWidgetTotal.stream().mapToInt(i -> i).sum() + " test run widgets");



        });

        // process the test results
        calculateTestResults(connection, clearOldResults);

        System.out.println("Succesfully imported test results");
    }

    private void mergeImport(Connection connection) {
        String query1 = "SELECT COUNT(*) FROM automated_test_run_import";
        String query2 = "SELECT COUNT(*) FROM test_run_widget_import";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet1 = statement.executeQuery(query1);
            if (!resultSet1.next()) {
                System.out.println("No runs were imported. Exiting TESTAR");
                exit(1);
            }
            int count = resultSet1.getInt(1);
            if (count <= 0) {
                System.out.println("No runs were imported. Exiting TESTAR");
                exit(1);
            }

            ResultSet resultSet2 = statement.executeQuery(query2);
            if (!resultSet2.next()) {
                System.out.println("No widget combos were imported. Exiting TESTAR");
                exit(1);
            }
            count = resultSet2.getInt(1);
            if (count <= 0) {
                System.out.println("No widget combos were imported. Exiting TESTAR");
                exit(1);
            }

            String query3 = "SELECT IFNULL(MAX(test_run_id), 0) from automated_test_run";
            String query4 = "SELECT MIN(test_run_id) from automated_test_run_import";
            ResultSet resultSet3 = statement.executeQuery(query3);
            resultSet3.next();
            int currentMaxTestRunId = resultSet3.getInt(1);
            ResultSet resultSet4 = statement.executeQuery(query4);
            resultSet4.next();
            int minImportTestRunId = resultSet4.getInt(1);
            System.out.println("current max test run id = " + currentMaxTestRunId);
            System.out.println("min import test run id = " + minImportTestRunId);
            System.out.println("Porting import test run ids");

            int targetStartingTestRunId = currentMaxTestRunId + 1;
            int difference = targetStartingTestRunId - minImportTestRunId;
            if (difference != 0) {
                // we need to change the test run ids in the import tables
                String alterQueryAtr = "UPDATE automated_test_run_import SET test_run_id = test_run_id" + (difference > 0 ? " + " : " - ") + Math.abs(difference);
                String alterQueryTrw = "UPDATE test_run_widget_import SET test_run_id = test_run_id" + (difference > 0 ? " + " : " - ") + Math.abs(difference);
                Statement statement1 = connection.createStatement();
                statement1.executeUpdate(alterQueryAtr);
                statement1.executeUpdate(alterQueryTrw);
            }

            // finally, insert the values from the import tables
            String query5 = "INSERT INTO automated_test_run(test_run_id, application_id, configured_sequences, configured_steps, reset_data_store_before_run, starting_time_ms, ending_time_ms, nr_of_steps_executed, deterministic_model, exception_thrown, exception_message, stack_trace) " +
                    "SELECT * FROM automated_test_run_import";
            String query6 = "INSERT INTO test_run_widget(test_run_id, widget_id) " +
                    "SELECT test_run_id, widget_id FROM test_run_widget_import";
            statement.execute(query5);
            statement.execute(query6);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while merging the imports. Exiting TESTAR");
            exit(1);
        }

    }

    private int booleanStringToIntHelper(String toConvert) {
        return toConvert.equals("false") ? 0 : 1;
    }

    private void calculateTestResults(Connection connection, boolean clearResults) {
        System.out.println("Calculating test results");
        if (clearResults) {
            System.out.println("Clearing old results.");
            String clearQuery = "DELETE FROM test_run_result";
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(clearQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String insertResultsQuery = "INSERT INTO test_run_result(widget_combo, widget_names, average_runtime_seconds, nr_of_steps_series,\n" +
                "                            steps_executed_total, steps_executed_average)\n" +
                "\n" +
                "SELECT test_run_base.widget_combo,\n" +
                "       test_run_base.widget_names,\n" +
                "       ROUND(AVG(test_run_base.runtime_ms) / 1000)                                                                 as average_runtime_seconds,\n" +
                "       GROUP_CONCAT(test_run_base.nr_of_steps_executed ORDER BY test_run_base.nr_of_steps_executed SEPARATOR\n" +
                "                    ', ')                                                                                          as steps_cumulative,\n" +
                "       SUM(test_run_base.nr_of_steps_executed)                                                                     as total_steps_executed,\n" +
                "\n" +
                "       AVG(test_run_base.nr_of_steps_executed)                                                                     as steps_executed_average\n" +
                "\n" +
                "FROM (\n" +
                "         SELECT trw.test_run_id,\n" +
                "                atr.ending_time_ms - atr.starting_time_ms                                       as runtime_ms,\n" +
                "                atr.nr_of_steps_executed,\n" +
                "                GROUP_CONCAT(trw.widget_id ORDER BY trw.widget_id SEPARATOR '-')                as widget_combo,\n" +
                "                GROUP_CONCAT(w.widget_config_name ORDER BY w.widget_config_name SEPARATOR ', ') as widget_names\n" +
                "\n" +
                "         FROM test_run_widget trw\n" +
                "\n" +
                "                  JOIN automated_test_run atr USING (test_run_id)\n" +
                "\n" +
                "                  JOIN widget w USING (widget_id)\n" +
                "\n" +
                "         GROUP BY trw.test_run_id\n" +
                "     ) as test_run_base\n" +
                "\n" +
                "GROUP BY test_run_base.widget_combo\n" +
                "\n" +
                "ORDER BY SUM(test_run_base.nr_of_steps_executed) DESC";
        try {
            System.out.println("Calculating and inserting test results.");
            Statement statement = connection.createStatement();
            statement.execute(insertResultsQuery);

            // we calculate the median in Java, as it is much more complicated to do in MySql.
            System.out.println("Calculating the median value for steps executed");
            String fetchTestRunResultsQuery = "SELECT\n" +
                    "\ttrw.test_run_id,\n" +
                    "\tatr.nr_of_steps_executed,\n" +
                    "\tGROUP_CONCAT( trw.widget_id ORDER BY trw.widget_id SEPARATOR '-' ) AS widget_combo \n" +
                    "FROM\n" +
                    "\ttest_run_widget trw\n" +
                    "\tJOIN automated_test_run atr USING ( test_run_id )\n" +
                    "\tJOIN widget w USING ( widget_id ) \n" +
                    "GROUP BY\n" +
                    "\ttrw.test_run_id";
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(fetchTestRunResultsQuery);
            List<TestRunStepsResultPojo> testRunStepsResultPojos = new ArrayList<>();
            while (resultSet.next()) {
                TestRunStepsResultPojo pojo = new TestRunStepsResultPojo();
                pojo.setNrOfStepsExecuted(resultSet.getInt(2));
                pojo.setComboIdentifier(resultSet.getString(3));
                testRunStepsResultPojos.add(pojo);
            }

            System.out.println("Nr of test run results found: " + testRunStepsResultPojos.size());

            // group the test results by the widget combo id
            Map<String, List<TestRunStepsResultPojo>> comboResultMapping = testRunStepsResultPojos.stream().collect(Collectors.groupingBy(TestRunStepsResultPojo::getComboIdentifier));
            String updateTestResultQuery = "UPDATE test_run_result \n" +
                    "SET steps_executed_median = ? \n" +
                    "WHERE\n" +
                    "\twidget_combo = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateTestResultQuery);
            comboResultMapping.keySet().stream().forEach(widgetCombo -> {
                double stepsExecutedMedian = calculateMedian(comboResultMapping.get(widgetCombo).stream().map(TestRunStepsResultPojo::getNrOfStepsExecuted).collect(Collectors.toList()));
                // update the test result table and insert the median

                try {
                    preparedStatement.setDouble(1, stepsExecutedMedian);
                    preparedStatement.setString(2, widgetCombo);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double calculateMedian(List<Integer> input) {
        int[] inputValues = input.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(inputValues);
        int nrOfEntries = inputValues.length;
        if ((nrOfEntries % 2) != 0) {
            // uneven number, pick the entry in the middle
            return inputValues[nrOfEntries / 2];
        }

        return (double) (inputValues[(nrOfEntries - 1) / 2] + inputValues[nrOfEntries / 2]) / 2.0;
    }

    public void analysisOfTopResults(int limit) {
        // this will examine the top results, as determined by total steps executed (supposing an equal nr of tests)
        // and will print how often each widget is used in these top results
        String query = "SELECT\n" +
                "\t* \n" +
                "FROM\n" +
                "\ttest_run_result \n" +
                "ORDER BY\n" +
                "\tsteps_executed_total DESC \n" +
                "\tLIMIT " + limit;

        String getWidgetQuery = "Select * from widget";

        Connection connection = getConnection();
        try {
            // get the widgets first
            Map<Integer, String> widgets = new HashMap<>();
            Statement widgetStatement = connection.createStatement();
            ResultSet widgetResultSet = widgetStatement.executeQuery(getWidgetQuery);
            while(widgetResultSet.next()) {
                widgets.put(widgetResultSet.getInt(1), widgetResultSet.getString(2));
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // now, we want to break up the combo id string into separate parts
            Map<Integer, Integer> counter = new HashMap<>();
            while(resultSet.next()) {
                String widgetComboIds = resultSet.getString(1);
                String[] ids = widgetComboIds.trim().split("-");
                Arrays.stream(ids).mapToInt(Integer::parseInt).forEach(i -> counter.merge(i, 1, Integer::sum));
            }

            // so now each id is mapped to the number of occurrences.
            // order these from high to low by nr of occurrence
            Map<Integer, List<Integer>> nrToId = new HashMap<>();
            counter.keySet().forEach(widgetId -> {
                int occurrences = counter.get(widgetId);
                if (!nrToId.containsKey(occurrences)) {
                    nrToId.put(occurrences, new ArrayList<>());
                }
                nrToId.get(occurrences).add(widgetId);
            });

            Stream<Integer> counterDesc = counter.values().stream().sorted(Comparator.reverseOrder()).distinct();

            counterDesc.forEach(occurrences -> {
                nrToId.get(occurrences).forEach(widgetId -> System.out.println(widgets.get(widgetId) + " : " + occurrences + " occurrences."));
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
