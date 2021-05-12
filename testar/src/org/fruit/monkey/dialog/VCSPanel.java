package org.fruit.monkey.dialog;

import org.eclipse.jgit.lib.ProgressMonitor;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsPanel;
import org.fruit.monkey.codeanalysis.CodeAnalysisService;
import org.fruit.monkey.codeanalysis.CodeAnalysisServiceImpl;
import org.fruit.monkey.codeanalysis.RepositoryLanguage;
import org.fruit.monkey.codeanalysis.RepositoryLanguageComposition;
import org.fruit.monkey.sonarqube.SonarqubeService;
import org.fruit.monkey.sonarqube.SonarqubeServiceImpl;
import org.fruit.monkey.vcs.GitCredentials;
import org.fruit.monkey.vcs.GitService;
import org.fruit.monkey.vcs.GitServiceImpl;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

public class VCSPanel extends SettingsPanel {

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
    private final JProgressBar[] languageContentProgressBars = new JProgressBar[LANGUAGES_TO_DISPLAY];
    private final JLabel[] languageContentLabels = new JLabel[LANGUAGES_TO_DISPLAY];
    private JButton fullScanningReportButton;
    private JButton sonarqubeButton;
    private final static int LANGUAGES_TO_DISPLAY = 3;
    private final static int LANGUAGE_CONTENT_BAR_MIN = 0;
    private final static int LANGUAGE_CONTENT_BAR_MAX = 100;
    private final static int LANGUAGE_CONTENT_SCALE = 0;
    private final static String GIT_URL_LABEL = "Repository URL";
    private final static String GIT_USERNAME_LABEL = "Username";
    private final static String GIT_PASSWORD_LABEL = "Password";
    private final static String AUTHORIZATION_REQUIRED_LABEL = "Authorization required";
    private final static String CLONE_BUTTON = "Clone";
    private final static String CLONE_ERROR_TITLE = "Clone error";
    private final static String CLONE_ERROR_MESSAGE = "Something went wrong, repository wasn't cloned cloned. Check whether the repository already exists locally or credentials are valid.";
    private final static String CLONE_SUCCESS_TITLE = "Clone success";
    private final static String CLONE_SUCCESS_MESSAGE = "Repository cloned successfully";
    private final static String CLONE_PROCESSING_LABEL = "Cloning project...";
    private final static String FULL_REPORT_BUTTON = "Full report";
    private final static String SONARQUBE_BUTTON = "Sonarqube";
    private final static String CLONING_PROPERTY = "cloning_property";

    private final static String DOCKER_UNAVAILABLE_TITLE = "Docker not available";
    private final static String DOCKER_UNAVAILABLE_MESSAGE = "Docker seems not to be up and running on your machine";
    private final static String BTN_CANCEL = "Cancel";
    private final static String BTN_INSTALL_DOCKER = "Install Docker >>";

    private final static String DOCKER_LINK = "https://docs.docker.com/get-docker/";

    private PropertyChangeSupport propertyChangeSupport;

    private GitService gitService;
    private CodeAnalysisService codeAnalysisService;
    private SonarqubeService sonarqubeService;

    private RepositoryLanguageComposition repositoryComposition;

    public VCSPanel() {
        gitService = new GitServiceImpl();
        codeAnalysisService = new CodeAnalysisServiceImpl();
        sonarqubeService = new SonarqubeServiceImpl();
        propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener(this::cloneFinished);
        initGitRepositoryUrlSection();
        initGitUsernameSection();
        initGitPasswordSection();
        initAuthorizationRequiredSection();
        initCloneSection();
        initLanguagesSection();
    }

