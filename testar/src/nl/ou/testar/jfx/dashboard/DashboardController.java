package nl.ou.testar.jfx.dashboard;

import nl.ou.testar.jfx.core.ViewController;
import org.fruit.monkey.Settings;

public class DashboardController extends ViewController {
    public DashboardController(Settings settings) {
        super("Dashboard", "jfx/dashboard.fxml", settings);
    }
}
