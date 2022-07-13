package nl.ou.testar.jfx.controls;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.IOException;

public class FlashFeedback extends Parent {
    public FlashFeedback(String message) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/flash_feedback.fxml"));
        try {
            Parent view = loader.load();
            Label messageLabel = (Label) view.lookup("#messageText");
            messageLabel.setText(message);

            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.0), messageLabel);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(Animation.INDEFINITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
