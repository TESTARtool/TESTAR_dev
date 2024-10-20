/***************************************************************************************************
 *
 * Copyright (c) 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 Open Universiteit - www.ou.nl
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

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

public class LlmPanel extends SettingsPanel {

	private static final long serialVersionUID = -5556840958014020780L;

	private JLabel labelLlmHostAddress = new JLabel("LLM Host Address");
	private JTextField fieldLlmHostAddress = new JTextField();

	private JLabel labelLlmHostPort = new JLabel("LLM Host Port");
	private JTextField fieldLlmHostPort = new JTextField();

	private JLabel labelLlmTestGoalDescription = new JLabel("LLM Test Goal Description");
	private JTextArea txtLlmTestGoalDescription = new JTextArea();

	private JLabel labelLlmFewshotFile = new JLabel("LLM Fewshot File");
	private JTextField fieldLlmFewshotFile  = new JTextField();
	private JButton dirLlmButton = new JButton("..");

	public LlmPanel() {
		setLayout(null);

		labelLlmHostAddress.setBounds(10, 12, 180, 27);
		labelLlmHostAddress.setToolTipText(ConfigTags.LlmHostAddress.getDescription());
		add(labelLlmHostAddress);
		fieldLlmHostAddress.setBounds(160, 12, 180, 27);
		fieldLlmHostAddress.setToolTipText(ConfigTags.LlmHostAddress.getDescription());
		add(fieldLlmHostAddress);

		labelLlmHostPort.setBounds(10, 40, 180, 27);
		labelLlmHostPort.setToolTipText(ConfigTags.LlmHostPort.getDescription());
		add(labelLlmHostPort);
		fieldLlmHostPort.setBounds(160, 40, 180, 27);
		fieldLlmHostPort.setToolTipText(ConfigTags.LlmHostPort.getDescription());
		((AbstractDocument) fieldLlmHostPort.getDocument()).setDocumentFilter(new PortNumberFilter());
		add(fieldLlmHostPort);

		labelLlmTestGoalDescription.setBounds(10, 70, 600, 27);
		labelLlmTestGoalDescription.setToolTipText(ConfigTags.LlmTestGoalDescription.getDescription());
		add(labelLlmTestGoalDescription);
		txtLlmTestGoalDescription.setLineWrap(true);
		JScrollPane llmTestGoalDescriptionPane = new JScrollPane(txtLlmTestGoalDescription);
		llmTestGoalDescriptionPane.setBounds(10, 100, 600, 50);
		llmTestGoalDescriptionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		llmTestGoalDescriptionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		llmTestGoalDescriptionPane.setToolTipText(ConfigTags.LlmTestGoalDescription.getDescription());
		add(llmTestGoalDescriptionPane);

		labelLlmFewshotFile.setBounds(10, 160, 180, 27);
		labelLlmFewshotFile.setToolTipText(ConfigTags.LlmFewshotFile.getDescription());
		add(labelLlmFewshotFile);
		fieldLlmFewshotFile.setBounds(160, 160, 180, 27);
		fieldLlmFewshotFile.setToolTipText(ConfigTags.LlmFewshotFile.getDescription());
		fieldLlmFewshotFile.setEditable(false);
		add(fieldLlmFewshotFile);
		dirLlmButton.setBounds(350, 160, 20, 27);
		dirLlmButton.addActionListener(this::chooseFileActionPerformed);
		add(dirLlmButton);
	}

	// show a file dialog to choose the LLM Fewshot File
	private void chooseFileActionPerformed(ActionEvent evt) {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(fieldLlmFewshotFile.getText()).getParentFile());

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();

			// Set the text from settings in txtSutPath
			fieldLlmFewshotFile.setText(file);
		}
	}

	@Override
	public void populateFrom(Settings settings) {
		fieldLlmHostAddress.setText(settings.get(ConfigTags.LlmHostAddress));
		fieldLlmHostPort.setText(settings.get(ConfigTags.LlmHostPort).toString());
		txtLlmTestGoalDescription.setText(settings.get(ConfigTags.LlmTestGoalDescription));
		fieldLlmFewshotFile.setText(settings.get(ConfigTags.LlmFewshotFile));
	}

	@Override
	public void extractInformation(Settings settings) {
		settings.set(ConfigTags.LlmHostAddress, fieldLlmHostAddress.getText());
		settings.set(ConfigTags.LlmHostPort, Integer.valueOf(fieldLlmHostPort.getText()));
		settings.set(ConfigTags.LlmTestGoalDescription, txtLlmTestGoalDescription.getText());
		settings.set(ConfigTags.LlmFewshotFile, fieldLlmFewshotFile.getText());
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
