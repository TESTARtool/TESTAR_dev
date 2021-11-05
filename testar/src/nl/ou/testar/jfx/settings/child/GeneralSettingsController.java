package nl.ou.testar.jfx.settings.child;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import nl.ou.testar.jfx.utils.DisplayModeWrapper;
import nl.ou.testar.jfx.utils.GeneralSettings;
import org.apache.commons.lang.math.NumberUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GeneralSettingsController extends ChildSettingsController {

    private DisplayMode availableDisplayModes[];
    private int displayModeSelectedIndex;

    private ComboBox sutComboBox;
    private ComboBox<DisplayModeWrapper> resolutionComboBox;
    private TextField webDriverPathField;
    private TextField locationInputField;
    private TextField numSequencesField;
    private TextField numActionsField;
    private CheckBox alwaysCompileCheckBox;

    private GeneralSettings generalSettings;

    public GeneralSettingsController(Settings settings, String settingsPath) {
        super("General settings", settings, settingsPath);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "General settings", "jfx/settings_general.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GraphicsDevice dev = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        availableDisplayModes = dev.getDisplayModes();


        sutComboBox = (ComboBox) view.lookup("#sutConnectorSelection");
        sutComboBox.getItems().addAll(
                Settings.SUT_CONNECTOR_CMDLINE,
                Settings.SUT_CONNECTOR_PROCESS_NAME,
                Settings.SUT_CONNECTOR_WINDOW_TITLE,
                Settings.SUT_CONNECTOR_WEBDRIVER
        );

        resolutionComboBox = (ComboBox<DisplayModeWrapper>) view.lookup("#resolutionSelection");
        resolutionComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(availableDisplayModes).map(mode -> new DisplayModeWrapper(mode, true))
                        .collect(Collectors.toList())
        ));
        resolutionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            generalSettings.setDisplayMode(newValue.getMode());
        });
        DisplayMode currentDisplayMode = dev.getDisplayMode();
        System.out.println(String.format("Current dislpay mode: %dx%d+%d+%d", currentDisplayMode.getWidth(), currentDisplayMode.getHeight(), currentDisplayMode.getBitDepth(), currentDisplayMode.getRefreshRate()));
        int index = 0;
        displayModeSelectedIndex = 0;
        generalSettings = new GeneralSettings(settings.get(ConfigTags.SUTConnectorValue, ""));

        DisplayMode selectedDisplayMode = generalSettings.getDisplayMode();
        if (selectedDisplayMode == null) {
            selectedDisplayMode = dev.getDisplayMode();
        }
        int selectedWidth = selectedDisplayMode.getWidth();
        int selectedHeight = selectedDisplayMode.getHeight();
        int displayModeIndex = 0;
        for (DisplayMode displayMode : availableDisplayModes) {
            if (displayMode.getWidth() == selectedWidth && displayMode.getHeight() == selectedHeight) {
                break;
            }
            displayModeIndex++;
        }
        if (displayModeIndex == availableDisplayModes.length) {
            resolutionComboBox.getItems().add(new DisplayModeWrapper(selectedDisplayMode, false));
        }
        resolutionComboBox.getSelectionModel().select(displayModeIndex);

        webDriverPathField = (TextField) view.lookup("#driverPath");
        webDriverPathField.setText(generalSettings.getDriver());
        webDriverPathField.textProperty().addListener((observable, oldValue, newValue) -> {
            generalSettings.setDriver(newValue);
        });

        locationInputField = (TextField) view.lookup("#locationInput");
        locationInputField.setText(generalSettings.getLocation());
        locationInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            generalSettings.setLocation(newValue);
        });

        FileChooser driverChooser = new FileChooser();
        FileChooser locationChooser = new FileChooser();

        Button btnSelectDriver = (Button) view.lookup("#btnSelectDriver");
        Button btnSelectLocation = (Button) view.lookup("#btnSelectLocation");

        btnSelectDriver.setOnAction(event -> {
            File driverFile = driverChooser.showOpenDialog(view.getScene().getWindow());
            webDriverPathField.setText(driverFile.getAbsolutePath());
        });

        btnSelectLocation.setOnAction(event -> {
            File locationFile = locationChooser.showOpenDialog(view.getScene().getWindow());
            locationInputField.setText(locationFile.toURI().toString());
        });

        numSequencesField = (TextField) view.lookup("#numSequences");
        numSequencesField.setPromptText("0");
        limitInputToPattern(numSequencesField, ChildSettingsController.INPUT_PATTERN_INTEGER);
        numActionsField = (TextField) view.lookup("#numActions");
        numActionsField.setPromptText("0");
        limitInputToPattern(numActionsField, ChildSettingsController.INPUT_PATTERN_INTEGER);
        alwaysCompileCheckBox = (CheckBox) view.lookup("#alwaysCompile");
        alwaysCompileCheckBox.setSelected(settings.get(ConfigTags.AlwaysCompile, false));

        sutComboBox.setValue(settings.get(ConfigTags.SUTConnector));

        numSequencesField.setText(settings.get(ConfigTags.Sequences, 0).toString());
        numActionsField.setText(settings.get(ConfigTags.SequenceLength, 0).toString());
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (!generalSettings.toString().equals(settings.get(ConfigTags.SUTConnectorValue, ""))) {
            return true;
        }
        if (!sutComboBox.getValue().toString().equals(settings.get(ConfigTags.SUTConnector, ""))) {
            return true;
        }
        if (!numSequencesField.getText().equals(settings.get(ConfigTags.Sequences, 0).toString())) {
            return true;
        }
        if (!numActionsField.getText().equals(settings.get(ConfigTags.SequenceLength, 0).toString())) {
            return true;
        }
        if (!alwaysCompileCheckBox.isSelected() == settings.get(ConfigTags.AlwaysCompile, false)) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        settings.set(ConfigTags.SUTConnectorValue, generalSettings.toString());
        settings.set(ConfigTags.SUTConnector, sutComboBox.getValue().toString());
        settings.set(ConfigTags.Sequences, NumberUtils.toInt(numSequencesField.getText(), 0));
        settings.set(ConfigTags.SequenceLength, NumberUtils.toInt(numActionsField.getText(), 0));
        settings.set(ConfigTags.AlwaysCompile, alwaysCompileCheckBox.isSelected());
    }
}
