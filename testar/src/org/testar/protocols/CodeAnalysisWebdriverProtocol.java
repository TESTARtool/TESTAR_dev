/**
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

package org.testar.protocols;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONTokener;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

/*
 * This protocol contains generic functionality for scriptless testing of web application SUTs
 * that are packaged using Docker Compose. The protocol optionally takes care of the following
 * tasks:
 *  - Setting the coverage context of the SUT instrumentation, so that collected coverage data can be
 *    related to the experiment.
 *  - Setting the log context of the SUT prior to each action, so that instrumentation data related to
 *    individual actions can be related to each action.
 *  - Retrieving action related data from the instrumented SUT and passing it to the application-level
 *    protocol for further processing.
 *
 * This protocol is based on DockerizedSUTWebdriverProtocol, which handles managing the SUT using Docker
 * Compose. See that class for additional information.
 *
 * Settings to configure:
 * - ApplicationBaseURL: base URL that the SUT will be running on, e.g. http://localhost:5000
 * - CoverageContext: label to attach to all collected coverage data in the instrumented SUT (string)
 * - SetCoverageContext: whether to set the coverage context (boolean)
 * - LogContextPrefix: prefix of the label to attach to all action-specific data collected by the SUT instrumentation.
 *   the context is generated based on this prefix, the sequence number, and action number.
 * - SetLogContext: whether to set the log context before each action (boolean)
 * - ProcessDataAfterAction: whether to collected action data after each action and pass it to the
 *   processActionData method in any class extending this class (boolean)
 * - ActionGetDataEndpoint: API endpoint in the web interface of the SUT for retrieving the action-related
 *   data. It is assumed the URL has the format http://sut/endpointname/logcontextname, and that this endpoint
 *   returns JSON data.
*/

public class CodeAnalysisWebdriverProtocol extends DockerizedSUTWebdriverProtocol {

	protected String coverageContext,logContextPrefix,applicationBaseURL,actionGetDataEndpoint;
    protected boolean setCoverageContext=false,setLogContext=false,processDataAfterAction=false;
    protected int sequenceNumber = -1, actionNumber = -1;

    // This boolean value can be used by the protocol to temporarily disable calls to the
    // instrumentation, e.g. during initial login actions.
    protected boolean instrumentationEnabled = true;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {

		this.coverageContext = settings.get(ConfigTags.CoverageContext);
        this.logContextPrefix = settings.get(ConfigTags.LogContextPrefix);
        this.applicationBaseURL = settings.get(ConfigTags.ApplicationBaseURL);
        this.setLogContext = settings.get(ConfigTags.SetLogContext);
        this.setCoverageContext = settings.get(ConfigTags.SetCoverageContext);
        this.processDataAfterAction = settings.get(ConfigTags.ProcessDataAfterAction);
        this.actionGetDataEndpoint = settings.get(ConfigTags.ActionGetDataEndpoint);

        super.initialize(settings);

	}

    @Override
	protected SUT startSystem() throws SystemStartException {
        SUT sut = super.startSystem();
        if ( this.instrumentationEnabled && this.setCoverageContext ) {
            setCoverageContext();
        }
        return sut;
	}

    private void setCoverageContext() {
		String setContextURL = this.applicationBaseURL + "/testar-covcontext/" + this.coverageContext;

		if (! waitForURL(setContextURL, 60, 5, 200) )  {
			System.out.println("Error: did not succeed in setting coverage context.");
		}
	}

    @Override
	protected boolean executeAction(SUT system, State state, Action action) {
		if (this.instrumentationEnabled && this.setLogContext) {
            setLogContext();
        }
		return super.executeAction(system, state, action);
    }

    private void setLogContext() {
		String context = this.logContextPrefix + "-" + Integer.toString(sequenceNumber) + "-" +
			Integer.toString(actionNumber);
		String setContextURL = applicationBaseURL + "/testar-logcontext/" + context;
		if ( ! waitForURL(setContextURL, 60, 5, 200) )  {
			System.out.println("Error: did not succeed in setting log context for context "
				+ context + ".");
		}
	}

    @Override
	protected void beginSequence(SUT system, State state) {
        this.sequenceNumber++;
        this.actionNumber = 0;
        super.beginSequence(system, state);
    }

    @Override
	protected Action selectAction(State state, Set<Action> actions) {
		this.actionNumber++;
        if (this.instrumentationEnabled && this.processDataAfterAction ) {
            retrieveSUTDataAfterAction();
        }
        return super.selectAction(state, actions);
    }

    protected void retrieveSUTDataAfterAction() {
		String queryUrl = this.applicationBaseURL + this.actionGetDataEndpoint + "/" +
			this.logContextPrefix + "-" + Integer.toString(sequenceNumber) + "-" +
			Integer.toString(actionNumber);

		try {
			URL url = new URL(queryUrl);
			JSONTokener tokener = new JSONTokener(url.openStream());
            processSUTDataAfterAction(tokener);
        }
        catch (Exception e) {
			System.out.println("Error during extracting action data from SUT: " + e.toString());
		}

    }

    protected void processSUTDataAfterAction(JSONTokener tokener) {
        // Left to be implemented in subclass, in case ProcessDataAfterAction
        // is set to true in the settings.
    }


    /**
     * Waits for requests to a particular URL to return a particular status code, with multiple retries.
     *
     * @param url_string URL to send requests to
     * @param maxWaitTime Approximate maximum time to wait, in seconds
     * @param retryTime Approximate time between retries, in seconds
     * @param expectedStatusCode return
     * @return boolean value that shows whether the requests
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     */
    public static boolean waitForURL(String url_string, int maxWaitTime, int retryTime, int expectedStatusCode) {
        long beginTime = System.currentTimeMillis() / 1000L;
        long currentTime = beginTime;
        while ( ( currentTime = System.currentTimeMillis() / 1000L ) < ( beginTime + maxWaitTime) ) {
        try {
                URL url = new URL(url_string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(retryTime*1000);
                con.setReadTimeout(retryTime*1000);
                int status = con.getResponseCode();
                System.out.println("Status is " + status);
            if ( status == expectedStatusCode ) {
                System.out.println("Waiting for " + url_string + " finished.");
                return true;
            }
            else
            {  System.out.println("Info: unexpected status code " + Integer.toString(status) +
                    " while waiting for " + url_string);
            }
        }
        catch ( SocketTimeoutException ste) {
            System.out.println("info: waiting for " + url_string + " ...");
            continue;
        }
        catch ( Exception e) {
            System.out.println("info: generic exception while waiting for " + url_string +
                    ": " + e.toString() );
            System.out.println(Long.toString(currentTime));
        }
        System.out.println("info: sleeping between retries for " + url_string + " ...");
        try {
         Thread.sleep(retryTime*1000);
        }
        catch (InterruptedException ie) {
         System.out.println("Sleep between retries for " + url_string + " was interrupted.");
        }
        }
        System.out.println("info: max wait time expired while waiting for " + url_string + " ...");
        return false;
    }

}
