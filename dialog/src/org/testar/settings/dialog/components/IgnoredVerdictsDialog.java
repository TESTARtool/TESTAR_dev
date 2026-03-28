/***************************************************************************************************
 *
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.settings.dialog.components;

import org.testar.monkey.VerdictProcessing;

import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class IgnoredVerdictsDialog extends JDialog {

	private final DefaultListModel<String> listModel = new DefaultListModel<>();
	private final JList<String> verdictList = new JList<>(listModel);
	private File ignoreFile;

	public IgnoredVerdictsDialog() {
		setTitle("Ignored Verdicts");
		setSize(new Dimension(700, 420));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		verdictList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(new JScrollPane(verdictList), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton removeSelectedButton = new JButton("Remove Selected");
		removeSelectedButton.addActionListener(e -> removeSelectedIgnoredVerdicts());
		JButton clearAllButton = new JButton("Clear All");
		clearAllButton.addActionListener(e -> clearAllIgnoredVerdicts());
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> dispose());
		buttonPanel.add(removeSelectedButton);
		buttonPanel.add(clearAllButton);
		buttonPanel.add(closeButton);
		add(buttonPanel, BorderLayout.SOUTH);

		loadIgnoredVerdicts();
	}

	private void loadIgnoredVerdicts() {
		listModel.clear();
		ignoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
		if (ignoreFile == null) {
			listModel.addElement("Ignored verdict file path is not available.");
			return;
		}
		try {
			File parent = ignoreFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			if (!ignoreFile.exists()) {
				ignoreFile.createNewFile();
			}
			List<String> lines = Files.readAllLines(ignoreFile.toPath(), StandardCharsets.UTF_8);
			boolean hasEntries = false;
			for (String line : lines) {
				String trimmed = line.trim();
				if (!trimmed.isEmpty()) {
					listModel.addElement(trimmed);
					hasEntries = true;
				}
			}
			if (!hasEntries) {
				listModel.addElement("(No ignored verdicts yet)");
			}
		} catch (IOException ex) {
			listModel.addElement("Unable to load ignored verdicts file: " + ex.getMessage());
		}
	}

	private void clearAllIgnoredVerdicts() {
		if (ignoreFile == null) {
			JOptionPane.showMessageDialog(this, "Ignored verdict file path is not available.", "Ignored Verdicts", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			Files.writeString(ignoreFile.toPath(), "", StandardCharsets.UTF_8);
			loadIgnoredVerdicts();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Unable to clear ignored verdicts:\n" + ex.getMessage(), "Ignored Verdicts", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void removeSelectedIgnoredVerdicts() {
		if (ignoreFile == null) {
			JOptionPane.showMessageDialog(this, "Ignored verdict file path is not available.", "Ignored Verdicts", JOptionPane.WARNING_MESSAGE);
			return;
		}
		List<String> selected = verdictList.getSelectedValuesList();
		if (selected == null || selected.isEmpty() || selected.contains("(No ignored verdicts yet)")) {
			return;
		}
		try {
			List<String> current = Files.readAllLines(ignoreFile.toPath(), StandardCharsets.UTF_8);
			List<String> remaining = new ArrayList<>();
			for (String line : current) {
				String trimmed = line.trim();
				if (trimmed.isEmpty()) {
					continue;
				}
				if (!selected.contains(trimmed)) {
					remaining.add(trimmed);
				}
			}
			Files.write(ignoreFile.toPath(), remaining, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			loadIgnoredVerdicts();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Unable to remove selected verdicts:\n" + ex.getMessage(), "Ignored Verdicts", JOptionPane.ERROR_MESSAGE);
		}
	}
}
