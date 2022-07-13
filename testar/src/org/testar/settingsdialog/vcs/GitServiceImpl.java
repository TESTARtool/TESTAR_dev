package org.testar.settingsdialog.vcs;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitServiceImpl implements GitService {

    public static final String LOCAL_REPOSITORIES_PATH = "cloned";

    @Override
    public Path cloneRepository(String repositoryUrl, ProgressMonitor progressMonitor, String branchName) {
            try {
                File repositoryDirectory = prepareRepositoryDirectory(repositoryUrl);
                CloneCommand cloneCommand = Git.cloneRepository()
                        .setURI(repositoryUrl)
                        .setDirectory(repositoryDirectory)
                        .setProgressMonitor(progressMonitor);

                if (branchName != null && branchName.length() > 0) {
                    cloneCommand = cloneCommand.setBranch(branchName);
                }

                cloneCommand.call();
                return repositoryDirectory.toPath();
            } catch (GitAPIException | JGitInternalException e) {
                e.printStackTrace();
                return null;
            }

    }

    @Override
    public Path cloneRepository(String repositoryUrl, GitCredentials gitCredentials, ProgressMonitor progressMonitor, String branchName) {
            try {
                File repositoryDirectory = prepareRepositoryDirectory(repositoryUrl);
                CloneCommand cloneCommand =Git.cloneRepository()
                        .setURI(repositoryUrl)
                        .setDirectory(repositoryDirectory)
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitCredentials.getUsername(), gitCredentials.getPassword()))
                        .setProgressMonitor(progressMonitor);

                if (branchName != null && branchName.length() > 0) {
                    cloneCommand = cloneCommand.setBranch(branchName);
                }

                cloneCommand.call();

                return repositoryDirectory.toPath();
            } catch (GitAPIException | JGitInternalException e) {
                e.printStackTrace();
                return null;
            }
    }

    private File prepareRepositoryDirectory(String repositoryUrl) {
        Path baseDirPath = Paths.get(System.getProperty("user.dir"))
                .getParent()
                .resolve(LOCAL_REPOSITORIES_PATH);
        Path repositoryPath = baseDirPath.resolve(extractRepositoryName(repositoryUrl));
        File repositoryDirectory = new File(repositoryPath.toAbsolutePath().toString());
        return repositoryDirectory;
    }

    private String extractRepositoryName(String repositoryUrl) {
        String[] urlElements = repositoryUrl.split("/");
        String repositoryNameWithExtension = urlElements[urlElements.length-1];
        String repositoryName = repositoryNameWithExtension.split("\\.")[0];
        return repositoryName;
    }
}
