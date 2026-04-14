/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Set;

import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.config.settings.Settings;
import org.testar.core.action.Action;
import org.testar.core.alayer.Canvas;
import org.testar.core.devices.KBKeys;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.util.VisualizationUtil;
import org.testar.dialog.tagsvisualization.TagFilter;
import org.testar.engine.manager.FilteringManager;

/**
 * Visualization and spy-filter interaction layer for scriptless protocols.
 */
public abstract class VisualizationProtocol extends ModeControlProtocol {

    private boolean preciseCoding = false; // false =>  CodingManager.ABSTRACT_R_T_ID; true => CodingManager.ABSTRACT_R_T_P_ID
    private boolean displayWhiteTabu = false;
    private boolean whiteTabuMode = false; // true => white, false = tabu
    private boolean shiftPressed = false;

    private double mouseX = Double.MIN_VALUE;
    private double mouseY = Double.MIN_VALUE;
    private double[] filterArea = new double[] { // <x1,y1,x2,y2>
        Double.MAX_VALUE, 
        Double.MAX_VALUE, 
        Double.MIN_VALUE, 
        Double.MIN_VALUE
    };

    protected boolean visualizationOn = false;
    public final boolean isVisualizationEnabled() {
        return visualizationOn;
    }

    private FilteringManager filteringManager;

    public VisualizationProtocol() {
		filteringManager = new FilteringManager();
		filteringManager.loadFilters();

		// If the environment is not headless, initialize the CAPS LOCK display mouse
		if (!GraphicsEnvironment.isHeadless()) {
			displayWhiteTabu = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		}
    }

    public final void visualizeState(Canvas canvas, State state) {
        VisualizationUtil.visualizeState(
            visualizationOn,
            mouse,
            canvas,
            state,
            this::shouldVisualizeTag
        );
    }

    public final void visualizeActions(Canvas canvas, State state, Set<Action> actions) {
        VisualizationUtil.visualizeActions(canvas, state, actions);
        if (displayWhiteTabu && mode == TestarMode.Spy) {
            filteringManager.visualizeActions(canvas, state);
        }
    }

    public final void visualizeSelectedAction(Canvas canvas, State state, Action action, Settings settings) {
        VisualizationUtil.visualizeSelectedAction(canvas, state, action, settings.get(ConfigTags.ActionDuration));
    }

    private boolean shouldVisualizeTag(Tag<?> tag) {
        return TagFilter.getInstance() == null || TagFilter.getInstance().visualizeTag(tag);
    }

    @Override
    public void keyDown(KBKeys key) {    	
        super.keyDown(key);        
        if (mode() == TestarMode.Spy){ 
        	if (key == KBKeys.VK_CAPS_LOCK || key == KBKeys.VK_ALT)
        		displayWhiteTabu = !displayWhiteTabu;
        	else if (key == KBKeys.VK_TAB)
        		preciseCoding = !preciseCoding;
        	else if (key == KBKeys.VK_SHIFT)
        		shiftPressed = true;
	    	else if (key == KBKeys.VK_CONTROL){
	    		filterArea[0] = mouseX;
	    		filterArea[1] = mouseY;
	    	}
        }
    }

    @Override
    public void keyUp(KBKeys key) {    	
    	super.keyUp(key);
        if (mode() == TestarMode.Spy){
        	if (key == KBKeys.VK_SHIFT) {
	    		shiftPressed = false;
        	} else if (key == KBKeys.VK_CONTROL && displayWhiteTabu){
	    		filterArea[2] = mouseX;
	    		filterArea[3] = mouseY;
	    		whiteTabuMode = shiftPressed;
                filteringManager.manageWhiteTabuLists(
                    runtimeContext.latesState(),
                    mouse,
                    filterArea,
                    whiteTabuMode,
                    preciseCoding
                );
	    	}
        }
    }

	@Override
	public void mouseMoved(double x, double y) {
		mouseX = x;
		mouseY = y;
	}
}
