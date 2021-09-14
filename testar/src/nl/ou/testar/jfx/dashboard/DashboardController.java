package nl.ou.testar.jfx.dashboard;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import nl.ou.testar.jfx.core.ViewController;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.Settings;

import java.io.File;
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
//            extractInformation(settings);
            checkSettings(settings);
            settings.set(ConfigTags.Mode, mode);
//            saveCurrentSettings();
//            ret = settings;
            System.out.println("Start testing...");
            if (delegate != null) {
                delegate.startTesting(settings);
            }
            else {
                System.out.println("No delegate!");
            }
        } catch (IllegalStateException ise) {
            final Alert alert = new Alert(Alert.AlertType.ERROR, ise.getMessage());
            alert.show();
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

        Button btnViewReports = (Button) view.lookup("#btnViewReports");
        btnViewReports.setOnAction(event -> {
            startTesting(view, RuntimeControlsProtocol.Modes.View);
        });
    }
}
