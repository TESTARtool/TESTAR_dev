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

public abstract class ChildSettingsController extends ViewController {

    public ChildSettingsController(String title, Settings settings) {
        super(title, "jfx/settings_child.fxml", settings);
    }
    @Override
    public void viewDidLoad(Parent view) {
        super.viewDidLoad(view);
        Button btnBack = (Button) view.lookup("#btnBack");
        btnBack.setOnAction(event -> {
            getNavigationController().navigateBack();
        });
        Button btnSave = (Button) view.lookup("#btnSave");
        btnSave.setOnAction(event -> {
            save(settings);
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

    protected abstract void save(Settings settings);
}
