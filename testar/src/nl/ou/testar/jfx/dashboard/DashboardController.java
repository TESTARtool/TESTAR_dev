package nl.ou.testar.jfx.dashboard;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import nl.ou.testar.jfx.JfxProgressMonitor;
import nl.ou.testar.jfx.StartupProgressMonitor;
import nl.ou.testar.jfx.WhiteboxTestLauncher;
import nl.ou.testar.jfx.core.ViewController;
import nl.ou.testar.jfx.thirdparty.DisplayShelf;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DashboardController extends ViewController {

    private String settingsPath;

    private DashboardDelegate delegate;

    public DashboardController(Settings settings, String settingsPath) {
        super("Dashboard", "jfx/dashboard.fxml", settings);
        this.settingsPath = settingsPath;
    }

    public DashboardDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(DashboardDelegate delegate) {
        this.delegate = delegate;
    }

    private void startTesting(Parent view, RuntimeControlsProtocol.Modes mode) {
        try {
            checkSettings(settings);
            settings.set(ConfigTags.Mode, mode);
            System.out.println("Start testing...");
            if (delegate != null) {
                Platform.runLater(() -> {
                    StartupProgressMonitor progressMonitor = new StartupProgressMonitor();
                    new Thread(() -> delegate.startTesting(settings, progressMonitor)).start();
                });

                final Stage stage = (Stage) view.getScene().getWindow();
            }
            else {
                System.out.println("No delegate!");
            }
        } catch (IllegalStateException ise) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, ise.getMessage());
            alert.show();
        }
    }

    private void startWhiteboxTesting(Parent view) {
        final Stage stage = (Stage) view.getScene().getWindow();
        final WhiteboxTestLauncher testStatus = new WhiteboxTestLauncher();

        final JfxProgressMonitor progressMonitor = new JfxProgressMonitor();
        final WhiteboxTestLauncher whiteboxTestLauncher = new WhiteboxTestLauncher(progressMonitor);
        try {
            whiteboxTestLauncher.setDashboardDelegate(delegate);
            whiteboxTestLauncher.start(stage, settings);
        }
        catch(IOException e) {
            progressMonitor.stop();
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot analyse code\n" + e.getMessage());
            alert.show();

            e.printStackTrace();
        }
    }

    private void checkSettings(Settings settings) throws IllegalStateException {
        String userInputPattern = settings.get(ConfigTags.ProcessesToKillDuringTest);
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            throw new IllegalStateException("Your ProcessFilter is not a valid regular expression!");
        }

        userInputPattern = settings.get(ConfigTags.ClickFilter);
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            throw new IllegalStateException("Your ClickFilter is not a valid regular expression!");
        }

        userInputPattern = settings.get(ConfigTags.SuspiciousTitles);
        try {
            Pattern.compile(userInputPattern);
        } catch (PatternSyntaxException exception) {
            throw new IllegalStateException("Your Oracle is not a valid regular expression!");
        }

        if (!new File(settings.get(ConfigTags.OutputDir)).exists()) {
            throw new IllegalStateException("Output Directory does not exist!");
        }
        if (!new File(settings.get(ConfigTags.TempDir)).exists()) {
            throw new IllegalStateException("Temp Directory does not exist!");
        }
//
//        settingPanels.forEach((k,v) -> v.right().checkSettings());
    }


    @Override
    public void viewDidLoad(Parent view) {
        Button btnSpyMode = (Button) view.lookup("#btnSpyMode");
        btnSpyMode.setOnAction(event -> {
            startTesting(view, RuntimeControlsProtocol.Modes.Spy);
        });

        Button btnAutoTest = (Button) view.lookup("#btnAutoTest");
        btnAutoTest.setOnAction(event -> {
            startTesting(view, RuntimeControlsProtocol.Modes.Generate);
        });

        Button btnRecTest = (Button) view.lookup("#btnRecTest");
        btnRecTest.setOnAction(event -> {
            startTesting(view, RuntimeControlsProtocol.Modes.Record);
        });

        Button btnReplayTest = (Button) view.lookup("#btnReplayTest");
        btnReplayTest.setOnAction(event -> {
            startTesting(view, RuntimeControlsProtocol.Modes.Replay);
        });

        Button btnWhiteboxTest = (Button) view.lookup("#btnWhiteboxTest");
        btnWhiteboxTest.setOnAction(event -> {
            startWhiteboxTesting(view);
        });

        Button btnViewReports = (Button) view.lookup("#btnViewReports");
        btnViewReports.setOnAction(event -> {
            startTesting(view, RuntimeControlsProtocol.Modes.View);
        });

        HBox carouselBox = (HBox) view.lookup("#carouselBox");
        final String imagePaths[] = {"/logos/ing.png", "/logos/marviq.png", "/logos/open_university.png", "/logos/philips.png", "/logos/sogeti.png"};
        final Image images[] = Arrays.stream(imagePaths).map(path -> new Image(path)).toArray(Image[]::new);
        DisplayShelf carouselView = new DisplayShelf(images);
        carouselBox.getChildren().add(carouselView);
        carouselView.prefWidthProperty().bind(carouselBox.widthProperty());
        carouselView.prefHeightProperty().bind(carouselBox.heightProperty());
    }
}
