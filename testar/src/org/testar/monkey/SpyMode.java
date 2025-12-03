/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey;

import java.util.Set;
import java.util.function.Function;

import org.testar.SutVisualization;
import org.testar.SystemProcessHandling;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.android.AndroidProtocolUtil;
import org.testar.monkey.alayer.android.spy_visualization.MobileVisualizationAndroid;
import org.testar.monkey.alayer.ios.IOSProtocolUtil;
import org.testar.monkey.alayer.ios.spy_visualization.MobileVisualizationIOS;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;

public class SpyMode {

	/**
	 * Run TESTAR on Spy Mode.
	 */
	public void runSpyLoop(DefaultProtocol protocol) {

		//Create or detect the SUT & build canvas representation
		SUT system = protocol.startSystem();
		protocol.cv = protocol.buildCanvas();

		//TODO: this must stay here as there is no canvas function called in the original default protocol
		MobileVisualizationAndroid mobileVisualizationAndroid = null;
		MobileVisualizationIOS mobileVisualizationIOS = null;

		if (isAndroid()) {
			mobileVisualizationAndroid = setupAndroidVisualization(protocol, system);
		} else if (isIOS()) {
			mobileVisualizationIOS = setupIOSVisualization(protocol, system);
		}

		while(protocol.mode() == Modes.Spy && system.isRunning()) {

			State state = protocol.getState(system);
			protocol.cv.begin();

			//TODO: can we work this into sutvisualization/ canvas?
			if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
				updateAndroidVisualization(protocol, mobileVisualizationAndroid, system, state);
			} else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS)) {
				updateIOSVisualization(protocol, mobileVisualizationIOS, system, state);
			} else {
				updateDefaultVisualization(protocol, system, state);
			}

			protocol.cv.end();

			int msRefresh = (int)(protocol.settings().get(ConfigTags.RefreshSpyCanvas, 0.5) * 1000);

			synchronized (this) {
				try {
					this.wait(msRefresh);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// TODO: can we work this into canvas thing?
			if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
				assert mobileVisualizationAndroid != null;
				if (mobileVisualizationAndroid.closedSpyVisualization) {
					break;
				}
			} else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS)) {
				assert mobileVisualizationIOS != null;
				if (mobileVisualizationIOS.closedSpyVisualization) {
					break;
				}
			}

		}

		//If user closes the SUT while in Spy-mode, TESTAR will close (or go back to SettingsDialog):
		if(!system.isRunning()){
			protocol.mode = Modes.Quit;
		}

		Util.clear(protocol.cv);
		protocol.cv.end();

		//finishSequence() content, but SPY mode is not a sequence
		if(!NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
			SystemProcessHandling.killTestLaunchedProcesses(protocol.contextRunningProcesses);
		}

		//Stop and close the SUT before return to the detectModeLoop
		protocol.stopSystem(system);
	}

	private boolean isAndroid() {
		return NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID);
	}

	private boolean isIOS() {
		return NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS);
	}

	private MobileVisualizationAndroid setupAndroidVisualization(DefaultProtocol protocol, SUT system) {
		System.out.println("SPY MODE, CREATING JAVA JFRAME WINDOW Android");
		State state = protocol.getState(system);
		Function<State, Set<Action>> deriveActionsFunction = s -> protocol.deriveActions(system, s);
		return new MobileVisualizationAndroid(AndroidProtocolUtil.getStateshotSpyMode(state), state, deriveActionsFunction);
	}

	private MobileVisualizationIOS setupIOSVisualization(DefaultProtocol protocol, SUT system) {
		System.out.println("SPY MODE, CREATING JAVA JFRAME WINDOW iOS");
		State state = protocol.getState(system);
		return new MobileVisualizationIOS(IOSProtocolUtil.getStateshotSpyMode(state), state);
	}

	private void updateAndroidVisualization(DefaultProtocol protocol, MobileVisualizationAndroid visualization, SUT system, State state) {
		Util.clear(protocol.cv);
		if (visualization == null) return;
		visualization.updateVisualization(state);
	}

	private void updateIOSVisualization(DefaultProtocol protocol, MobileVisualizationIOS visualization, SUT system, State state) {
		Util.clear(protocol.cv);
		if (visualization == null) return;
		visualization.updateStateVisualization(state);
	}

	private void updateDefaultVisualization(DefaultProtocol protocol, SUT system, State state) {
		Set<Action> actions = protocol.deriveActions(system, state);
		protocol.buildStateActionsIdentifiers(state, actions);

		// When we have the state and actions, clear and draw new visualizers
		Util.clear(protocol.cv);

		//in Spy-mode, always visualize the widget info under the mouse cursor:
		SutVisualization.visualizeState(
				protocol.visualizationOn,
				protocol.markParentWidget,
				protocol.mouse,
				protocol.lastPrintParentsOf,
				protocol.cv,
				state
				);

		//in Spy-mode, always visualize the green dots:
		protocol.visualizeActions(protocol.cv, state, actions);

		//in some OS mode like WebDriver, paint all visualizers
		protocol.cv.paintBatch();
	}
}
