package org.testar.settingsdialog.vcs;


import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;

import java.nio.file.Path;

public interface GitService {

    Path cloneRepository(String repositoryUrl, ProgressMonitor progressMonitor, String branchName)
      throws GitAPIException, JGitInternalException;

    Path cloneRepository(String repositoryUrl, GitCredentials gitCredentials, ProgressMonitor progressMonitor, String branchName)
      throws GitAPIException, JGitInternalException;
}
