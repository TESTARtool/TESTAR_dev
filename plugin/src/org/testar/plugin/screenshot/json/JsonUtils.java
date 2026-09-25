/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin.screenshot.json;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testar.core.Pair;
import org.testar.core.alayer.Rect;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.windows.tag.UIATags;

public class JsonUtils {

    public static boolean createFullWidgetTreeJsonFile(State state, String fileNamePath) {
        // If the state has no children, do not continue
        if (state.childCount() == 0) {
            return false;
        }

        Set<FullWidgetInfoJsonObject> widgetTreeObjects = new HashSet<>();
        for (Widget widget : state) {
            Set<Pair<String, String>> widgetTags = new HashSet<>();
            for (Tag<?> tag : widget.tags()) {
                Pair<String, String> widgetTag = new Pair<>(tag.name(), widget.get(tag).toString());
                widgetTags.add(widgetTag);
            }
            widgetTreeObjects.add(new FullWidgetInfoJsonObject(widgetTags));
        }
        return writeJsonToFile(widgetTreeObjects, fileNamePath);
    }

    public static boolean createWidgetInfoJsonFile(State state) {
        // If the state has no children, do not continue
        if (state.childCount() == 0) {
            return false;
        }

        Rect sutRect;
        try {
            sutRect = (Rect) state.child(0).get(Tags.Shape, null);
        } catch (NullPointerException exception) {
            System.out.println("ERROR: Reading State bounds for JSON file");
            return false;
        }

        Set<WidgetJsonObject> widgetJsonObjects = new HashSet<>();
        for (Widget widget : state) {
            boolean enabled = widget.get(Tags.Enabled, null);
            String role = widget.get(Tags.Role, null).toString();
            boolean blocked = widget.get(Tags.Blocked, null);
            Rect rect = (Rect) widget.get(Tags.Shape, null);
            Vertice[] vertices = new Vertice[4];
            vertices[0] = new Vertice(rect.x() - sutRect.x(), rect.y() - sutRect.y());
            vertices[1] = new Vertice(rect.x() - sutRect.x() + rect.width(), rect.y() - sutRect.y());
            vertices[2] = new Vertice(rect.x() - sutRect.x() + rect.width(), rect.y() - sutRect.y() + rect.height());
            vertices[3] = new Vertice(rect.x() - sutRect.x(), rect.y() - sutRect.y() + rect.height());
            BoundingPoly boundingPoly = new BoundingPoly(vertices);
            String className = widget.get(UIATags.UIAClassName, "");
            String title = widget.get(Tags.Title, "");
            String description = widget.get(Tags.Desc, "");
            String name = widget.get(UIATags.UIAName, "");
            String toolTipText = widget.get(Tags.ToolTipText, "");
            String valuePattern = widget.get(Tags.ValuePattern, "");
            WidgetJsonObject widgetJsonObject = new WidgetJsonObject(
                    enabled,
                    role,
                    blocked,
                    boundingPoly,
                    className,
                    title,
                    description,
                    name,
                    toolTipText,
                    valuePattern
            );
            widgetJsonObjects.add(widgetJsonObject);
        }

        String screenshotPath = state.get(Tags.ScreenshotPath);
        ScreenshotWidgetJsonObject screenshotWidgetJsonObject = new ScreenshotWidgetJsonObject(widgetJsonObjects, screenshotPath);
        String filePath = screenshotPath.substring(0, screenshotPath.lastIndexOf(".")) + ".json";

        return writeJsonToFile(screenshotWidgetJsonObject, filePath);
    }

    private static boolean writeJsonToFile(Object objectToWrite, String fileNamePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fileWriter = new FileWriter(fileNamePath)) {
            gson.toJson(objectToWrite, fileWriter);
            fileWriter.flush();
        } catch (Exception exception) {
            System.out.println("ERROR: Writing JSON into file failed!");
            exception.printStackTrace();
            return false;
        }
        return true;
    }
}
