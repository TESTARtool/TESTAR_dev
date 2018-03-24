package nl.ou.testar.tgherkin.model;

import java.util.List;

import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

/**
 * ActionWidgetProxy interface.
 *
 */
public interface ActionWidgetProxy {

	/**
	 * Check whether widget is not filtered.
	 * @param widget given widget
	 * @return true if widget is not filtered, otherwise false
	 */
	boolean isUnfiltered(Widget widget);

	/**
	 * Check whether widget is clickable.
	 * @param widget given widget
	 * @return true if widget is clickable, otherwise false
	 */
	boolean isClickable(Widget widget);
	
	/**
	 * Check whether widget is typeable.
	 * @param widget given widget
	 * @return true if widget is typeable, otherwise false
	 */
	boolean isTypeable(Widget widget);
	
    /**
     * Retrieve random text.
     * @param widget given widget
     * @return generated random text
     */
    String getRandomText(Widget widget);
    
	/**
	 * Retrieve top widgets.
	 * @param state given state
	 * @return list of top widgets
	 */
	List<Widget> getTopWidgets(State state);

	/**
	 * Retrieve sequence count.
	 * @return sequence count
	 */
    int getSequenceCount();

	/**
	 * Retrieve action count.
	 * @return action count
	 */
    int getActionCount();
	
}
