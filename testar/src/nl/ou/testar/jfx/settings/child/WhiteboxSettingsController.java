package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class WhiteboxSettingsController extends ChildSettingsController {

    private TextField gitUrlField;
    private TextField gitUsernameField;
    private TextField gitTokenField;
    private TextField gitBranchField;

    private TextField sonarUrlField;
    private TextField sonarUsernameField;
    private TextField sonarPasswordField;

    private CheckBox sonarDockerizeBox;

    private TextArea sonarProjectPropertiesArea;
    private TextField sonarProjectNameField;
    private TextField sonarProjectKeyField;
    private CheckBox sonarSaveResultBox;

    public WhiteboxSettingsController(Settings settings, String settingsPath) {
        super("Whitebox", settings, settingsPath);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Git", "jfx/settings_git.fxml");
            putSection(view, "Sonarqube service", "jfx/settings_sonarqube.fxml");
            putSection(view, "Sonarqube project", "jfx/settings_sonar_project.fxml");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        gitUrlField = (TextField) view.lookup("#gitUrl");
        gitUsernameField = (TextField) view.lookup("#gitUsername");
        gitTokenField = (TextField) view.lookup("#gitToken");
        gitBranchField = (TextField) view.lookup("#gitBranch");

        sonarUrlField = (TextField) view.lookup("#sonarUrl");
        sonarUsernameField = (TextField) view.lookup("#sonarUsername");
        sonarPasswordField = (TextField) view.lookup("#sonarPassword");

        sonarDockerizeBox = (CheckBox) view.lookup("#sonarDockerize");

        sonarProjectPropertiesArea = (TextArea) view.lookup("#sonarProjectProperties");
        sonarProjectNameField = (TextField) view.lookup("#sonarProjectName");
        sonarProjectKeyField = (TextField) view.lookup("#sonarProjectKey");
        sonarSaveResultBox = (CheckBox) view.lookup("#sonarSaveResult");

        sonarDockerizeBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateSonarFields(newValue);
        });

        gitUrlField.setText(settings.get(ConfigTags.GitUrl, ""));
        gitUsernameField.setText(settings.get(ConfigTags.GitUsername, ""));
        gitTokenField.setText(settings.get(ConfigTags.GitToken, ""));
        gitBranchField.setText(settings.get(ConfigTags.GitBranch, ""));

        sonarUrlField.setText(settings.get(ConfigTags.SonarUrl, ""));
        sonarUsernameField.setText(settings.get(ConfigTags.SonarUsername, ""));
        sonarPasswordField.setText(settings.get(ConfigTags.SonarPassword, ""));

        sonarDockerizeBox.setSelected(settings.get(ConfigTags.SonarDockerize, false));

        sonarProjectPropertiesArea.setText(settings.get(ConfigTags.SonarProjectProperties, ""));
        sonarProjectNameField.setText(settings.get(ConfigTags.SonarProjectName, ""));
        sonarProjectKeyField.setText(settings.get(ConfigTags.SonarProjectKey, ""));
        sonarSaveResultBox.setSelected(settings.get(ConfigTags.SonarSaveResult, true));

        updateSonarFields(sonarDockerizeBox.isSelected());
    }

    private void updateSonarFields(boolean dockerEnabled) {
        if (dockerEnabled) {
            sonarUrlField.setDisable(true);
            sonarUsernameField.setDisable(true);
            sonarPasswordField.setDisable(true);
        }
        else {
            sonarUrlField.setDisable(false);
            sonarUsernameField.setDisable(false);
            sonarPasswordField.setDisable(false);

        }
    }

    @Override
    protected void save(Settings settings) {
        // TODO: show alert view
        settings.set(ConfigTags.GitUrl, gitUrlField.getText());
        settings.set(ConfigTags.GitUsername, gitUsernameField.getText());
        settings.set(ConfigTags.GitToken, gitTokenField.getText());
        settings.set(ConfigTags.GitBranch, gitBranchField.getText());

        settings.set(ConfigTags.SonarUrl, sonarUrlField.getText());
        settings.set(ConfigTags.SonarUsername, sonarUsernameField.getText());
        settings.set(ConfigTags.SonarPassword, sonarPasswordField.getText());
    }
}
