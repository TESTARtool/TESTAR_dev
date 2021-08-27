package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class TimeSettingsController extends ChildSettingsController {
    public TimeSettingsController(Settings settings) {
        super("Time settings", settings);
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
