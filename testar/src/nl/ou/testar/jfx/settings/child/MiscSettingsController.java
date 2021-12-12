package nl.ou.testar.jfx.settings.child;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import org.fruit.Pair;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MiscSettingsController extends SettingsEditController {
    private Label labelOutDir;
    private Label labelTmpDir;

    private TableView<Pair<String, String>> copyTable;
    private TableView<String> deleteTable;

    private String outPath = settings.get(ConfigTags.OutputDir, "");
    private String tmpPath = settings.get(ConfigTags.TempDir, "");

    public MiscSettingsController(Settings settings, String settingsPath) {
        super("", settings, settingsPath);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);

//        try {
//            putSection(view, "Stub", "jfx/settings_stub.fxml");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (true) return;

        try {
            putSection(view, "Misc", "jfx/settings_misc.fxml", false);
            putSection(view, "Files on SUT startup", "jfx/settings_startup.fxml", true);
            if(isWebDriver(settings)) {
                putSection(view, "Web settings", "jfx/settings_web.fxml", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button btnSelectOutDir = (Button) view.lookup("#btnSelectOutDir");
        Button btnSelectTmpDir = (Button) view.lookup("#btnSelectTmpDir");
        Button btnClearOut = (Button) view.lookup("#btnClearOut");
        Button btnClearTmp =  (Button) view.lookup("#btnClearTmp");
        labelOutDir = (Label) view.lookup("#labelOutDir");
        labelTmpDir = (Label) view.lookup("#labelTmpDir");

        btnClearOut.setOnAction(event -> {
            outPath = null;
            labelOutDir.setText("Not selected");
            btnClearOut.setDisable(true);
        });

        btnClearTmp.setOnAction(event -> {
            tmpPath = null;
            labelTmpDir.setText("Not selected");
            btnClearTmp.setDisable(true);
        });

        if (outPath != null && outPath.length() > 0) {
            labelOutDir.setText("Selected: ".concat(outPath));
            btnClearOut.setDisable(false);
        }
        else {
            labelOutDir.setText("Not selected");
            btnClearOut.setDisable(true);
        }

        if (tmpPath != null && tmpPath.length() > 0) {
            labelTmpDir.setText("Selected: ".concat(tmpPath));
            btnClearTmp.setDisable(false);
        }
        else {
            labelTmpDir.setText("Not selected");
            btnClearTmp.setDisable(true);
        }

        btnSelectOutDir.setOnAction(event -> {
            DirectoryChooser outChooser = new DirectoryChooser();
            if (outPath != null && outPath.length() > 0) {
                File outDir = new File(outPath);
                if (outDir != null) {
                    outChooser.setInitialDirectory(outDir);
                }
                File newOutDir = outChooser.showDialog(view.getScene().getWindow());
                if (newOutDir != null) {
                    outPath = newOutDir.getAbsolutePath();
                    labelOutDir.setText("Selected: ".concat(outPath));
                    btnClearOut.setDisable(false);
                }
            }
        });

        btnSelectTmpDir.setOnAction(event -> {
            DirectoryChooser tmpChooser = new DirectoryChooser();
            if (tmpPath != null && tmpPath.length() > 0) {
                File tmpDir = new File(tmpPath);
                if (tmpDir != null) {
                    tmpChooser.setInitialDirectory(tmpDir);
                }
                File newTmpDir = tmpChooser.showDialog(view.getScene().getWindow());
                if (newTmpDir != null) {
                    tmpPath = newTmpDir.getAbsolutePath();
                    labelTmpDir.setText("Selected: ".concat(tmpPath));
                    btnClearTmp.setDisable(false);
                }
            }
        });

        copyTable = (TableView) view.lookup("#copyTable");

        TableColumn<Pair<String, String>, String> copyFromColumn = (TableColumn<Pair<String, String>, String>) copyTable.getColumns().get(0);
        TableColumn<Pair<String, String>, String> copyToColumn = (TableColumn<Pair<String, String>, String>) copyTable.getColumns().get(1);
        TableColumn<Pair<String, String>, Void> copyToolsColumn = (TableColumn<Pair<String, String>, Void>) copyTable.getColumns().get(2);

        copyToolsColumn.setCellFactory(column -> {
            final TableCell<Pair<String, String>, Void> cell = new TableCell<Pair<String, String>, Void>() {

                @Override
                public void updateItem(Void item, boolean empty) {
                    final int index = getIndex();
                    final boolean isLast = (index == copyTable.getItems().size() - 1);
                    final Button btnEdit = new Button(isLast ? "Add" : "Edit");
                    btnEdit.setStyle("-fx-font-size:10");

                    btnEdit.setOnAction(event -> {
                        Stage stage = (Stage) view.getScene().getWindow();

                        Pair<String, String> itemValue = copyTable.getItems().get(index);
                        FileChooser srcChooser = new FileChooser();
                        if (itemValue.left().length() > 0) {
                            File sourceFile = new File(itemValue.left());
                            if (sourceFile != null) {
                                srcChooser.setInitialFileName(sourceFile.getAbsolutePath());
                            }
                        }
                        String originalTitle = stage.getTitle();
                        stage.setTitle("Select source file");

                        File newSourceFile = srcChooser.showOpenDialog(view.getScene().getWindow());

                        if (newSourceFile != null) {
                            DirectoryChooser dstChooser = new DirectoryChooser();
                            if (itemValue.right().length() > 0) {
                                File destinatlonFile = new File(itemValue.right());
                                if (destinatlonFile != null) {
                                    dstChooser.setInitialDirectory(destinatlonFile);
                                }
                            }
                            stage.setTitle("Select destination");

                            File newDestinationFile = dstChooser.showDialog(view.getScene().getWindow());

                            if (newDestinationFile != null) {
                                copyTable.getItems().set(index,
                                        new Pair<>(newSourceFile.getAbsolutePath(), newDestinationFile.getAbsolutePath()));

                                if (isLast) {
                                    copyTable.getItems().add(new Pair<>("", ""));
                                }
                            }
                        }

                        stage.setTitle(originalTitle);
                    });

                    if (isLast) {
                        setGraphic(btnEdit);
                    }
                    else {
                        final Button btnDelete = new Button("Delete");
                        btnDelete.setStyle("-fx-font-size:10");
                        btnDelete.setOnAction(event -> {
                            copyTable.getItems().remove(index);
                        });

                        setGraphic(empty ? null : new HBox(btnEdit, btnDelete));
                    }
                }
            };
            return cell;
        });

        copyFromColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().left()));
        copyToColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().right()));

        copyTable.getItems().addAll(settings.get(ConfigTags.CopyFromTo));
        copyTable.getItems().add(new Pair<String, String>("", ""));

        deleteTable = (TableView) view.lookup("#deleteTable");

        TableColumn<String, String> deleteColumn = (TableColumn<String, String>) deleteTable.getColumns().get(0);
        TableColumn<String, Void> deleteToolsColumn = (TableColumn<String, Void>) deleteTable.getColumns().get(1);
        deleteColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));

        deleteTable.getItems().addAll(settings.get(ConfigTags.Delete, Collections.emptyList()));
        deleteTable.getItems().add("");

        deleteColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        deleteToolsColumn.setCellFactory(column -> {
            final TableCell<String, Void> cell = new TableCell<String, Void>() {

                @Override
                public void updateItem(Void item, boolean empty) {
                    final int index = getIndex();
                    final boolean isLast = (index == deleteTable.getItems().size() - 1);

                    final Button btnEdit = new Button(isLast ? "Add" : "Edit");

                    btnEdit.setStyle("-fx-font-size:10");

                    btnEdit.setOnAction(event -> {
                        String itemValue = deleteTable.getItems().get(index);
                        FileChooser deleteChooser = new FileChooser();
                        deleteChooser.setTitle("Delete file");
                        if (itemValue.length() > 0) {
                            File file = new File(itemValue);
                            if (file != null) {
                                deleteChooser.setInitialFileName(file.getAbsolutePath());
                            }
                        }

                        File newFile = deleteChooser.showOpenDialog(view.getScene().getWindow());
                        if (newFile != null) {
                            deleteTable.getItems().set(index, newFile.getAbsolutePath());

                            if (index == deleteTable.getItems().size() - 1) {
                                deleteTable.getItems().add("");
                            }
                        }
                    });

                    if (isLast) {
                        setGraphic(btnEdit);
                    }
                    else {
                        final Button btnDelete = new Button("Delete");
                        btnDelete.setStyle("-fx-font-size:10");
                        btnDelete.setOnAction(event -> {
                            deleteTable.getItems().remove(index);
                        });
                        setGraphic(empty ? null : new HBox(btnEdit, btnDelete));
                    }
                }
            };
            return cell;
        });

        if(isWebDriver(settings)) {
            CheckBox webFollowLinks = (CheckBox) view.lookup("#webFollowLinks");
            CheckBox webBrowserFullscreen = (CheckBox) view.lookup("#webBrowserFullscreen");
            CheckBox webSwitchNewTabs = (CheckBox) view.lookup("#webSwitchNewTabs");

            addBinding(webFollowLinks, ConfigTags.FollowLinks, ConfigBinding.GenericType.CHECK_BOX);
            addBinding(webBrowserFullscreen, ConfigTags.BrowserFullScreen, ConfigBinding.GenericType.CHECK_BOX);
            addBinding(webSwitchNewTabs, ConfigTags.SwitchNewTabs, ConfigBinding.GenericType.CHECK_BOX);
        }
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (super.needsSave(settings)) {
            return true;
        }
        if (outPath != null && !outPath.equals(settings.get(ConfigTags.OutputDir, ""))) {
            return true;
        }
        if (tmpPath != null && !tmpPath.equals(settings.get(ConfigTags.TempDir, ""))) {
            return  true;
        }
        if (!itemsToCopy().equals(settings.get(ConfigTags.CopyFromTo, Collections.emptyList()))) {
            return true;
        }
        if (!itemsToDelete().equals(settings.get(ConfigTags.Delete, Collections.emptyList()))) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        super.save(settings);
        settings.set(ConfigTags.OutputDir, outPath);
        settings.set(ConfigTags.TempDir, tmpPath);
        settings.set(ConfigTags.CopyFromTo, itemsToCopy());
        settings.set(ConfigTags.Delete, itemsToDelete());
    }

    private boolean isWebDriver(Settings settings) {
        return settings.get(ConfigTags.ProtocolClass, "").contains("webdriver");
    }

    private List<Pair<String, String>> itemsToCopy() {
        return copyTable.getItems().stream().filter(item -> item.left().length() > 0 && item.right().length() > 0).collect(Collectors.toList());
    }

    private List<String> itemsToDelete() {
        return deleteTable.getItems().stream().filter(item -> item.length() > 0).collect(Collectors.toList());
    }
}
