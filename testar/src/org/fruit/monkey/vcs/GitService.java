package org.fruit.monkey.vcs;

public interface GitService {

    boolean cloneRepository(String repositoryUrl);

    boolean cloneRepository(String repositoryUrl, GitCredentials gitAuth);
}
