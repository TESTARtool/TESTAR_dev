package nl.ou.testar.jfx.settings.child;

import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import nl.ou.testar.jfx.controls.TagControl;
import nl.ou.testar.jfx.controls.TagInput;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterSettingsController extends SettingsEditController {

    private final static String descriptions[] = {
            "Filter actions on the widgets based on Tags values (regular expression):",
            "Tags to apply the filters (semicolon to customize multiple Tags)",
            "Kill processes by name (regular expression):",
            "Suspicious Titles based on Tags values (regular expression)",
            "Tags to apply the Suspicious Titles (semicolon to customize multiple Tags)",
            "Suspicious Process Output (regular expression)"
    };

    private int selectedIndex = 0;

    private Label descriptionLabel;
    private JFXTextArea textArea;
    private FlowPane tagsPane;
    private Button choiceButtons[] = new Button[6];

    private String clickFilter;
    private String processesToKill;
    private String suspiciousTitles;
    private String suspiciousProcessOutput;

    private List<String> filterTags;
    private List<String> oracleTags;

    FXMLLoader tagLoader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/tag.fxml"));

    private void selectItem(int index) {
        if (index == selectedIndex) {
            return;
        }

        storeValue(selectedIndex);

        setSelection(selectedIndex, false);
        setSelection(index, true);
        selectedIndex = index;
    }

    private void storeValue(int index) {
        switch(index) {
            case 0:
                clickFilter = textArea.getText();
                break;
            case 1:
                filterTags = readTagsFromPane(tagsPane);
                break;
            case 2:
                processesToKill = textArea.getText();
                break;
            case 3:
                suspiciousTitles = textArea.getText();
                break;
            case 4:
                oracleTags = readTagsFromPane(tagsPane);
                break;
            default:
                suspiciousProcessOutput = textArea.getText();
                break;
        }
    }

    private void setSelection(int index, boolean selected) {
        if (selected) {
            descriptionLabel.setText(descriptions[index]);
            choiceButtons[index].setStyle("-fx-background-color:#a7a7a7;-fx-background-radius:18");
        }
        else {
            choiceButtons[index].setStyle("-fx-background-color:#f7f7f7;;-fx-background-radius:18");
        }

        switch (index) {
            case 0:
                textArea.setVisible(true);
                tagsPane.setVisible(false);
                textArea.setText(clickFilter);
                break;
            case 1:
                textArea.setVisible(false);
                tagsPane.setVisible(true);
                fillPaneWithTags(tagsPane, filterTags);
                break;
            case 2:
                textArea.setVisible(true);
                tagsPane.setVisible(false);
                textArea.setText(processesToKill);
                break;
            case 3:
                textArea.setVisible(true);
                tagsPane.setVisible(false);
                textArea.setText(suspiciousTitles);
                break;
            case 4:
                textArea.setVisible(false);
                tagsPane.setVisible(true);
                fillPaneWithTags(tagsPane, oracleTags);
                break;
            default:
                textArea.setVisible(true);
                tagsPane.setVisible(false);
                textArea.setText(suspiciousProcessOutput);
                break;
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

        clickFilter = settings.get(ConfigTags.ClickFilter, "");
        filterTags = settings.get(ConfigTags.TagsToFilter, Collections.emptyList());
        processesToKill = settings.get(ConfigTags.ProcessesToKillDuringTest, "");
        suspiciousTitles = settings.get(ConfigTags.SuspiciousTitles, "");
        oracleTags = settings.get(ConfigTags.TagsForSuspiciousOracle, Collections.emptyList());
        suspiciousProcessOutput = settings.get(ConfigTags.SuspiciousProcessOutput, "");

        descriptionLabel = (Label) view.lookup("#descriptionLabel");
        textArea = (JFXTextArea) view.lookup("#textArea");
        tagsPane = (FlowPane) view.lookup("#tagsPane");

        choiceButtons[0] = (Button) view.lookup("#btnFilterClick");
        choiceButtons[0].setOnAction(event -> {
            selectItem(0);
        });
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

    private void fillPaneWithTags(FlowPane pane, List<String> tags) {
        pane.getChildren().clear();
        System.out.println(String.format("Found %d tag(s)", tags.size()));
        pane.getChildren().addAll(tags.stream().map(tag -> {
            final TagControl tagControl = new TagControl(tag);
            tagControl.setDelegate(() -> {
                pane.getChildren().remove(tagControl);
            });
            return tagControl;
        }/*new TagControl(tag, new TagControl.Delegate() {
            @Override
            public void onClose() {
                pane.getChildren().remove(this);
            }
        }*/).collect(Collectors.toList()));

        final TagInput tagInput = new TagInput();
        tagInput.setDelegate(tag -> {
            int index = pane.getChildren().size() - 1;
            final TagControl tagControl = new TagControl(tag);
            tagControl.setDelegate(() -> {
                pane.getChildren().remove(tagControl);
            });
            pane.getChildren().add(index, tagControl);
            tagInput.clear();
        });
        pane.getChildren().add(tagInput);
    }

    private List<String> readTagsFromPane(FlowPane pane) {
        return pane.getChildren().stream().filter(child -> TagControl.class.isInstance(child))
                .map(child -> ((TagControl) child).getTag()).collect(Collectors.toList());
    }

    @Override
    protected boolean needsSave(Settings settings) {
        if (clickFilter != null && !clickFilter.equals(settings.get(ConfigTags.ClickFilter, ""))) {
            return true;
        }
        if (filterTags != null & !filterTags.equals(settings.get(ConfigTags.TagsToFilter, Collections.emptyList()))) {
            return true;
        }
        if (processesToKill != null && !processesToKill.equals(settings.get(ConfigTags.ProcessesToKillDuringTest, ""))) {
            return true;
        }
        if (suspiciousTitles != null && !suspiciousTitles.equals(settings.get(ConfigTags.SuspiciousTitles, ""))) {
            return true;
        }
        if (oracleTags != null && !oracleTags.equals((settings.get(ConfigTags.TagsForSuspiciousOracle, Collections.emptyList())))) {
            return true;
        }
        if (suspiciousProcessOutput != null && !suspiciousProcessOutput.equals(settings.get(ConfigTags.SuspiciousProcessOutput, ""))) {
            return true;
        }
        return false;
    }

    @Override
    protected void save(Settings settings) {
        storeValue(selectedIndex);

        settings.set(ConfigTags.ClickFilter, clickFilter);
        settings.set(ConfigTags.TagsToFilter, filterTags);
        settings.set(ConfigTags.ProcessesToKillDuringTest, processesToKill);
        settings.set(ConfigTags.SuspiciousTitles, suspiciousTitles);
        settings.set(ConfigTags.TagsForSuspiciousOracle, oracleTags);
        settings.set(ConfigTags.SuspiciousProcessOutput, suspiciousProcessOutput);
    }
}
