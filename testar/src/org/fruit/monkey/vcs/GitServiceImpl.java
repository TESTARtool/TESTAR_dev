package org.fruit.monkey.vcs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

public class GitServiceImpl implements GitService {

    private static final String LOCAL_REPOSITORIES_PATH = "";

    @Override
    public boolean cloneRepository(String repositoryUrl) {
        try {
            Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(new File(LOCAL_REPOSITORIES_PATH))
                    .call();
            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cloneRepositoryWithAuth(String repositoryUrl, GitAuth gitAuth) {
        try {
            Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(new File(LOCAL_REPOSITORIES_PATH))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitAuth.getUsername(), gitAuth.getPassword()))
                    .call();
            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }    }
}
