package nl.ou.testar.jfx;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.testar.monkey.Settings;
import org.testar.settingsdialog.codeanalysis.RepositoryLanguage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class JfxProgressMonitor implements ProgressMonitor {

    private Parent view;
    private Stage parentStage;
    private Stage whiteboxStage;

    private Label stageLabel;
    private Label statusLabel;
    private ProgressBar progressBar;
    private VBox contentBox;

    private Long currentProgress;
    private Long totalProgress;

    private final static int HEADER_HEIGHT = 56;
    private final static int ITEM_HEIGHT = 44;

    @Override
    public void start(int totalTasks) {
    }

    @Override
    public void beginTask(String title, int totalWork) {
        Platform.runLater(() -> {
            statusLabel.setText(title);
            currentProgress = 0L;
            totalProgress = (long)totalWork;
            progressBar.setProgress(totalWork > 0 ? 0 : ProgressBar.INDETERMINATE_PROGRESS);
        });
    }

    @Override
    public void update(int completed) {
        Platform.runLater(() -> {
            currentProgress += completed;
            if (totalProgress!= null && totalProgress > 0) {
                progressBar.setProgress((double)currentProgress / totalProgress);
            }
        });
    }

    public void update(String status, Long currentProgress, Long totalProgress) {
        this.currentProgress = currentProgress;
        this.totalProgress = totalProgress;
        Platform.runLater(() -> {
            statusLabel.setText(status);
            if (totalProgress!= null && totalProgress > 0) {
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

    public void start(Stage stage, Settings settings) {
        Platform.runLater(() -> {
            parentStage = stage;
            parentStage.hide();
            whiteboxStage = new Stage(StageStyle.UNDECORATED);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/progress.fxml"));
            try {
                view = loader.load();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            stageLabel = (Label) view.lookup("#procStage");
            statusLabel = (Label) view.lookup("#procStatus");
            progressBar = (ProgressBar) view.lookup("#procProgressBar");
            contentBox = (VBox) view.lookup("#contentBox");

            whiteboxStage.setScene(new Scene(view));
            whiteboxStage.show();
        });
    }

    public void stop() {
        Platform.runLater(() -> {
            if (view == null) {
                return;
            }

            final Stage stage = (Stage) view.getScene().getWindow();
            stage.close();
            parentStage.show();
        });
    }

    public void updateStage(String title) {
        Platform.runLater(() -> {
            stageLabel.setText(title);
        });
    }

    public void displayLanguages(List<RepositoryLanguage> languages) {
        Platform.runLater(() -> {
            try {
                FXMLLoader headerLoader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/lang_header.fxml"));
                contentBox.getChildren().add(headerLoader.load());
                whiteboxStage.setHeight(whiteboxStage.getHeight() + HEADER_HEIGHT);
                for (RepositoryLanguage language : languages) {
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
    }
}
