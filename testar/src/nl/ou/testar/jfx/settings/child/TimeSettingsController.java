package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class TimeSettingsController extends ChildSettingsController {
    public TimeSettingsController(Settings settings) {
        super("Time settings", settings);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Time settings", "jfx/settings_time.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Spinner actionDurationSpinner = (Spinner) view.lookup("#actionDuration");
        SpinnerValueFactory<Double> actionDurationValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        actionDurationValueFactory.setValue(settings.get(ConfigTags.ActionDuration));
        actionDurationSpinner.setValueFactory(actionDurationValueFactory);
        Spinner waitTimeSpinner = (Spinner) view.lookup("#waitTime");
        SpinnerValueFactory<Double> waitTimeValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        waitTimeValueFactory.setValue(settings.get(ConfigTags.TimeToWaitAfterAction));
        waitTimeSpinner.setValueFactory(waitTimeValueFactory);
        Spinner startupTimeSpinner = (Spinner) view.lookup("#startupTime");
        SpinnerValueFactory<Double> startupTimeValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        startupTimeValueFactory.setValue(settings.get(ConfigTags.StartupTime));
        startupTimeSpinner.setValueFactory(startupTimeValueFactory);
        Spinner maxTestTimeSpinner = (Spinner) view.lookup("#maxTestTime");
        SpinnerValueFactory<Double> maxTestTimeValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        maxTestTimeValueFactory.setValue(settings.get(ConfigTags.MaxTime));
        maxTestTimeSpinner.setValueFactory(maxTestTimeValueFactory);

        CheckBox useRecordTimingCheckbox = (CheckBox) view.lookup("#useRecordTiming");
        useRecordTimingCheckbox.setSelected(settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay));
    }
}
