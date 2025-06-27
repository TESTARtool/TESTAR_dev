/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AdvancedPanel extends SettingsPanel {

	private static final long serialVersionUID = -7748826272325778421L;

	private JCheckBox keyBoardListenCheck;
	private JCheckBox accessBridgeEnabledCheck;
	private JLabel labelSutProcesses = new JLabel("Desktop SUT Multi-Processes");
	private JTextField sutProcessesField = new JTextField();

	private JPanel coveragePanel = new JPanel();
	private Set<JComponent> jacocoCoverageComponents;
	private JCheckBox enableJacocoCoverage = new JCheckBox("Enable JaCoCo Coverage");
	private JLabel labelIpJacocoCoverage = new JLabel("IP address JaCoCo agent");
	private JTextField fieldIpJacocoCoverage = new JTextField("localhost");
	private JLabel labelPortJacocoCoverage = new JLabel("Port JaCoCo agent");
	private JTextField fieldPortJacocoCoverage = new JTextField("5000");
	private JLabel labelClassesJacocoCoverage = new JLabel("JaCoCo SUT classes");
	private JTextField fieldClassesJacocoCoverage  = new JTextField();
	private JButton dirButton = new JButton("..");
	private JCheckBox enableAccumulativeJacocoCoverage = new JCheckBox("Accumulative Action Coverage");

	public AdvancedPanel() {
		setLayout(null);

		keyBoardListenCheck = new JCheckBox("Listen to Keyboard shortcuts");
		keyBoardListenCheck.setBounds(10, 12, 192, 21);
		add(keyBoardListenCheck);

		accessBridgeEnabledCheck = new JCheckBox("AccessBridge enabled");
		accessBridgeEnabledCheck.setBounds(10, 40, 192, 21);
		add(accessBridgeEnabledCheck);

		labelSutProcesses.setBounds(10, 70, 180, 27);
		labelSutProcesses.setToolTipText(ToolTipTexts.sutProcessesTTT);
		add(labelSutProcesses);
		sutProcessesField.setBounds(190, 70, 420, 27);
		sutProcessesField.setToolTipText(ToolTipTexts.sutProcessesTTT);
		add(sutProcessesField);

		coveragePanel.setLayout(null);
		coveragePanel.setBounds(5, 100, 610, 170);
		coveragePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY, 1), 
				"JaCoCo Coverage Settings", 
				TitledBorder.LEFT, 
				TitledBorder.TOP));
		coveragePanel(); // Prepare coverage components
		add(coveragePanel);
	}

	private void coveragePanel() {
		// The group of settings coverage components
		jacocoCoverageComponents = new HashSet<>();
		jacocoCoverageComponents.add(labelIpJacocoCoverage);
		jacocoCoverageComponents.add(fieldIpJacocoCoverage);
		jacocoCoverageComponents.add(labelPortJacocoCoverage);
		jacocoCoverageComponents.add(fieldPortJacocoCoverage);
		jacocoCoverageComponents.add(labelClassesJacocoCoverage);
		jacocoCoverageComponents.add(fieldClassesJacocoCoverage);
		jacocoCoverageComponents.add(dirButton);
		jacocoCoverageComponents.add(enableAccumulativeJacocoCoverage);

		enableJacocoCoverage.setBounds(10, 20, 180, 27);
		enableJacocoCoverage.setToolTipText(ConfigTags.JacocoCoverage.getDescription());
		enableJacocoCoverage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				jacocoCoverageComponents.forEach((component) -> component.setEnabled(enableJacocoCoverage.isSelected()));
			}
		});
		coveragePanel.add(enableJacocoCoverage);

		labelIpJacocoCoverage.setBounds(10, 50, 180, 27);
		labelIpJacocoCoverage.setToolTipText(ConfigTags.JacocoCoverageIpAddress.getDescription());
		coveragePanel.add(labelIpJacocoCoverage);
		fieldIpJacocoCoverage.setBounds(160, 50, 180, 27);
		fieldIpJacocoCoverage.setToolTipText(ConfigTags.JacocoCoverageIpAddress.getDescription());
		coveragePanel.add(fieldIpJacocoCoverage);

		labelPortJacocoCoverage.setBounds(10, 80, 180, 27);
		labelPortJacocoCoverage.setToolTipText(ConfigTags.JacocoCoveragePort.getDescription());
		coveragePanel.add(labelPortJacocoCoverage);
		fieldPortJacocoCoverage.setBounds(160, 80, 180, 27);
		fieldPortJacocoCoverage.setToolTipText(ConfigTags.JacocoCoveragePort.getDescription());
		((AbstractDocument) fieldPortJacocoCoverage.getDocument()).setDocumentFilter(new PortNumberFilter());
		coveragePanel.add(fieldPortJacocoCoverage);

		labelClassesJacocoCoverage.setBounds(10, 110, 180, 27);
		labelClassesJacocoCoverage.setToolTipText(ConfigTags.JacocoCoverageClasses.getDescription());
		coveragePanel.add(labelClassesJacocoCoverage);
		fieldClassesJacocoCoverage.setBounds(160, 110, 180, 27);
		fieldClassesJacocoCoverage.setToolTipText(ConfigTags.JacocoCoverageClasses.getDescription());
		fieldClassesJacocoCoverage.setEditable(false);
		coveragePanel.add(fieldClassesJacocoCoverage);
		dirButton.setBounds(350, 110, 20, 27);
		dirButton.addActionListener(this::chooseFileActionPerformed);
		coveragePanel.add(dirButton);

		enableAccumulativeJacocoCoverage.setBounds(10, 140, 200, 27);
		enableAccumulativeJacocoCoverage.setToolTipText(ConfigTags.JacocoCoverageAccumulate.getDescription());
		coveragePanel.add(enableAccumulativeJacocoCoverage);
	}

	// show a file dialog to choose the directory where the JaCoCo classes are located
	private void chooseFileActionPerformed(ActionEvent evt) {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fd.setCurrentDirectory(new File(fieldClassesJacocoCoverage.getText()).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();

			// Set the text from settings in txtSutPath
			fieldClassesJacocoCoverage.setText(file);
		}
	}

	public void populateFrom(final Settings settings) {
		keyBoardListenCheck.setSelected(settings.get(ConfigTags.KeyBoardListener));
		accessBridgeEnabledCheck.setSelected(settings.get(ConfigTags.AccessBridgeEnabled));
		sutProcessesField.setText(settings.get(ConfigTags.SUTProcesses));

		enableJacocoCoverage.setSelected(settings.get(ConfigTags.JacocoCoverage));
		fieldIpJacocoCoverage.setText(settings.get(ConfigTags.JacocoCoverageIpAddress));
		fieldPortJacocoCoverage.setText(settings.get(ConfigTags.JacocoCoveragePort).toString());
		fieldClassesJacocoCoverage.setText(settings.get(ConfigTags.JacocoCoverageClasses));
		enableAccumulativeJacocoCoverage.setSelected(settings.get(ConfigTags.JacocoCoverageAccumulate));		
		jacocoCoverageComponents.forEach((component) -> component.setEnabled(enableJacocoCoverage.isSelected()));
	}

	/**
	 * Retrieve information from the Advanced GUI.
	 * @param settings reference to the object where the settings will be stored.
	 */
	@Override
	public void extractInformation(final Settings settings) {
		settings.set(ConfigTags.KeyBoardListener, keyBoardListenCheck.isSelected());
		settings.set(ConfigTags.AccessBridgeEnabled, accessBridgeEnabledCheck.isSelected());
		settings.set(ConfigTags.SUTProcesses, sutProcessesField.getText());

		settings.set(ConfigTags.JacocoCoverage, enableJacocoCoverage.isSelected());
		settings.set(ConfigTags.JacocoCoverageIpAddress, fieldIpJacocoCoverage.getText());
		settings.set(ConfigTags.JacocoCoveragePort, Integer.valueOf(fieldPortJacocoCoverage.getText()));
		settings.set(ConfigTags.JacocoCoverageClasses, fieldClassesJacocoCoverage.getText());
		settings.set(ConfigTags.JacocoCoverageAccumulate, enableAccumulativeJacocoCoverage.isSelected());
	}

	// DocumentFilter that allows only valid port numbers
	private class PortNumberFilter extends DocumentFilter {
		private static final int MIN_PORT = 1;
		private static final int MAX_PORT = 65535;

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
			String newText = new StringBuilder(currentText).insert(offset, string).toString();
			if (isValidPortNumber(newText)) {
				super.insertString(fb, offset, string, attr);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
			String newText = new StringBuilder(currentText).replace(offset, offset + length, text).toString();
			if (isValidPortNumber(newText)) {
				super.replace(fb, offset, length, text, attrs);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
			String newText = new StringBuilder(currentText).delete(offset, offset + length).toString();
			if (isValidPortNumber(newText)) {
				super.remove(fb, offset, length);
			}
		}

		// Helper method to check if the text is a valid port number within the range
		private boolean isValidPortNumber(String text) {
			if (text.isEmpty()) return true; // Allow empty string
			try {
				int port = Integer.parseInt(text);
				return port >= MIN_PORT && port <= MAX_PORT;
			} catch (NumberFormatException e) {
				return false; // Not a valid integer
			}
		}
	}
}