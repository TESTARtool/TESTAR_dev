package nl.ou.testar.jfx.core;

import javafx.scene.Parent;

import java.io.IOException;
import java.util.Stack;

public class NavigationController {

    private NavigationDelegate delegate;
    private Stack<ViewController> viewControllerStack = new Stack<>();
    private ViewController currentViewController;
    private ViewController rootViewController;

    public NavigationController(ViewController rootViewController) {
        this.currentViewController = rootViewController;
        this.rootViewController = rootViewController;
        rootViewController.setNavigationController(this);
    }

    public NavigationDelegate getDelegate() {
        return delegate;
    }

    void setDelegate(NavigationDelegate delegate) {
        this.delegate = delegate;
    }

    public void startWithDelegate(NavigationDelegate delegate) {
        this.delegate = delegate;
        try {
            Parent view = currentViewController.obtainView();
            currentViewController.viewWillAppear(view);
            delegate.onViewControllerActivated(currentViewController, view);
            currentViewController.viewDidAppear(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ViewController getRootViewController() {
        return rootViewController;
    }

    public ViewController getCurrentViewController() {
        return currentViewController;
    }

    public void navigateTo(ViewController viewController, Boolean pushToStack) {
        //TODO: delegate shouldn't be nil
        try {
            Parent view = viewController.obtainView();
            ViewController previousViewController = currentViewController;
            previousViewController.viewWillDisappear();
            if (pushToStack) {
                viewControllerStack.push(currentViewController);
            }
            currentViewController = viewController;
            viewController.setNavigationController(this);

            currentViewController.viewWillAppear(view);
            delegate.onViewControllerActivated(currentViewController, view);
            previousViewController.viewDidDisappear();
            currentViewController.viewDidAppear(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBackAvailable() {
        return !currentViewController.equals(rootViewController);
    }

    public ViewController navigateBack() throws UnsupportedOperationException {
        if (currentViewController.equals(rootViewController)) {
            throw new UnsupportedOperationException("No way back from the root view controller");
        }

        try {
            ViewController redundantViewController = currentViewController;
            redundantViewController.viewWillDisappear();

            currentViewController = viewControllerStack.pop();

            Parent view = currentViewController.obtainView();
            currentViewController.viewWillAppear(view);
            delegate.onViewControllerActivated(currentViewController, view);
            redundantViewController.viewDidDisappear();
            currentViewController.viewDidAppear(view);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentViewController;
    }
}
