package nl.ou.testar.jfx.settings;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import nl.ou.testar.jfx.core.ViewController;
import nl.ou.testar.jfx.settings.child.*;
import org.fruit.monkey.Settings;

public class SettingsController extends ViewController {
    public SettingsController(Settings settings) {
        super("Settings", "jfx/settings_new.fxml", settings);
    }

    @Override
    public void viewDidLoad(Parent view) {
        Button btnGeneral = (Button) view.lookup("#btnGeneral");
        Button btnFilters = (Button) view.lookup("#btnFilters");
        Button btnTime = (Button) view.lookup("#btnTime");
        Button btnMisc = (Button) view.lookup("#btnMisc");
        Button btnState = (Button) view.lookup("#btnState");

        btnGeneral.setOnAction(event -> {
            getNavigationController().navigateTo(new GeneralSettingsController(settings), true);
        });
        btnFilters.setOnAction(event -> {
            getNavigationController().navigateTo(new FilterSettingsController(settings), true);
        });
        btnTime.setOnAction(event -> {
            getNavigationController().navigateTo(new TimeSettingsController(settings), true);
        });
        btnMisc.setOnAction(event -> {
            getNavigationController().navigateTo(new MiscSettingsController(settings), true);
        });
        btnState.setOnAction(event -> {
            getNavigationController().navigateTo(new StateSettingsController(settings), true);
        });
    }
}
