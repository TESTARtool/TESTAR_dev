package org.fruit.monkey.orientdb;

public interface OrientDBServiceDelegate {
    void onStateChanged(OrientDBServiceDelegate.State state, String description);

    void onServiceReady();

    enum State {
        BUILDING_IMAGE, STARTING_SERVICE, CONNECTING, READY
    }
}

