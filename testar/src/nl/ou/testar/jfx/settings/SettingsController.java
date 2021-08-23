package nl.ou.testar.jfx.settings;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import nl.ou.testar.jfx.core.ViewController;
import nl.ou.testar.jfx.settings.child.FilterSettingsController;
import nl.ou.testar.jfx.settings.child.GeneralSettingsController;

public class SettingsController extends ViewController {
    public SettingsController() {
        super("Settings", "jfx/settings_new.fxml");
    }

    @Override
    public void viewDidLoad(Parent view) {
        Button btnGeneral = (Button) view.lookup("#btnGeneral");
        Button btnFilters = (Button) view.lookup("#btnFilters");
        Button btnTime = (Button) view.lookup("#btnTime");
        Button btnMisc = (Button) view.lookup("#btnMisc");
        Button btnState = (Button) view.lookup("#btnState");

        btnGeneral.setOnAction(event -> {
            getNavigationController().navigateTo(new GeneralSettingsController(), true);
        });
        btnFilters.setOnAction(event -> {
            getNavigationController().navigateTo(new FilterSettingsController(), true);
        });
        btnTime.setOnAction(event -> {
            getNavigationController().navigateTo(new TimeSettingsController(), true);
        });
        btnMisc.setOnAction(event -> {
            getNavigationController().navigateTo(new MiscSettingsController(), true);
        });
        btnState.setOnAction(event -> {
            getNavigationController().navigateTo(new StateSettingsController(), true);
        });
    }
}
