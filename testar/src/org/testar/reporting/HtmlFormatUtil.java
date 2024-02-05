/***************************************************************************************************
 *
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.reporting;

import org.testar.monkey.Assert;

import java.util.Collections;
import java.util.List;

public class HtmlFormatUtil extends BaseFormatUtil
{
    public HtmlFormatUtil(String filePath)
    {
        super(filePath, "html");
    }
    
    public void addContent(String text)
    {
        if(text.contains("\n"))     Collections.addAll(content, splitStringAtNewline(text));
        else                        content.add(text);
    }
    
    public void addContent(String text, String tag)
    {
        if(text.contains("\n"))
        {
            content.add("<" + tag + ">");
            Collections.addAll(content, splitStringAtNewline(text));
            content.add("</" + tag + ">");
        }
        else
            content.add("<"+tag+">"+text+"</"+tag+">");
    }
    
    public void addContent(String text, String id, String tag)
    {
        if(text.contains("\n"))
        {
            content.add("<" + tag + " id='" + id + "' >");
            Collections.addAll(content, splitStringAtNewline(text));
            content.add("</" + tag + ">");
        }
        else
            content.add("<"+tag+" id='"+id+"' >"+text+"</"+tag+">");
    }
    
    public void addHeader(String title)
    {
        addHeader(title, "");
    }
    
    public void addHeader(String title, String script)
    {
        Assert.notNull(script);
        Assert.notNull(title);
    
        content.add("<!DOCTYPE html>");
        content.add("<html lang=\"en\">");
        content.add("<head>");
        if(!script.equals(""))
            addContent(script, "script");
        content.add("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        addContent(title, "title");
        content.add("</head>");
        content.add("<body>");
    }
    
    public void addFooter()
    {
        content.add("</body>");
        content.add("</html>");
    }
    
    public void addHeading(int level, String text)
    {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
        Assert.notNull(text);
        addContent(text, "h" + level);
    }
    
    public void addHeading(int level, String id, String text)
    {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
        Assert.notNull(text);
        addContent(text, id, "h" + level);
    }
    
    public void addParagraph(String text) {
        Assert.notNull(text);
        addContent(text, "p");
    }
    
    public void addLineBreak()
    {
        content.add("<br>");
    }
    
    public void addList(boolean ordered, List<String> items)
    {
        if(ordered)
            content.add("<ol>");
        else //unordered
            content.add("<ul>");
    
        for(String item : items)
            addContent(item,"li");
        
        if(ordered)
            content.add("</ol>");
        else //unordered
            content.add("</ul>");
    }
}
