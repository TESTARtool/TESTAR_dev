/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.oracle;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.prompt.IPromptOracleGenerator;
import org.testar.oracle.Oracle;
import org.testar.config.ConfigTags;
import org.testar.config.TestarInfo;
import org.testar.config.settings.Settings;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.llm.LlmConversation;
import org.testar.llm.LlmFactory;
import org.testar.llm.LlmResponse;
import org.testar.llm.LlmTestGoal;
import org.testar.llm.LlmUtils;
import org.testar.plugin.screenshot.ScreenshotProviderFactory;

public class LlmOracle implements Oracle {

	protected static final Logger logger = LogManager.getLogger();

	private IPromptOracleGenerator promptGenerator;

	private final String platform;
	private final String model;
	private final String reasoning;
	private final String hostUrl;
	private final String authorizationHeader;
	private final String fewshotOracleFile;
	private final String appName;
	private final float temperature;
	private final boolean stateless;

	private LlmConversation conversation;
	private int tokens_used;

	private String previousTestGoal = "";
	private LlmTestGoal currentTestGoal;

	public LlmOracle(Settings settings, IPromptOracleGenerator oracleGenerator) {
		this.promptGenerator = oracleGenerator;

		this.platform = settings.get(ConfigTags.LlmPlatform);
		this.model = settings.get(ConfigTags.LlmModel);
		this.reasoning = settings.get(ConfigTags.LlmReasoning);
		this.hostUrl = settings.get(ConfigTags.LlmHostUrl);
		this.authorizationHeader = settings.get(ConfigTags.LlmAuthorizationHeader);
		this.fewshotOracleFile = settings.get(ConfigTags.LlmOracleFewshotFile);
		this.appName = settings.get(ConfigTags.ApplicationName);
		this.temperature = settings.get(ConfigTags.LlmTemperature);
		this.stateless = settings.get(ConfigTags.LlmStateless);

		initialize();
	}

	public void reset(LlmTestGoal newTestGoal, boolean appendPreviousTestGoal) {
		// Reset variables
		tokens_used = 0;

		if(appendPreviousTestGoal) {
			previousTestGoal = currentTestGoal.getTestGoal();
		} else {
			previousTestGoal = "";
		}

		currentTestGoal = newTestGoal;

		// When a new goal is attached, always re-initialize a new conversation
		initialize();
	}

	@Override
	public void initialize() {
		conversation = LlmFactory.createLlmConversation(this.platform, this.model, this.reasoning, this.temperature);
		conversation.initConversation(this.fewshotOracleFile);
	}

	@Override
	public List<Verdict> getVerdicts(State state) {
		// If the stateless option is enabled, initialize a new prompt to reduce tokens usage
		if(this.stateless) initialize();

		return Collections.singletonList(getVerdictWithLlm(state));
	}

	private Verdict getVerdictWithLlm(State state) {
		String prompt = promptGenerator.generateOraclePrompt(state, appName, currentTestGoal.getTestGoal(), previousTestGoal);
		logger.log(Level.DEBUG, "Generated oracle prompt: " + prompt);

		if (promptGenerator.attachImage()) {

			ByteArrayOutputStream screenshotBytes = new ByteArrayOutputStream();
			AWTCanvas screenshot = ScreenshotProviderFactory.current().getStateshotBinary(state);

			try {
				screenshot.saveAsPng(screenshotBytes);
				byte[] imageBytes = screenshotBytes.toByteArray();
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				conversation.addMessage("user", prompt, base64Image);
			} catch (IOException e) {
				logger.log(Level.WARN, "OracleImagePromptGenerator: Issue generating base64 image");
				conversation.addMessage("user", prompt);
			}

		} else {
			conversation.addMessage("user", prompt);
		}

		String conversationJson = conversation.buildRequestBody();

		try {

			String llmResponse = getResponseFromLlm(conversationJson);

			LlmVerdict llmVerdict = LlmVerdictParser.parse(llmResponse);
			String info = llmVerdict.getInfo() == null ? "" : llmVerdict.getInfo();

			switch (llmVerdict.getDecision()) {
			case COMPLETED:
				return new Verdict(Verdict.Severity.LLM_COMPLETE, info);
			case INVALID:
				return new Verdict(Verdict.Severity.LLM_INVALID, info);
			case CONTINUE:
				return Verdict.OK;
			case UNKNOWN:
			default:
				logger.log(Level.WARN, "LLM oracle response did not include a recognized verdict status/match.");
				return Verdict.OK;
			}

		} catch(Exception e) {
			logger.log(Level.ERROR, "Error obtaining the verdict with the LLM");
		}

		return Verdict.OK;
	}

	private String getResponseFromLlm(String requestBody) {
		String testarVer = TestarInfo.VERSION.substring(0, TestarInfo.VERSION.indexOf(" "));
		URI uri = URI.create(LlmUtils.replaceApiKeyPlaceholder(this.hostUrl));

		try {
			URL url = uri.toURL();
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "testar/" + testarVer);

			// Check optional Authorization Header parameter
			if (this.authorizationHeader != null && !this.authorizationHeader.isEmpty()) {
				con.setRequestProperty("Authorization", LlmUtils.replaceApiKeyPlaceholder(this.authorizationHeader));
			}

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setConnectTimeout(10000);

			try(OutputStream os = con.getOutputStream()) {
				byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			if(con.getResponseCode() == 200) {
				try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}

					LlmResponse llmResponse = LlmFactory.createResponse(this.platform, response);
					this.tokens_used = llmResponse.getUsageTokens();
					logger.log(Level.INFO, String.format("LLM tokens_used for oracle: [%s]", this.tokens_used));

					String responseContent = llmResponse.getResponse();
					// From testing, response often includes newlines and spaces at the end.
					// We strip this here to so we can parse the result easier.
					responseContent = responseContent.replace("\n", "").replace("\r", "");
					responseContent = responseContent.replaceFirst("\\s++$", "");

					logger.log(Level.INFO, String.format("LLM Response: [%s]", responseContent));

					return responseContent;
				}
			} else {
				// If response is not 200 OK, debug the error message
				try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
					StringBuilder errorResponse = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						errorResponse.append(responseLine.trim());
					}

					logger.log(Level.ERROR, String.format("LLM error code %d response: %s", con.getResponseCode(), errorResponse));

					throw new Exception("Server returned " + con.getResponseCode() + " status code.");
				}
			}
		} catch(Exception e) {
			logger.log(Level.ERROR, "Unable to communicate with the LLM due to the cause:");
			if(e.getMessage() != null && !e.getMessage().isEmpty()) {
				logger.log(Level.ERROR, e.getMessage());
			} else {
				e.printStackTrace();
			}
			return null;
		}
	}
}
