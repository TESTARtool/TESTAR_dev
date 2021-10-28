package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.File;
import java.io.IOException;

public class StateSettingsController extends ChildSettingsController {
    private TextField dataStore;
    private TextField dataStoreDB;
    private ComboBox<String> dataStoreType;
    private TextField dataStoreUser;
    private TextField dataStoreServer;
    private TextField dataStorePassword;
    private TextField dataStoreDirectory;
    private ComboBox<String> dataStoreMode;
    private Button selectDir;

    private CheckBox storeWidgets;
    private CheckBox accessbridgeEnabled;
    private CheckBox resetDatabase;

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

        dataStore = (TextField) view.lookup("#dataStore");
        dataStoreDB = (TextField) view.lookup("#dataStoreDB");
        dataStoreType = (ComboBox<String>) view.lookup("#dataStoreType");
        dataStoreUser = (TextField) view.lookup("#dataStoreUser");
        dataStoreServer = (TextField) view.lookup("#dataStoreServer");
        dataStorePassword = (TextField) view.lookup("#dataStorePassword");
        dataStoreDirectory = (TextField) view.lookup("#dataStoreDirectory");
        dataStoreMode = (ComboBox<String>) view.lookup("#dataStoreMode");
        selectDir = (Button) view.lookup("#selectDir");

        storeWidgets = (CheckBox) view.lookup("#storeWidgets");
        accessbridgeEnabled = (CheckBox) view.lookup("#accessbridgeEnabled");
        resetDatabase = (CheckBox) view.lookup("#resetDatabase");

        dataStoreType.getItems().addAll("remote", "plocal", "docker");
        dataStoreMode.getItems().addAll("none", "instant", "delayed", "hybrid");

        dataStore.setText(settings.get(ConfigTags.DataStore, ""));
        dataStoreDB.setText(settings.get(ConfigTags.DataStoreDB, ""));
        dataStoreType.setValue(settings.get(ConfigTags.DataStoreType, ""));
        dataStoreUser.setText(settings.get(ConfigTags.DataStoreUser, ""));
        dataStoreServer.setText(settings.get(ConfigTags.DataStoreServer, ""));
        dataStorePassword.setText(settings.get(ConfigTags.DataStorePassword, ""));
        dataStoreDirectory.setText(settings.get(ConfigTags.DataStoreDirectory, ""));
        dataStoreMode.setValue(settings.get(ConfigTags.DataStoreMode, ""));

        storeWidgets.setSelected(settings.get(ConfigTags.StateModelStoreWidgets, false));
        accessbridgeEnabled.setSelected(settings.get(ConfigTags.AccessBridgeEnabled, false));
        resetDatabase.setSelected(settings.get(ConfigTags.ResetDataStore, false));

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
                dataStoreDirectory.setText(newDir.getAbsolutePath());
            }
        });
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (!dataStore.getText().equals(settings.get(ConfigTags.DataStore, ""))) {
            return true;
        }
        if (!dataStoreDB.getText().equals(settings.get(ConfigTags.DataStoreDB, ""))) {
            return true;
        }
        if (!dataStoreType.getValue().equals(settings.get(ConfigTags.DataStoreType, ""))) {
            return true;
        }
        if (!dataStoreUser.getText().equals(settings.get(ConfigTags.DataStoreUser, ""))) {
            return true;
        }
        if (!dataStoreServer.getText().equals(settings.get(ConfigTags.DataStoreServer, ""))) {
            return true;
        }
        if (!dataStorePassword.getText().equals(settings.get(ConfigTags.DataStorePassword, ""))) {
            return true;
        }
        if (!dataStoreDirectory.getText().equals(settings.get(ConfigTags.DataStoreDirectory, ""))) {
            return true;
        }
        if (!dataStoreMode.getValue().equals(settings.get(ConfigTags.DataStoreMode, ""))) {
            return true;
        }

        if (storeWidgets.isSelected() != settings.get(ConfigTags.StateModelStoreWidgets, false)) {
            return true;
        }
        if (accessbridgeEnabled.isSelected() != settings.get(ConfigTags.AccessBridgeEnabled, false)) {
            return true;
        }
        if (resetDatabase.isSelected() != settings.get(ConfigTags.ResetDataStore, false)) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        settings.set(ConfigTags.DataStore, dataStore.getText());
        settings.set(ConfigTags.DataStoreDB, dataStoreDB.getText());
        settings.set(ConfigTags.DataStoreType, dataStoreType.getValue());
        settings.set(ConfigTags.DataStoreUser, dataStoreUser.getText());
        settings.set(ConfigTags.DataStoreServer, dataStoreServer.getText());
        settings.set(ConfigTags.DataStorePassword, dataStorePassword.getText());
        settings.set(ConfigTags.DataStoreDirectory, dataStoreDirectory.getText());
        settings.set(ConfigTags.DataStoreMode, dataStoreMode.getValue());

        settings.set(ConfigTags.StateModelStoreWidgets, storeWidgets.isSelected());
        settings.set(ConfigTags.AccessBridgeEnabled, accessbridgeEnabled.isSelected());
        settings.set(ConfigTags.ResetDataStore, resetDatabase.isSelected());
    }
}
