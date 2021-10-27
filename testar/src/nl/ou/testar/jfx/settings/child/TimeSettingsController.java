package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.io.IOException;

public class TimeSettingsController extends ChildSettingsController {
    private SpinnerValueFactory<Double> actionDurationValueFactory;
    private SpinnerValueFactory<Double> waitTimeValueFactory;
    private SpinnerValueFactory<Double> startupTimeValueFactory;
    private SpinnerValueFactory<Double> maxTestTimeValueFactory;
    private CheckBox useRecordTimingCheckbox;

    public TimeSettingsController(Settings settings, String settingsPath) {
        super("Time settings", settings, settingsPath);
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
        actionDurationValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        actionDurationValueFactory.setValue(settings.get(ConfigTags.ActionDuration, 0.0));
        actionDurationSpinner.setValueFactory(actionDurationValueFactory);
        Spinner waitTimeSpinner = (Spinner) view.lookup("#waitTime");
        waitTimeValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        waitTimeValueFactory.setValue(settings.get(ConfigTags.TimeToWaitAfterAction, 0.0));
        waitTimeSpinner.setValueFactory(waitTimeValueFactory);
        Spinner startupTimeSpinner = (Spinner) view.lookup("#startupTime");
        startupTimeValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        startupTimeValueFactory.setValue(settings.get(ConfigTags.StartupTime, 0.0));
        startupTimeSpinner.setValueFactory(startupTimeValueFactory);
        Spinner maxTestTimeSpinner = (Spinner) view.lookup("#maxTestTime");
        maxTestTimeValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE);
        maxTestTimeValueFactory.setValue(settings.get(ConfigTags.MaxTime, 0.0));
        maxTestTimeSpinner.setValueFactory(maxTestTimeValueFactory);

        useRecordTimingCheckbox = (CheckBox) view.lookup("#useRecordTiming");
        useRecordTimingCheckbox.setSelected(settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, false));
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (!actionDurationValueFactory.getValue().equals(settings.get(ConfigTags.ActionDuration, 0.0))) {
            return true;
        }
        if (!waitTimeValueFactory.getValue().equals(settings.get(ConfigTags.TimeToWaitAfterAction, 0.0))) {
            return true;
        }
        if (!startupTimeValueFactory.getValue().equals(settings.get(ConfigTags.StartupTime, 0.0))) {
            return true;
        }
        if (!maxTestTimeValueFactory.getValue().equals(settings.get(ConfigTags.MaxTime, 0.0))) {
            return true;
        }
        if (useRecordTimingCheckbox.isSelected() != settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, false)) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        settings.set(ConfigTags.ActionDuration, actionDurationValueFactory.getValue());
        settings.set(ConfigTags.TimeToWaitAfterAction, waitTimeValueFactory.getValue());
        settings.set(ConfigTags.StartupTime, startupTimeValueFactory.getValue());
        settings.set(ConfigTags.MaxTime, maxTestTimeValueFactory.getValue());

        settings.set(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, useRecordTimingCheckbox.isSelected());
    }
}
