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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.settings.Settings;

public class LlmPanel extends SettingsPanel {

	private static final long serialVersionUID = -5556840958014020780L;

	private JLabel labelLlmPlatform = new JLabel("LLM Platform");
	private JComboBox<String> llmPlatformBox = new JComboBox<>(new String[]{"OpenAI", "Gemini"});

	private JLabel labelLlmModel = new JLabel("LLM Model");
	private JTextField fieldLlmModel = new JTextField();

	private JLabel labelLlmHostUrl = new JLabel("LLM Host Url");
	private JTextField fieldLlmHostUrl = new JTextField();

	private JLabel labelLlmAuthorizationHeader= new JLabel("LLM Auth Header");
	private JTextField fieldLlmAuthorizationHeader = new JTextField();

	private JLabel labelLlmFewshotFile = new JLabel("LLM Fewshot File");
	private JTextField fieldLlmFewshotFile  = new JTextField();
	private JButton dirLlmButton = new JButton("..");

	private JLabel labelLlmTemperature = new JLabel("LLM Temperature");
	private JTextField fieldLlmTemperature = new JTextField();

	private JLabel labelLlmTestGoalDescription = new JLabel("LLM Test Goal Description (User story, Gherkin structure, Playwright script, or other)");
	private JButton dirLlmTestGoalLoad = new JButton("Load Goal");
	private JTextArea txtLlmTestGoalDescription = new JTextArea();

    private JLabel labelLlmHistorySize = new JLabel("Action History Size");
    private JTextField fieldLlmHistorySize = new JTextField();

