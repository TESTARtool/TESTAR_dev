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

import org.apache.commons.lang.StringUtils;
import org.testar.monkey.Assert;

import java.util.Collections;
import java.util.List;

public class PlainTextReportUtil extends BaseReportUtil
{

    protected PlainTextReportUtil(String filePath)
    {
        super(filePath, "txt");
    }
    
    public void addContent(String text)
    {
        
        if(text.contains("\n"))
            Collections.addAll(content, splitStringAtNewline(text));
        else
            content.add(text);
    }
    
    public void addHeading(int level, String text)
    {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid heading level: must be between 1 and 6");
        Assert.notNull(text);
        content.add(StringUtils.repeat("#", level) + text); //repeat # char
    }

    public void addParagraph(String text)
    {
        Assert.notNull(text);
        addEmptyLine();
        Collections.addAll(content, splitStringAtNewline(text));
        addEmptyLine();
    }

    public void addEmptyLine()
    {
        content.add("");
    }

    public void addHorizontalLine()
    {
        addHorizontalLine(20);
    }

    public void addHorizontalLine(int length)
    {
        Assert.isTrue(length >= 3 && length <= 100, "Invalid horizontal line length: must be between 3 and 100");
        content.add(StringUtils.repeat("=", length)); //repeat # char
    }
    
    public void addList(boolean ordered, List<String> items)
    {
        addEmptyLine();
        for(int i = 0; i < items.size(); i++)
        {
            if(ordered)
                addContent(String.valueOf(i+1) + items.get(i));
            else
                addContent("- " + items.get(i));
        }
        addEmptyLine();
    }
}
