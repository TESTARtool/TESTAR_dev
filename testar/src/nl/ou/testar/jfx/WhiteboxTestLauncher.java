package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.sonarqube.SonarqubeService;
import org.fruit.monkey.sonarqube.SonarqubeServiceDelegate;
import org.fruit.monkey.sonarqube.SonarqubeServiceImpl;
import org.fruit.monkey.vcs.GitCredentials;
import org.fruit.monkey.vcs.GitService;
import org.fruit.monkey.vcs.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class WhiteboxTestLauncher implements ProgressMonitor, SonarqubeServiceDelegate {

    // TODO: should use a special test protocol

    private Parent view;
    private Stage parentStage;
    private Stage whiteboxStage;

    private Label stageLabel;
    private Label statusLabel;
    private ProgressBar progressBar;

    private int currentProgress;
    private int totalProgress;

    public void start(Stage stage, Settings settings) throws IOException {
        parentStage = stage;
        parentStage.hide();
        whiteboxStage = new Stage(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/whitebox_test_status.fxml"));
        view = loader.load();

        stageLabel = (Label) view.lookup("#procStage");
        statusLabel = (Label) view.lookup("#procStatus");
        progressBar = (ProgressBar) view.lookup("#procProgressBar");

        whiteboxStage.setScene(new Scene(view));
        whiteboxStage.show();

        // Clone GIT repository

        final GitService gitService = new GitServiceImpl();
        final String repositoryUrl = settings.get(ConfigTags.GitUrl);

        final SonarqubeService sonarqubeService = new SonarqubeServiceImpl("sonarqube");
        sonarqubeService.setDelegate(this);

        String path = settings.get(ConfigTags.OutputDir);
        if (!path.substring(path.length() - 1).equals(File.separator)) {
            path += File.separator;
        }

        final String branchName = settings.get(ConfigTags.GitBranch, null);

        final String sonarqubeConfPath = path + "sonarqube" + File.separator;
        final String sonarqubeClientConfPath = path + "sonarqube_client" + File.separator;
        final String projectName = settings.get(ConfigTags.SonarProjectName, "Demo");
        final String projectKey = settings.get(ConfigTags.SonarProjectKey, "demo");

        final GitCredentials gitCredentials = new GitCredentials(settings.get(ConfigTags.GitUsername), settings.get(ConfigTags.GitToken));

        if (settings.get(ConfigTags.GitAuthRequired, false)) {
            // TODO: set credentials
        }

        new Thread(() -> {
            System.out.println("Cloning repository...");
            Path repositoryPath;
            if (gitCredentials == null) {
                repositoryPath = gitService.cloneRepository(repositoryUrl, this, branchName);
            }
            else {
                repositoryPath = gitService.cloneRepository(repositoryUrl, gitCredentials, this, branchName);
            }
            System.out.println("...done");

            // TODO: scan repository and analyse code

            String projectSourceDir = repositoryPath.toString();
            if (!projectSourceDir.substring(projectSourceDir.length() - 1).equals(File.separator)) {
                projectSourceDir += File.separator;
            }

            try {
                sonarqubeService.analyseProject(projectName, projectKey, sonarqubeConfPath, projectSourceDir);
            } catch (IOException e) {
                // TODO: handle error
                e.printStackTrace();
            }
        }).start();

//        scanRepository(repositoryPath);
//        toggleCloneProcessingVisibility(false);
//        viewRepositoryComposition();
//        propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, repositoryPath);
    }

    public void stop() {
        if (view == null) {
            return;
        }

        final Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
        parentStage.show();
    }

    @Override
    public void start(int totalTasks) {
    }

    @Override
    public void beginTask(String title, int totalWork) {
        System.out.println("Process started");
        Platform.runLater(() -> {
            statusLabel.setText(title);
            progressBar.setProgress(0);
            currentProgress = 0;
            totalProgress = totalWork;
        });
    }

    @Override
    public void update(int completed) {
        System.out.println(String.format("%d more", completed));
        Platform.runLater(() -> {
            currentProgress += completed;
            if (totalProgress > 0) {
                progressBar.setProgress((double)currentProgress / totalProgress);
            }
        });
    }

    @Override
    public void endTask() {
        System.out.println("Process finished");
        Platform.runLater(() -> {
            progressBar.setProgress(1.0);
        });
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void onStatusChange(String statusDescripton, Long currentProgress, Long totalProgress) {
        Platform.runLater(() -> {
            statusLabel.setText(statusDescripton);
            System.out.println("Current: " + currentProgress + ", total: " + totalProgress);
            if (currentProgress != null && totalProgress != null && totalProgress > 0) {
                progressBar.setProgress((double)currentProgress / totalProgress);
            }
            else {
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            }
        });
    }

    @Override
    public void onStageChange(InfoStage stage, String description) {
        Platform.runLater(() -> {
            stageLabel.setText(description);
        });
    }

    @Override
    public void onError(ErrorCode errorCode, String message) {
        // TODO: show error dialog
        System.err.println(message);
    }

    @Override
    public void onComplete(String report) {
        // TODO: complete process
        Platform.runLater(() -> {
            whiteboxStage.close();
            parentStage.show();
        });
    }
}
