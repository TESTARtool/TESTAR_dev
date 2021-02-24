package org.fruit.monkey.dialog;

import javax.swing.*;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

public class VCSPanel extends JPanel {

    private JTextField gitRepositoryUrlTextField;
    private JTextField gitUsernameTextField;
    private JTextField gitPasswordTextField;
    private JCheckBox authorizationRequiredCheckBox;
    private JLabel gitRepositoryUrlLabel;
    private JLabel gitUsernameLabel;
    private JLabel gitPasswordLabel;
    private JLabel authorizationRequiredLabel;
    private JButton cloneButton;
    private final static String GIT_URL_LABEL = "Repository URL";
    private final static String GIT_USERNAME_LABEL = "Username";
    private final static String GIT_PASSWORD_LABEL = "Password";
    private final static String AUTHORIZATION_REQUIRED_LABEL = "Authorization required";
    private final static String CLONE_BUTTON = "Clone";

    public VCSPanel() {
        initGitRepositoryUrl();
        initGitUsername();
        initGitPassword();
        initAuthorizationRequired();
        initClone();
        initLayout();
    }

    private void initGitRepositoryUrl() {
        gitRepositoryUrlLabel = new JLabel(GIT_URL_LABEL);
        gitRepositoryUrlTextField = new JTextField();
    }

    private void initGitUsername() {
        gitUsernameLabel = new JLabel(GIT_USERNAME_LABEL);
        gitUsernameTextField = new JTextField();

    }

    private void initGitPassword() {
        gitPasswordLabel = new JLabel(GIT_PASSWORD_LABEL);
        gitPasswordTextField = new JTextField();
    }

    private void initAuthorizationRequired() {
        authorizationRequiredLabel = new JLabel(AUTHORIZATION_REQUIRED_LABEL);
        authorizationRequiredCheckBox = new JCheckBox();
        authorizationRequiredCheckBox.setSelected(true);
        authorizationRequiredCheckBox.addItemListener(e -> setAuthorizationFieldsEnabled(authorizationRequiredCheckBox.isSelected()));
    }

    private void setAuthorizationFieldsEnabled(boolean enabled) {
        gitUsernameTextField.setEnabled(enabled);
        gitPasswordTextField.setEnabled(enabled);
    }

    private void initClone() {
        cloneButton = new JButton(CLONE_BUTTON);
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
                                        .addComponent(gitPasswordTextField, PREFERRED_SIZE, 346, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(cloneButton, PREFERRED_SIZE, 65, PREFERRED_SIZE))));
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
                                        .addComponent(gitPasswordTextField, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                                        .addComponent(gitPasswordLabel))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cloneButton, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE))
                                .addPreferredGap(UNRELATED))
        );
    }
}
