package org.fruit.monkey.dialog;

import org.fruit.Pair;
import org.fruit.monkey.sonarqube.SonarqubeServiceDelegate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SonarqubeDialog extends JDialog implements Runnable {

    private String statusString;
    private Pair<String, String> pendingError;
    private Pair<String, String> pendingReport;

    @Override
    public void run() {
        if (processStarted) {
            statusLabel.setText(statusString);
            if (pendingError != null) {
                JOptionPane.showMessageDialog(this, pendingError.right(),
                        pendingError.left(), JOptionPane.ERROR_MESSAGE);
                dispose();
            }
            else if (pendingReport != null) {
                JOptionPane.showMessageDialog(this,
                        "A report should be saved (not implemented yet)", "Completed", JOptionPane.PLAIN_MESSAGE);
                dispose();
            }
        }
    }

    public interface Delegate {
        void callback(String projectName, String projectKey);
    }

    private JPanel projectPropertiesPanel;
    private GridLayout projectPropertiesLayout;
    private JButton startButton;
    private JButton cancelButton;

    private JTextField projectNameField;
    private JTextField projectKeyField;

    private Box bottomBox;
    private boolean processStarted = false;
    private Delegate delegate;

    private JLabel statusLabel;

    public SonarqubeDialog(Frame owner) {
        initProjectPropertiesPanel();

        startButton = new JButton("Start");
        cancelButton = new JButton("Cancel");

        startButton.addActionListener(event -> {
            startProcess();
            if (delegate != null) {
                delegate.callback(projectNameField.getText(), projectKeyField.getText());
            }
        });

        cancelButton.addActionListener(event -> {
            this.dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(new JSeparator(), BorderLayout.NORTH);

        bottomBox = new Box(BoxLayout.X_AXIS);
        bottomBox.setBorder(new EmptyBorder(new Insets(8, 8, 8, 8)));

        bottomBox.add(startButton);
        bottomBox.add(Box.createHorizontalStrut(32));
        bottomBox.add(cancelButton);

        bottomPanel.add(bottomBox, BorderLayout.EAST);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        getContentPane().add(projectPropertiesPanel, BorderLayout.CENTER);

        //TEMP
        projectNameField.setText("Demo");
        projectKeyField.setText("demo");
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;

    }

    public Delegate getDelegate() {
        return this.delegate;
    }

    private void initProjectPropertiesPanel() {
        projectPropertiesPanel = new JPanel();
        projectPropertiesPanel.setBorder(new EmptyBorder(new Insets(8, 8, 8, 8)));
        projectPropertiesLayout = new GridLayout(0, 2);
        projectPropertiesLayout.setVgap(8);
        projectPropertiesLayout.setHgap(12);
        projectPropertiesPanel.setLayout(projectPropertiesLayout);

        projectPropertiesPanel.add(new JLabel("Project name"));
        projectNameField = new JTextField();
        projectNameField.setPreferredSize(new Dimension(160, 24));
        projectPropertiesPanel.add(projectNameField);
        projectKeyField = new JTextField();
        projectPropertiesPanel.add(new JLabel("Project key"));
        projectKeyField.setPreferredSize(new Dimension(160, 24));
        projectPropertiesPanel.add(projectKeyField);
    }

    private void startProcess() {
        projectNameField.setEnabled(false);
        projectKeyField.setEnabled(false);

        bottomBox.removeAll();
        statusLabel = new JLabel();
        bottomBox.add(statusLabel);

        processStarted = true;
    }

    public void setStatus(String status) {
        statusString = status;
        SwingUtilities.invokeLater(this);
    }

    public void showError(String title, String message) {
        pendingError = new Pair<>(title, message);
        SwingUtilities.invokeLater(this);
    }

    public void complete(String reportURL, String reportJSON) {
        pendingReport = new Pair<>(reportURL, reportJSON);
        SwingUtilities.invokeLater(this);
    }
}
