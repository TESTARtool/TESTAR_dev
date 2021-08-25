package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;

import java.io.IOException;

public class MiscSettingsController extends ChildSettingsController {
    public MiscSettingsController() {
        super("");
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
