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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.util.List;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.exceptions.SystemStartException;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

/*
 * This protocol contains generic functionality for scriptless
 * testing of web application SUTs that are packaged using Docker Compose.
 * The protocol takes care of resetting and starting the SUT prior to each
 * run. It can optionally reset the SUT after each sequence by removing one
 * or more of its volumes and restarting it.
 *
 * Parameters to configure in settings file:
 * - DockerComposeDirectory: directory that contains SUT Docker Compose configuration (String)
 * - ResetSUTAfterSequence: whether to reset the SUT to its initial state after each sequence (Boolean)
 * - VolumesToResetAfterSequence: list of Docker volumes to remove when resetting the SUT after a sequence
*/

public class DockerizedSUTWebdriverProtocol extends GenericWebdriverProtocol {

	protected String dockerComposeDirectory,applicationBaseURL;
	protected boolean resetSUTAfterSequence = false;
	protected List<String> volumesToResetAfterSequence;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		this.dockerComposeDirectory = settings.get(ConfigTags.DockerComposeDirectory);
		this.resetSUTAfterSequence = settings.get(ConfigTags.ResetSUTAfterSequence);
		this.volumesToResetAfterSequence = settings.get(ConfigTags.VolumesToResetAfterSequence);
		this.applicationBaseURL = settings.get(ConfigTags.ApplicationBaseURL);

		initializeSUTDockerImages();
	    fullResetDockerSUT();

		super.initialize(settings);
	}

	/** Pull latest version of SUT Docker images */
	private void initializeSUTDockerImages() {
		String[] pruneCommand = {"docker", "image", "prune", "-f"};
		runDockerCommand(pruneCommand);
		String[] pullCommand = {"docker-compose", "pull"};
		runDockerCommand(pullCommand);
    }

	/** Resets all state of the SUT
	 */
	private void fullResetDockerSUT() {
		String[] fullStopCommand = { "docker-compose", "down", "-v"};
		runDockerCommand(fullStopCommand);
		String[] containerPruneCommand = {"docker", "container", "prune", "-f"};
		runDockerCommand(containerPruneCommand);
		String[] volumePruneCommand = {"docker", "volume", "prune", "-f"};
		runDockerCommand(volumePruneCommand);
	}

	@Override
	protected SUT startSystem() throws SystemStartException {
		System.out.println("DS protocol starting SUT");
		startDockerSUT();
		System.out.println("DS protocol starting call super");
		return super.startSystem();
	}

	protected void startDockerSUT() {
		String[] command = {"docker-compose", "up", "--force-recreate", "--build", "-d"};
		runDockerCommand(command);
		if (! waitForURL(this.applicationBaseURL, 300, 5, 200) ) {
			System.out.println("Error: did not succeed in bringing up SUT.");
		}
	}

	protected void stopDockerSUT() {
		String[] command = {"docker-compose", "down"};
		runDockerCommand(command);
	}

	protected void resetSUTBetweenSequences() {
		stopDockerSUT();
		removeSUTApplicationVolumes();
		startDockerSUT();
	}

	protected void removeSUTApplicationVolumes() {
		for (String volume : this.volumesToResetAfterSequence ) {
			String[] command = {"docker", "volume", "rm", volume};
			runDockerCommand(command);
		}
	}

	@Override
	protected void finishSequence() {
		super.finishSequence();
		resetSUTBetweenSequences();
	}

	/** Runs a docker command in the Docker Compose Directory */
	protected void runDockerCommand(String[] command) {
		ProcessBuilder builder = new ProcessBuilder(command);
		System.out.println("Protocol executing command: " + String.join(" ", command));
		builder = builder.directory(new File(dockerComposeDirectory));
		builder.redirectErrorStream(true);
		try {
			synchronized (builder) {
				Process p = builder.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null)
					System.out.println("Command output: " + line);
				p.waitFor();
			}
		} catch (IOException ioe) {
			System.out.println("Exception when starting command" + String.join(" ", command)
					+ " " + ioe.toString());
		} catch (InterruptedException ie) {
			System.out.println("Exception when waiting for command" + String.join(" ", command)
					+ " " + ie.toString());
		}
	}

/**
     * Waits for requests to a particular URL to return a particular status code, with multiple retries.
     *
     * @param urlString URL to send requests to
     * @param maxWaitTime Approximate maximum time to wait, in seconds
     * @param retryTime Approximate time between retries, in seconds
     * @param expectedStatusCode return
     * @return boolean value that shows whether the requests
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     */
    public static boolean waitForURL(String urlString, int maxWaitTime, int retryTime, int expectedStatusCode) {
        long beginTime = System.currentTimeMillis() / 1000L;
        long currentTime = beginTime;
        while ( ( currentTime = System.currentTimeMillis() / 1000L ) < ( beginTime + maxWaitTime) ) {
        try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(retryTime*1000);
                con.setReadTimeout(retryTime*1000);
                int status = con.getResponseCode();
                System.out.println("Status is " + status);
            if ( status == expectedStatusCode ) {
                System.out.println("Waiting for " + urlString + " finished.");
                return true;
            }
            else
            {  System.out.println("Info: unexpected status code " + Integer.toString(status) +
                    " while waiting for " + urlString);
            }
        }
        catch ( SocketTimeoutException ste) {
            System.out.println("info: waiting for " + urlString + " ...");
            continue;
        }
        catch ( Exception e) {
            System.out.println("info: generic exception while waiting for " + urlString +
                    ": " + e.toString() );
            System.out.println(Long.toString(currentTime));
        }
        System.out.println("info: sleeping between retries for " + urlString + " ...");
        try {
         Thread.sleep(retryTime*1000);
        }
        catch (InterruptedException ie) {
         System.out.println("Sleep between retries for " + urlString + " was interrupted.");
        }
        }
        System.out.println("info: max wait time expired while waiting for " + urlString + " ...");
        return false;
    }

}
