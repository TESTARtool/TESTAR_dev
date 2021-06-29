package org.fruit.monkey.mysql;

public interface MySqlServiceDelegate {
    enum State {
        BUILDING_IMAGE, STARTING_SERVICE, CONNECTING, READY
    }

    void onStateChanged(State state, String description);
    void onServiceReady(String url);
}
