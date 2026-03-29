/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.util.Util;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ConcreteStateFactory {

    protected static final Logger logger = LogManager.getLogger();

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
        String srcPath = newState.get(Tags.ScreenshotPath, null);
        if (srcPath != null && !srcPath.isEmpty()) {
            Path normalizePath = Paths.get(srcPath).normalize();

            // wait for state screenshot to be saved (max ~2s)
            for (int i = 0; i < 20 && !Files.isRegularFile(normalizePath); i++) {
                Util.pause(0.1);
            }

            try {
                if (Files.isRegularFile(normalizePath)) {
                    byte[] bytes = Files.readAllBytes(normalizePath);
                    concreteState.setScreenshot(bytes);
                } else {
                    logger.log(Level.WARN, "Screenshot file not found: " + normalizePath.toAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return concreteState;
    }

    /**
     * Helper method to transfer attribute information from the testar enitities to our own entities.
     * @param modelWidget
     * @param testarWidget
     */
    private static void setAttributes(ModelWidget modelWidget, Widget testarWidget) {
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
