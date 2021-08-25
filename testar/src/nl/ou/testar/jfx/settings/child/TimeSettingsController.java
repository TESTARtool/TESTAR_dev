package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;

import java.io.IOException;

public class TimeSettingsController extends ChildSettingsController {
    public TimeSettingsController() {
        super("Time settings");
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Time settings", "jfx/settings_time.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
