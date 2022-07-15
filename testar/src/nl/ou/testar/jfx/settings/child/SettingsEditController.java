package nl.ou.testar.jfx.settings.child;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import nl.ou.testar.jfx.core.ViewController;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import nl.ou.testar.jfx.settings.bindings.ConfigBindingException;
import org.testar.extendedsettings.ExtendedSettingsFactory;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Tag;
import org.testar.serialisation.LogSerialiser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class SettingsEditController extends ViewController {

    private String settingsPath;

    private Set<ConfigBinding> bindings;

    public SettingsEditController(String title, Settings settings, String settingsPath) {
        super(title, "jfx/settings_child.fxml", settings);
        bindings = new HashSet<>();
        this.settingsPath = settingsPath;
    }
    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        Button btnBack = (Button) view.lookup("#btnBack");
        btnBack.setOnAction(event -> {
            getNavigationController().navigateBack();
        });
        Button btnSave = (Button) view.lookup("#btnSave");
        btnSave.setOnAction(event -> {
            save(settings);
            persist(settings);
            getNavigationController().navigateBack();
        });
    }

    // TODO: put some docs
    protected Parent putSection(Parent contentView, String title, String resourcePath, boolean stretchAllowed) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/settings_section.fxml"));
        VBox sectionBox =  (VBox) loader.load();

        loader = new FXMLLoader(getClass().getClassLoader().getResource(resourcePath));
        Parent section = loader.load();
        VBox.setVgrow(section, stretchAllowed ? Priority.ALWAYS : Priority.NEVER);
        sectionBox.getChildren().add(section);

        Label titleLabel = (Label) sectionBox.lookup("#titleLabel");
        titleLabel.setText(title);

//        AnchorPane contentPane = (AnchorPane) sectionBox.lookup("#contentPane");
//        contentPane.getChildren().add(contentView);
//        AnchorPane.setLeftAnchor(contentView, 0.0);
//        AnchorPane.setTopAnchor(contentView, 0.0);
//        AnchorPane.setRightAnchor(contentView, 0.0);
//        AnchorPane.setBottomAnchor(contentView, 0.0);

        VBox contentBox = (VBox) contentView.lookup("#contentBox");
        VBox.setVgrow(sectionBox, stretchAllowed ? Priority.ALWAYS : Priority.NEVER);
        contentBox.getChildren().add(sectionBox);

        return section;
    }

    protected Parent putSection(Parent contentView, String title, String resourcePath) throws IOException {
        return putSection(contentView, title, resourcePath, true);
    }

    protected boolean needsSave(Settings settings) {
        for (ConfigBinding binding: bindings) {
            if (binding.needsSave()) {
                return true;
            }
        }
        return false;
    }

    protected void save(Settings settings) {
        for (ConfigBinding binding: bindings) {
            binding.save();
        }
    }

    private void persist(Settings settings) {
        ExtendedSettingsFactory.SaveAll();
        try {
            Util.saveToFile(settings.toFileString(), settingsPath);
            Settings.setSettingsPath(settingsPath.substring(0,settingsPath.indexOf(Main.SETTINGS_FILE)-1));
            System.out.println("Saved current settings to <" + settingsPath + ">");
        } catch (IOException e1) {
            LogSerialiser.log("Unable to save current settings to <" + settingsPath + ">: " + e1.toString() + "\n");
        }
    }

    @Override
    public boolean checkBeforeExit() {
        if (!needsSave(settings)) {
            return true;
        }

        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Are you sure?");
        saveAlert.setHeaderText("There are unsaved changes");
        saveAlert.setContentText("Do you want to save them?");

        ButtonType save = new ButtonType("Save");
        ButtonType discard = new ButtonType("Discard");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        saveAlert.getButtonTypes().setAll(save, discard, cancel);

        Optional<ButtonType> result = saveAlert.showAndWait();
        if (result.get() == save) {
            save(settings);
            return true;
        } else if (result.get() == discard) {
            return true;
        }

        return false;
    }

    protected void addBinding(ConfigBinding binding) {
        binding.bind();
        bindings.add(binding);
    }

    protected ConfigBinding addBinding(Control control, Tag tag, ConfigBinding.GenericType bindingType) {
        ConfigBinding binding = null;
        ConfigBinding.Builder builder = new ConfigBinding.Builder();
        try {
            binding = builder
                    .withControl(control)
                    .withGenericType(bindingType)
                    .withSettings(settings)
                    .withTag(tag)
                    .build();
            addBinding(binding);
        } catch (ConfigBindingException e) {
            e.printStackTrace();
        }
        return binding;
    }
}
