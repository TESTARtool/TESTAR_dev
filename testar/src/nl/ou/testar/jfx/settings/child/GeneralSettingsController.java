package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;

import java.io.IOException;

public class GeneralSettingsController extends ChildSettingsController {
    public GeneralSettingsController() {
        super("General settings");
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
