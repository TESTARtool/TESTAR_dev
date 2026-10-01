/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.action.Action;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;

public class ConcreteActionFactory {

    private static final Logger logger = LogManager.getLogger();
    private static final Duration SCREENSHOT_WAIT = Duration.ofSeconds(2);

    public static ConcreteAction createConcreteAction(Action action, AbstractAction abstractAction) {
        ConcreteAction concreteAction =  new ConcreteAction(action.get(Tags.ConcreteID), abstractAction);

        // check if a widget is attached to this action.
        // if so, copy all the attributes to the action
        if (action.get(Tags.OriginWidget, null) != null) {
            setAttributes(concreteAction, action.get(Tags.OriginWidget));
        }

        // check if the action as attached a Description (More info than a Widget)
        // if so, set this Description to the current ConcreteAction
        if (action.get(Tags.Desc, null) != null) {
            setSpecificAttribute(concreteAction, Tags.Desc, action.get(Tags.Desc));
        }

        String inputText = action.get(Tags.InputText, null);
        if (inputText != null) {
            setSpecificAttribute(concreteAction, Tags.InputText, inputText);
        }

        String screenshotPath = action.get(Tags.ActionScreenshotPath, null);
        if (screenshotPath != null && !screenshotPath.isBlank()) {
            try {
                loadScreenshot(concreteAction, Path.of(screenshotPath).normalize());
            } catch (InvalidPathException exception) {
                logger.warn("Invalid action screenshot path: {}", screenshotPath, exception);
            }
        }

        return concreteAction;
    }

    private static void loadScreenshot(ConcreteAction concreteAction, Path path) {
        long deadline = System.nanoTime() + SCREENSHOT_WAIT.toNanos();
        while (!Files.isRegularFile(path) && System.nanoTime() < deadline) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (!Files.isRegularFile(path)) {
            logger.warn("Action screenshot file not found: {}", path.toAbsolutePath());
            return;
        }
        try {
            concreteAction.setScreenshot(Files.readAllBytes(path));
        } catch (IOException exception) {
            logger.warn("Unable to read action screenshot: {}", path.toAbsolutePath(), exception);
        }
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
