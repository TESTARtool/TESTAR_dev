/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;

public class ConcreteActionFactory {

    public static ConcreteAction createConcreteAction(Action action, AbstractAction abstractAction) {
        ConcreteAction concreteAction =  new ConcreteAction(action.get(Tags.ConcreteID), abstractAction);

        // check if a widget is attached to this action.
        // if so, copy all the attributes to the action
        if (action.get(Tags.OriginWidget, null) != null) {
            setAttributes(concreteAction, action.get(Tags.OriginWidget));
        }
        
        // check if the action as attached a Description (More info than a Widget)
        // if so, set this Description to the current ConcreteAction
        if(action.get(Tags.Desc, null) != null)
        	setSpecificAttribute(concreteAction, Tags.Desc, action.get(Tags.Desc));

        // check if the action as attached an InputText
        // if so, set this InputText to the current ConcreteAction
        if(action.get(Tags.InputText, null) != null)
        	setSpecificAttribute(concreteAction, Tags.InputText, action.get(Tags.InputText));

        return concreteAction;
    }

    /**
     * Helper method to transfer attribute information from the testar enitities to our own entities.
     * @param widget
     * @param testarWidget
     */
    private static void setAttributes(Widget widget, org.testar.monkey.alayer.Widget testarWidget) {
        for (Tag<?> t : testarWidget.tags()) {
            widget.addAttribute(t, testarWidget.get(t, null));
        }
    }
    
    /**
     * Helper method to set a specific attribute information from the testar enitities to our own entities.
     * @param widget
     * @param tagAttribute
     * @param value
     */
    private static void setSpecificAttribute(Widget widget, Tag tagAttribute, Object value) {
            widget.addAttribute(tagAttribute, value);
    }

}
