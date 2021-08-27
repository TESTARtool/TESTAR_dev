package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class GeneralSettingsController extends ChildSettingsController {
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
    }
}
