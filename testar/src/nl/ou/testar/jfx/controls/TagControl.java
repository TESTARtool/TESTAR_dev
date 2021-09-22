package nl.ou.testar.jfx.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class TagControl extends Parent {

    public interface Delegate {
        void onClose();
    }

    private Delegate delegate;
    final String tag;

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public String getTag() {
        return tag;
    }

    public TagControl(String tag) {
        this.tag = tag;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/tag.fxml"));
        try {
            Parent view = loader.load();
            Label tagLabel = (Label) view.lookup("#tagLabel");
            tagLabel.setText(tag);
            Button btnClose = (Button) view.lookup("#btnClose");
            btnClose.setOnAction(event -> {
                if (delegate != null) {
                    delegate.onClose();
                }
            });

            getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
