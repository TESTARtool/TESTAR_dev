/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar;

import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.MouseButtons;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

/**
 * An event handler for the testing protocol layers.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
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
	public final void nativeKeyPressed(NativeKeyEvent e) {
		for(KBKeys key : KBKeys.values())
			if(key.code() == e.getKeyCode())
				eventListener.keyDown(key);
	}

	@Override
	public final void nativeKeyReleased(NativeKeyEvent e) {
		for(KBKeys key : KBKeys.values())
			if(key.code() == e.getKeyCode())
				eventListener.keyUp(key);
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