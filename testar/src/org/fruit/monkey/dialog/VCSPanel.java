package org.fruit.monkey.dialog;

import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.fruit.monkey.vcs.GitCredentials;
import org.fruit.monkey.vcs.GitService;
import org.fruit.monkey.vcs.GitServiceImpl;

import javax.swing.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

public class VCSPanel extends JPanel {

    private JTextField gitRepositoryUrlTextField;
    private JTextField gitUsernameTextField;
    private JPasswordField gitPasswordField;
    private JCheckBox authorizationRequiredCheckBox;
    private JLabel gitRepositoryUrlLabel;
    private JLabel gitUsernameLabel;
    private JLabel gitPasswordLabel;
    private JLabel authorizationRequiredLabel;
    private JButton cloneButton;
    private JLabel cloneProcessingLabel;
    private JProgressBar cloneProcessingProgressBar;
    private JLabel cloneTaskLabel;
    private final static String GIT_URL_LABEL = "Repository URL";
    private final static String GIT_USERNAME_LABEL = "Username";
    private final static String GIT_PASSWORD_LABEL = "Password";
    private final static String AUTHORIZATION_REQUIRED_LABEL = "Authorization required";
    private final static String CLONE_BUTTON = "Clone";
    private final static String CLONE_ERROR_TITLE = "Clone error";
    private final static String CLONE_ERROR_MESSAGE = "Something went wrong, repository wasn't cloned cloned. Check whether the repository already exists locally or credentials are valid.";
    private final static String CLONE_SUCCESS_TITLE = "Clone success";
    private final static String CLONE_SUCCESS_MESSAGE = "Repository cloned successfully";
    private final static String CLONING_PROPERTY = "cloned_successfully";
    private final static String CLONE_PROCESSING_LABEL = "Cloning project...";


    private PropertyChangeSupport propertyChangeSupport;

    private GitService gitService;

    public VCSPanel() {
        gitService = new GitServiceImpl();
        propertyChangeSupport = new PropertyChangeSupport(this);
        initGitRepositoryUrlSection();
        initGitUsernameSection();
        initGitPasswordSection();
        initAuthorizationRequiredSection();
        initCloneSection();
        initLayout();
    }

    private void initGitRepositoryUrlSection() {
        gitRepositoryUrlLabel = new JLabel(GIT_URL_LABEL);
        gitRepositoryUrlTextField = new JTextField();
    }

    private void initGitUsernameSection() {
        gitUsernameLabel = new JLabel(GIT_USERNAME_LABEL);
        gitUsernameTextField = new JTextField();

    }

    private void initGitPasswordSection() {
        gitPasswordLabel = new JLabel(GIT_PASSWORD_LABEL);
        gitPasswordField = new JPasswordField();
    }

    private void initAuthorizationRequiredSection() {
        authorizationRequiredLabel = new JLabel(AUTHORIZATION_REQUIRED_LABEL);
        authorizationRequiredCheckBox = new JCheckBox();
        authorizationRequiredCheckBox.setSelected(true);
        authorizationRequiredCheckBox.addItemListener(e -> setAuthorizationFieldsEnabled(authorizationRequiredCheckBox.isSelected()));
    }

    private void setAuthorizationFieldsEnabled(boolean enabled) {
        gitUsernameTextField.setEnabled(enabled);
        gitPasswordField.setEnabled(enabled);
    }

    private void initCloneSection() {
        cloneButton = new JButton(CLONE_BUTTON);
        cloneButton.addActionListener(e -> {
            if(authorizationRequiredCheckBox.isSelected()) {
                cloneRepositoryWithAuth(this::cloneFinished);
            } else {
                cloneRepository(this::cloneFinished);
            }
            cloneButton.setEnabled(false);
        });
        cloneProcessingLabel = new JLabel(CLONE_PROCESSING_LABEL);
        cloneProcessingProgressBar = new JProgressBar();
        cloneTaskLabel = new JLabel();
        toggleCloneProcessingVisibility(false);
    }

