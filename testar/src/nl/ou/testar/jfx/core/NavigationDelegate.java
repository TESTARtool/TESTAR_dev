package nl.ou.testar.jfx.core;

import javafx.scene.Parent;

public interface NavigationDelegate {
    void onViewControllerActivated(ViewController viewController, Parent view);
}
