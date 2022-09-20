package org.testar.screenshotjson;

//import javafx.util.Pair;

import org.testar.monkey.Pair;

import java.util.Set;

public class FullWidgetInfoJsonObject {
    Set<Pair<String, String>> widgetTreeObjects;
    //= new HashSet<javafx.util.Pair<String, String>>();

    public FullWidgetInfoJsonObject(Set<Pair<String, String>> widgetTreeObjects) {
        this.widgetTreeObjects = widgetTreeObjects;
    }
}