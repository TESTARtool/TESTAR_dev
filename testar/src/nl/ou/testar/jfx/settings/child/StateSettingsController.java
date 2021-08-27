package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class StateSettingsController extends ChildSettingsController {
    public StateSettingsController(Settings settings) {
        super("", settings);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "State", "jfx/settings_state.fxml");
            putSection(view, "Widgets", "jfx/settings_widgets.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
