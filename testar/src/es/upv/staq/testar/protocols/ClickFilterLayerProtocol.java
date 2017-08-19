/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.protocols;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.DefaultProtocol;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.managers.FilteringManager;

/**
 * Testing protocol enhancements to ease tester work.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 * To be developed: actions ordering
 *
 */

public class ClickFilterLayerProtocol extends DefaultProtocol { // OraclesLayerProtocol {

    private boolean preciseCoding = false; // false =>  CodingManager.ABSTRACT_R_T_ID; true => CodingManager.ABSTRACT_R_T_P_ID
    private boolean displayWhiteTabu = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    private boolean whiteTabuMode = false; // true => white, false = tabu
    private boolean ctrlPressed = false, altPressed = false, shiftPressed = false;

    private double mouseX = Double.MIN_VALUE, mouseY = Double.MIN_VALUE;
    private double[] filterArea = new double[]{Double.MAX_VALUE,Double.MAX_VALUE,Double.MIN_VALUE,Double.MIN_VALUE}; // <x1,y1,x2,y2>
    
    private FilteringManager filteringManager;
    private DataManager dataManager;
    
    /**
     * Constructor.
     */
	public ClickFilterLayerProtocol(){
		super();
		filteringManager = new FilteringManager();
		dataManager = new DataManager();
		filteringManager.loadFilters();
		dataManager.loadInputValues();		
	}
	
    @Override
    public void keyDown(KBKeys key) {    	
        super.keyDown(key);        
        if (mode() == Modes.Spy){ 
        	if (key == KBKeys.VK_CAPS_LOCK)
        		displayWhiteTabu = !displayWhiteTabu;
        	else if (key == KBKeys.VK_TAB)
        		preciseCoding = !preciseCoding;
        	else if (key == KBKeys.VK_SHIFT)
        		shiftPressed = true;
	    	else if (key == KBKeys.VK_CONTROL){
	    		ctrlPressed = true;
	    		filterArea[0] = mouseX;
	    		filterArea[1] = mouseY;
	    	} else if (key == KBKeys.VK_ALT){
	    		altPressed = true;
	    		if (!ctrlPressed && !shiftPressed)
	    			filteringManager.setWidgetFilter(this.state,this.mouse,preciseCoding);
	    	}
        }
    }

    @Override
    public void keyUp(KBKeys key) {    	
    	super.keyUp(key);
        if (mode() == Modes.Spy){
        	if (key == KBKeys.VK_SHIFT)
	    		shiftPressed = false;
        	else if (key == KBKeys.VK_CONTROL && displayWhiteTabu){
	    		filterArea[2] = mouseX;
	    		filterArea[3] = mouseY;
	    		ctrlPressed = false; whiteTabuMode = shiftPressed;
	    		filteringManager.manageWhiteTabuLists(this.state,this.mouse,this.filterArea,this.whiteTabuMode,this.preciseCoding);
	    	} else if (key == KBKeys.VK_ALT)
	    		altPressed = false;
        }
    }
    	
	@Override
	public void mouseMoved(double x, double y) {
		mouseX = x;
		mouseY = y;
	}
	    
    @Override
	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		super.visualizeActions(canvas, state, actions);
    	if(displayWhiteTabu && (mode() == Modes.Spy))// || mode() == Modes.GenerateDebug)){ // && settings().get(ConfigTags.VisualizeActions)){
    		filteringManager.visualizeActions(canvas,state);
	}
    
    protected boolean blackListed(Widget w){
    	return filteringManager.blackListed(w);
    }

    protected boolean whiteListed(Widget w){
    	return filteringManager.whiteListed(w);
    }
    
    @Override
    protected String getRandomText(Widget w){
    	String randomText = filteringManager.getRandomText(w);
    	if (randomText == null || randomText.length() == 0)
    		return super.getRandomText(w);
    	else
    		return randomText;
    }
        
}