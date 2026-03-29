/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class QueueVisualizer extends JDialog {

    private Label msg;

    public QueueVisualizer(String title) {
        super((JFrame) null, title, false);
        setLocationRelativeTo(null);
        this.setType(Type.POPUP);
        this.setUndecorated(true);
        msg = new Label(title);
        msg.setBackground(Color.BLACK);
        msg.setForeground(Color.WHITE);
        this.add(msg);
        int dimW = (title.length() + 1) * 12;
        this.setSize(new Dimension(dimW > 512 ? 512 : dimW, 32));
        this.setEnabled(false);
        this.setOpacity(0.75f);
        this.setVisible(true);
    }

    public void updateMessage(String message) {
        msg.setText(message);
        msg.repaint();
    }

    public void stop() {
        this.setVisible(false);
        this.dispose();
    }
}
