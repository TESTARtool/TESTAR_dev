package org.fruit.monkey;

import org.testar.monkey.Settings;

import java.net.URI;

public interface ProtocolDelegate {
    void updateStage(String stage);
    void startProgress(Settings settings, String status);
    void updateStatus(String status, int timeout);
    void endProgress();
    void popupMessage(String message);
    void openURI(URI uri);
}
