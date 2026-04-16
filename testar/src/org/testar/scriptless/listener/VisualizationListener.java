/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.listener;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Set;

import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.action.Action;
import org.testar.core.devices.IEventListener;
import org.testar.core.devices.KBKeys;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.util.VisualizationUtil;
import org.testar.dialog.tagsvisualization.TagFilter;
import org.testar.engine.manager.FilteringManager;
import org.testar.scriptless.RuntimeContext;

/**
 * Visualization and spy-filter interaction layer for scriptless protocols.
 */
public final class VisualizationListener implements IEventListener {

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

    private final FilteringManager filteringManager;
    private final RuntimeContext runtimeContext;

    public VisualizationListener(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
		filteringManager = new FilteringManager();
		filteringManager.loadFilters();

		// If the environment is not headless, initialize the CAPS LOCK display mouse
		if (!GraphicsEnvironment.isHeadless()) {
			displayWhiteTabu = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		}
    }

    public final void visualizeState(State state) {
        VisualizationUtil.visualizeState(
            runtimeContext.isVisualizationEnabled(),
            runtimeContext.mouse(),
            runtimeContext.canvas(),
            state,
            this::shouldVisualizeTag
        );
    }

    public final void visualizeActions(State state, Set<Action> actions) {
        VisualizationUtil.visualizeActions(
            runtimeContext.canvas(), 
            state, 
            actions
        );
        if (displayWhiteTabu && runtimeContext.mode() == TestarMode.Spy) {
            filteringManager.visualizeActions(runtimeContext.canvas(), state);
        }
    }

    public final void visualizeSelectedAction(State state, Action action) {
        VisualizationUtil.visualizeSelectedAction(
            runtimeContext.canvas(), 
            runtimeContext.settings().get(ConfigTags.ActionDuration, 0.0),
            state, 
            action
        );
    }

    private boolean shouldVisualizeTag(Tag<?> tag) {
        return TagFilter.getInstance() == null || TagFilter.getInstance().visualizeTag(tag);
    }

    @Override
    public void keyDown(KBKeys key) {   
        if (runtimeContext.mode() == TestarMode.Spy){ 
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
        if (runtimeContext.mode() == TestarMode.Spy){
        	if (key == KBKeys.VK_SHIFT) {
	    		shiftPressed = false;
        	} else if (key == KBKeys.VK_CONTROL && displayWhiteTabu){
	    		filterArea[2] = mouseX;
	    		filterArea[3] = mouseY;
	    		whiteTabuMode = shiftPressed;
                filteringManager.manageWhiteTabuLists(
                    runtimeContext.latestState(),
                    runtimeContext.mouse(),
                    filterArea,
                    whiteTabuMode,
                    preciseCoding
                );
	    	}
        }
    }

    @Override
    public void mouseDown(org.testar.core.devices.MouseButtons btn, double x, double y) {
    }

    @Override
    public void mouseUp(org.testar.core.devices.MouseButtons btn, double x, double y) {
    }

	@Override
	public void mouseMoved(double x, double y) {
		mouseX = x;
		mouseY = y;
	}
}
