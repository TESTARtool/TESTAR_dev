package nl.ou.testar.jfx.dashboard;

import nl.ou.testar.jfx.StartupProgressMonitor;
import org.fruit.monkey.Settings;

import java.net.URI;

public interface DashboardDelegate {
    void startTesting(Settings settings, StartupProgressMonitor progressMonitor);
    void openURI(URI uri);
}
