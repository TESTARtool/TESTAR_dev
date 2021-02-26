package org.fruit.monkey.vcs;


import org.eclipse.jgit.lib.ProgressMonitor;

public interface GitService {

    boolean cloneRepository(String repositoryUrl, ProgressMonitor progressMonitor);

    boolean cloneRepository(String repositoryUrl, GitCredentials gitCredentials, ProgressMonitor progressMonitor);
}
