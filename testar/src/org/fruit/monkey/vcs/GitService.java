package org.fruit.monkey.vcs;

public interface GitService {

    boolean cloneRepository(String repositoryUrl);

    boolean cloneRepositoryWithAuth(String repositoryUrl, GitAuth gitAuth);
}
