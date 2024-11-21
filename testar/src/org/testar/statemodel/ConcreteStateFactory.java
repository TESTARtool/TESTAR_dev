/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.ProtocolUtil;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.WdProtocolUtil;
import org.testar.monkey.alayer.android.AndroidProtocolUtil;
import org.testar.monkey.alayer.ios.IOSProtocolUtil;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

public abstract class ConcreteStateFactory {

    /**
     * This builder method will create a new concrete state class and populate it with the needed data
     * @param newState the testar State to serve as a base
     * @param tags the tags containing the atributes that were used in the construction of the concrete state id
     * @return the new concrete state
     */
    public static ConcreteState createConcreteState(State newState, Set<Tag<?>> tags, AbstractState abstractState, boolean storeWidgets) {
        String concreteStateId = newState.get(Tags.ConcreteID);
        ConcreteState concreteState = new ConcreteState(concreteStateId, tags, abstractState);

        // next we want to add all the attributes contained in the state, and then do the same thing for the child widgets
        setAttributes(concreteState, newState);
        if (storeWidgets) {
            copyWidgetTreeStructure(newState, concreteState, concreteState);
        }

        // get a screenshot for this concrete state
        ByteArrayOutputStream screenshotBytes = new ByteArrayOutputStream();

        AWTCanvas screenshot;
        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
            screenshot = WdProtocolUtil.getStateshotBinary(newState);
        } else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
            screenshot = AndroidProtocolUtil.getStateshotBinary(newState, null);
        } else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS)) {
            screenshot = IOSProtocolUtil.getStateshotBinary(newState, null);
        }
        else screenshot = ProtocolUtil.getStateshotBinary(newState);

        try {
            screenshot.saveAsPng(screenshotBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        concreteState.setScreenshot(screenshotBytes.toByteArray());

        return concreteState;
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
     * This method recursively populates the widget tree in our own models.
     * @param testarWidget
     * @param stateModelWidget
     * @param rootWidget
     */
    private static void copyWidgetTreeStructure(org.testar.monkey.alayer.Widget testarWidget, Widget stateModelWidget, ConcreteState rootWidget) {
        // we loop through the testar widget's children to copy their attributes into new widgets
        for (int i = 0; i < testarWidget.childCount(); i++) {
            org.testar.monkey.alayer.Widget testarChildWidget = testarWidget.child(i);
            String widgetId = testarChildWidget.get(Tags.ConcreteID);
            Widget newStateModelWidget = new Widget(widgetId);
            newStateModelWidget.setRootWidget(rootWidget);
            // copy the attribute info
            setAttributes(newStateModelWidget, testarChildWidget);
            // then add the new model widget to the tree
            stateModelWidget.addChild(newStateModelWidget);
            // recursively deal with the entire widget tree
            copyWidgetTreeStructure(testarChildWidget, newStateModelWidget, rootWidget);
        }
    }

}
