/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2023 Open Universiteit - www.ou.nl
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

package org.testar.screenshotjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.windows.UIATags;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class JsonUtils {

    public static boolean createFullWidgetTreeJsonFile(State state, String fileNamePath){
    	// If the state has no children, do not continue
    	if(state.childCount() == 0) return false;

        Set<FullWidgetInfoJsonObject> widgetTreeObjects = new HashSet<FullWidgetInfoJsonObject>();
        for(Widget widget:state){
            Set<Pair<String, String>> widgetTags = new HashSet<Pair<String, String>>();
            for(Tag tag:widget.tags()){
                Pair<String, String> widgetTag = new Pair<String, String>(tag.name(), widget.get(tag).toString());
                widgetTags.add(widgetTag);
            }
            widgetTreeObjects.add(new FullWidgetInfoJsonObject(widgetTags));
        }
        return writeJsonToFile(widgetTreeObjects,fileNamePath);
    }

    public static boolean createWidgetInfoJsonFile(State state){
    	// If the state has no children, do not continue
    	if(state.childCount() == 0) return false;

    	Rect sutRect;
    	try {
    		sutRect = (Rect) state.child(0).get(Tags.Shape, null);
    	}catch(NullPointerException e){
    		System.out.println("ERROR: Reading State bounds for JSON file");
    		return false;
    	}

        Set<WidgetJsonObject> widgetJsonObjects = new HashSet<WidgetJsonObject>();
        for(Widget widget:state){
            boolean enabled = widget.get(Tags.Enabled, null);
            String role = widget.get(Tags.Role, null).toString();
            boolean blocked = widget.get(Tags.Blocked, null);
            Rect rect = (Rect) widget.get(Tags.Shape, null);
            Vertice[] vertices = new Vertice[4];
            vertices[0]=new Vertice(rect.x()-sutRect.x(), rect.y()-sutRect.y());
            vertices[1]=new Vertice(rect.x()-sutRect.x()+rect.width(), rect.y()-sutRect.y());
            vertices[2]=new Vertice(rect.x()-sutRect.x()+rect.width(), rect.y()-sutRect.y()+rect.height());
            vertices[3]=new Vertice(rect.x()-sutRect.x(), rect.y()-sutRect.y()+rect.height());
            BoundingPoly boundingPoly = new BoundingPoly(vertices);
            String className= widget.get(UIATags.UIAClassName, "");
            String title= widget.get(Tags.Title, "");
            String desc= widget.get(Tags.Desc, "");
            String name= widget.get(UIATags.UIAName, "");
            String toolTipText= widget.get(Tags.ToolTipText, "");
            String valuePattern= widget.get(Tags.ValuePattern, "");
            WidgetJsonObject widgetJsonObject = new WidgetJsonObject(enabled, role, blocked, boundingPoly, className, title, desc, name, toolTipText, valuePattern);
            widgetJsonObjects.add(widgetJsonObject);
        }

        String screenshotPath = state.get(Tags.ScreenshotPath);
        ScreenshotWidgetJsonObject screenshotWidgetJsonObject = new ScreenshotWidgetJsonObject(widgetJsonObjects, screenshotPath);
        String filePath = screenshotPath.substring(0, screenshotPath.lastIndexOf("."))+".json";

        return writeJsonToFile(screenshotWidgetJsonObject, filePath);
    }

    private static boolean writeJsonToFile(Object objectToWrite, String fileNamePath){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            FileWriter fileWriter = new FileWriter(fileNamePath);
            gson.toJson(objectToWrite, fileWriter);
            fileWriter.flush(); //flush data to file   <---
            fileWriter.close(); //close write          <---
        }catch(Exception e){
            System.out.println("ERROR: Writing JSON into file failed!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
