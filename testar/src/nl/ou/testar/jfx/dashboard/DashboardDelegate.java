package nl.ou.testar.jfx.dashboard;

import nl.ou.testar.jfx.StartupProgressMonitor;

import java.net.URI;
import org.testar.monkey.Settings;

public interface DashboardDelegate {
    void startTesting(Settings settings, StartupProgressMonitor progressMonitor);
    void openURI(URI uri);
}
