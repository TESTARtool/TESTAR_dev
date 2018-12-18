package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.fruit.alayer.StdState;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

/**
 * Simplified State representation that only defines an iterator over widgets.
 *
 */
public class StateMock extends StdState{

  private static final long serialVersionUID = 7755367830576251631L;
  private List<Widget> widgets = new ArrayList<Widget>();


  /**
   * Constructor.
   * @param widgets given list of widgets
   */
  public StateMock(List<Widget> widgets) {
    super();
    this.widgets = widgets;
    // generate a random identifier
    Random random = new Random();
    int randomNumber  = random.nextInt(Integer.MAX_VALUE);
    set(Tags.ConcreteID, "ConcreteID" + randomNumber);
  }

  @Override
    public Iterator<Widget> iterator() {
        Iterator<Widget> it = new Iterator<Widget>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < widgets.size() && widgets.get(currentIndex) != null;
            }
            @Override
            public Widget next() {
                return widgets.get(currentIndex++);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

}
