package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class MiscSettingsController extends ChildSettingsController {
    public MiscSettingsController(Settings settings) {
        super("", settings);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Misc", "jfx/settings_misc.fxml");
            putSection(view, "Files on SUT startup", "jfx/settings_startup.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
