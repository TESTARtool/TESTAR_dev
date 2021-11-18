package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import org.apache.commons.lang.math.NumberUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class TimeSettingsController extends SettingsEditController {

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

        TextField actionDurationField = (TextField) view.lookup("#actionDuration");
        TextField waitTimeField = (TextField) view.lookup("#waitTime");
        TextField startupTimeField = (TextField) view.lookup("#startupTime");
        TextField maxTestTimeField = (TextField) view.lookup("#maxTestTime");

        useRecordTimingCheckbox = (CheckBox) view.lookup("#useRecordTiming");
        useRecordTimingCheckbox.setSelected(settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, false));

        addBinding(actionDurationField, ConfigTags.ActionDuration, ConfigBinding.GenericType.FIELD_DOUBLE);
        addBinding(waitTimeField, ConfigTags.TimeToWaitAfterAction, ConfigBinding.GenericType.FIELD_DOUBLE);
        addBinding(startupTimeField, ConfigTags.StartupTime, ConfigBinding.GenericType.FIELD_DOUBLE);
        addBinding(maxTestTimeField, ConfigTags.MaxTime, ConfigBinding.GenericType.FIELD_DOUBLE);
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (super.needsSave(settings)) {
            return  true;
        }
        if (useRecordTimingCheckbox.isSelected() != settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, false)) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        super.save(settings);
        settings.set(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay, useRecordTimingCheckbox.isSelected());
    }
}
