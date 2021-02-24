package org.fruit.monkey.vcs;

import java.beans.PropertyChangeListener;

public interface GitService {

    void cloneRepository(String repositoryUrl, PropertyChangeListener cloningListener);

    void cloneRepository(String repositoryUrl, GitCredentials gitAuth, PropertyChangeListener cloningListener);
}
