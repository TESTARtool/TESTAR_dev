package org.testar.reporting_proofofconcept;

import org.testar.monkey.Assert;

import java.io.File;
import java.util.Collections;

public class PlainTextReportUtil extends BaseReportUtil
{
    
    protected PlainTextReportUtil(String filePath)
    {
        super(filePath, "txt");
    }
    
    public void addHeading(int level, String text)
    {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid heading level: must be between 1 and 6");
        Assert.notNull(text);
        content.add("#".repeat(level)); //repeat # char
        content.add(text);
    }
    
    public void addParagraph(String text)
    {
        Assert.notNull(text);
        addLineBreak();
        Collections.addAll(content, splitStringAtNewline(text));
        addLineBreak();
    }
    
    public void addLineBreak()
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
        content.add("=".repeat(length));
    }
}
