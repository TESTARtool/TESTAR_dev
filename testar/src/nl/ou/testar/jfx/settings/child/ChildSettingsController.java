package nl.ou.testar.jfx.settings.child;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nl.ou.testar.jfx.core.ViewController;
import org.fruit.Pair;
import org.fruit.monkey.Settings;

import java.io.IOException;

public class ChildSettingsController extends ViewController {

//    private String contentResourcePath;

    public ChildSettingsController(String title, Settings settings) {
        super(title, "jfx/settings_child.fxml", settings);
//        this.contentResourcePath = resourcePath;
    }

//    @Override
//    public Parent obtainView() throws IOException {
//        Parent view = super.obtainView();
//        VBox contentBox = (VBox) view.lookup("#contentBox");
//        ObservableList<Node> children = contentBox.getChildren();
//        if (children.size() == 0) {
//            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(contentResourcePath));
//            Parent contentView = loader.load();
//            children.add(contentView);
//            contentViewDidLoad(contentView);
//        }
//        return view;
//    }
//
//    public void contentViewDidLoad(Parent contentView) {
//        //To be overriden
//    }

    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        Button btnBack = (Button) view.lookup("#btnBack");
        btnBack.setOnAction(event -> {
            getNavigationController().navigateBack();
        });
    }

    // TODO: put some docs
    protected void putSection(Parent contentView, String title, String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("jfx/settings_section.fxml"));
        VBox sectionBox =  (VBox) loader.load();

        loader = new FXMLLoader(getClass().getClassLoader().getResource(resourcePath));
        sectionBox.getChildren().add(loader.load());

        Label titleLabel = (Label) sectionBox.lookup("#titleLabel");
        titleLabel.setText(title);

        VBox contentBox = (VBox) contentView.lookup("#contentBox");
        contentBox.getChildren().add(sectionBox);
    }
}
