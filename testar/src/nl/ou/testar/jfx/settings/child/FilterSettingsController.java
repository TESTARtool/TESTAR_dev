package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class FilterSettingsController extends ChildSettingsController {
    public FilterSettingsController(Settings settings) {
        super("Filters & oracles", settings);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Filters & oracles", "jfx/settings_filter.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
