/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.LlmConversation;
import org.testar.llm.LlmFactory;
import org.testar.llm.LlmUtils;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.settings.Settings;

public class LlmPanel extends SettingsPanel {

	private static final long serialVersionUID = -5556840958014020780L;

	protected static final Logger logger = LogManager.getLogger();

	private JLabel labelLlmPlatform = new JLabel("LLM Platform");
	private JComboBox<String> llmPlatformBox = new JComboBox<>(new String[]{"OpenAI", "Gemini"});

	private JLabel labelLlmModel = new JLabel("LLM Model");
	private JTextField fieldLlmModel = new JTextField();

	private JLabel labelLlmReasoning = new JLabel("Reasoning");
	private JComboBox<String> llmReasoningBox = new JComboBox<>(new String[]{"default", "minimal", "low", "medium", "high"});

	private JButton buttonLlmConnection = new JButton("Ping");

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

	private JCheckBox checkboxStateless = new JCheckBox("Stateless prompt");

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
		llmPlatformBox.setBounds(150, 12, 80, 27);
		llmPlatformBox.setToolTipText(ConfigTags.LlmPlatform.getDescription());
		add(llmPlatformBox);
		// Add an ActionListener to change the settings based on the selected platform
		llmPlatformBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String) llmPlatformBox.getSelectedItem()) {
				case "OpenAI":
					fieldLlmHostUrl.setText("https://api.openai.com/v1/chat/completions");
					fieldLlmModel.setText("gpt-5-mini");
					llmReasoningBox.setSelectedItem("minimal");
					buttonLlmConnection.setBackground(null);
					fieldLlmAuthorizationHeader.setText("Bearer %OPENAI_API%");
					fieldLlmActionFewshot.setText("prompts/fewshot_openai_action.json");
					fieldLlmOracleFewshot.setText("prompts/fewshot_openai_oracle.json");
					break;
				case "Gemini":
					fieldLlmHostUrl.setText("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%GEMINI_API_KEY%");
					fieldLlmModel.setText("");
					llmReasoningBox.setSelectedItem("default");
					buttonLlmConnection.setBackground(null);
					fieldLlmAuthorizationHeader.setText("");
					fieldLlmActionFewshot.setText("prompts/fewshot_gemini_action.json");
					fieldLlmOracleFewshot.setText("prompts/fewshot_gemini_oracle.json");
					break;

				default:
					return;
				}
			}
		});

		labelLlmModel.setBounds(240, 12, 65, 27);
		labelLlmModel.setToolTipText(ConfigTags.LlmModel.getDescription());
		add(labelLlmModel);
		fieldLlmModel.setBounds(310, 12, 80, 27);
		fieldLlmModel.setToolTipText(ConfigTags.LlmModel.getDescription());
		add(fieldLlmModel);

		labelLlmReasoning.setBounds(395, 12, 65, 27);
		labelLlmReasoning.setToolTipText(ConfigTags.LlmReasoning.getDescription());
		add(labelLlmReasoning);
		llmReasoningBox.setBounds(465, 12, 80, 27);
		llmReasoningBox.setToolTipText(ConfigTags.LlmReasoning.getDescription());
		add(llmReasoningBox);

		buttonLlmConnection.setBounds(560, 12, 60, 27);
		buttonLlmConnection.setToolTipText("Check the LLM connection returns 200 OK");
		buttonLlmConnection.addActionListener(this::checkLlmConnection);
		add(buttonLlmConnection);

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
		fieldLlmTemperature.setBounds(150, 160, 50, 27);
		fieldLlmTemperature.setToolTipText(ConfigTags.LlmTemperature.getDescription());
		add(fieldLlmTemperature);

		labelLlmHistorySize.setBounds(230, 160, 100, 27);
		labelLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(labelLlmHistorySize);
		fieldLlmHistorySize.setBounds(350, 160, 50, 27);
		fieldLlmHistorySize.setToolTipText(ConfigTags.LlmHistorySize.getDescription());
		add(fieldLlmHistorySize);

		checkboxStateless.setBounds(450, 160, 120, 27);
		checkboxStateless.setToolTipText(ConfigTags.LlmStateless.getDescription());
		add(checkboxStateless);

		labelLlmTestGoalDescription.setBounds(10, 190, 460, 27);
		labelLlmTestGoalDescription.setToolTipText(ConfigTags.LlmTestGoals.getDescription());
		add(labelLlmTestGoalDescription);
		dirLlmTestGoalLoad.setBounds(520, 220, 100, 27);
		dirLlmTestGoalLoad.addActionListener(this::chooseTestGoalFileActionPerformed);
		add(dirLlmTestGoalLoad);

		// Dynamic JTextArea Container
		testGoalContainer.setLayout(new BoxLayout(testGoalContainer, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(testGoalContainer);
		scrollPane.setBounds(10, 220, 500, 150);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);

		// Add & Remove Buttons
		addLlmTestGoalButton.setBounds(530, 270, 80, 27);
		addLlmTestGoalButton.addActionListener(this::addTestGoalTextArea);
		add(addLlmTestGoalButton);
		removeLlmTestGoalButton.setBounds(530, 310, 80, 27);
		removeLlmTestGoalButton.addActionListener(this::removeLastTestGoalTextArea);
		add(removeLlmTestGoalButton);
	}

	// Send a hello world message to the llm to check the llm settings are correct
	private void checkLlmConnection(ActionEvent evt) {
		try {
			LlmConversation conversation = LlmFactory.createLlmConversation(
					(String) llmPlatformBox.getSelectedItem(), 
					fieldLlmModel.getText(), 
					(String) llmReasoningBox.getSelectedItem(), 
					Float.parseFloat(fieldLlmTemperature.getText()));

			conversation.addMessage("user", "Hello world! Confirm you can respond in JSON!");

			String conversationJson = conversation.buildRequestBody();

			String testarVer = Main.TESTAR_VERSION.substring(0, Main.TESTAR_VERSION.indexOf(" "));

			URI uri = URI.create(LlmUtils.replaceApiKeyPlaceholder(fieldLlmHostUrl.getText()));
			URL url = uri.toURL();
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "testar/" + testarVer);

			// Check optional Authorization Header parameter
			if (fieldLlmAuthorizationHeader.getText() != null && !this.fieldLlmAuthorizationHeader.getText().isEmpty()) {
				con.setRequestProperty("Authorization", LlmUtils.replaceApiKeyPlaceholder(fieldLlmAuthorizationHeader.getText()));
			}

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setConnectTimeout(10000);

			try(OutputStream os = con.getOutputStream()) {
				byte[] input = conversationJson.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			if(con.getResponseCode() == 200) {
				buttonLlmConnection.setBackground(Color.GREEN);
			} else {
				buttonLlmConnection.setBackground(Color.RED);
				// If response is not 200 OK, debug the error message
				try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
					StringBuilder errorResponse = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						errorResponse.append(responseLine.trim());
					}

					logger.log(Level.ERROR, String.format("LLM connection error code %d response: %s", con.getResponseCode(), errorResponse));

					throw new Exception("Server returned " + con.getResponseCode() + " status code.");
				}
			}

		} catch (Exception e) {
			buttonLlmConnection.setBackground(Color.RED);
			if(e.getMessage() != null && !e.getMessage().isEmpty()) {
				logger.log(Level.ERROR, String.format("LLM connection exception %s", e.getMessage()));
			}
		}
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
		int size = testGoalTextAreas.size();
		if (size > 0) {
			JTextArea lastTextArea = testGoalTextAreas.get(size - 1);

			// Only remove if the text area is empty
			if (lastTextArea.getDocument().getLength() == 0) {
				JScrollPane lastScrollPane = testGoalScrollPanes.get(size - 1);

				testGoalTextAreas.remove(size - 1);
				testGoalScrollPanes.remove(size - 1);
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

			// Only allow files with .goal extension
			if (!selectedFile.getName().toLowerCase().endsWith(".goal")) {
				JOptionPane.showMessageDialog(this,
						"Only files with the '.goal' extension are allowed.",
						"Invalid File Type",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
				StringBuilder content = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					content.append(line).append("\n");
				}

				// Refill the text area with the file content
				testGoalTextAreas.clear();
				testGoalScrollPanes.clear();
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
		selectComboItem(llmPlatformBox, settings.get(ConfigTags.LlmPlatform));
		selectComboItem(llmReasoningBox, settings.get(ConfigTags.LlmReasoning)); 

		fieldLlmModel.setText(settings.get(ConfigTags.LlmModel));
		fieldLlmHostUrl.setText(settings.get(ConfigTags.LlmHostUrl));
		fieldLlmAuthorizationHeader.setText(settings.get(ConfigTags.LlmAuthorizationHeader));
		fieldLlmActionFewshot.setText(settings.get(ConfigTags.LlmActionFewshotFile));
		fieldLlmOracleFewshot.setText(settings.get(ConfigTags.LlmOracleFewshotFile));
		fieldLlmTemperature.setText(settings.get(ConfigTags.LlmTemperature).toString());
		fieldLlmHistorySize.setText(settings.get(ConfigTags.LlmHistorySize).toString());
		checkboxStateless.setSelected(settings.get(ConfigTags.LlmStateless));

		testGoalTextAreas.clear();
		testGoalScrollPanes.clear();
		testGoalContainer.removeAll();
		List<String> goals = settings.get(ConfigTags.LlmTestGoals);
		for (String goal : goals) {
			addTestGoalTextArea(null);
			testGoalTextAreas.get(testGoalTextAreas.size() - 1).setText(goal.replace("\\n", "\n"));
		}
		testGoalContainer.revalidate();
		testGoalContainer.repaint();
	}

	private static void selectComboItem(JComboBox<String> box, String value) {
		if (box == null || value == null) return;
		for (int i = 0; i < box.getItemCount(); i++) {
			String item = box.getItemAt(i);
			if (item != null && item.equalsIgnoreCase(value)) {
				box.setSelectedIndex(i);
				return;
			}
		}
		// If not found, do nothing (keeps existing selection)
	}

	@Override
	public void extractInformation(Settings settings) {
		settings.set(ConfigTags.LlmPlatform, selectedOrDefaultFromCombo(llmPlatformBox,  "OpenAI"));
		settings.set(ConfigTags.LlmReasoning, selectedOrDefaultFromCombo(llmReasoningBox, "default"));

		settings.set(ConfigTags.LlmModel, fieldLlmModel.getText());
		settings.set(ConfigTags.LlmHostUrl, fieldLlmHostUrl.getText());
		settings.set(ConfigTags.LlmAuthorizationHeader, fieldLlmAuthorizationHeader.getText());
		settings.set(ConfigTags.LlmActionFewshotFile, fieldLlmActionFewshot.getText());
		settings.set(ConfigTags.LlmOracleFewshotFile, fieldLlmOracleFewshot.getText());
		settings.set(ConfigTags.LlmTemperature, Float.parseFloat(fieldLlmTemperature.getText()));
		settings.set(ConfigTags.LlmHistorySize, Integer.parseInt(fieldLlmHistorySize.getText()));
		settings.set(ConfigTags.LlmStateless, checkboxStateless.isSelected());

		List<String> goals = new ArrayList<>();
		for (JTextArea textArea : testGoalTextAreas) {
			goals.add(textArea.getText().replace("\n", "\\n").replace("\r", ""));
		}
		settings.set(ConfigTags.LlmTestGoals, goals);
	}

	private static String selectedOrDefaultFromCombo(JComboBox<String> box, String defaultValue) {
		if (box == null) return defaultValue;

		Object sel = box.getSelectedItem();
		if (sel instanceof String) {
			String s = ((String) sel).trim();
			if (!s.isEmpty()) return s;
		}

		if (box.getItemCount() > 0) {
			String first = box.getItemAt(0);
			return first != null ? first : defaultValue;
		}
		return defaultValue;
	}

}
