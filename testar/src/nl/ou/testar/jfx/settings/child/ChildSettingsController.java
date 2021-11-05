package nl.ou.testar.jfx.settings.child;

import es.upv.staq.testar.serialisation.LogSerialiser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import nl.ou.testar.jfx.core.ViewController;
import org.fruit.Util;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.testar.settings.ExtendedSettingsFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public abstract class ChildSettingsController extends ViewController {

    public final static String INPUT_PATTERN_INTEGER = "\\d*";
    public final static String INPUT_PATTERN_NUMBER = "\\d*|\\d+\\,\\d*";

    private String settingsPath;

    public ChildSettingsController(String title, Settings settings, String settingsPath) {
        super(title, "jfx/settings_child.fxml", settings);
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
    protected void putSection(Parent contentView, String title, String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/settings_section.fxml"));
        VBox sectionBox =  (VBox) loader.load();

        loader = new FXMLLoader(getClass().getClassLoader().getResource(resourcePath));
        sectionBox.getChildren().add(loader.load());

        Label titleLabel = (Label) sectionBox.lookup("#titleLabel");
        titleLabel.setText(title);

        VBox contentBox = (VBox) contentView.lookup("#contentBox");
        contentBox.getChildren().add(sectionBox);
    }

    protected abstract boolean needsSave(Settings settings);

    protected abstract void save(Settings settings);

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

    protected void limitInputToPattern(TextField textField, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);

        textField.setTextFormatter(formatter);
    }
}
