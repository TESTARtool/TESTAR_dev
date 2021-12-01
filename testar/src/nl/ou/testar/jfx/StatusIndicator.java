package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.fruit.monkey.Settings;
import org.fruit.monkey.sonarqube.AdvancedDockerPoolServiceDelegate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StatusIndicator implements ProgressMonitor, AdvancedDockerPoolServiceDelegate {

    protected Parent view;
    protected Stage parentStage;
    protected Stage indicatorStage;

    protected Label stageLabel;
    protected Label statusLabel;
    protected ProgressBar progressBar;

    protected int currentProgress;
    protected int totalProgress;

    @Override
    public void start(int totalTasks) {
    }

    public void start(Stage stage, Settings settings) throws IOException {
        parentStage = stage;
        parentStage.hide();
        indicatorStage = new Stage(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/whitebox_test_status.fxml"));
        view = loader.load();

        stageLabel = (Label) view.lookup("#procStage");
        statusLabel = (Label) view.lookup("#procStatus");
        progressBar = (ProgressBar) view.lookup("#procProgressBar");

        indicatorStage.setScene(new Scene(view));
        indicatorStage.show();
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
    public void beginTask(String title, int totalWork) {
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
        Platform.runLater(() -> {
            indicatorStage.close();
            parentStage.show();
        });
    }
}
