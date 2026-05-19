/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.editor;

import org.testar.core.util.Util;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

abstract class AbstractTextFileEditor extends JDialog {

    private static final long serialVersionUID = 2674114874906349201L;

    private final String filePath;
    private final String defaultTemplate;

    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveAndClose;
    private JEditorPane editorPane;
    private JTextArea console;
    private JScrollPane editorScrollPane;
    private JScrollPane consoleScrollPane;

    AbstractTextFileEditor(String title, String filePath, String defaultTemplate) {
        this.filePath = filePath;
        this.defaultTemplate = defaultTemplate;
        initComponents(title);
        loadContent();
    }

    private void initComponents(String title) {
        editorScrollPane = new JScrollPane();
        editorPane = new JEditorPane();
        btnSave = new javax.swing.JButton();
        btnSaveAndClose = new javax.swing.JButton();
        consoleScrollPane = new JScrollPane();
        console = new JTextArea();

        setTitle(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 400));

        editorPane.setContentType("text/plain");
        editorPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                editorPaneKeyPressed(evt);
            }
        });
        editorScrollPane.setViewportView(editorPane);
        editorScrollPane.getVerticalScrollBar().setUnitIncrement(5);

        btnSave.setText("Save");
        btnSave.addActionListener(this::btnSaveActionPerformed);
        btnSave.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                buttonKeyPressed(evt);
            }
        });

        btnSaveAndClose.setText("Save and Close");
        btnSaveAndClose.addActionListener(this::btnSaveAndCloseActionPerformed);
        btnSaveAndClose.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                buttonKeyPressed(evt);
            }
        });

        console.setColumns(20);
        console.setLineWrap(true);
        console.setRows(5);
        console.setEditable(false);
        console.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                consoleKeyPressed(evt);
            }
        });
        consoleScrollPane.setViewportView(console);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(editorScrollPane)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSaveAndClose, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(consoleScrollPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(editorScrollPane, GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSave)
                                        .addComponent(btnSaveAndClose))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(consoleScrollPane, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }

    private void loadContent() {
        File file = new File(filePath);
        if (file.exists()) {
            editorPane.setText(Util.readFile(file));
            return;
        }

        editorPane.setText(defaultTemplate);
    }

    private boolean save() {
        try {
            console.setText("Saving...");
            console.update(console.getGraphics());
            Util.saveToFile(editorPane.getText(), filePath);
            console.setText("Saving... OK");
            return true;
        } catch (IOException exception) {
            if (exception.getMessage() != null) {
                console.setText("Saving..." + System.lineSeparator() + exception.getMessage());
            }
            return false;
        }
    }

    private void btnSaveActionPerformed(ActionEvent evt) {
        save();
    }

    private void btnSaveAndCloseActionPerformed(ActionEvent evt) {
        if (save()) {
            dispose();
        }
    }

    private void editorPaneKeyPressed(KeyEvent evt) {
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                save();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispose();
        }
    }

    private void consoleKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispose();
        }
    }

    private void buttonKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispose();
        }
    }
}
