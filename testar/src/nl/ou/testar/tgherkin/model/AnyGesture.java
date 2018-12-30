package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;


/**
 * Class responsible for handling any gesture.
 *
 */
public class AnyGesture extends Gesture {


  private List<Gesture> gestures = new ArrayList<Gesture>();

    /**
     * AnyGesture constructor.
     * @param parameterBase container for parameters
     */
    public AnyGesture(ParameterBase parameterBase) {
      super(parameterBase);
      // pass boolean argument unchecked to the click gestures
    gestures.add(new ClickGesture(parameterBase));
    gestures.add(new DoubleClickGesture(parameterBase));
    gestures.add(new DragSliderGesture(new ParameterBase()));
    gestures.add(new DragDropGesture(new ParameterBase()));
    gestures.add(new DropDownAtGesture(new ParameterBase()));
    gestures.add(new HitKeyGesture(new ParameterBase()));
    gestures.add(new MouseMoveGesture(new ParameterBase()));
    gestures.add(new RightClickGesture(new ParameterBase()));
    gestures.add(new TripleClickGesture(parameterBase));
    gestures.add(new TypeGesture(new ParameterBase()));
    }


    @Override
    public boolean gesturePossible(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
         // at least one gesture has to be possible
      for (Gesture gesture: gestures) {
           if (gesture.gesturePossible(widget, proxy, dataTable)) {
               return true;
           }
         }
      return false;
    }


    @Override
    public Set<Action> getActions(Widget widget, ProtocolProxy proxy, DataTable dataTable) {
    Set<Action> actions = new HashSet<Action>();
      for (Gesture gesture: gestures) {
        if (gesture.gesturePossible(widget, proxy, dataTable)) {
            actions.addAll(gesture.getActions(widget, proxy, dataTable));
        }
      }
      return actions;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
       result.append("anyGesture");
       result.append(getParameterBase().toString());
      return result.toString();
    }
}
