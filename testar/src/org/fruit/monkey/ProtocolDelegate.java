package org.fruit.monkey;

import java.net.URI;

public interface ProtocolDelegate {
    void popupMessage(String message);
    void openURI(URI uri);
}
