package nl.ou.testar.visualvalidation.extractor;

import org.fruit.alayer.Widget;

import static org.fruit.alayer.webdriver.enums.WdTags.WebIsFullOnScreen;
import static org.fruit.alayer.webdriver.enums.WdTags.WebTextContent;

class ExpectedTextExtractorWebdriver extends ExpectedTextExtractorBase {

    ExpectedTextExtractorWebdriver() {
        super(WebTextContent);
    }

    @Override
    protected boolean widgetIsIncluded(Widget widget, String role) {
        // Check if the widget is visible
        return widget.get(WebIsFullOnScreen) && super.widgetIsIncluded(widget, role);
    }
}
