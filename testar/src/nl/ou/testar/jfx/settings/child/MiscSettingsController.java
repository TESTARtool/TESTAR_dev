package nl.ou.testar.jfx.settings.child;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fruit.Pair;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class MiscSettingsController extends ChildSettingsController {
    public MiscSettingsController(Settings settings) {
        super("", settings);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Misc", "jfx/settings_misc.fxml");
            putSection(view, "Files on SUT startup", "jfx/settings_startup.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableView<Pair<String, String>> copyTable = (TableView) view.lookup("#copyTable");

        TableColumn<Pair<String, String>, String> copyFromColumn = (TableColumn<Pair<String, String>, String>) copyTable.getColumns().get(0);
        TableColumn<Pair<String, String>, String> copyToColumn = (TableColumn<Pair<String, String>, String>) copyTable.getColumns().get(1);
        copyFromColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().left()));
        copyToColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().right()));


        copyTable.getItems().addAll(settings.get(ConfigTags.CopyFromTo));

        TableView<String> deleteTable = (TableView) view.lookup("#deleteTable");

        TableColumn<String, String> deleteColumn = (TableColumn<String, String>) deleteTable.getColumns().get(0);
        deleteColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<String>(data.getValue()));

        deleteTable.getItems().addAll(settings.get(ConfigTags.Delete));
    }
}
