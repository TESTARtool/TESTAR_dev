package nl.ou.testar.jfx.settings.child;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.Settings;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.webserver.JettyServer;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import java.io.File;
import java.io.IOException;


public class StateSettingsController extends SettingsEditController {

    private Button selectDir;
    public TextField dataStoreDB;
    public ComboBox<String> dataStoreType;

    private String outputDir;
    public StateSettingsController(Settings settings, String settingsPath) {
        super("", settings, settingsPath);
    }


    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "State", "jfx/settings_state.fxml");
            putSection(view, "Widgets", "jfx/settings_widgets.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextField dataStore = (TextField) view.lookup("#dataStore");
        dataStoreDB = (TextField) view.lookup("#dataStoreDB");
        dataStoreType = (ComboBox<String>) view.lookup("#dataStoreType");
        TextField dataStoreUser = (TextField) view.lookup("#dataStoreUser");
        TextField dataStoreServer = (TextField) view.lookup("#dataStoreServer");
        TextField dataStorePassword = (TextField) view.lookup("#dataStorePassword");
        TextField dataStoreDirectory = (TextField) view.lookup("#dataStoreDirectory");
        ComboBox<String> dataStoreMode = (ComboBox<String>) view.lookup("#dataStoreMode");
        selectDir = (Button) view.lookup("#selectDir");

        CheckBox storeWidgets = (CheckBox) view.lookup("#storeWidgets");
        CheckBox accessbridgeEnabled = (CheckBox) view.lookup("#accessbridgeEnabled");
        CheckBox resetDatabase = (CheckBox) view.lookup("#resetDatabase");

        dataStoreType.getItems().addAll("remote", "plocal", "docker");
        dataStoreMode.getItems().addAll("none", "instant", "delayed", "hybrid");

        addBinding(dataStore, ConfigTags.DataStore, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(dataStoreDB, ConfigTags.DataStoreDB, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(dataStoreType, ConfigTags.DataStoreType, ConfigBinding.GenericType.COMBO_BOX);
        addBinding(dataStoreUser, ConfigTags.DataStoreUser, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(dataStoreServer, ConfigTags.DataStoreServer, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(dataStorePassword, ConfigTags.DataStorePassword, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(dataStoreMode, ConfigTags.DataStoreMode, ConfigBinding.GenericType.COMBO_BOX);
        addBinding(storeWidgets, ConfigTags.StateModelStoreWidgets, ConfigBinding.GenericType.CHECK_BOX);
        addBinding(accessbridgeEnabled, ConfigTags.AccessBridgeEnabled, ConfigBinding.GenericType.CHECK_BOX);
        addBinding(resetDatabase, ConfigTags.ResetDataStore, ConfigBinding.GenericType.CHECK_BOX);

        ConfigBinding<String> dataStoreDirectoryBinding = addBinding(dataStoreDirectory, ConfigTags.DataStoreDirectory, ConfigBinding.GenericType.FIELD_STRING);

        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + "graphs" + File.separator;

        selectDir.setOnAction(event -> {
            String path = dataStoreDirectory.getText();
            DirectoryChooser chooser = new DirectoryChooser();
            if (path.length() > 0) {
                File dir = new File(path);
                if (dir != null) {
                    chooser.setInitialDirectory(dir);
                }
            }
            final File newDir = chooser.showDialog(view.getScene().getWindow());
            if (newDir != null) {
                dataStoreDirectoryBinding.setValue(newDir.getAbsolutePath());
            }
        });

        Button btnAnalysis = (Button) view.lookup("#btnAnalysis");
        btnAnalysis.setOnAction(event -> {
            startAnalysis();
        });
    }

    public void startAnalysis(){
        System.out.println("START ANALYSIS");
        openServer();
    }

    // this helper method will start a jetty integrated server and show the model listings page
    public void openServer() {
        try {
//            // create a config object for the orientdb database connection info
            Config config = new Config();
            config.setConnectionType(settings.get(ConfigTags.DataStoreType));
            config.setServer(settings.get(ConfigTags.DataStoreServer));
            config.setDatabase(settings.get(ConfigTags.DataStoreDB));
            config.setUser(settings.get(ConfigTags.DataStoreUser));
            config.setPassword(settings.get(ConfigTags.DataStorePassword));
            config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory));
            System.out.println(settings.get(ConfigTags.DataStoreType));
            System.out.println(settings.get(ConfigTags.DataStoreServer));
            System.out.println(settings.get(ConfigTags.DataStoreDB));
            System.out.println(settings.get(ConfigTags.DataStoreUser));
            System.out.println(settings.get(ConfigTags.DataStorePassword));
            System.out.println(settings.get(ConfigTags.DataStoreDirectory));
            System.out.println(outputDir);
            AnalysisManager analysisManager = new AnalysisManager(config, outputDir);
            System.out.println("SERVER");
            JettyServer jettyServer = new JettyServer();
            jettyServer.start(outputDir, analysisManager);
        } catch (ODatabaseException de) {
            // There it can be a root cause that indicates that the IP address or server is not running
            if(de.getCause() != null && de.getCause().getMessage() != null && de.getCause().getMessage().contains("Cannot create a connection")) {
                System.out.println(de.getCause().getMessage());
                return;
            }
            // If the database does not exists
            else if(de.getMessage() != null && de.getMessage().contains("Cannot open database")) {
                System.out.println(de.getMessage());
                return;
            }
            // Not expected exception, throw trace in the console
            else {
                de.printStackTrace();
                return;
            }
        } catch (OSecurityAccessException se) {
            // If the user credential are wrong
            if(se.getMessage() != null && se.getMessage().contains("User or password not valid")) {
                System.out.println(se.getMessage());
                return;
            }
            // Not expected exception, throw trace in the console
            else {
                se.printStackTrace();
                return;
            }
        } catch (IOException e) {
            // If the exception is because the server is already running, just catch and connect
            if(e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("Address already in use")) {
                System.out.println(e.getCause().getMessage());
                // Continue and try to open the browser to the running server
            } else {
//                label14.setText("Please check your connection credentials.");
                e.printStackTrace();
                // Something wrong with the database connection, return because we don't want to open the browser
                return;
            }
        } catch (Exception e) {
            // the plain Exception is coming from 3rd party code
//            label14.setText("Please check your connection credentials.");
            e.printStackTrace();
            // Something wrong with the database connection, return because we don't want to open the browser
            return;
        }
        System.out.println("DONE");
//
//        openBrowser();
    }

}
