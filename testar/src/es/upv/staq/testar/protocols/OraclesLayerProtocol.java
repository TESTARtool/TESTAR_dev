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

package es.upv.staq.testar.protocols;

import java.util.ArrayList;
import java.util.List;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.monkey.DefaultProtocol;

import es.upv.staq.testar.FlashFeedback;
import es.upv.staq.testar.oracles.Oracle;
import es.upv.staq.testar.oracles.OracleContext;

/**
 * Testing protocol enhancements to ease oracles specification.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */

public class OraclesLayerProtocol extends DefaultProtocol {

    private boolean ctrlPressed = false;

    private double mouseX = Double.MIN_VALUE, mouseY = Double.MIN_VALUE;
    private double[] filterArea = new double[]{Double.MAX_VALUE,Double.MAX_VALUE,Double.MIN_VALUE,Double.MIN_VALUE}; // <x1,y1,x2,y2>
            
    private OracleContext oracleContext = null;
    
    private List<Oracle> oracles;
    
    private Modes restoreMode = null;
    
    private boolean changeSequence = false;
    
    /**
     * Constructor.
     */
	public OraclesLayerProtocol(){
		super();
		oracles = new ArrayList<Oracle>();
	}
	
    @Override
    public void keyDown(KBKeys key) {    	
    	if (key == KBKeys.VK_CONTROL){
    		ctrlPressed = true;
    		filterArea[0] = mouseX;
    		filterArea[1] = mouseY;
    		if (oracleContext == null)
    			super.keyDown(key);
    	} else if (key == KBKeys.VK_S && ctrlPressed){ // Specify oracle
    		if (oracleContext == null){
	    		if (mode() != Modes.GenerateManual){
	    			restoreMode = mode();
	    			setMode(Modes.GenerateManual);
	    		}
	    		oracleContext = new OracleContext(this.state);
	    		FlashFeedback.flash("ORACLE SPECIFICATION STARTED ...");
    		}
    	} else if (key == KBKeys.VK_O && ctrlPressed){ // oracle verification point
    		Widget w = ProtocolUtil.getWidgetUnderCursor(this.state, mouse);
    		oracleContext.setVerificationPoint(this.state,w);
    		FlashFeedback.flash("VERIFICATION POINT MARKED"); // = " + w.get(Tags.AbstractID));
    	} else if (key == KBKeys.VK_E && ctrlPressed){ // finish oracle specification
    		if (oracleContext != null){
	    		List<Oracle> newOracles = oracleContext.infer(); 
	    		oracles.addAll(newOracles);
	    		oracleContext = null;
	    		changeSequence = true;
	    		if (restoreMode != null){
	    			setMode(restoreMode);
	    			restoreMode = null;
	    		}
	    		FlashFeedback.flash("NEW ORACLES = " + newOracles.size());
    		}
    	} else
    		super.keyDown(key);
    }

    @Override
    public void keyUp(KBKeys key) {
    	super.keyUp(key);
    	if (key == KBKeys.VK_CONTROL){
    		filterArea[2] = mouseX;
    		filterArea[3] = mouseY;
    		ctrlPressed = false;
    	}
    }
    	
	@Override
	public void mouseMoved(double x, double y) {
		super.mouseMoved(x, y);
		mouseX = x;
		mouseY = y;
	}
	
	@Override
	protected void beginSequence(){
		super.beginSequence();
		changeSequence = false;
	}
	
	@Override
	protected boolean moreActions(State state){
		boolean ret = super.moreActions(state);
		if (changeSequence)
			return false;
		else
			return ret;
	}
	
	@Override
	protected State getState(SUT system){
		State state = super.getState(system);
		if (oracleContext != null)
			oracleContext.notifyState(state);
		return state;
	}
	
	@Override
	protected void actionExecuted(SUT system, State state, Action action){
		super.actionExecuted(system, state, action);
		if (oracleContext != null)
			oracleContext.notifyAction(state, action);
	}
	
	@Override
	protected Verdict getVerdict(State state){
		Verdict verdict = super.getVerdict(state);
		if (!changeSequence){
			for (Oracle o : oracles)
				verdict = verdict.join(o.getVerdict(this.jipWrapper));
		}
		return verdict;
	}
        
}