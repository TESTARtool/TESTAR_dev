/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.yolo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.monkey.Assert;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.exceptions.SystemStopException;

public class YoloFramework extends SUTBase {

	public static YoloFramework yoloSUT = null;

	public static YoloFramework fromExecutable(String path) throws SystemStartException {
		if (yoloSUT != null) {
			yoloSUT = null;
		}

		return new YoloFramework(path);
	}

	public YoloFramework(String path) throws SystemStartException {
		Assert.notNull(path);

		try {

			ProcessBuilder processBuilder = new ProcessBuilder(path);

			if(path.contains(".jar")) {
				processBuilder = new ProcessBuilder("java", "-jar", path);
			}

			Process process = processBuilder.start();

			// Get the PID of the process (Java 9 or above)
			long pid = getProcessId(process);

			System.out.println("Yolo SUT started with PID: " + pid);
			this.set(Tags.PID, pid);

			yoloSUT = this;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use ProcessHandle API to obtain process PID. 
	 * Available starting from Java 9. 
	 * 
	 * @param process
	 * @return
	 */
	private long getProcessId(Process process) {
		ProcessHandle handle = process.toHandle();
		return handle.pid();
	}

	public static List<SUT> fromAll() {
		if (yoloSUT == null) {
			return new ArrayList<>();
		}

		return Collections.singletonList(yoloSUT);
	}

	@Override
	public void stop() throws SystemStopException {
	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public String getStatus() {
		return "Yolo SUT status : ";
	}

	@Override
	public AutomationCache getNativeAutomationCache() {
		return null;
	}

	@Override
	public void setNativeAutomationCache() {
	}

}
