package org.fruit.monkey.vcs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitServiceImpl implements GitService {

    private static final String LOCAL_REPOSITORIES_PATH = "cloned";

    public static final String CLONING_PROPERTY = "cloned_successfully";

    private PropertyChangeSupport propertyChangeSupport;

    public GitServiceImpl() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public void cloneRepository(String repositoryUrl, PropertyChangeListener cloningListener) {
        propertyChangeSupport.addPropertyChangeListener(cloningListener);
        new Thread(() -> {
            try {
                Git.cloneRepository()
                        .setURI(repositoryUrl)
                        .setDirectory(prepareRepositoryDirectory(repositoryUrl))
                        .call();
                propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, true);
            } catch (GitAPIException e) {
                e.printStackTrace();
                propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, false);
            } finally {
                propertyChangeSupport.removePropertyChangeListener(cloningListener);
            }
        }).start();

    }

    @Override
    public void cloneRepository(String repositoryUrl, GitCredentials gitCredentials, PropertyChangeListener cloningListener) {
        propertyChangeSupport.addPropertyChangeListener(cloningListener);
        new Thread(() -> {
            try {
                Git.cloneRepository()
                        .setURI(repositoryUrl)
                        .setDirectory(prepareRepositoryDirectory(repositoryUrl))
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitCredentials.getUsername(), gitCredentials.getPassword()))
                        .call();
                propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, true);
            } catch (GitAPIException e) {
                e.printStackTrace();
                propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, false);
            } finally {
                propertyChangeSupport.removePropertyChangeListener(cloningListener);
            }
        }).start();
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
