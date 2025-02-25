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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

	private JLabel labelLlmActionFewshot = new JLabel("LLM Action Fewshot");
	private JTextField fieldLlmActionFewshot  = new JTextField();
	private JButton dirLlmActionFewshotButton = new JButton("..");

	private JLabel labelLlmOracleFewshot = new JLabel("LLM Oracle Fewshot");
	private JTextField fieldLlmOracleFewshot  = new JTextField();
	private JButton dirLlmOracleFewshotButton = new JButton("..");

	private JLabel labelLlmTemperature = new JLabel("LLM Temperature");
	private JTextField fieldLlmTemperature = new JTextField();

	private JLabel labelLlmHistorySize = new JLabel("Action History Size");
	private JTextField fieldLlmHistorySize = new JTextField();

	private JLabel labelLlmTestGoalDescription = new JLabel("LLM Test Goal Description (User story, Gherkin structure, Playwright script, or other)");
	private JButton dirLlmTestGoalLoad = new JButton("Load Goal");
	private JPanel testGoalContainer = new JPanel();
	private List<JScrollPane> testGoalScrollPanes = new ArrayList<>();
	private List<JTextArea> testGoalTextAreas = new ArrayList<>();
	private JButton addLlmTestGoalButton = new JButton("Add");
	private JButton removeLlmTestGoalButton = new JButton("Remove");

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
					fieldLlmActionFewshot.setText("prompts/fewshot_openai_action.json");
					fieldLlmOracleFewshot.setText("prompts/fewshot_openai_oracle.json");
					fieldLlmTemperature.setText("0.2");
					break;
				case "Gemini":
					fieldLlmHostUrl.setText("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%GEMINI_API_KEY%");
					fieldLlmActionFewshot.setText("prompts/fewshot_gemini_action.json");
					fieldLlmOracleFewshot.setText("prompts/fewshot_gemini_oracle.json");
					fieldLlmTemperature.setText("0.2");
					break;

				default:
					return;
				}
			}
		});

		labelLlmModel.setBounds(330, 12, 100, 27);
		labelLlmModel.setToolTipText(ConfigTags.LlmModel.getDescription());
		add(labelLlmModel);
		fieldLlmModel.setBounds(440, 12, 110, 27);
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

		labelLlmActionFewshot.setBounds(10, 100, 120, 27);
		labelLlmActionFewshot.setToolTipText(ConfigTags.LlmActionFewshotFile.getDescription());
		add(labelLlmActionFewshot);
		fieldLlmActionFewshot.setBounds(150, 100, 400, 27);
		fieldLlmActionFewshot.setToolTipText(ConfigTags.LlmActionFewshotFile.getDescription());
		fieldLlmActionFewshot.setEditable(false);
		add(fieldLlmActionFewshot);
		dirLlmActionFewshotButton.setBounds(550, 100, 20, 27);
		dirLlmActionFewshotButton.addActionListener(evt -> chooseFewshotFileFileActionPerformed(evt, fieldLlmActionFewshot));
		add(dirLlmActionFewshotButton);

		labelLlmOracleFewshot.setBounds(10, 130, 120, 27);
		labelLlmOracleFewshot.setToolTipText(ConfigTags.LlmOracleFewshotFile.getDescription());
		add(labelLlmOracleFewshot);
		fieldLlmOracleFewshot.setBounds(150, 130, 400, 27);
		fieldLlmOracleFewshot.setToolTipText(ConfigTags.LlmOracleFewshotFile.getDescription());
		fieldLlmOracleFewshot.setEditable(false);
		add(fieldLlmOracleFewshot);
		dirLlmOracleFewshotButton.setBounds(550, 130, 20, 27);
		dirLlmOracleFewshotButton.addActionListener(evt -> chooseFewshotFileFileActionPerformed(evt, fieldLlmOracleFewshot));
		add(dirLlmOracleFewshotButton);

		labelLlmTemperature.setBounds(10, 160, 120, 27);
		labelLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(labelLlmTemperature);
		fieldLlmTemperature.setBounds(150, 160, 150, 27);
		fieldLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(fieldLlmTemperature);

		labelLlmHistorySize.setBounds(330, 160, 100, 27);
		labelLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(labelLlmHistorySize);
		fieldLlmHistorySize.setBounds(440, 160, 110, 27);
		fieldLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(fieldLlmHistorySize);

		labelLlmTestGoalDescription.setBounds(10, 190, 460, 27);
		labelLlmTestGoalDescription.setToolTipText(ConfigTags.LlmTestGoals.getDescription());
		add(labelLlmTestGoalDescription);
		dirLlmTestGoalLoad.setBounds(510, 190, 100, 27);
		dirLlmTestGoalLoad.addActionListener(this::chooseTestGoalFileActionPerformed);
		add(dirLlmTestGoalLoad);

		// Dynamic JTextArea Container
		testGoalContainer.setLayout(new BoxLayout(testGoalContainer, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(testGoalContainer);
		scrollPane.setBounds(10, 220, 500, 150);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);

		// Add & Remove Buttons
		addLlmTestGoalButton.setBounds(520, 260, 80, 27);
		addLlmTestGoalButton.addActionListener(this::addTestGoalTextArea);
		add(addLlmTestGoalButton);
		removeLlmTestGoalButton.setBounds(520, 300, 80, 27);
		removeLlmTestGoalButton.addActionListener(this::removeLastTestGoalTextArea);
		add(removeLlmTestGoalButton);
	}

	// show a file dialog to choose the LLM Fewshot File
	private void chooseFewshotFileFileActionPerformed(ActionEvent evt, JTextField textField) {
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setCurrentDirectory(new File(Main.testarDir));

		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String file = fd.getSelectedFile().getAbsolutePath();

			// Set the text from settings in txtSutPath
			textField.setText(file);
		}
	}

	// Add a new JTextArea dynamically
	private void addTestGoalTextArea(ActionEvent evt) {
		JTextArea newTextArea = new JTextArea(3, 40);
		newTextArea.setLineWrap(true);
		newTextArea.setWrapStyleWord(true);

		JScrollPane textAreaScrollPane = new JScrollPane(newTextArea);
		testGoalScrollPanes.add(textAreaScrollPane);
		testGoalTextAreas.add(newTextArea);

		testGoalContainer.add(textAreaScrollPane);
		testGoalContainer.revalidate();
		testGoalContainer.repaint();
	}

	// Remove the last JTextArea
	private void removeLastTestGoalTextArea(ActionEvent evt) {
		if (!testGoalTextAreas.isEmpty()) {
			int lastIndex = testGoalTextAreas.size() - 1;
			JTextArea lastTextArea = testGoalTextAreas.get(lastIndex);

			// Only remove if the text area is empty
			if (lastTextArea.getText().trim().isEmpty()) {
				JScrollPane lastScrollPane = testGoalScrollPanes.remove(lastIndex);
				testGoalTextAreas.remove(lastIndex);

				testGoalContainer.remove(lastScrollPane);
				testGoalContainer.revalidate();
				testGoalContainer.repaint();
			} else {
				JOptionPane.showMessageDialog(this, "Cannot remove a non-empty test goal. Please clear the content first.", 
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	// Load a file and populate a new JTextArea
	private void chooseTestGoalFileActionPerformed(ActionEvent evt) {
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

				// Refill the text area with the file content
				testGoalTextAreas.clear();
				testGoalContainer.removeAll();
				List<String> goals = Arrays.asList(content.toString().split(";", -1));
				for (String goal : goals) {
					addTestGoalTextArea(null);
					testGoalTextAreas.get(testGoalTextAreas.size() - 1).setText(goal);
				}
				// Remove extra empty lines
				for (JTextArea textArea : testGoalTextAreas) {
					String cleanedText = textArea.getText().replaceAll("(?m)^\\s*$\n?", "").trim();
					textArea.setText(cleanedText);
				}
				testGoalContainer.revalidate();
				testGoalContainer.repaint();

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
		fieldLlmActionFewshot.setText(settings.get(ConfigTags.LlmActionFewshotFile));
		fieldLlmOracleFewshot.setText(settings.get(ConfigTags.LlmOracleFewshotFile));
		fieldLlmTemperature.setText(settings.get(ConfigTags.LlmTemperature).toString());
		fieldLlmHistorySize.setText(settings.get(ConfigTags.LlmHistorySize).toString());

		testGoalTextAreas.clear();
		testGoalContainer.removeAll();
		List<String> goals = settings.get(ConfigTags.LlmTestGoals);
		for (String goal : goals) {
			addTestGoalTextArea(null);
			testGoalTextAreas.get(testGoalTextAreas.size() - 1).setText(goal.replace("\\n", "\n"));
		}
		testGoalContainer.revalidate();
		testGoalContainer.repaint();
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
		settings.set(ConfigTags.LlmActionFewshotFile, fieldLlmActionFewshot.getText());
		settings.set(ConfigTags.LlmOracleFewshotFile, fieldLlmOracleFewshot.getText());
		settings.set(ConfigTags.LlmTemperature, Float.parseFloat(fieldLlmTemperature.getText()));
		settings.set(ConfigTags.LlmHistorySize, Integer.parseInt(fieldLlmHistorySize.getText()));

		List<String> goals = new ArrayList<>();
		for (JTextArea textArea : testGoalTextAreas) {
			goals.add(textArea.getText().replace("\n", "\\n").replace("\r", ""));
		}
		settings.set(ConfigTags.LlmTestGoals, goals);
	}

}