    private void cloneRepository(PropertyChangeListener cloneListener) {
        toggleCloneProcessingVisibility(true);
        propertyChangeSupport.addPropertyChangeListener(cloneListener);
        new Thread(() -> {
            boolean cloneSuccess = gitService.cloneRepository(gitRepositoryUrlTextField.getText(), new ProgressBarMonitor());
            propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, cloneSuccess);
            toggleCloneProcessingVisibility(false);
        }).start();
    }

    private void toggleCloneProcessingVisibility(boolean visible) {
        cloneProcessingLabel.setVisible(visible);
        cloneProcessingProgressBar.setVisible(visible);
        cloneTaskLabel.setVisible(visible);
    }

    private void cloneRepositoryWithAuth(PropertyChangeListener cloneListener) {
        toggleCloneProcessingVisibility(true);
        propertyChangeSupport.addPropertyChangeListener(cloneListener);
        new Thread(() -> {
            GitCredentials credentials = new GitCredentials(gitUsernameTextField.getText(), new String(gitPasswordField.getPassword()));
            boolean cloneSuccess = gitService.cloneRepository(gitRepositoryUrlTextField.getText(), credentials, new ProgressBarMonitor());
            propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, cloneSuccess);
        }).start();
    }

    private void cloneFinished(PropertyChangeEvent evt) {
        Boolean cloneSuccessful = (Boolean) evt.getNewValue();
        if(cloneSuccessful) {
            showCloneSuccessDialog();
        } else {
            showCloneErrorDialog();
        }
        cloneButton.setEnabled(true);
        toggleCloneProcessingVisibility(false);
    }

    private void showCloneErrorDialog() {
        JOptionPane.showMessageDialog(this,
                CLONE_ERROR_MESSAGE,
                CLONE_ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showCloneSuccessDialog() {
        JOptionPane.showMessageDialog(this,
                CLONE_SUCCESS_MESSAGE,
                CLONE_SUCCESS_TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void initLayout() {
        GroupLayout groupLayout = new GroupLayout(this);
        this.setLayout(groupLayout);
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addContainerGap(18, 20)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(gitRepositoryUrlLabel, PREFERRED_SIZE, 120, PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(gitRepositoryUrlTextField, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(authorizationRequiredLabel, PREFERRED_SIZE, 120, PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(authorizationRequiredCheckBox, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(gitUsernameLabel, PREFERRED_SIZE, 120, PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(gitUsernameTextField, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(gitPasswordLabel, PREFERRED_SIZE, 120, PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(gitPasswordField, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(cloneButton, PREFERRED_SIZE, 65, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(cloneProcessingLabel, PREFERRED_SIZE, 120, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(cloneProcessingProgressBar, PREFERRED_SIZE, 200, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(cloneTaskLabel, PREFERRED_SIZE, 200, PREFERRED_SIZE))));

        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap(18, 20)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(gitRepositoryUrlTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                                        .addComponent(gitRepositoryUrlLabel))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(authorizationRequiredCheckBox, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                                        .addComponent(authorizationRequiredLabel))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(gitUsernameTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                                        .addComponent(gitUsernameLabel))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(gitPasswordField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                                        .addComponent(gitPasswordLabel))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cloneButton, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cloneProcessingLabel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cloneProcessingProgressBar, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cloneTaskLabel, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                                .addPreferredGap(UNRELATED)
                        )
        );
    }

    private class ProgressBarMonitor implements ProgressMonitor {

        @Override
        public void start(int totalTasks) {}

        @Override
        public void beginTask(String title, int totalWork) {
            cloneProcessingProgressBar.setMinimum(0);
            cloneProcessingProgressBar.setMaximum(totalWork);
            cloneProcessingProgressBar.setValue(0);
            cloneTaskLabel.setText(title);
        }

        @Override
        public void update(int completed) {
            cloneProcessingProgressBar.setValue(cloneProcessingProgressBar.getValue()+completed);
        }

        @Override
        public void endTask() {
            cloneProcessingProgressBar.setValue(cloneProcessingProgressBar.getMaximum());
            cloneTaskLabel.setText("");

        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}
