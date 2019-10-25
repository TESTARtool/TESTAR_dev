/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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

package nl.ou.testar.ScreenshotJsonFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.ou.testar.ScreenshotJsonFile.BoundingPoly;
import nl.ou.testar.ScreenshotJsonFile.ScreenshotWidgetJsonObject;
import nl.ou.testar.ScreenshotJsonFile.Vertice;
import nl.ou.testar.ScreenshotJsonFile.WidgetJsonObject;
import org.fruit.alayer.Rect;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.windows.UIATags;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class JsonUtils {

    public static void createWidgetInfoJsonFile(State state){
    	
    	Rect sutRect;
    	try {
    		sutRect = (Rect) state.child(0).get(Tags.Shape, null);
    	}catch(Exception e){
    		System.out.println("ERROR: Reading State bounds for JSON file");
    		return;
    	}
//        System.out.println("DEBUG: SUT rect x="+sutRect.x()+", y="+sutRect.y()+", width="+sutRect.width()+", height="+sutRect.height());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
//			for(Tag tag:widget.tags()){
//				System.out.println("Tag:"+tag.toString()+"="+widget.get(tag));
//			}
        }
//        System.out.println("Widget size="+widgetJsonObjects.size());
        String screenshotPath = state.get(Tags.ScreenshotPath);
//        System.out.println("ScreenshotPath="+screenshotPath);
        ScreenshotWidgetJsonObject screenshotWidgetJsonObject = new ScreenshotWidgetJsonObject(widgetJsonObjects, screenshotPath);
//        System.out.println("JSON:"+ gson.toJson(screenshotWidgetJsonObject));
        String filePath = screenshotPath.substring(0, screenshotPath.lastIndexOf("."))+".json";
//        System.out.println("FilePath="+filePath);
        try{
            FileWriter fileWriter = new FileWriter(filePath);
            gson.toJson(screenshotWidgetJsonObject, fileWriter);
            fileWriter.flush(); //flush data to file   <---
            fileWriter.close(); //close write          <---
        }catch(Exception e){
            System.out.println("ERROR: Writing JSON into file failed!");
        }
    }
}
