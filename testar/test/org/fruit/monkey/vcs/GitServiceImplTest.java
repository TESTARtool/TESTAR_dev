package org.fruit.monkey.vcs;
import static org.junit.Assert.*;

import org.eclipse.jgit.lib.TextProgressMonitor;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class GitServiceImplTest {

    private final static String PUBLIC_REPOSITORY_NAME = "test-clone";
    private final static String PUBLIC_REPOSITORY_URL = "https://github.com/ivs-testar-tester/test-clone.git";
    private final static String PRIVATE_REPOSITORY_NAME = "test-clone-private";
    private final static String PRIVATE_REPOSITORY_URL = "https://github.com/ivs-testar-tester/test-clone-private.git";
    private final static String PRIVATE_REPOSITORY_USERNAME = "ivs.testar.tester@gmail.com";
    private final static String PRIVATE_REPOSITORY_CORRECT_PASSWORD = "Testing123456!";
    private final static String PRIVATE_REPOSITORY_WRONG_PASSWORD = "Testing123456";
    private GitService gitService;

    @Before
    public void setUp() throws IOException {
        gitService = new GitServiceImpl();
        removeLocalRepository(PUBLIC_REPOSITORY_NAME);
        removeLocalRepository(PRIVATE_REPOSITORY_NAME);

    }

    private static void removeLocalRepository(String repositoryName) throws IOException {
        Path repositoryDirectory = Paths.get(System.getProperty("user.dir"))
                .getParent()
                .resolve(GitServiceImpl.LOCAL_REPOSITORIES_PATH)
                .resolve(repositoryName);
        if(Files.exists(repositoryDirectory)) {
            Files.walk(repositoryDirectory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @AfterClass
    public static void tearDown() throws IOException {
        removeLocalRepository(PUBLIC_REPOSITORY_NAME);
        removeLocalRepository(PRIVATE_REPOSITORY_NAME);
    }

    @Test
    public void cloneRepository() {
        Path resultSuccess = gitService.cloneRepository(PUBLIC_REPOSITORY_URL, new TextProgressMonitor(), null);
        Path resultDirectoryExistsError = gitService.cloneRepository(PUBLIC_REPOSITORY_URL, new TextProgressMonitor(), null);
        assertNotNull(resultSuccess);
        assertNull(resultDirectoryExistsError);
    }

    @Test
    public void cloneRepositoryWithAuth() {
        GitCredentials wrongCredentials = new GitCredentials(PRIVATE_REPOSITORY_USERNAME, PRIVATE_REPOSITORY_WRONG_PASSWORD);
        GitCredentials correctCredentials = new GitCredentials(PRIVATE_REPOSITORY_USERNAME, PRIVATE_REPOSITORY_CORRECT_PASSWORD);

        Path resultAuthError = gitService.cloneRepository(PRIVATE_REPOSITORY_URL, wrongCredentials, new TextProgressMonitor(), null);
        Path resultAuthSuccess = gitService.cloneRepository(PRIVATE_REPOSITORY_URL, correctCredentials, new TextProgressMonitor(), null);
        Path resultDirectoryExistsError = gitService.cloneRepository(PRIVATE_REPOSITORY_URL, correctCredentials, new TextProgressMonitor(), null);

        assertNull(resultAuthError);
        assertNotNull(resultAuthSuccess);
        assertNull(resultDirectoryExistsError);
    }
}