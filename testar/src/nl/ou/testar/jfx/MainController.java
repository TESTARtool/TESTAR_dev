package nl.ou.testar.jfx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import nl.ou.testar.jfx.core.NavigationController;
import nl.ou.testar.jfx.core.NavigationDelegate;
import nl.ou.testar.jfx.core.ViewController;
import nl.ou.testar.jfx.dashboard.DashboardController;
import nl.ou.testar.jfx.misc.MiscController;
import nl.ou.testar.jfx.settings.SettingsController;

import javafx.scene.control.*;
import org.fruit.monkey.Settings;

import java.io.File;

public class MainController extends ViewController {

    enum Mode {
        HOME, SETTINGS, MISC
    }

    private Mode mode;
    private String settingsPath;

    private DashboardController dashboardController;
    private SettingsController settingsController;
    private MiscController miscController;

    private Stage stage = null;

    public DashboardController getDashboardController() {
        return dashboardController;
    }

    public MainController(Settings settings, String settingsPath) {
        super("Testar", "jfx/main.fxml", settings);
        this.settingsPath = settingsPath;
        dashboardController = new DashboardController(settings, settingsPath);
        settingsController = new SettingsController(settings, settingsPath);
        miscController = new MiscController("Misc", settings);
    }

    private void setupMode(Parent view, Mode mode) {

        if (mode != this.mode) {
//            final Label titleLabel = (Label) view.lookup("#titleLabel");
//            final Button btnBack = (Button) view.lookup("#btnBack");

//            final BorderPane contentPane = (BorderPane) view.lookup("#contentPane");
            final AnchorPane contentPane = (AnchorPane) view.lookup("#contentPane");
            ViewController targetController;
            switch (mode) {
                case SETTINGS:
                    targetController = settingsController;
                    break;
                case MISC:
                    targetController = miscController;
                    break;
                default: //HOME
                    targetController = dashboardController;
                    break;
            }
            final NavigationController navigationController = new NavigationController(targetController);
            navigationController.startWithDelegate(new NavigationDelegate() {
                @Override
                public void onViewControllerActivated(ViewController viewController, Parent view) {
//                    titleLabel.setText(viewController.getTitle());
                    contentPane.getChildren().clear();
                    contentPane.getChildren().add(view);
                    AnchorPane.setLeftAnchor(view, 0.0);
                    AnchorPane.setTopAnchor(view, 0.0);
                    AnchorPane.setRightAnchor(view, 0.0);
                    AnchorPane.setBottomAnchor(view, 0.0);

                    if (stage != null) {
                        double width = stage.getWidth();
                        double height = stage.getHeight();

                        view.setVisible(false);
                        stage.sizeToScene();
                        stage.setWidth(Math.max(width, stage.getWidth()));
                        stage.setHeight(Math.max(height, stage.getHeight()));
                        view.setVisible(true);
                    }
                }
            });

//            btnBack.setOnAction(event -> {
//                navigationController.navigateBack();
//            });

            this.mode = mode;
        }
    }

    @Override
    public void viewDidLoad(Parent view) {
        Button btnHome = (Button) view.lookup("#btnHome");
        Button btnSettings = (Button) view.lookup("#btnSettings");
        Button btnMisc = (Button) view.lookup("#btnMisc");

        btnHome.setOnAction(event -> {
            setupMode(view, Mode.HOME);

        });

        btnSettings.setOnAction(event -> {
            setupMode(view, Mode.SETTINGS);
        });

//        btnMisc.setOnAction(event -> {
//            setupMode(view, Mode.MISC);
//        });

        setupMode(view, Mode.HOME);
    }

    @Override
    public void viewDidAppear(Parent view) {
        stage = (Stage) view.getScene().getWindow();
    }
}
