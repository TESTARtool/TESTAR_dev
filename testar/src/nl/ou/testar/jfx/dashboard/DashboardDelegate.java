package nl.ou.testar.jfx.dashboard;

import java.net.URI;
import org.testar.monkey.Settings;

public interface DashboardDelegate {
    void startTesting(Settings settings);
    void openURI(URI uri);
}
