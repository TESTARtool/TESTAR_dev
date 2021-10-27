package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.ou.testar.jfx.dashboard.DashboardDelegate;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.codeanalysis.CodeAnalysisService;
import org.fruit.monkey.codeanalysis.CodeAnalysisServiceImpl;
import org.fruit.monkey.codeanalysis.RepositoryLanguage;
import org.fruit.monkey.codeanalysis.RepositoryLanguageComposition;
import org.fruit.monkey.sonarqube.SonarqubeService;
import org.fruit.monkey.sonarqube.SonarqubeServiceDelegate;
import org.fruit.monkey.sonarqube.SonarqubeServiceImpl;
import org.fruit.monkey.vcs.GitCredentials;
import org.fruit.monkey.vcs.GitService;
import org.fruit.monkey.vcs.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class WhiteboxTestLauncher implements ProgressMonitor, SonarqubeServiceDelegate {

    // TODO: should use a special test protocol

    private Parent view;
    private Stage parentStage;
    private Stage whiteboxStage;

    private Label stageLabel;
    private Label statusLabel;
    private ProgressBar progressBar;
    private VBox contentBox;

    private int currentProgress;
    private int totalProgress;

    private DashboardDelegate dashboardDelegate;

    private String projectName;
    private String projectKey;
    private GitCredentials gitCredentials;

    private final static int HEADER_HEIGHT = 56;
    private final static int ITEM_HEIGHT = 44;

    private final CodeAnalysisService codeAnalysisService = new CodeAnalysisServiceImpl();
    private final SonarqubeService sonarqubeService = new SonarqubeServiceImpl("sonarqube");

    public WhiteboxTestLauncher() {
        sonarqubeService.setDelegate(this);
    }

    public void setDashboardDelegate(DashboardDelegate dashboardDelegate) {
        this.dashboardDelegate = dashboardDelegate;
    }

    public DashboardDelegate getDashboardDelegate() {
        return dashboardDelegate;
    }

    public void start(Stage stage, Settings settings) throws IOException {
        parentStage = stage;
        parentStage.hide();
        whiteboxStage = new Stage(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/whitebox_test_status.fxml"));
        view = loader.load();

        stageLabel = (Label) view.lookup("#procStage");
        statusLabel = (Label) view.lookup("#procStatus");
        progressBar = (ProgressBar) view.lookup("#procProgressBar");
        contentBox = (VBox) view.lookup("#contentBox");

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

        projectName = settings.get(ConfigTags.SonarProjectName, "Demo");
        projectKey = settings.get(ConfigTags.SonarProjectKey, "demo");

        if (settings.get(ConfigTags.GitAuthRequired, false)) {
            gitCredentials = new GitCredentials(settings.get(ConfigTags.GitUsername), settings.get(ConfigTags.GitToken));
        }

        stageLabel.setText("Cloning repository");
        new Thread(() -> {
            Path repositoryPath;
            if (gitCredentials == null) {
                repositoryPath = gitService.cloneRepository(repositoryUrl, this, branchName);
            }
            else {
                repositoryPath = gitService.cloneRepository(repositoryUrl, gitCredentials, this, branchName);
            }
            System.out.println("...done");

            Platform.runLater(() -> {
                stageLabel.setText("Analysing code");
                statusLabel.setText("Scaning project");
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            });

            RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);

            Platform.runLater(() -> {
                try {
                    FXMLLoader headerLoader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/lang_header.fxml"));
                    contentBox.getChildren().add(headerLoader.load());
                    whiteboxStage.setHeight(whiteboxStage.getHeight() + HEADER_HEIGHT);
                    for (RepositoryLanguage language: composition.getRepositoryLanguages()) {
                        whiteboxStage.setHeight(whiteboxStage.getHeight() + ITEM_HEIGHT);
                        FXMLLoader itemLoader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/lang_item.fxml"));
                        Parent itemView = itemLoader.load();
                        Label itemLabel = (Label) itemView.lookup("#lang");
                        ProgressBar itemProgressBar = (ProgressBar) itemView.lookup("#ratio");
                        BigDecimal percentage = language.getPercentage();
                        itemLabel.setText(String.format("%d%% %s", percentage.intValue(), language.getSupportedLanguage().getName()));
                        itemProgressBar.setProgress(0.01 * percentage.doubleValue());
                        contentBox.getChildren().add(itemView);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            String projectSourceDir = repositoryPath.toString();
            if (!projectSourceDir.substring(projectSourceDir.length() - 1).equals(File.separator)) {
                projectSourceDir += File.separator;
            }

            try {
                sonarqubeService.analyseProject(projectName, projectKey, sonarqubeConfPath, projectSourceDir);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Whitebox testing exception");
                alert.setHeaderText("Project analysis failure");
                alert.setContentText(e.getLocalizedMessage());

                alert.showAndWait();
                stop();
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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Whitebox testing exception");
            alert.setHeaderText("Sonarqube service failure");
            alert.setContentText(message);

            alert.showAndWait();
            stop();
        });
    }

    @Override
    public void onComplete(String report) {
        System.out.println("Report: " + report);
        Platform.runLater(() -> {
            whiteboxStage.close();
            parentStage.show();

            if (dashboardDelegate != null) {
                try {
                    dashboardDelegate.openURI(new URI("http://localhost:9000/dashboard?id=" + projectKey));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
