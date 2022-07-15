package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.io.File;
import java.io.IOException;

public class StateSettingsController extends SettingsEditController {

    private Button selectDir;

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
        TextField dataStoreDB = (TextField) view.lookup("#dataStoreDB");
        ComboBox<String> dataStoreType = (ComboBox<String>) view.lookup("#dataStoreType");
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
    }
}
