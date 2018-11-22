package nl.ou.testar.tgherkin.model;

import java.util.List;

import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.monkey.Settings;

/**
 * This interface defines methods on states, widgets and actions that the document model needs.
 *
 */
public interface ProtocolProxy {

  /**
   * Retrieve configuration settings.
   * @return settings
   */
  Settings getSettings();

  /**
   * Retrieve state.
   * @return state
   */
  State getState();

  /**
   * Retrieve Tgherkin source code.
   * @return Tgherkin source code
   */
  String getTgherkinSourceCode();

  /**
   * Check whether widget is not filtered.
   * @param widget widget involved
   * @return true if widget is not filtered, otherwise false
   */
  boolean isUnfiltered(Widget widget);

  /**
   * Check whether widget is clickable.
   * @param widget widget involved
   * @return true if widget is clickable, otherwise false
   */
  boolean isClickable(Widget widget);

  /**
   * Check whether widget is typeable.
   * @param widget widget involved
   * @return true if widget is typeable, otherwise false
   */
  boolean isTypeable(Widget widget);

  /**
   * Retrieve random text.
   * @param widget widget involved
   * @return generated random text
   */
  String getRandomText(Widget widget);

  /**
   * Retrieve top widgets.
   * @param state the SUT's current state
   * @return list of top widgets
   */
  List<Widget> getTopWidgets(State state);

  /**
   * Store widget.
   * @param stateID state identifier
   * @param widget to be stored widget 
   */
  void storeWidget(String stateID, Widget widget);

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


