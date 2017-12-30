package nl.ou.testar;

import org.fruit.alayer.StdState;
import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;

public class Util {

   static StdState createState(String tag) {
        StdState state = new StdState();
        state.set(Tags.ConcreteID, tag);
        state.set(Tags.Abstract_R_ID,"dummy");
        return state;
    }

    static StdWidget createWidget(String tag) {
       StdWidget widget = new StdWidget();
       widget.set(Tags.ConcreteID,tag);
       widget.set(Tags.Abstract_R_ID,"role");
       widget.set(Tags.Abstract_R_T_ID,"role_type");
       widget.set(Tags.Abstract_R_T_P_ID,"role_type_path");
       return widget;
    }
}
