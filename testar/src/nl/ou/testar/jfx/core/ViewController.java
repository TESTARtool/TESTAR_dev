package nl.ou.testar.jfx.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.testar.monkey.Settings;

import java.io.IOException;
import java.lang.ref.WeakReference;

public abstract class ViewController {

    private WeakReference<Parent> viewReference = new WeakReference<>(null);
    private String title;
    private String resourcePath;
    protected Settings settings;

    private NavigationController navigationController;

    public String getTitle() {
        return title;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }

    public ViewController(String title, String resourcePath, Settings settings) {
        this.title = title;
        this.resourcePath = resourcePath;
        this.settings = settings;
    }

    public Parent obtainView() throws IOException {
        Parent view = viewReference.get();
        if (view == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(resourcePath));
            view = loader.load();
            viewDidLoad(view);
        }
        viewReference = new WeakReference<>(view);
        return view;
    }

    public void viewDidLoad(Parent view) {
        // To be overridden
    }

    public void viewWillAppear(Parent view) {
        // To be overridden
    }

    public void viewDidAppear(Parent view) {
        // To be overridden
    }

    public void viewWillDisappear() {
        // To be overridden
    }

    public void viewDidDisappear() {
        // to be overridden
    }

    public boolean checkBeforeExit() {
        return true;
    }
}
