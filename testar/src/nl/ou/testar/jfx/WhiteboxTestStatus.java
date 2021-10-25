package nl.ou.testar.jfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.ou.testar.jfx.core.ViewController;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class WhiteboxTestStatus {

    private Parent view;
    private Stage mainStage;

    public void start(Stage stage) throws IOException {
        mainStage = stage;
        mainStage.hide();
        Stage whiteboxStage = new Stage(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/whitebox_test_status.fxml"));
        view = loader.load();
        whiteboxStage.setScene(new Scene(view));
        whiteboxStage.show();
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
