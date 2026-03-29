/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.testar.core.Assert;

public class PlainTextFormatUtil extends BaseFormatUtil {

    protected PlainTextFormatUtil(String filePath) {
        super(filePath, "txt");
    }

    public void addContent(String text) {
        if (text.contains("\n")) {
            Collections.addAll(content, splitStringAtNewline(text));
        } else {
            content.add(text);
        }
    }

    public void addHeading(int level, String text) {
        Assert.isTrue(level >= 1 && level <= 6, "Invalid heading level: must be between 1 and 6");
        Assert.notNull(text);
        content.add(StringUtils.repeat("#", level) + text); //repeat # char
    }

    public void addParagraph(String text) {
        Assert.notNull(text);
        addEmptyLine();
        Collections.addAll(content, splitStringAtNewline(text));
        addEmptyLine();
    }

    public void addEmptyLine() {
        content.add("");
    }

    public void addHorizontalLine() {
        addHorizontalLine(20);
    }

    public void addHorizontalLine(int length) {
        Assert.isTrue(length >= 3 && length <= 100,
                "Invalid horizontal line length: must be between 3 and 100");
        content.add(StringUtils.repeat("=", length)); //repeat # char
    }

    public void addList(boolean ordered, List<String> items) {
        addEmptyLine();
        for (int i = 0; i < items.size(); i++) {
            if (ordered) {
                addContent(String.valueOf(i + 1) + items.get(i));
            } else {
                addContent("- " + items.get(i));
            }
        }
        addEmptyLine();
    }
}
