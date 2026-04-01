/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2016-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.devices;

import org.testar.core.devices.IEventListener;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.MouseButtons;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

/**
 * An event handler for the testing protocol layers.
 */

public class EventHandler implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {

	private IEventListener eventListener;

	public EventHandler(IEventListener eventListener){
		this.eventListener = eventListener;
	}

	// ##############################################
	//  AbstractProtocol layer handlers (refactored)
	// ##############################################

	@Override
	public final void nativeKeyPressed (NativeKeyEvent e) {
		for (KBKeys key : KBKeys.values()) {
			if (key.scanCode() == e.getKeyCode()) {
				eventListener.keyDown(key);
			}
		}
	}

	@Override
	public final void nativeKeyReleased (NativeKeyEvent e) {
		for (KBKeys key : KBKeys.values()) {
			if (key.scanCode() == e.getKeyCode()) {
				eventListener.keyUp(key);
			}
		}
	}

	@Override
	public final void nativeMouseClicked(NativeMouseEvent arg0) {}

	@Override
	public final void nativeMousePressed(NativeMouseEvent arg0) {
		if(arg0.getButton() == 1)
			eventListener.mouseDown(MouseButtons.BUTTON1, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 2)
			eventListener.mouseDown(MouseButtons.BUTTON3, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 3)
			eventListener.mouseDown(MouseButtons.BUTTON2, arg0.getX(), arg0.getY());
	}

	@Override
	public final void nativeMouseReleased(NativeMouseEvent arg0) {
		if(arg0.getButton() == 1)
			eventListener.mouseUp(MouseButtons.BUTTON1, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 2)
			eventListener.mouseUp(MouseButtons.BUTTON3, arg0.getX(), arg0.getY());
		else if(arg0.getButton() == 3)
			eventListener.mouseUp(MouseButtons.BUTTON2, arg0.getX(), arg0.getY());
	}

	// #########################################
	//  ClickFilterLayerProtocol event handlers
	// #########################################

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		eventListener.mouseMoved(e.getX(),e.getY());
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {}

	@Override
	public final void nativeKeyTyped(NativeKeyEvent e) {}

}
