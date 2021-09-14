package nl.ou.testar.jfx.settings.child;

import com.jfoenix.controls.JFXTextArea;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.IOException;
import java.util.Arrays;

public class FilterSettingsController extends ChildSettingsController {

    private final static String descriptions[] = {
            "Filter actions on the widgets based on Tags values (regular expression):",
            "Tags to apply the filters (semicolon to customize multiple Tags)",
            "Kill processes by name (regular expression):",
            "Suspicious Titles based on Tags values (regular expression)",
            "Tags to apply the Suspicious Titles (semicolon to customize multiple Tags)",
            "Suspicious Process Output (regular expression)"
    };

    private String values[] = new String[6];

    private int selectedIndex = 0;

    private Label descriptionLabel;
    private JFXTextArea textArea;
    private Button choiceButtons[] = new Button[6];

    private void selectItem(int index) {
        if (index == selectedIndex) {
            return;
        }
        values[selectedIndex] = textArea.getText();
        setSelection(selectedIndex, false);
        setSelection(index, true);
        selectedIndex = index;
    }

    private void setSelection(int index, boolean selected) {
        if (selected) {
            descriptionLabel.setText(descriptions[index]);
            textArea.setText(values[index]);
            choiceButtons[index].setStyle("-fx-background-color:#a7a7a7;-fx-background-radius:18");
        }
        else {
            choiceButtons[index].setStyle("-fx-background-color:#f7f7f7;;-fx-background-radius:18");
        }
    }

    public FilterSettingsController(Settings settings, String settingsPath) {
        super("Filters & oracles", settings, settingsPath);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Filters & oracles", "jfx/settings_filter.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        values[0] = settings.get(ConfigTags.ClickFilter);
        values[1] = StringUtils.join(settings.get(ConfigTags.TagsToFilter));
        values[2] = settings.get(ConfigTags.ProcessesToKillDuringTest);
        values[3] = settings.get(ConfigTags.SuspiciousTitles);
        values[4] = StringUtils.join(settings.get(ConfigTags.TagsForSuspiciousOracle));
        values[5] = settings.get(ConfigTags.SuspiciousProcessOutput);

        descriptionLabel = (Label) view.lookup("#descriptionLabel");
        textArea = (JFXTextArea) view.lookup("#textArea");

        choiceButtons[0] = (Button) view.lookup("#btnFilterClick");
        choiceButtons[0].setOnAction(event -> {selectItem(0);});
        choiceButtons[1] = (Button) view.lookup("#btnFilterTags");
        choiceButtons[1].setOnAction(event -> {selectItem(1);});
        choiceButtons[2] = (Button) view.lookup("#btnFilterProcess");
        choiceButtons[2].setOnAction(event -> {selectItem(2);});
        choiceButtons[3] = (Button) view.lookup("#btnOracleTitles");
        choiceButtons[3].setOnAction(event -> {selectItem(3);});
        choiceButtons[4] = (Button) view.lookup("#btnOracleTags");
        choiceButtons[4].setOnAction(event -> {selectItem(4);});
        choiceButtons[5] = (Button) view.lookup("#btnOracleProcess");
        choiceButtons[5].setOnAction(event -> {selectItem(5);});

        setSelection(selectedIndex, true);
    }

    @Override
    protected void save(Settings settings) {
        settings.set(ConfigTags.ClickFilter, values[0]);
        settings.set(ConfigTags.TagsToFilter, Arrays.asList(values[1].split(";")));
        settings.set(ConfigTags.ProcessesToKillDuringTest, values[2]);
        settings.set(ConfigTags.SuspiciousTitles, values[3]);
        settings.set(ConfigTags.TagsForSuspiciousOracle, Arrays.asList(values[4].split(";")));
        settings.set(ConfigTags.SuspiciousProcessOutput, values[5]);
    }
}