	public LlmPanel() {
		setLayout(null);

		labelLlmPlatform.setBounds(10, 12, 120, 27);
		labelLlmPlatform.setToolTipText(ConfigTags.LlmPlatform.getDescription());
		add(labelLlmPlatform);
		llmPlatformBox.setBounds(150, 12, 150, 27);
		llmPlatformBox.setToolTipText(ConfigTags.LlmPlatform.getDescription());
		add(llmPlatformBox);
		// Add an ActionListener to change the settings based on the selected platform
		llmPlatformBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String) llmPlatformBox.getSelectedItem()) {
				case "OpenAI":
					fieldLlmHostUrl.setText("http://192.168.108.242:1234/v1/chat/completions");
					fieldLlmFewshotFile.setText("prompts/fewshot_login_openai.json");
					fieldLlmTemperature.setText("0.2");
					break;
				case "Gemini":
					fieldLlmHostUrl.setText("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%GEMINI_API_KEY%");
					fieldLlmFewshotFile.setText("prompts/fewshot_login_gemini.json");
					fieldLlmTemperature.setText("0.2");
					break;

				default:
					return;
				}
			}
		});

		labelLlmModel.setBounds(330, 12, 70, 27);
		labelLlmModel.setToolTipText(ConfigTags.LlmModel.getDescription());
		add(labelLlmModel);
		fieldLlmModel.setBounds(400, 12, 150, 27);
		fieldLlmModel.setToolTipText(ConfigTags.LlmModel.getDescription());
		add(fieldLlmModel);

		labelLlmHostUrl.setBounds(10, 40, 120, 27);
		labelLlmHostUrl.setToolTipText(ConfigTags.LlmHostUrl.getDescription());
		add(labelLlmHostUrl);
		fieldLlmHostUrl.setBounds(150, 40, 400, 27);
		fieldLlmHostUrl.setToolTipText(ConfigTags.LlmHostUrl.getDescription());
		add(fieldLlmHostUrl);

		labelLlmAuthorizationHeader.setBounds(10, 70, 120, 27);
		labelLlmAuthorizationHeader.setToolTipText(ConfigTags.LlmAuthorizationHeader.getDescription());
		add(labelLlmAuthorizationHeader);
		fieldLlmAuthorizationHeader.setBounds(150, 70, 400, 27);
		fieldLlmAuthorizationHeader.setToolTipText(ConfigTags.LlmAuthorizationHeader.getDescription());
		add(fieldLlmAuthorizationHeader);

		labelLlmFewshotFile.setBounds(10, 100, 120, 27);
		labelLlmFewshotFile.setToolTipText(ConfigTags.LlmFewshotFile.getDescription());
		add(labelLlmFewshotFile);
		fieldLlmFewshotFile.setBounds(150, 100, 400, 27);
		fieldLlmFewshotFile.setToolTipText(ConfigTags.LlmFewshotFile.getDescription());
		fieldLlmFewshotFile.setEditable(false);
		add(fieldLlmFewshotFile);
		dirLlmButton.setBounds(550, 100, 20, 27);
		dirLlmButton.addActionListener(this::chooseFewshotFileFileActionPerformed);
		add(dirLlmButton);

		labelLlmTemperature.setBounds(10, 130, 120, 27);
		labelLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(labelLlmTemperature);
		fieldLlmTemperature.setBounds(150, 130, 400, 27);
		fieldLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(fieldLlmTemperature);

		labelLlmHistorySize.setBounds(10, 160, 180, 27);
		labelLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(labelLlmHistorySize);
		fieldLlmHistorySize.setBounds(150, 160, 400, 27);
		fieldLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(fieldLlmHistorySize);

		labelLlmTestGoalDescription.setBounds(10, 190, 460, 27);
		labelLlmTestGoalDescription.setToolTipText(ConfigTags.LlmTestGoalDescription.getDescription());
		add(labelLlmTestGoalDescription);
		dirLlmTestGoalLoad.setBounds(510, 190, 100, 27);
		dirLlmTestGoalLoad.addActionListener(this::chooseTestGoalFileFileActionPerformed);
		add(dirLlmTestGoalLoad);
		txtLlmTestGoalDescription.setLineWrap(true);
		JScrollPane llmTestGoalDescriptionPane = new JScrollPane(txtLlmTestGoalDescription);
		llmTestGoalDescriptionPane.setBounds(10, 220, 600, 150);
		llmTestGoalDescriptionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		llmTestGoalDescriptionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		llmTestGoalDescriptionPane.setToolTipText(ConfigTags.LlmTestGoalDescription.getDescription());
		add(llmTestGoalDescriptionPane);
	}

	// show a file dialog to choose the LLM Fewshot File
	private void chooseFewshotFileFileActionPerformed(ActionEvent evt) {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(Main.testarDir));

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();

			// Set the text from settings in txtSutPath
			fieldLlmFewshotFile.setText(file);
		}
	}

	// show a file dialog to choose the test goal file content
	private void chooseTestGoalFileFileActionPerformed(ActionEvent evt) {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(Main.testarDir));

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fd.getSelectedFile();

			try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
				StringBuilder content = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					content.append(line).append("\n");
				}

				// Set the content of the selected file
				txtLlmTestGoalDescription.setText(content.toString());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
			}
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

		fieldLlmModel.setText(settings.get(ConfigTags.LlmModel));
		fieldLlmHostUrl.setText(settings.get(ConfigTags.LlmHostUrl));
		fieldLlmAuthorizationHeader.setText(settings.get(ConfigTags.LlmAuthorizationHeader));
		fieldLlmFewshotFile.setText(settings.get(ConfigTags.LlmFewshotFile));
		fieldLlmTemperature.setText(settings.get(ConfigTags.LlmTemperature).toString());
		fieldLlmHistorySize.setText(settings.get(ConfigTags.LlmHistorySize).toString());
		txtLlmTestGoalDescription.setText(settings.get(ConfigTags.LlmTestGoalDescription).replace("\\n", "\n"));
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

		settings.set(ConfigTags.LlmModel, fieldLlmModel.getText());
		settings.set(ConfigTags.LlmHostUrl, fieldLlmHostUrl.getText());
		settings.set(ConfigTags.LlmAuthorizationHeader, fieldLlmAuthorizationHeader.getText());
		settings.set(ConfigTags.LlmFewshotFile, fieldLlmFewshotFile.getText());
		settings.set(ConfigTags.LlmTemperature, Float.parseFloat(fieldLlmTemperature.getText()));
		settings.set(ConfigTags.LlmHistorySize, Integer.parseInt(fieldLlmHistorySize.getText()));
		settings.set(ConfigTags.LlmTestGoalDescription, txtLlmTestGoalDescription.getText().replace("\n", "\\n").replace("\r", ""));
	}

}