package org.testar.settings.backend;

import java.io.File;
import java.io.IOException;

import org.testar.monkey.Util;
import org.testar.settings.Settings;

class SettingsFile {

    public final String fileName;
    private final File file;
    private long lastModified;
    private Settings settings;

    SettingsFile(String fileName) throws IOException {
        this(fileName, Settings.loadSettings(new String[0], fileName));
    }

    SettingsFile(String fileName, Settings settings) {
        this.fileName = fileName;
        this.file = new File(fileName);
        this.settings = settings;
        this.lastModified = getLastModified();
    }

    public Settings getSettings() {
        return settings;
    }

    public long getLastModified() {
        return file.lastModified();
    }

    public void updateLastModified() {
        this.lastModified = getLastModified();
    }

    public boolean wasModified() {
        long newModifiedTime = file.lastModified();
        return newModifiedTime != lastModified;
    }

    public void reloadSettingsIfModified() throws IOException {
        if (wasModified()) {
            settings = Settings.loadSettings(new String[0], fileName);

            updateLastModified();
        }
    }

    public void saveToFile() throws IOException {
        Util.saveToFile(settings.toFileString(), fileName);
        updateLastModified();
    }
}
