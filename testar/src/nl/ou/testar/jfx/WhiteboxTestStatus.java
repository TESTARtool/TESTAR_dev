package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
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
import org.fruit.monkey.vcs.GitCredentials;
import org.fruit.monkey.vcs.GitService;
import org.fruit.monkey.vcs.GitServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

public class WhiteboxTestStatus implements ProgressMonitor {

    private Parent view;
    private Stage mainStage;

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

    public void start(Stage stage, Settings settings) throws IOException {
        mainStage = stage;
        mainStage.hide();
        Stage whiteboxStage = new Stage(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/whitebox_test_status.fxml"));
        view = loader.load();
        whiteboxStage.setScene(new Scene(view));
        whiteboxStage.show();

        stageLabel = (Label) view.lookup("#procStage");
        statusLabel = (Label) view.lookup("#procStatus");
        progressBar = (ProgressBar) view.lookup("#procProgressBar");
        contentBox = (VBox) view.lookup("#contentBox");

        // Clone GIT repository

        final GitService gitService = new GitServiceImpl();
        final String repositoryUrl = settings.get(ConfigTags.GitUrl);

        final String branchName = settings.get(ConfigTags.GitBranch, null);

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
            } else {
                repositoryPath = gitService.cloneRepository(repositoryUrl, gitCredentials, this, branchName);
            }
            System.out.println("...done");

            RepositoryLanguageComposition composition = codeAnalysisService.scanRepository(repositoryPath);

            Platform.runLater(() -> {
                try {
                    FXMLLoader headerLoader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/lang_header.fxml"));
                    contentBox.getChildren().add(headerLoader.load());
                    whiteboxStage.setHeight(whiteboxStage.getHeight() + HEADER_HEIGHT);
                    for (RepositoryLanguage language : composition.getRepositoryLanguages()) {
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

                stageLabel.setText("All done");
                statusLabel.setText("Press any key to close");
                progressBar.setVisible(false);

                whiteboxStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        stop();
                    }
                });
            });
        }).start();
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

    public void stop() {
        if (view == null) {
            return;
        }

        final Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
        mainStage.show();
    }
}
