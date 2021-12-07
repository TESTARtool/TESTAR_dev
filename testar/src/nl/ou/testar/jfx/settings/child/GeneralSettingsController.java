package nl.ou.testar.jfx.settings.child;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import nl.ou.testar.jfx.settings.bindings.AbstractConfigBinding;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import nl.ou.testar.jfx.settings.bindings.ConfigBindingException;
import nl.ou.testar.jfx.settings.bindings.control.ComboBoxBinding;
import nl.ou.testar.jfx.settings.bindings.control.ControlBinding;
import nl.ou.testar.jfx.settings.bindings.control.StringFieldBinding;
import nl.ou.testar.jfx.settings.bindings.data.DataSource;
import nl.ou.testar.jfx.utils.DisplayModeWrapper;
import nl.ou.testar.jfx.utils.GeneralSettings;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GeneralSettingsController extends SettingsEditController {

    private DisplayMode availableDisplayModes[];

    private GeneralSettings generalSettings;

    public GeneralSettingsController(Settings settings, String settingsPath) {
        super("General settings", settings, settingsPath);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        Parent generalSection = null;
        try {
            generalSection = putSection(view, "General settings", "jfx/settings_general.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("General settings loaded");

        GraphicsDevice dev = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        availableDisplayModes = dev.getDisplayModes();

//        TextField webDriverPathField = (TextField) generalSection.lookup("#driverPath");
//        TextField locationInputField = (TextField) generalSection.lookup("#locationInput");
//
//        Button btnSelectDriver = (Button) generalSection.lookup("#btnSelectDriver");
//        Button btnSelectLocation = (Button) generalSection.lookup("#btnSelectLocation");
//
//        TextField numSequencesField = (TextField) generalSection.lookup("#numSequences");
//        TextField numActionsField = (TextField) generalSection.lookup("#numActions");
//        CheckBox alwaysCompileCheckBox = (CheckBox) generalSection.lookup("#alwaysCompile");

//        if (webDriverPathField == null) {
//            System.out.println("Failed to lookup web driver path field");
//        }
//        if (locationInputField == null) {
//            System.out.println("Failed to lookup location input field");
//        }
//        if (btnSelectDriver == null) {
//            System.out.println("Failed to lookup driver selection button");
//        }
//        if (btnSelectLocation == null) {
//            System.out.println("Failed to lookup location selection button");
//        }
//        if (numSequencesField == null) {
//            System.out.println("Failed to lookup sequence number field");
//        }
//        if (numActionsField == null) {
//            System.out.println("Failed to lookup action number field");
//        }
//        if (alwaysCompileCheckBox == null) {
//            System.out.println("Failed to lookup sa check box");
//        }

        ComboBox sutComboBox = (ComboBox) generalSection.lookup("#sutConnectorSelection");
        if (sutComboBox == null) {
            System.out.println("Failed to lookup SUT combo box");
        }
        sutComboBox.getItems().addAll(
                Settings.SUT_CONNECTOR_CMDLINE,
                Settings.SUT_CONNECTOR_PROCESS_NAME,
                Settings.SUT_CONNECTOR_WINDOW_TITLE,
                Settings.SUT_CONNECTOR_WEBDRIVER
        );

        final List<DisplayModeWrapper> availableResolutions = Arrays.stream(availableDisplayModes).map(mode -> new DisplayModeWrapper(mode, true))
                .collect(Collectors.toList());
        ComboBox<DisplayModeWrapper> resolutionComboBox = (ComboBox<DisplayModeWrapper>) generalSection.lookup("#resolutionSelection");
        DisplayMode currentDisplayMode = dev.getDisplayMode();
        System.out.println(String.format("Current dislpay mode: %dx%d+%d+%d", currentDisplayMode.getWidth(), currentDisplayMode.getHeight(), currentDisplayMode.getBitDepth(), currentDisplayMode.getRefreshRate()));
        generalSettings = new GeneralSettings(settings.get(ConfigTags.SUTConnectorValue, ""));

        DisplayMode selectedDisplayMode = generalSettings.getDisplayMode();

        if (!isModeAvailable(selectedDisplayMode)) {
            availableResolutions.add(new DisplayModeWrapper(selectedDisplayMode, false));
        }
        resolutionComboBox.setItems(FXCollections.observableArrayList(availableResolutions));

        TextField webDriverPathField = (TextField) generalSection.lookup("#driverPath");
        TextField locationInputField = (TextField) generalSection.lookup("#locationInput");

        FileChooser driverChooser = new FileChooser();
        FileChooser locationChooser = new FileChooser();

        Button btnSelectDriver = (Button) generalSection.lookup("#btnSelectDriver");
        Button btnSelectLocation = (Button) generalSection.lookup("#btnSelectLocation");

        TextField numSequencesField = (TextField) generalSection.lookup("#numSequences");
        TextField numActionsField = (TextField) generalSection.lookup("#numActions");
        CheckBox alwaysCompileCheckBox = (CheckBox) generalSection.lookup("#alwaysCompile");

        AbstractConfigBinding<TextField, String> webDriverBinding = new AbstractConfigBinding<TextField, String>(webDriverPathField) {
            @Override
            protected String getTargetValue() {
                return generalSettings.getDriver();
            }

            @Override
            protected void setTargetValue(String value) {
                generalSettings.setDriver(value);
            }

            @Override
            public void onBind() {
                control.textProperty().set(getTargetValue());
                webDriverPathField.textProperty().addListener((observable, oldValue, newValue) -> {
                    setTargetValue(newValue);
                });
            }
        };

        addBinding(numSequencesField, ConfigTags.Sequences, ConfigBinding.GenericType.FIELD_INT);
        addBinding(numActionsField, ConfigTags.SequenceLength, ConfigBinding.GenericType.FIELD_INT);
        addBinding(sutComboBox, ConfigTags.SUTConnector, ConfigBinding.GenericType.COMBO_BOX);
        addBinding(alwaysCompileCheckBox, ConfigTags.AlwaysCompile, ConfigBinding.GenericType.CHECK_BOX);

        ConfigBinding.Builder<String> builder = new ConfigBinding.Builder<>();

        ControlBinding<String> webDriverControlBinding = new StringFieldBinding(webDriverPathField);
        DataSource<String> webDriverDataSource = new DataSource<String>() {
            @Override
            public Class getDataType() {
                return String.class;
            }

            @Override
            public String getData() {
                return generalSettings.getDriver();
            }

            @Override
            public void setData(String data) {
                generalSettings.setDriver(data);
            }
        };

        try {
            final ConfigBinding webDriverConfigBinding = builder
                    .withCustomControlBinding(webDriverControlBinding)
                    .withCustomDataSource(webDriverDataSource)
                    .build();

            addBinding(webDriverConfigBinding);

            btnSelectDriver.setOnAction(event -> {
                File driverFile = driverChooser.showOpenDialog(view.getScene().getWindow());
                webDriverConfigBinding.setValue(driverFile.getAbsolutePath());
            });
        } catch (ConfigBindingException e) {
            e.printStackTrace();
        }

        ControlBinding<String> locationControlBinding = new StringFieldBinding(locationInputField);
        DataSource<String> locationDataSource = new DataSource<String>() {
            @Override
            public Class getDataType() {
                return String.class;
            }

            @Override
            public String getData() {
                return generalSettings.getLocation();
            }

            @Override
            public void setData(String data) {
                generalSettings.setLocation(data);
            }
        };

        try {
            final ConfigBinding<String> locationConfigBinding = builder
                    .withCustomControlBinding(locationControlBinding)
                    .withCustomDataSource(locationDataSource)
                    .build();

            addBinding(locationConfigBinding);

            btnSelectLocation.setOnAction(event -> {
                File locationFile = locationChooser.showOpenDialog(view.getScene().getWindow());
                locationConfigBinding.setValue(locationFile.toURI().toString());
            });
        } catch (ConfigBindingException e) {
            e.printStackTrace();
        }

        ControlBinding<DisplayModeWrapper> resolutionControlBinding = new ComboBoxBinding<>(resolutionComboBox);
        DataSource<DisplayModeWrapper> resolutionDataSource = new DataSource<DisplayModeWrapper>() {
            @Override
            public Class getDataType() {
                return DisplayModeWrapper.class;
            }

            @Override
            public DisplayModeWrapper getData() {
                DisplayMode displayMode = generalSettings.getDisplayMode();
                return new DisplayModeWrapper(displayMode, isModeAvailable(displayMode));
            }

            @Override
            public void setData(DisplayModeWrapper data) {
                generalSettings.setDisplayMode(data.getMode());
            }
        };
        try {
            ConfigBinding<DisplayModeWrapper> resolutionConfigBinding = new ConfigBinding.Builder<DisplayModeWrapper>()
                    .withCustomControlBinding(resolutionControlBinding)
                    .withCustomDataSource(resolutionDataSource)
                    .build();
            addBinding(resolutionConfigBinding);
        } catch (ConfigBindingException e) {
            e.printStackTrace();
        }
    }

    private boolean isModeAvailable(DisplayMode displayMode) {
        return Arrays.stream(availableDisplayModes).anyMatch(displayMode::equals);
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (super.needsSave(settings)) {
            return true;
        }
        if (!generalSettings.toString().equals(settings.get(ConfigTags.SUTConnectorValue, ""))) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        super.save(settings);
        settings.set(ConfigTags.SUTConnectorValue, generalSettings.toString());
    }
}
