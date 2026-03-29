/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;

public class HtmlFormatUtil extends BaseFormatUtil {
    public HtmlFormatUtil(String filePath) {
        super(filePath, "html");
    }

    public void addContent(String text) {
        if (text.contains("\n")) {
            Collections.addAll(content, splitStringAtNewline(text));
        } else {
            content.add(text);
        }
    }

    public void addContent(String text, String tag) {
        if (text.contains("\n")) {
            content.add("<" + tag + ">");
            Collections.addAll(content, splitStringAtNewline(text));
            content.add("</" + tag + ">");
        } else {
            content.add("<" + tag + ">" + text + "</" + tag + ">");
        }
    }

    public void addHeader(String title) {
        addHeader(title, "", "");
    }

    public void addHeader(String title, String script) {
        addHeader(title, script, "");
    }

    public void addHeader(String title, String script, String style) {
        Assert.notNull(title);
        Assert.notNull(script);
        Assert.notNull(style);

        content.add("<!DOCTYPE html>");
        content.add("<html lang=\"en\">");
        content.add("<head>");

        if (!script.isEmpty()) {
            addContent(script, "script");
        }

        if (!style.isEmpty()) {
            addContent(style, "style");
        }

        content.add("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        addContent(title, "title");
        content.add("</head>");
        content.add("<body>");
    }

    public void addFooter() {
        content.add("</body>");
        content.add("</html>");
    }

    public void addHeading(int level, String text) {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
        Assert.notNull(text);
        addContent(text, "h" + level);
    }

    public void addParagraph(String text) {
        Assert.notNull(text);
        addContent(text, "p");
    }

    public void addLineBreak() {
        content.add("<br>");
    }

    public void addList(boolean ordered, List<String> items) {
        if (ordered) {
            content.add("<ol>");
        } else {
            //unordered
            content.add("<ul>");
        }

        for (String item : items) {
            addContent(item, "li");
        }

        if (ordered) {
            content.add("</ol>");
        } else {
            //unordered
            content.add("</ul>");
        }
    }

    public void addButton(String buttonClass, String buttonText) {
        content.add("<button type='button' class='" + buttonClass + "'>" + buttonText + "</button>");
    }
}
