package nl.ou.testar.subroutine;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.SUT;

/**
 * This interface defines methods on states, widgets and actions that the subroutine framework needs.
 *
 */
public interface SubroutineProxy {

    /**
   * State is fulfilling criterion for running a subroutine.
   * Important to define in this method
   * - setSourceFile(<a valid Tgherkin subroutine filename>)
   * - setActualIndexSD(<index of the actual subroutine>)
   * @param state the SUT's current state
     * @return state is ready for subroutine action
   */
  boolean startState(State state);

  /**
   * This method is invoked each time TESTAR starts to generate a new subroutine.
   * @param state the SUT's current state
   */
   void startSubroutine(State state);

    /** Define action to be taken when switching from subroutine to TESTAR.
     * @param state the SUT's current state
   * @return set of possible actions that can be taken
    */
  Set<Action> finishState(SUT sut, State state);
;
  /**
   * This method is invoked each time after TESTAR finished the generation of a subroutine.
   * @param state the SUT's current state
   */
  void finishSubroutine(State state);
}
