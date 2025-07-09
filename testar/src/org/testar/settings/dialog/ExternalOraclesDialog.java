/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.settings.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.testar.oracles.OracleSelection;

public class ExternalOraclesDialog extends JDialog {
	private static final long serialVersionUID = 8181334482682809135L;

	private String savedSelectedExternalOracles;

	Window window = SwingUtilities.getWindowAncestor(this);

	private Map<String, List<JCheckBox>> fileToCheckboxes = new LinkedHashMap<>();
	private Map<String, JCheckBox> fileHeaderCheckboxes = new LinkedHashMap<>();

	public ExternalOraclesDialog(String selectedExternalOracles) {
		this.savedSelectedExternalOracles = selectedExternalOracles;

		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		init(selectedExternalOracles);

		setVisible(true);
	}

	private void init(String selectedExternalOracles) {
		JLabel label = new JLabel("Enable or disable the external oracles");
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(label, BorderLayout.NORTH);

		// Load external oracles from the testar/bin/oracles path
		Map<String, List<String>> groupedOracles = OracleSelection.getAvailableExternalOracles();

		// Load activated oracles from settings
		List<String> activatedOracles = List.of(selectedExternalOracles.split(","));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		for (Map.Entry<String, List<String>> entry : groupedOracles.entrySet()) {
			String fileName = entry.getKey();
			List<String> oracles = entry.getValue();

			JPanel containerPanel = new JPanel();
			containerPanel.setLayout(new BorderLayout());

			JPanel fileHeader = new JPanel(new BorderLayout());
			fileHeader.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			JPanel headerContent = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JCheckBox selectAllBox = new JCheckBox("Select All");
			fileHeaderCheckboxes.put(fileName, selectAllBox);

			JButton toggleButton = new JButton("> " + fileName);
			toggleButton.setMargin(new Insets(0, 5, 0, 5));
			headerContent.add(toggleButton);
			headerContent.add(selectAllBox);

			fileHeader.add(headerContent, BorderLayout.CENTER);
			containerPanel.add(fileHeader, BorderLayout.NORTH);

			JPanel oraclePanel = new JPanel();
			oraclePanel.setLayout(new BoxLayout(oraclePanel, BoxLayout.Y_AXIS));
			oraclePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

			List<JCheckBox> oracleBoxes = new ArrayList<>();
			for (String oracle : oracles) {
				JCheckBox box = new JCheckBox(oracle, activatedOracles.contains(oracle));
				oracleBoxes.add(box);
				oraclePanel.add(box);

				box.addActionListener(e -> updateFileCheckbox(fileName));
			}

			selectAllBox.addActionListener(e -> {
				boolean selected = selectAllBox.isSelected();
				for (JCheckBox cb : oracleBoxes) {
					cb.setSelected(selected);
				}
			});

			toggleButton.addActionListener(e -> {
				boolean isVisible = oraclePanel.isVisible();
				oraclePanel.setVisible(!isVisible);
				toggleButton.setText(isVisible 
						? toggleButton.getText().replaceFirst("v", ">") // switch open to close 
						: toggleButton.getText().replaceFirst(">", "v") // switch close to open 
						);
				revalidate();
				repaint();
			});

			oraclePanel.setVisible(false); // start collapsed

			fileToCheckboxes.put(fileName, oracleBoxes);
			containerPanel.add(oraclePanel, BorderLayout.CENTER);

			mainPanel.add(containerPanel);
		}

		JScrollPane scrollPane = new JScrollPane(mainPanel);
		add(scrollPane, BorderLayout.CENTER);

		JButton saveButton = new JButton("Save and Close");
		saveButton.addActionListener(e -> saveExternalOracles());

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(saveButton);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public String getSavedExternalOracles() {
		return savedSelectedExternalOracles;
	}

	private void updateFileCheckbox(String fileName) {
		List<JCheckBox> children = fileToCheckboxes.get(fileName);
		JCheckBox parent = fileHeaderCheckboxes.get(fileName);
		boolean allSelected = children.stream().allMatch(AbstractButton::isSelected);
		parent.setSelected(allSelected);
	}

	private void saveExternalOracles() {
		List<String> selectedOracles = new ArrayList<>();
		for (List<JCheckBox> checkboxes : fileToCheckboxes.values()) {
			for (JCheckBox cb : checkboxes) {
				if (cb.isSelected()) {
					selectedOracles.add(cb.getText());
				}
			}
		}
		savedSelectedExternalOracles = String.join(",", selectedOracles);
		dispatchEvent(new WindowEvent(window ,WindowEvent.WINDOW_CLOSING));
		dispose();
	}
}
