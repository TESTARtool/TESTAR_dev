package nl.ou.testar.jfx.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class TagInput extends Parent {
    public interface Delegate {
        void tagTyped(String tag);
    }

    private TextField tagInputField;
    private Delegate delegate;

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public TagInput() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/new_tag.fxml"));
        try {
            Parent view = loader.load();
            tagInputField = (TextField) view.lookup("#newTagInput");
            tagInputField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\w*")) {
                    tagInputField.setText(newValue.replaceAll("\\W+", ""));
                }
            });
            tagInputField.setOnKeyPressed(event -> {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    final String tag = tagInputField.getText();
                    if (delegate != null && tag.length() > 0) {
                        delegate.tagTyped(tag);
                    }
                }
            });
            getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        tagInputField.clear();
    }
}
