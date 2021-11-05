package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.apache.commons.lang.math.NumberUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class TimeSettingsController extends ChildSettingsController {

    private CheckBox useRecordTimingCheckbox;
    private TextField actionDurationField;
    private TextField waitTimeField;
    private TextField startupTimeField;
    private TextField maxTestTimeField;

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

        actionDurationField = (TextField) view.lookup("#actionDuration");
        actionDurationField.setPromptText("0");
        limitInputToPattern(actionDurationField, ChildSettingsController.INPUT_PATTERN_NUMBER);
        waitTimeField = (TextField) view.lookup("#waitTime");
        waitTimeField.setPromptText("0");
        limitInputToPattern(waitTimeField, ChildSettingsController.INPUT_PATTERN_NUMBER);
        startupTimeField = (TextField) view.lookup("#startupTime");
        startupTimeField.setPromptText("0");
        limitInputToPattern(startupTimeField, ChildSettingsController.INPUT_PATTERN_NUMBER);
        maxTestTimeField = (TextField) view.lookup("#maxTestTime");
        maxTestTimeField.setPromptText("0");
        limitInputToPattern(maxTestTimeField, ChildSettingsController.INPUT_PATTERN_NUMBER);

        actionDurationField.setText(settings.get(ConfigTags.ActionDuration, 0.0).toString());
        waitTimeField.setText(settings.get(ConfigTags.TimeToWaitAfterAction, 0.0).toString());
        startupTimeField.setText(settings.get(ConfigTags.StartupTime, 0.0).toString());
        maxTestTimeField.setText(settings.get(ConfigTags.MaxTime, 0.0).toString());

        useRecordTimingCheckbox = (CheckBox) view.lookup("#useRecordTiming");
        useRecordTimingCheckbox.setSelected(settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, false));
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (!actionDurationField.getText().equals(settings.get(ConfigTags.ActionDuration, 0.0).toString())) {
            return true;
        }
        if (!waitTimeField.getText().equals(settings.get(ConfigTags.TimeToWaitAfterAction, 0.0).toString())) {
            return true;
        }
        if (!startupTimeField.getText().equals(settings.get(ConfigTags.StartupTime, 0.0).toString())) {
            return true;
        }
        if (!maxTestTimeField.getText().equals(settings.get(ConfigTags.MaxTime, 0.0).toString())) {
            return true;
        }
        if (useRecordTimingCheckbox.isSelected() != settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, false)) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        settings.set(ConfigTags.ActionDuration, NumberUtils.toDouble(actionDurationField.getText(), 0));
        settings.set(ConfigTags.TimeToWaitAfterAction, NumberUtils.toDouble(waitTimeField.getText(), 0));
        settings.set(ConfigTags.StartupTime, NumberUtils.toDouble(startupTimeField.getText(), 0));
        settings.set(ConfigTags.MaxTime, NumberUtils.toDouble(maxTestTimeField.getText(), 0));

        settings.set(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, useRecordTimingCheckbox.isSelected());
    }
}
