package org.fruit.monkey.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SonarqubeDialog extends JDialog {

    public interface Delegate {
        void callback(String projectName, String projectKey);
    }

    private JPanel projectPropertiesPanel;
    private GridLayout projectPropertiesLayout;
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;

    private JTextField projectNameField;
    private JTextField projectKeyField;

    private Delegate delegate;

    public SonarqubeDialog(Frame owner, Delegate delegate) {
        this.delegate = delegate;
        initComponents();
    }

    private void initComponents() {
        JPanel buttonPanel = new JPanel();
        Box buttonBox = new Box(BoxLayout.X_AXIS);

        initProjectPropertiesPanel();

        backButton = new JButton("Back");
        nextButton = new JButton("Next");
        cancelButton = new JButton("Cancel");

        nextButton.addActionListener(event -> {
            if (delegate != null) {
                delegate.callback(projectNameField.getText(), projectKeyField.getText());
            }
        });

        cancelButton.addActionListener(event -> {
            this.dispose();
        });

        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

        buttonBox.setBorder(new EmptyBorder(new Insets(8, 8, 8, 8)));
        buttonBox.add(backButton);
        buttonBox.add(Box.createHorizontalStrut(8));
        buttonBox.add(nextButton);
        buttonBox.add(Box.createHorizontalStrut(32));
        buttonBox.add(cancelButton);
        buttonPanel.add(buttonBox, BorderLayout.EAST);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(projectPropertiesPanel, BorderLayout.CENTER);
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
}
