package nl.ou.testar.jfx.settings;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import nl.ou.testar.jfx.core.ViewController;
import nl.ou.testar.jfx.settings.child.*;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

public class SettingsController extends ViewController {

    private String settingsPath;

    public SettingsController(Settings settings, String settingsPath) {
        super("Settings", "jfx/settings_new.fxml", settings);
        this.settingsPath = settingsPath;
    }

    @Override
    public void viewDidLoad(Parent view) {
        Button btnGeneral = (Button) view.lookup("#btnGeneral");
        Button btnFilters = (Button) view.lookup("#btnFilters");
        Button btnTime = (Button) view.lookup("#btnTime");
        Button btnMisc = (Button) view.lookup("#btnMisc");
        Button btnWhitebox = (Button) view.lookup("#btnWhitebox");
        Button btnState = (Button) view.lookup("#btnState");

        btnGeneral.setOnAction(event -> {
            getNavigationController().navigateTo(new GeneralSettingsController(settings, settingsPath), true);
        });
        btnFilters.setOnAction(event -> {
            getNavigationController().navigateTo(new FilterSettingsController(settings, settingsPath), true);
        });
        btnTime.setOnAction(event -> {
            getNavigationController().navigateTo(new TimeSettingsController(settings, settingsPath), true);
        });
        btnMisc.setOnAction(event -> {
            getNavigationController().navigateTo(new MiscSettingsController(settings, settingsPath), true);
        });
        btnWhitebox.setOnAction(event -> {
            getNavigationController().navigateTo(new WhiteboxSettingsController(settings, settingsPath), true);
        });
        btnState.setOnAction(event -> {
            getNavigationController().navigateTo(new StateSettingsController(settings, settingsPath), true);
        });

        btnState.setDisable(!settings.get(ConfigTags.StateModelEnabled, false));
    }
}
