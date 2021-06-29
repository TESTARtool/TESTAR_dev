package org.fruit.monkey.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProgressDialog extends JDialog implements Runnable {

    private JLabel statusLabel;
    private boolean statusChanged = false;
    private boolean isRedundant = false;
    private String statusString;

    public ProgressDialog() {
        setModal(true);
        setUndecorated(true);
        setPreferredSize(new Dimension(320, 80));

        Box statusBox = new Box(BoxLayout.X_AXIS);
        statusBox.setBorder(new EmptyBorder(new Insets(16, 16, 16, 16)));
        statusLabel = new JLabel();
        statusBox.add(statusLabel, Box.CENTER_ALIGNMENT);
        getContentPane().add(statusBox);
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
        statusChanged = true;
        SwingUtilities.invokeLater(this);
    }

    public void endProgress(String url, boolean closeImmediately) {
        //FIXME: now it closes immediately
        isRedundant = true;
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        if (isRedundant) {
            dispose();
            return;
        }
        if (statusChanged) {
            statusLabel.setText(statusString);
            toFront();
            repaint();
            statusChanged = false;
        }
    }
}
