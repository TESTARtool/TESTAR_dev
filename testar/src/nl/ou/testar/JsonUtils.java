package nl.ou.testar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Set<WidgetJsonObject> widgetJsonObjects = new HashSet<WidgetJsonObject>();
        for(Widget widget:state){
            boolean enabled = widget.get(Tags.Enabled, null);
            String role = widget.get(Tags.Role, null).toString();
            boolean blocked = widget.get(Tags.Blocked, null);
            Rect rect = (Rect) widget.get(Tags.Shape, null);
            Set<Vertice> vertices = new HashSet<Vertice>();
            vertices.add(new Vertice(rect.x(), rect.y()));
            vertices.add(new Vertice(rect.x()+rect.width(), rect.y()));
            vertices.add(new Vertice(rect.x(), rect.y()+rect.height()));
            vertices.add(new Vertice(rect.x()+rect.width(), rect.y()+rect.height()));
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
        System.out.println("Widget size="+widgetJsonObjects.size());
        String screenshotPath = state.get(Tags.ScreenshotPath);
        System.out.println("ScreenshotPath="+screenshotPath);
        ScreenshotWidgetJsonObject screenshotWidgetJsonObject = new ScreenshotWidgetJsonObject(widgetJsonObjects, screenshotPath);
        System.out.println("JSON:"+ gson.toJson(screenshotWidgetJsonObject));
        String filePath = screenshotPath.substring(0, screenshotPath.lastIndexOf("."))+".json";
        System.out.println("FilePath="+filePath);
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
