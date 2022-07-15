package nl.ou.testar.jfx.settings.child;

import javafx.scene.Parent;
import nl.ou.testar.jfx.settings.bindings.ConfigBinding;
import nl.ou.testar.jfx.settings.bindings.ConfigBindingException;
import nl.ou.testar.jfx.settings.bindings.control.ControlBinding;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

import java.io.IOException;

public class WhiteboxSettingsController extends SettingsEditController {

    private TextField gitUsernameField;
    private TextField gitTokenField;

    private TextField sonarUrlField;
    private TextField sonarUsernameField;
    private TextField sonarPasswordField;

    public WhiteboxSettingsController(Settings settings, String settingsPath) {
        super("Whitebox", settings, settingsPath);
    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        try {
            putSection(view, "Git", "jfx/settings_git.fxml", false);
            putSection(view, "Sonarqube service", "jfx/settings_sonarqube.fxml", false);
            putSection(view, "Sonarqube project", "jfx/settings_sonar_project.fxml", true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        TextField gitUrlField = (TextField) view.lookup("#gitUrl");
        gitUsernameField = (TextField) view.lookup("#gitUsername");
        gitTokenField = (TextField) view.lookup("#gitToken");
        TextField gitBranchField = (TextField) view.lookup("#gitBranch");

        final CheckBox gitAuthorizationRequiredBox = (CheckBox) view.lookup("#authorizationRequired");
        gitAuthorizationRequiredBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateGitFields(newValue);
        });

        sonarUrlField = (TextField) view.lookup("#sonarUrl");
        sonarUsernameField = (TextField) view.lookup("#sonarUsername");
        sonarPasswordField = (TextField) view.lookup("#sonarPassword");

        CheckBox sonarDockerizeBox = (CheckBox) view.lookup("#sonarDockerize");

        TextArea sonarProjectPropertiesArea = (TextArea) view.lookup("#sonarProjectProperties");
        TextField sonarProjectNameField = (TextField) view.lookup("#sonarProjectName");
        TextField sonarProjectKeyField = (TextField) view.lookup("#sonarProjectKey");
        CheckBox sonarSaveResultBox = (CheckBox) view.lookup("#sonarSaveResult");

        addBinding(gitUrlField, ConfigTags.GitUrl, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(gitUsernameField, ConfigTags.GitUsername, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(gitTokenField, ConfigTags.GitToken, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(gitBranchField, ConfigTags.GitBranch, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(sonarUrlField, ConfigTags.SonarUrl, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(sonarUsernameField, ConfigTags.SonarUsername, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(sonarPasswordField, ConfigTags.SonarPassword, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(sonarProjectPropertiesArea, ConfigTags.SonarProjectProperties, ConfigBinding.GenericType.TEXT_INPUT);
        addBinding(sonarProjectNameField, ConfigTags.SonarProjectName, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(sonarProjectKeyField, ConfigTags.SonarProjectKey, ConfigBinding.GenericType.FIELD_STRING);
        addBinding(sonarSaveResultBox, ConfigTags.SonarSaveResult, ConfigBinding.GenericType.CHECK_BOX);

        ControlBinding<Boolean> gitAuthorizationRequiredControlBinding = new ControlBinding<Boolean>() {
            @Override
            public void setValue(Boolean value) {
                gitAuthorizationRequiredBox.setSelected(value);
                updateGitFields(value);
            }

            @Override
            public Boolean getValue() {
                return gitAuthorizationRequiredBox.isSelected();
            }

            @Override
            public void onBind(Callback<Boolean> callback) {
                gitAuthorizationRequiredBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    callback.onUpdate(newValue);
                });
            }
        };
        try {
            ConfigBinding<Boolean> gitAuthorizationRequiredConfigBinding = new ConfigBinding.Builder<Boolean>()
                    .withCustomControlBinding(gitAuthorizationRequiredControlBinding)
                    .withSettings(settings)
                    .withTag(ConfigTags.GitAuthRequired)
                    .withGenericType(ConfigBinding.GenericType.CHECK_BOX)
                    .build();
            addBinding(gitAuthorizationRequiredConfigBinding);
        } catch (ConfigBindingException e) {
            e.printStackTrace();
        }

        ControlBinding<Boolean> sonarDockerizeControlBinding = new ControlBinding<Boolean>() {

            @Override
            public void setValue(Boolean value) {
                System.out.println("Dockerize: " + value.toString());
                sonarDockerizeBox.setSelected(value);
                updateSonarFields(value);
            }

            @Override
            public Boolean getValue() {
                return sonarDockerizeBox.isSelected();
            }

            @Override
            public void onBind(Callback<Boolean> callback) {
                sonarDockerizeBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    callback.onUpdate(newValue);
                });
            }
        };
        ConfigBinding<Boolean> sonarDockerizeConfigBinding = null;
        try {
            sonarDockerizeConfigBinding = new ConfigBinding.Builder<Boolean>()
                    .withCustomControlBinding(sonarDockerizeControlBinding)
                    .withSettings(settings)
                    .withTag(ConfigTags.SonarDockerize)
                    .withGenericType(ConfigBinding.GenericType.CHECK_BOX)
                    .build();
            addBinding(sonarDockerizeConfigBinding);
        } catch (ConfigBindingException e) {
            e.printStackTrace();
        }
    }

    private void updateGitFields(boolean authorizationRequired) {
        gitUsernameField.setDisable(!authorizationRequired);
        gitTokenField.setDisable(!authorizationRequired);
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
}
