package org.fruit.monkey.vcs;

import java.beans.PropertyChangeListener;

public interface GitService {

    boolean cloneRepository(String repositoryUrl);

    boolean cloneRepository(String repositoryUrl, GitCredentials gitCredentials);
}
