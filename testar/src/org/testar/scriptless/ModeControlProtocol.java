/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.devices.IEventListener;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.Mouse;
import org.testar.core.devices.MouseButtons;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.engine.devices.EventHandler;

import java.util.EnumSet;
import java.util.Set;

public abstract class ModeControlProtocol extends AbstractProtocol implements IEventListener {

	protected TestarMode mode;
	protected synchronized void setMode(TestarMode mode){
		if (mode() == mode) return;
		else this.mode = mode;
	}
	public synchronized TestarMode mode(){
		return mode;
	}

    protected Mouse mouse;
    public final Mouse mouse() {
        return mouse;
    }

	private Set<KBKeys> pressed = EnumSet.noneOf(KBKeys.class);

	public EventHandler initializeEventHandler() {
		return new EventHandler(this);
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
		if (settings.get(ConfigTags.KeyBoardListener)) {
			pressed.add(key);

			// SHIFT + ARROW-DOWN --> stop TESTAR run
			if(key == KBKeys.VK_DOWN && pressed.contains(KBKeys.VK_SHIFT)){
				LogSerialiser.log("User requested to stop monkey!\n", LogSerialiser.LogLevel.Info);
				mode = TestarMode.Quit;
			}

			// SHIFT + 0 --> print in the PID, Windows Handle, and process name of the running applications
			else if (key == KBKeys.VK_0  && pressed.contains(KBKeys.VK_SHIFT)) {
				System.setProperty("DEBUG_WINDOWS_PROCESS_NAMES","true");
			}
		}
	}

	@Override
	public void keyUp(KBKeys key){
		if (settings.get(ConfigTags.KeyBoardListener)) {
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
