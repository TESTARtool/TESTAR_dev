package nl.ou.testar.jfx.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.lang.ref.WeakReference;

public abstract class ViewController {

    private WeakReference<Parent> viewReference = new WeakReference<>(null);
    private String title;
    private String resourcePath;

    public String getTitle() {
        return title;
    }

    public ViewController(String title, String resourcePath) {
        this.title = title;
        this.resourcePath = resourcePath;
    }

    Parent obtainView() throws IOException {
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

    public NavigationController addChild(ViewController child, NavigationDelegate delegate) {
        NavigationController navigationController = new NavigationController(child);
        navigationController.startWithDelegate(delegate);
        return navigationController;
    }
}
