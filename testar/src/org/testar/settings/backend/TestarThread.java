package org.testar.settings.backend;

import org.testar.settings.Settings;

import static org.testar.monkey.Main.runTestar;


public class TestarThread extends Thread {

    private final Settings settings;
    private Exception exception;

    public TestarThread(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        System.out.println("Running new Testar thread...");
        try {
            runTestar(this.settings);
        } catch (Exception e) {
            System.err.println("Error starting new Testar thread:" + e.getMessage());
            this.exception = e;
        } finally {
            WebSocket.clearActiveThread(this, exception);
        }
    }

    public Exception getException() {
        return exception;
    }
}
