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
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;

public class LlmPanel extends SettingsPanel {

	private static final long serialVersionUID = -5556840958014020780L;

	private JLabel labelLlmPlatform = new JLabel("LLM Platform");
	private JComboBox<String> llmPlatformBox = new JComboBox<>(new String[]{"OpenAI", "Gemini"});

	private JLabel labelLlmHostAddress = new JLabel("LLM Host Address");
	private JTextField fieldLlmHostAddress = new JTextField();

	private JLabel labelLlmHostPort = new JLabel("LLM Host Port");
	private JTextField fieldLlmHostPort = new JTextField();

	private JLabel labelLlmTestGoalDescription = new JLabel("LLM Test Goal Description");
	private JTextArea txtLlmTestGoalDescription = new JTextArea();

	private JLabel labelLlmFewshotFile = new JLabel("LLM Fewshot File");
	private JTextField fieldLlmFewshotFile  = new JTextField();
	private JButton dirLlmButton = new JButton("..");

	private JLabel labelLlmTemperature = new JLabel("LLM Temperature");
	private JTextField fieldLlmTemperature = new JTextField();

	private JLabel labelLlmHistorySize = new JLabel("Action History Size");
	private JTextField fieldLlmHistorySize = new JTextField();

	public LlmPanel() {
		setLayout(null);

		labelLlmPlatform.setBounds(10, 12, 180, 27);
		labelLlmPlatform.setToolTipText(ConfigTags.LlmPlatform.getDescription());
		add(labelLlmPlatform);
		llmPlatformBox.setBounds(160, 12, 180, 27);
		llmPlatformBox.setToolTipText(ConfigTags.LlmPlatform.getDescription());
		add(llmPlatformBox);
		// Add an ActionListener to change the settings based on the selected platform
		llmPlatformBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String) llmPlatformBox.getSelectedItem()) {
				case "OpenAI":
					fieldLlmHostAddress.setText("http://192.168.108.242");
					fieldLlmHostPort.setText("1234/v1/chat/completions");
					fieldLlmFewshotFile.setText("prompts/fewshot_login_openai.json");
					fieldLlmTemperature.setText("0.2");
					break;
				case "Gemini":
					fieldLlmHostAddress.setText("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest");
					fieldLlmHostPort.setText("generateContent?key=%GEMINI_API_KEY%");
					fieldLlmFewshotFile.setText("prompts/fewshot_login_gemini.json");
					fieldLlmTemperature.setText("0.2");
					break;

				default:
					return;
				}
			}
		});

		labelLlmHostAddress.setBounds(10, 40, 180, 27);
		labelLlmHostAddress.setToolTipText(ConfigTags.LlmHostAddress.getDescription());
		add(labelLlmHostAddress);
		fieldLlmHostAddress.setBounds(160, 40, 400, 27);
		fieldLlmHostAddress.setToolTipText(ConfigTags.LlmHostAddress.getDescription());
		add(fieldLlmHostAddress);

		labelLlmHostPort.setBounds(10, 70, 180, 27);
		labelLlmHostPort.setToolTipText(ConfigTags.LlmHostPort.getDescription());
		add(labelLlmHostPort);
		fieldLlmHostPort.setBounds(160, 70, 400, 27);
		fieldLlmHostPort.setToolTipText(ConfigTags.LlmHostPort.getDescription());
		add(fieldLlmHostPort);

		labelLlmTestGoalDescription.setBounds(10, 100, 600, 27);
		labelLlmTestGoalDescription.setToolTipText(ConfigTags.LlmTestGoalDescription.getDescription());
		add(labelLlmTestGoalDescription);
		txtLlmTestGoalDescription.setLineWrap(true);
		JScrollPane llmTestGoalDescriptionPane = new JScrollPane(txtLlmTestGoalDescription);
		llmTestGoalDescriptionPane.setBounds(10, 130, 600, 100);
		llmTestGoalDescriptionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		llmTestGoalDescriptionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		llmTestGoalDescriptionPane.setToolTipText(ConfigTags.LlmTestGoalDescription.getDescription());
		add(llmTestGoalDescriptionPane);

		labelLlmFewshotFile.setBounds(10, 240, 180, 27);
		labelLlmFewshotFile.setToolTipText(ConfigTags.LlmFewshotFile.getDescription());
		add(labelLlmFewshotFile);
		fieldLlmFewshotFile.setBounds(160, 240, 180, 27);
		fieldLlmFewshotFile.setToolTipText(ConfigTags.LlmFewshotFile.getDescription());
		fieldLlmFewshotFile.setEditable(false);
		add(fieldLlmFewshotFile);
		dirLlmButton.setBounds(350, 240, 20, 27);
		dirLlmButton.addActionListener(this::chooseFileActionPerformed);
		add(dirLlmButton);

		labelLlmTemperature.setBounds(10, 270, 180, 27);
		labelLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(labelLlmTemperature);
		fieldLlmTemperature.setBounds(160, 270, 400, 27);
		fieldLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(fieldLlmTemperature);

		labelLlmHistorySize.setBounds(10, 300, 180, 27);
		labelLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(labelLlmHistorySize);
		fieldLlmHistorySize.setBounds(160, 300, 400, 27);
		fieldLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(fieldLlmHistorySize);
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
		String llmPlatform = settings.get(ConfigTags.LlmPlatform);
		for (int i =0; i < llmPlatformBox.getItemCount(); i++) {
			if (llmPlatformBox.getItemAt(i).equals("OpenAI") && llmPlatform.equals("OpenAI")) {
				llmPlatformBox.setSelectedIndex(i);
				break;
			}
			if (llmPlatformBox.getItemAt(i).equals("Gemini") && llmPlatform.equals("Gemini")) {
				llmPlatformBox.setSelectedIndex(i);
				break;
			}
		}

		fieldLlmHostAddress.setText(settings.get(ConfigTags.LlmHostAddress));
		fieldLlmHostPort.setText(settings.get(ConfigTags.LlmHostPort));
		txtLlmTestGoalDescription.setText(settings.get(ConfigTags.LlmTestGoalDescription));
		fieldLlmFewshotFile.setText(settings.get(ConfigTags.LlmFewshotFile));
		fieldLlmTemperature.setText(settings.get(ConfigTags.LlmTemperature).toString());
		fieldLlmHistorySize.setText(settings.get(ConfigTags.LlmHistorySize).toString());
	}

	@Override
	public void extractInformation(Settings settings) {
		switch ((String) llmPlatformBox.getSelectedItem()) {
		case "Gemini":
			settings.set(ConfigTags.LlmPlatform, "Gemini");
			break;

		default:
			settings.set(ConfigTags.LlmPlatform, "OpenAI");
		}

		settings.set(ConfigTags.LlmHostAddress, fieldLlmHostAddress.getText());
		settings.set(ConfigTags.LlmHostPort, fieldLlmHostPort.getText());
		settings.set(ConfigTags.LlmTestGoalDescription, txtLlmTestGoalDescription.getText());
		settings.set(ConfigTags.LlmFewshotFile, fieldLlmFewshotFile.getText());
		float temperature = Float.parseFloat(fieldLlmTemperature.getText());
		settings.set(ConfigTags.LlmTemperature, temperature);
		int historySize = Integer.parseInt(fieldLlmHistorySize.getText());
		settings.set(ConfigTags.LlmHistorySize, historySize);
	}

}
