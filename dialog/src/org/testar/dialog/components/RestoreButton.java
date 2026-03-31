/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextArea;

public class RestoreButton extends JButton implements ActionListener {
	private static final long serialVersionUID = -1959638418354471229L;

	private JTextArea textAreaRegex;
	private final String SUSPICIOUS_PATTERN = ".*[eE]rror.*|.*[eE]xcepti[o?]n.*";

	public RestoreButton(JTextArea textAreaRegex) {
		this.textAreaRegex = textAreaRegex;
		this.setText("Restore");
		setSize(75, 27);
		addActionListener(this);
	}

	public void setPosition(int x, int y) {
		setLocation(x, y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(textAreaRegex.getText() == null || textAreaRegex.getText().isEmpty()) {
			textAreaRegex.setText(SUSPICIOUS_PATTERN);
		}
	}
}
