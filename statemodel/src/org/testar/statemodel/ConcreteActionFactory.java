/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2026 Universitat Politecnica de Valencia - www.upv.es
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConcreteActionFactory {

    protected static final Logger logger = LogManager.getLogger();

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

        // get a screenshot for this concrete action
        // in a headless environment, there may not be screenshots
        String srcPath = action.get(Tags.ActionScreenshotPath, null);
        if (srcPath != null && !srcPath.isEmpty()) {
            Path normalizePath = Paths.get(srcPath).normalize();

            // wait for action screenshot to be saved (max ~2s)
            for (int i = 0; i < 20 && !Files.isRegularFile(normalizePath); i++) {
                Util.pause(0.1);
            }

            try {
                if (Files.isRegularFile(normalizePath)) {
                    byte[] bytes = Files.readAllBytes(normalizePath);
                    concreteAction.setScreenshot(bytes);
                } else {
                    logger.log(Level.WARN,"Action screenshot file not found: {}", normalizePath.toAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return concreteAction;
    }

    /**
     * Helper method to transfer attribute information from the testar entities to the model entities.
     * @param modelWidget
     * @param testarWidget
     */
    private static void setAttributes(ModelWidget modelWidget, Widget testarWidget) {
        for (Tag<?> t : testarWidget.tags()) {
            modelWidget.addAttribute(t, testarWidget.get(t, null));
        }
    }

    /**
     * Helper method to set a specific attribute information from the testar entities to the model entities.
     * @param modelWidget
     * @param tagAttribute
     * @param value
     */
    private static void setSpecificAttribute(ModelWidget modelWidget, Tag tagAttribute, Object value) {
        modelWidget.addAttribute(tagAttribute, value);
    }

}
