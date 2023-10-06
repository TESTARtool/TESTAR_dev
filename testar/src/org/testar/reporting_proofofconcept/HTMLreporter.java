package org.testar.reporting_proofofconcept;

import org.openqa.selenium.remote.tracing.opentelemetry.SeleniumSpanExporter;
import org.testar.monkey.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class HTMLreporter extends BaseReporter
{
    public HTMLreporter(String filePath)
    {
        super(new File(filePath.endsWith(".html") ? filePath : filePath + ".html"));
    }
    
    public void addContent(String text)
    {
        if(text.contains("\n"))
            Collections.addAll(content, splitStringAtNewline(text));
        else
            content.add(text);
    }
    
    public void addHeader()
    {
        addContent("<!DOCTYPE html>");
        addContent("<html>");
    }
    
    public void addTitle(String text, boolean inclHeadTags)
    {
        Assert.notNull(text);
        if(inclHeadTags)
            addContent("<head>");
        addContent("<title>");
        addContent(text); //Accessibility Evaluation Report
        addContent("</title>");
        if(inclHeadTags)
            addContent("</head>");
    }
    
    public void addScript(String text)
    {
        Assert.notNull(text);
        addContent("<script>");
        addContent(text);
        addContent("</script>");
    }
    
    public void addStartBody()
    {
        addContent("<body>");
    }
    
    public void addFooter()
    {
        addContent("</body>");
        addContent("</html>");
    }
    
    public void addHeading(int level, String text)
    {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
        Assert.notNull(text);
        addContent("<h" + level + ">" + text + "</h" + level + ">");
    }
    
    public void addParagraph(String text) {
        Assert.notNull(text);
        addContent("<p>");
        addContent(text);
        addContent("</p>");
    }
    
    public void addLineBreak()
    {
        addContent("<br>");
    }
    
    public void addList(boolean ordered, ArrayList<String> items)
    {
        if(ordered)
            content.add("<ol>");
        else //unordered
            content.add("<ul>");
    
        for(String item : items)
            content.add("<li>" + item + "</li>");
        
        if(ordered)
            content.add("</ol>");
        else //unordered
            content.add("</ul>");
    }
}
