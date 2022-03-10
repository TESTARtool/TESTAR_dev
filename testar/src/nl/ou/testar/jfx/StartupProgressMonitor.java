package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.ou.testar.jfx.dashboard.DashboardDelegate;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.testar.monkey.Settings;

import java.io.IOException;

public class StartupProgressMonitor implements ProgressMonitor {
    private Parent view;
    private Stage parentStage;

    private Label stageLabel;
    private Label statusLabel;
    private ProgressBar progressBar;
    private Stage progressStage;

    private int currentProgress;
    private int totalProgress;

    private DashboardDelegate dashboardDelegate;

    public void start(Stage stage, Settings settings) {
        Platform.runLater(() -> {
            parentStage = stage;
            parentStage.hide();
            progressStage = new Stage(StageStyle.UNDECORATED);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/progress_monitor.fxml"));
            try {
                view = loader.load();
            } catch (IOException e) {
                System.err.println("Cannot load progress monitor: "+ e.getMessage());
                e.printStackTrace();
                return;
            }

            stageLabel = (Label) view.lookup("#procStage");
            statusLabel = (Label) view.lookup("#procStatus");
            progressBar = (ProgressBar) view.lookup("#procProgressBar");

            progressStage.setScene(new Scene(view));
            progressStage.show();
        });
    }

    public void stop() {
        System.out.println("Stopping progress monitor");
        if (view == null) {
            return;
        }

        Platform.runLater(() -> {
            final Stage stage = (Stage) view.getScene().getWindow();
            stage.close();
            parentStage.show();
        });
    }

    @Override
    public void start(int totalTasks) {
    }

    public void setTitle(String title) {
        Platform.runLater(() -> {
            stageLabel.setText(title);
        });
    }

    @Override
    public void beginTask(String title, int totalWork) {
        Platform.runLater(() -> {
            statusLabel.setText(title);
            progressBar.setProgress(totalProgress > 0 ? 0 : ProgressBar.INDETERMINATE_PROGRESS);
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
}
