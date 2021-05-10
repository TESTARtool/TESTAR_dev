package org.fruit.monkey.vcs;


import org.eclipse.jgit.lib.ProgressMonitor;

import java.nio.file.Path;

public interface GitService {

    Path cloneRepository(String repositoryUrl, ProgressMonitor progressMonitor);

    Path cloneRepository(String repositoryUrl, GitCredentials gitCredentials, ProgressMonitor progressMonitor);
}
