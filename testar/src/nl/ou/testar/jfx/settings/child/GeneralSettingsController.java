package nl.ou.testar.jfx.settings.child;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import nl.ou.testar.jfx.utils.DisplayModeWrapper;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GeneralSettingsController extends ChildSettingsController {

    private DisplayMode availableDisplayModes[];
    private int displayModeSelectedIndex;

    public GeneralSettingsController(Settings settings) {
        super("General settings", settings);
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


        ComboBox sutComboBox = (ComboBox) view.lookup("#sutConnectorSelection");
        sutComboBox.getItems().addAll(
                Settings.SUT_CONNECTOR_CMDLINE,
                Settings.SUT_CONNECTOR_PROCESS_NAME,
                Settings.SUT_CONNECTOR_WINDOW_TITLE,
                Settings.SUT_CONNECTOR_WEBDRIVER
        );

        ComboBox<DisplayModeWrapper> resolutionComboBox = (ComboBox<DisplayModeWrapper>) view.lookup("#resolutionSelection");
//        SingleSelectionModel<DisplayMode> resolutionSelectionModel = new SingleSelectionModel<DisplayMode>() {
//            @Override
//            protected DisplayMode getModelItem(int index) {
//                return availableDisplayModes[index];
//            }
//
//            @Override
//            protected int getItemCount() {
//                return availableDisplayModes.length;
//            }
//        };
//        resolutionComboBox.setSelectionModel(resolutionSelectionModel);
        resolutionComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(availableDisplayModes).map(DisplayModeWrapper::new).collect(Collectors.toList())
        ));
        DisplayMode currentDisplayMode = dev.getDisplayMode();
        int index = 0;
        displayModeSelectedIndex = 0;
        for (DisplayMode displayMode : availableDisplayModes) {
//            resolutionComboBox.getItems().add(String.format("%dx%d", displayMode.getWidth(), displayMode.getHeight()));
            if (displayMode.equals(currentDisplayMode)) {
                resolutionComboBox.getSelectionModel().select(index);
//                resolutionComboBox.setValue("Skunk");
//                displayModeSelectedIndex = index;
            }
            index++;
        }
        System.out.println(String.format("Current display mode: %dx%d", currentDisplayMode.getWidth(), currentDisplayMode.getHeight()));
        System.out.println("General settings: " + settings.get(ConfigTags.OverrideWebDriverDisplayScale));
//        resolutionComboBox.setId(String.format("%dx%d", currentDisplayMode.getWidth(), currentDisplayMode.getHeight()));

//        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment()
//                .getScreenDevices();
//        for (int i = 0; i < devices.length; i++) {
////            GraphicsDevice dev = devices[i];
////            System.out.println("device " + i);
//            DisplayMode[] modes = dev.getDisplayModes();
//            for (int j = 0; j < modes.length; j++) {
//                DisplayMode m = modes[j];
//                System.out.println(" " + j + ": " + m.getWidth() + " x " + m.getHeight());
//            }
//        }
    }
}