    private void cloneFinished(PropertyChangeEvent evt) {
        Object cloneSuccessful = evt.getNewValue();
        if (cloneSuccessful != null) {
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
            cloneButton.setEnabled(false);
            toggleLanguageSectionVisibility(false);
            if (authorizationRequiredCheckBox.isSelected()) {
                cloneRepositoryWithAuth();
            } else {
                cloneRepository();
            }
        });
        cloneProcessingLabel = new JLabel(CLONE_PROCESSING_LABEL);
        cloneProcessingProgressBar = new JProgressBar();
        cloneTaskLabel = new JLabel();
        toggleCloneProcessingVisibility(false);
    }

    private void toggleLanguageSectionVisibility(boolean visible) {
        for (int i = 0; i < LANGUAGES_TO_DISPLAY; i++) {
            toggleLanguageVisibility(visible, i);
        }
        this.fullScanningReportButton.setVisible(visible);
        this.sonarqubeButton.setVisible(visible);
    }

    private void toggleLanguageVisibility(boolean visible, int index) {
        languageContentProgressBars[index].setVisible(visible);
        languageContentLabels[index].setVisible(visible);
    }

    private void cloneRepositoryWithAuth() {
        toggleCloneProcessingVisibility(true);
        new Thread(() -> {
            GitCredentials credentials = new GitCredentials(gitUsernameTextField.getText(), new String(gitPasswordField.getPassword()));
            Path repositoryPath = gitService.cloneRepository(gitRepositoryUrlTextField.getText(), credentials, new ProgressBarMonitor());
            scanRepository(repositoryPath);
            toggleCloneProcessingVisibility(false);
            viewRepositoryComposition();
            propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, repositoryPath);
        }).start();
    }

    private void toggleCloneProcessingVisibility(boolean visible) {
        cloneProcessingLabel.setVisible(visible);
        cloneProcessingProgressBar.setVisible(visible);
        cloneTaskLabel.setVisible(visible);
    }

    private void scanRepository(Path repositoryPath) {
        cloneTaskLabel.setText("Scanning languages...");
        this.repositoryComposition = codeAnalysisService.scanRepository(repositoryPath);
        cloneTaskLabel.setText("");
    }

    private void viewRepositoryComposition() {
        List<RepositoryLanguage> repositoryLanguages = repositoryComposition.getRepositoryLanguages()
                .stream()
                .sorted(Comparator.comparing(RepositoryLanguage::getLinesOfCode, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        for (int i = 0; (i < LANGUAGES_TO_DISPLAY) && (i < repositoryComposition.getRepositoryLanguages().size()); i++) {
            setLanguageValue(repositoryLanguages.get(i), i);
        }
        if (repositoryComposition.getRepositoryLanguages().size() > LANGUAGES_TO_DISPLAY) {
            fullScanningReportButton.setVisible(true);
            this.sonarqubeButton.setVisible(true);
        }
    }

    private void setLanguageValue(RepositoryLanguage repositoryLanguage, int index) {
        toggleLanguageVisibility(true, index);
        styleLanguageProgressBar(languageContentProgressBars[index]);
        languageContentProgressBars[index].setValue(repositoryLanguage.getPercentage()
                .multiply(new BigDecimal(Math.pow(10, LANGUAGE_CONTENT_SCALE)))
                .intValue());
        languageContentLabels[index].setText(repositoryLanguage.getPercentage().setScale(LANGUAGE_CONTENT_SCALE, RoundingMode.HALF_UP).toString() + "% " + repositoryLanguage.getSupportedLanguage().getName());
    }

    private void styleLanguageProgressBar(JProgressBar progressBar) {
        Random colorGen = new Random();
        progressBar.setUI(new LanguageProgressBarUI());
        Color foregroundColor = new Color(colorGen.nextInt(255), colorGen.nextInt(255), colorGen.nextInt(255));
        progressBar.setForeground(foregroundColor);
        progressBar.setBackground(Color.LIGHT_GRAY);
    }

    private void cloneRepository() {
        toggleCloneProcessingVisibility(true);
        new Thread(() -> {
            Path repositoryPath = gitService.cloneRepository(gitRepositoryUrlTextField.getText(), new ProgressBarMonitor());
            scanRepository(repositoryPath);
            toggleCloneProcessingVisibility(false);
            viewRepositoryComposition();
            propertyChangeSupport.firePropertyChange(CLONING_PROPERTY, null, repositoryPath);
        }).start();
    }

    private void initLanguagesSection() {
        for (int i = 0; i < LANGUAGES_TO_DISPLAY; i++) {
            languageContentLabels[i] = new JLabel();
            languageContentProgressBars[i] = new JProgressBar(LANGUAGE_CONTENT_BAR_MIN, LANGUAGE_CONTENT_BAR_MAX);
        }
        this.fullScanningReportButton = new JButton(FULL_REPORT_BUTTON);
        this.fullScanningReportButton.addActionListener((event) -> {
            viewFullReport();
        });
        this.sonarqubeButton = new JButton((SONARQUBE_BUTTON));
        this.sonarqubeButton.addActionListener((event -> {
            processWithSonarqube();
        }));
        toggleLanguageSectionVisibility(false);
    }

    private void viewFullReport() {
        new ScanningReportDialog(repositoryComposition);
    }

    private void processWithSonarqube() {
        if (!sonarqubeService.isAvailable()) {
            Object[] options = {BTN_CANCEL, BTN_INSTALL_DOCKER};
            int n = JOptionPane.showOptionDialog(this,
                    DOCKER_UNAVAILABLE_MESSAGE,
                    DOCKER_UNAVAILABLE_TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (n > 0) {
                try {
                    Desktop.getDesktop().browse(new URI(DOCKER_LINK));
                }
                catch (Exception e) {}
            }
        }
        else {
            //TODO
            JOptionPane.showMessageDialog(this,
                    "Docker support is not implemented yet",
                    "Coming soon",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Populate the fields from Settings structure.
     *
     * @param settings The settings to load.
     */
    @Override
    public void populateFrom(Settings settings) {
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
                                        .addComponent(cloneTaskLabel, PREFERRED_SIZE, 200, PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(languageContentProgressBars[0], PREFERRED_SIZE, 200, PREFERRED_SIZE)
                                        .addGap(PREFERRED_SIZE, 10, PREFERRED_SIZE)
                                        .addComponent(languageContentLabels[0], 20, 60, 200))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(languageContentProgressBars[1], PREFERRED_SIZE, 200, PREFERRED_SIZE)
                                        .addGap(PREFERRED_SIZE, 10, PREFERRED_SIZE)
                                        .addComponent(languageContentLabels[1], 20, 60, 200))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(languageContentProgressBars[2], PREFERRED_SIZE, 200, PREFERRED_SIZE)
                                        .addGap(PREFERRED_SIZE, 10, PREFERRED_SIZE)
                                        .addComponent(languageContentLabels[2], 20, 60, 200))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(PREFERRED_SIZE, 138, PREFERRED_SIZE)
                                        .addComponent(fullScanningReportButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(sonarqubeButton))
                        ));

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
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(languageContentProgressBars[0])
                                        .addComponent(languageContentLabels[0]))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(languageContentProgressBars[1])
                                        .addComponent(languageContentLabels[1]))
                                .addPreferredGap(RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(languageContentProgressBars[2])
                                        .addComponent(languageContentLabels[2]))
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(fullScanningReportButton)//)
//                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sonarqubeButton))
                                .addPreferredGap(UNRELATED)
                        ));
    }

    /**
     * Retrieve information from the GUI.
     *
     * @param settings reference to the object where the settings will be stored.
     */
    @Override
    public void extractInformation(Settings settings) {
        // TODO: Add information saving
    }

    private class ProgressBarMonitor implements ProgressMonitor {

        @Override
        public void start(int totalTasks) {
        }

        @Override
        public void beginTask(String title, int totalWork) {
            cloneProcessingProgressBar.setMinimum(0);
            cloneProcessingProgressBar.setMaximum(totalWork);
            cloneProcessingProgressBar.setValue(0);
            cloneTaskLabel.setText(title);
        }

        @Override
        public void update(int completed) {
            cloneProcessingProgressBar.setValue(cloneProcessingProgressBar.getValue() + completed);
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

    class LanguageProgressBarUI extends BasicProgressBarUI {
        Rectangle r = new Rectangle();

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            r = getBox(r);
            g.setColor(progressBar.getForeground());
            g.fillOval(r.x, r.y, r.width, r.height);
        }
    }
}
