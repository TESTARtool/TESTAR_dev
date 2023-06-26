/**
 * Copyright (c) 2021 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 - 2023 Universitat Politecnica de Valencia - www.upv.es
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

import org.apache.commons.io.FileUtils;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdFillFormAction;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.OutputStructure;
import org.testar.protocols.WebdriverProtocol;

import java.io.File;
import java.util.*;

/**
 * This protocol is used to test TESTAR by executing a gradle CI workflow.
 * 
 * ".github/workflows/gradle.yml"
 */
public class Protocol_test_gradle_workflow_webdriver_form_filling extends WebdriverProtocol {

	@Override
	protected Set<Action> deriveActions(SUT system, State state)
			throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all widgets but only derive the form filling action
		for (Widget widget : state) {
			// fill forms actions
			if (isAtBrowserCanvas(widget) && isForm(widget)) {
				String protocol = settings.get(ConfigTags.ProtocolClass, "");
				Action formFillingAction = new WdFillFormAction(ac, widget, protocol.substring(0, protocol.lastIndexOf('/')));
				if(formFillingAction instanceof NOP){
					// do nothing with NOP actions - the form was not actionable
				}else{
					actions.add(formFillingAction);
				}
			}
		}

		return actions;
	}

	/**
	 * Here you can put graceful shutdown sequence for your SUT
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		// Get the state before stop the SUT and verify we logged correctly using form filling
		State state = getState(system);
		boolean loggedUser = false;
		for(Widget widget : state) {
			if(widget.get(WdTags.WebTextContent, "").trim().equals("Welcome")) {
				loggedUser = true;
			}
		}

		Assert.isTrue(loggedUser, String.format("Trying to login in parabank app using form filling"));

		super.stopSystem(system);
	}

	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		try {
			File originalFolder = new File(OutputStructure.outerLoopOutputDir).getCanonicalFile();
			File artifactFolder = new File(Main.testarDir + settings.get(ConfigTags.ApplicationName,""));
			FileUtils.copyDirectory(originalFolder, artifactFolder);
		} catch(Exception e) {System.out.println("ERROR: Creating Artifact Folder");}
	}

}
