package nl.ou.testar.jfx.settings.child;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import nl.ou.testar.jfx.core.ViewController;

import java.io.IOException;

public class ChildSettingsController extends ViewController {

    private String contentResourcePath;

    public ChildSettingsController(String title, String resourcePath) {
        super(title, "jfx/settings_child.fxml");
        this.contentResourcePath = resourcePath;
    }

    @Override
    public Parent obtainView() throws IOException {
        Parent view = super.obtainView();
        VBox contentBox = (VBox) view.lookup("#contentBox");
        ObservableList<Node> children = contentBox.getChildren();
        if (children.size() == 0) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(contentResourcePath));
            Parent contentView = loader.load();
            children.add(contentView);
            contentViewDidLoad(contentView);
        }
        return view;
    }

    public void contentViewDidLoad(Parent contentView) {
        //To be overriden
    }
}
