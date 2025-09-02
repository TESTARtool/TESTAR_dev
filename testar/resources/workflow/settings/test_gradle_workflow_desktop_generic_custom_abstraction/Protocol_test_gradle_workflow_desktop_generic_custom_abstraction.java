/***************************************************************************************************
 *
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
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


import java.io.File;
import java.util.Set;
import java.util.zip.CRC32;

import org.apache.commons.io.FileUtils;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.OutputStructure;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.protocols.DesktopProtocol;

import org.testar.CodingManager;
import org.testar.DerivedActions;
import org.testar.RandomActionSelector;
import org.testar.SutVisualization;

/**
 * This protocol is used to test TESTAR by executing a gradle CI workflow.
 * 
 * ".github/workflows/gradle.yml"
 */
public class Protocol_test_gradle_workflow_desktop_generic_custom_abstraction extends DesktopProtocol {

    @Override
    protected void buildStateIdentifiers(State state) {
        CodingManager.buildIDs(state);

        // Reset widgets AbstractID identifier values to empty
        resetAbstractID(state);
        // Custom the State AbstractID identifier to ignore Format menu bar widgets
        customBuildAbstractID(state);

        System.out.println("*DEBUG* buildStateIdentifiers: " + state.get(Tags.AbstractID, "StateHasNoAbstractID"));
    }

    private synchronized void resetAbstractID(State state) {
        for(Widget w : state) {
            w.set(Tags.AbstractID, "");
        }
    }

    private synchronized void customBuildAbstractID(Widget widget){
        if (widget.parent() != null) {
            // Skip Format UIAMenu from the state abstraction
            if(widget.get(Tags.Role, Roles.Invalid).toString().equals("UIAMenu")) {
                if(widget.get(Tags.Title, "NoTitle").contains("Format")) {
                    return;
                }
            }
            // Skip widgets elements from Format UIAMenu from the state abstraction
            if(widget.get(Tags.Role, Roles.Invalid).toString().equals("UIAMenuItem")) {
                // Skip Word Wrap widget (English and Spanish)
                if(widget.get(Tags.Title, "NoTitle").contains("Word Wrap") || widget.get(Tags.Title, "NoTitle").contains("Ajuste de")) {
                    return;
                }
                // Skip Font widget (English and Spanish)
                if(widget.get(Tags.Title, "NoTitle").contains("Font") || widget.get(Tags.Title, "NoTitle").contains("Fuente")) {
                    return;
                }
            }
            widget.set(Tags.AbstractID, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + lowCollisionID(widget.get(Tags.Title, "NoTitle")));
        } else if (widget instanceof State) {
            StringBuilder abstractIdCustom;
            abstractIdCustom = new StringBuilder();
            for (Widget childWidget : (State) widget) {
                if (childWidget != widget) {
                    customBuildAbstractID(childWidget);
                    abstractIdCustom.append(childWidget.get(Tags.AbstractID));
                }
            }
            widget.set(Tags.AbstractID, CodingManager.ID_PREFIX_STATE + CodingManager.ID_PREFIX_ABSTRACT + lowCollisionID(abstractIdCustom.toString()));
        }
    }

    @Override
    protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
        CodingManager.buildIDs(state, actions);

        // Custom the Action AbstractID identifier
        for(Action a : actions) {
            // Action Custom Identifier based on OriginWidget Title (ignoring State)
            // This means that an action that opens/closes a menubar will be consider the same in both states
            String customIdentifier = CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT + lowCollisionID(a.get(Tags.OriginWidget).get(Tags.Title));
            a.set(Tags.AbstractID, customIdentifier);

            System.out.println("*DEBUG* Available action: " + a.get(Tags.Desc, "No description") + ", AbstractID: " + a.get(Tags.AbstractID, "NoAbstractID"));
        }
    }

    private String lowCollisionID(String text){ // reduce ID collision probability
        CRC32 crc32 = new CRC32();
        crc32.update(text.getBytes());
        return Integer.toUnsignedString(text.hashCode(), Character.MAX_RADIX) +
                Integer.toHexString(text.length()) +
                crc32.getValue();
    }

    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        Set<Action> actions = super.deriveActions(system,state);

        // Derive left-click actions, click and type actions, and scroll actions from
        // top level widgets of the GUI:
        DerivedActions derived = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

        if(derived.getAvailableActions().isEmpty()){
            // If the top level widgets did not have any executable widgets, try all widgets:
            // Derive left-click actions, click and type actions, and scroll actions from
            // all widgets of the GUI:
            derived = deriveClickTypeScrollActionsFromAllWidgets(actions, state);
        }

        Set<Action> filteredActions = derived.getFilteredActions();
        actions = derived.getAvailableActions();

        //Showing the grey dots for filtered actions if visualization is on:
        if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

        //return the set of derived actions
        return actions;
    }

    @Override
    protected Action selectAction(State state, Set<Action> actions){
    	//The default select action mechanism tries to use the state model action selector. 
    	//If the state model is not enabled, it returns a random action.
    	return super.selectAction(state, actions);
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
