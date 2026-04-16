/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.listener;

import org.testar.config.TestarMode;
import org.testar.core.devices.IEventListener;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.MouseButtons;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.scriptless.RuntimeContext;

import java.util.EnumSet;
import java.util.Set;

public final class ModeListener implements IEventListener {

	private final Set<KBKeys> pressed = EnumSet.noneOf(KBKeys.class);

	private final RuntimeContext runtimeContext;
	private final boolean keyBoardListener;

	public ModeListener(RuntimeContext runtimeContext, boolean keyBoardListener) {
		this.runtimeContext = runtimeContext;
		this.keyBoardListener = keyBoardListener;
	}

	//TODO: key commands come through java.awt.event but are the key codes same for all OS? if they are the same, then move to platform independent protocol?
	//TODO: Investigate better shortcut combinations to control TESTAR that does not interfere with SUT
	// (e.g. SHIFT + 1 puts an ! in the notepad and hence interferes with SUT state).
	/**
	 * Override the default keylistener to implement the TESTAR shortcuts
	 * SHIFT + SPACE
	 * SHIFT + ARROW-UP
	 * SHIFT + ARROW-RIGHT
	 * SHIFT + ARROW-LEFT
	 * SHIFT + ARROW-DOWN
	 * SHIFT + {0, 1, 2, 3, 4}
	 * SHIFT + ALT
	 * @param key
	 */
	@Override
	public void keyDown(KBKeys key){
		if (keyBoardListener) {
			pressed.add(key);

			// SHIFT + ARROW-DOWN --> stop TESTAR run
			if(key == KBKeys.VK_DOWN && pressed.contains(KBKeys.VK_SHIFT)){
				LogSerialiser.log("User requested to stop monkey!\n", LogSerialiser.LogLevel.Info);
				runtimeContext.setMode(TestarMode.Quit);
			}

			// SHIFT + 0 --> print in the PID, Windows Handle, and process name of the running applications
			else if (key == KBKeys.VK_0  && pressed.contains(KBKeys.VK_SHIFT)) {
				System.setProperty("DEBUG_WINDOWS_PROCESS_NAMES","true");
			}
		}
	}

	@Override
	public void keyUp(KBKeys key){
		if (keyBoardListener) {
			pressed.remove(key);
		}
	}

	/**
	 * TESTAR does not listen to mouse down clicks in any mode
	 * @param btn
	 * @param x
	 * @param y
	 */
	@Override
	public void mouseDown(MouseButtons btn, double x, double y){}

	@Override
	public void mouseUp(MouseButtons btn, double x, double y){}

	@Override
	public void mouseMoved(double x, double y) {}
}
