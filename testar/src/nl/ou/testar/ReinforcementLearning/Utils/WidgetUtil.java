package nl.ou.testar.ReinforcementLearning.Utils;

import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

public class WidgetUtil {

    public static boolean equals(Widget widget1, Widget widget2) {
        System.out.println(widget1.getAbstractRepresentation());
        System.out.println(widget2.getAbstractRepresentation());

        return widget1.get(Tags.Title).equals(widget2.get(Tags.Title));

//        return widget1.getAbstractRepresentation().equals(widget2.getAbstractRepresentation());
    }
}
