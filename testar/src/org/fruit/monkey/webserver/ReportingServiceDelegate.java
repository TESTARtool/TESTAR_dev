package org.fruit.monkey.webserver;

public interface ReportingServiceDelegate {
    enum State {
        BUILDING_IMAGE, STARTING_SERVICE, CONNECTING, READY
    }

    void onStateChanged(State state, String description);
    void onServiceReady(String url);
}
