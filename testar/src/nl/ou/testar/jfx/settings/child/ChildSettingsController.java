package nl.ou.testar.jfx.settings.child;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nl.ou.testar.jfx.core.ViewController;
import org.testar.extendedsettings.ExtendedSettingsFactory;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;
import org.testar.monkey.Util;
import org.testar.serialisation.LogSerialiser;

import java.io.IOException;

public abstract class ChildSettingsController extends ViewController {

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
}
