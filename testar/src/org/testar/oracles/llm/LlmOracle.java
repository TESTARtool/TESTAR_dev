/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles.llm;

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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.prompt.IPromptOracleGenerator;
import org.testar.llm.prompt.OracleImagePromptGenerator;
import org.testar.ProtocolUtil;
import org.testar.llm.LlmConversation;
import org.testar.llm.LlmFactory;
import org.testar.llm.LlmResponse;
import org.testar.llm.LlmTestGoal;
import org.testar.llm.LlmUtils;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.android.AndroidProtocolUtil;
import org.testar.monkey.alayer.ios.IOSProtocolUtil;
import org.testar.monkey.alayer.webdriver.WdProtocolUtil;
import org.testar.oracles.Oracle;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.settings.Settings;

import com.google.gson.Gson;

public class LlmOracle implements Oracle {

	protected static final Logger logger = LogManager.getLogger();

	private IPromptOracleGenerator promptGenerator;

	private final String platform;
	private final String model;
	private final String hostUrl;
	private final String authorizationHeader;
	private final String fewshotOracleFile;
	private final String appName;
	private final float temperature;
	private final boolean stateless;

	private LlmConversation conversation;
	private int tokens_used;

	private Gson gson = new Gson();
	private String previousTestGoal = "";
	private LlmTestGoal currentTestGoal;

	public LlmOracle(Settings settings, IPromptOracleGenerator oracleGenerator) {
		this.promptGenerator = oracleGenerator;

		this.platform = settings.get(ConfigTags.LlmPlatform);
		this.model = settings.get(ConfigTags.LlmModel);
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
		conversation = LlmFactory.createLlmConversation(this.platform, this.model, this.temperature);
		conversation.initConversation(this.fewshotOracleFile);
	}

	@Override
	public String getMessage() {
		return "";
	}

	@Override
	public Verdict getVerdict(State state) {
		// If the stateless option is enabled, initialize a new prompt to reduce tokens usage
		if(this.stateless) initialize();

		return getVerdictWithLlm(state);
	}

	private Verdict getVerdictWithLlm(State state) {
		String prompt = promptGenerator.generateOraclePrompt(state, appName, currentTestGoal.getTestGoal(), previousTestGoal);
		logger.log(Level.DEBUG, "Generated oracle prompt: " + prompt);

		if (promptGenerator instanceof OracleImagePromptGenerator) {

			ByteArrayOutputStream screenshotBytes = new ByteArrayOutputStream();
			AWTCanvas screenshot;
			if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
				screenshot = WdProtocolUtil.getStateshotBinary(state);
			} else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
				screenshot = AndroidProtocolUtil.getStateshotBinary(state);
			} else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS)) {
				screenshot = IOSProtocolUtil.getStateshotBinary(state);
			}
			else screenshot = ProtocolUtil.getStateshotBinary(state);

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

		String conversationJson = gson.toJson(conversation);

		try {

			String llmResponse = getResponseFromLlm(conversationJson);

			LlmVerdict llmVerdict = gson.fromJson(llmResponse, LlmVerdict.class);

			if(llmVerdict.match()) return new Verdict(Verdict.Severity.LLM_COMPLETE, llmVerdict.getInfo());

		} catch(Exception e) {
			logger.log(Level.ERROR, "Error obtaining the verdict with the LLM");
		}

		return Verdict.OK;
	}

	private String getResponseFromLlm(String requestBody) {
		String testarVer = Main.TESTAR_VERSION.substring(0, Main.TESTAR_VERSION.indexOf(" "));
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
