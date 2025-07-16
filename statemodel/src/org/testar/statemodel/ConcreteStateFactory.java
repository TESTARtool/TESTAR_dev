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

import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class ConcreteStateFactory {

    /**
     * This builder method will create a new concrete state class and populate it with the needed data
     * @param newState the testar State to serve as a base
     * @return the new concrete state
     */
    public static ConcreteState createConcreteState(State newState, AbstractState abstractState, boolean storeWidgets) {
        String concreteStateId = newState.get(Tags.ConcreteID);
        ConcreteState concreteState = new ConcreteState(concreteStateId, abstractState);

        // next we want to add all the attributes contained in the state, and then do the same thing for the child widgets
        setAttributes(concreteState, newState);
        if (storeWidgets) {
            copyWidgetTreeStructure(newState, concreteState, concreteState);
        }

        // get a screenshot for this concrete state
        // in a headless environment, there may not be screenshots
        if(newState.get(Tags.ScreenshotImage, null) != null) {
        	ByteArrayOutputStream screenshotBytes = new ByteArrayOutputStream();
        	AWTCanvas screenshot = newState.get(Tags.ScreenshotImage);
        	try {
        		screenshot.saveAsPng(screenshotBytes);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	concreteState.setScreenshot(screenshotBytes.toByteArray());
        }

        return concreteState;
    }

    /**
     * Helper method to transfer attribute information from the testar enitities to our own entities.
     * @param modelWidget
     * @param testarWidget
     */
    private static void setAttributes(ModelWidget modelWidget, org.testar.monkey.alayer.Widget testarWidget) {
        for (Tag<?> t : testarWidget.tags()) {
        	modelWidget.addAttribute(t, testarWidget.get(t, null));
        }
    }

    /**
     * This method recursively populates the widget tree in our own models.
     * @param testarWidget
     * @param stateModelWidget
     * @param rootWidget
     */
    private static void copyWidgetTreeStructure(Widget testarWidget, ModelWidget modelWidget, ConcreteState rootWidget) {
        // we loop through the testar widget's children to copy their attributes into new widgets
        for (int i = 0; i < testarWidget.childCount(); i++) {
            Widget testarChildWidget = testarWidget.child(i);
            String widgetId = testarChildWidget.get(Tags.ConcreteID);
            ModelWidget newStateModelWidget = new ModelWidget(widgetId);
            newStateModelWidget.setRootWidget(rootWidget);
            // copy the attribute info
            setAttributes(newStateModelWidget, testarChildWidget);
            // then add the new model widget to the tree
            modelWidget.addChild(newStateModelWidget);
            // recursively deal with the entire widget tree
            copyWidgetTreeStructure(testarChildWidget, newStateModelWidget, rootWidget);
        }
    }

}
