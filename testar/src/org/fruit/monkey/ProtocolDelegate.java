package org.fruit.monkey;

import org.testar.monkey.Settings;

import java.net.URI;

public interface ProtocolDelegate {
    void startProgress(Settings settings, String status);
    void changeStatus(String status);
    void endProgress();
    void popupMessage(String message);
    void openURI(URI uri);
}
