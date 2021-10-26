package nl.ou.testar.jfx.dashboard;

import org.fruit.monkey.Settings;

import java.net.URI;

public interface DashboardDelegate {
    void startTesting(Settings settings);
    void openURI(URI uri);
}
