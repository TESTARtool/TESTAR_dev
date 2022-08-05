package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import nl.ou.testar.jfx.dashboard.DashboardDelegate;
import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.sonarqube.SonarqubeService;
import org.fruit.monkey.sonarqube.SonarqubeServiceDelegate;
import org.fruit.monkey.sonarqube.SonarqubeServiceImpl;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.settingsdialog.codeanalysis.CodeAnalysisService;
import org.testar.settingsdialog.codeanalysis.CodeAnalysisServiceImpl;
import org.testar.settingsdialog.codeanalysis.RepositoryLanguageComposition;
import org.testar.settingsdialog.vcs.GitCredentials;
import org.testar.settingsdialog.vcs.GitService;
import org.testar.settingsdialog.vcs.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class WhiteboxTestLauncher implements /*ProgressMonitor, */SonarqubeServiceDelegate {

    // TODO: should use a special test protocol

    private JfxProgressMonitor progressMonitor;

    private DashboardDelegate dashboardDelegate;

    private String projectName;
    private String projectKey;
    private GitCredentials gitCredentials;

    private final CodeAnalysisService codeAnalysisService = new CodeAnalysisServiceImpl();
    private final SonarqubeService sonarqubeService = new SonarqubeServiceImpl("sonarqube");

    public WhiteboxTestLauncher(JfxProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
        sonarqubeService.setDelegate(this);
    }

    public void setDashboardDelegate(DashboardDelegate dashboardDelegate) {
        this.dashboardDelegate = dashboardDelegate;
    }

    public DashboardDelegate getDashboardDelegate() {
        return dashboardDelegate;
    }

    public void start(Stage stage, Settings settings) throws IOException {
        progressMonitor.start(stage);

        // Clone GIT repository

        final GitService gitService = new GitServiceImpl();
        final String repositoryUrl = settings.get(ConfigTags.GitUrl);

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

        progressMonitor.updateStage("Cloning repository");
        new Thread(() -> {
            Path repositoryPath;
            if (gitCredentials == null) {
                repositoryPath = gitService.cloneRepository(repositoryUrl, progressMonitor, branchName);
            }
            else {
                repositoryPath = gitService.cloneRepository(repositoryUrl, gitCredentials, progressMonitor, branchName);
            }
            System.out.println("...done");

            progressMonitor.updateStage("Analysing code");
            progressMonitor.beginTask("Scanning project", 0);

            RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);

            progressMonitor.displayLanguages(composition.getRepositoryLanguages());

            String projectSourceDir = repositoryPath.toString();
            if (!projectSourceDir.substring(projectSourceDir.length() - 1).equals(File.separator)) {
                projectSourceDir += File.separator;
            }

            final SonarqubeService sonarqubeService = new SonarqubeServiceImpl("sonarqube");
            sonarqubeService.setDelegate(this);

            try {
                sonarqubeService.analyseProject(projectName, projectKey, sonarqubeConfPath, projectSourceDir);
            } catch (IOException | TestarServiceException e) {
                System.out.println("Whitebox test failed: " + e.getClass().getName());
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Whitebox testing exception");
                alert.setHeaderText("Project analysis failure");
                alert.setContentText(e.getLocalizedMessage());

                alert.showAndWait();
                progressMonitor.stop();
            }
        }).start();
    }

    @Override
    public void onStatusChange(String statusDescripton, Long currentProgress, Long totalProgress) {
        progressMonitor.update(statusDescripton, currentProgress, totalProgress);
    }

    @Override
    public void onStageChange(InfoStage stage, String description) {
        progressMonitor.updateStage(description);
    }

    @Override
    public void onError(ErrorCode errorCode, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Whitebox testing exception");
            alert.setHeaderText("Sonarqube service failure");
            alert.setContentText(message);

            alert.showAndWait();
//            stop();
        });
        progressMonitor.stop();
    }

    @Override
    public void onComplete(String report) {
        progressMonitor.stop();
        Platform.runLater(() -> {
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
