
/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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


import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.testar.protocols.DesktopProtocol;

import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.util.Set;

/**
 * This protocol provides a simple sequence of actions that TESTAR can apply to the Typora application.
 */
public class Protocol_simple_sequence_Typora extends DesktopProtocol {
	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){
        // Set the text size to H1
        Widget widget = getWidgetWithMatchingTag(Tags.Title, "Paragraph", state);
        StdActionCompiler ac = new AnnotatingActionCompiler();
        executeAction(system, state, ac.leftClickAt(widget));
        for (int i = 0; i < widget.childCount(); i++) {
            System.out.println("Found in dialog: " + widget.child(i).get(Tags.Title).toString());
            if (widget.child(i).get(Tags.Title).toString().contains("1")) {
                executeAction(system, state, ac.leftClickAt(widget.child(i)));
                break;
            }
        }
        
        // Type in the document and hit enter.
        waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tags.Role, "UIACustomControl", "TESTAR" + System.currentTimeMillis(), state, system, 5, 1.0);
        executeAction(system, state, ac.hitKey(KBKeys.VK_ENTER));
        
        // Saving the file.
        waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "File", state, system, 5, 1.0);
        waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "Save", state, system, 5, 1.0);
        
        executeAction(system, state, ac.hitKey(KBKeys.VK_ENTER));
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * It is being modified to support the Save actions for within the Save dialog.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to the foreground. 
        Set<Action> actions = super.deriveActions(system,state);
        StdActionCompiler ac = new AnnotatingActionCompiler();
        
        // control over the Save dialog to return the editable document eventually.
        for (Widget w : getTopWidgets(state)) {
        	if (w.get(Enabled, true) && !w.get(Blocked, false)) {
        		if (!blackListed(w)) {
        			if (w.get(Tags.Title).toString().contains("Save") && w.get(Tags.Role).toString().contains("UIAWindow")) {
        				actions.add(ac.leftClickAt(w));
                        w.set(Tags.ActionSet, actions);
        				for (int i = 0; i < w.childCount(); i++) {
        					if (w.child(i).get(Tags.Title).toString().contains("Save")) {
        						StdActionCompiler ac2 = new AnnotatingActionCompiler();
                                actions.add(ac2.leftClickAt(w.child(i)));
                                w.child(i).set(Tags.ActionSet, actions);
                            }
        				}
        			}
        		}
        	}
        }
        
        return actions;
	}
}
