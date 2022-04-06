package org.testar.visualvalidation.extractor;

import org.testar.monkey.alayer.Widget;

import static org.testar.monkey.alayer.webdriver.enums.WdTags.WebIsFullOnScreen;
import static org.testar.monkey.alayer.webdriver.enums.WdTags.WebTextContent;

class ExpectedTextExtractorWebdriver extends ExpectedTextExtractorBase {

    ExpectedTextExtractorWebdriver() {
        super(WebTextContent);
    }

    @Override
    protected boolean widgetIsIncluded(Widget widget, String role, String ancestors) {
        // Check if the widget is visible
        return widget.get(WebIsFullOnScreen) && super.widgetIsIncluded(widget, role, ancestors);
    }
}
