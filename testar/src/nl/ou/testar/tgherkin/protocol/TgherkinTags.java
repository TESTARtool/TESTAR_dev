package nl.ou.testar.tgherkin.protocol;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;

/**
 * TgherkinTags provides tags for reporting the execution results.
 *
 */
public class TgherkinTags extends TagsBase {
   /**
   * Tgherkin tag for  sequence number.
   */
  public static final Tag<Integer> TGHERKIN_SEQUENCE_NR = from("tgherkin_sequence_nr", Integer.class);
   /**
   * Tgherkin tag for action number.
   */
   public static final Tag<Integer> TGHERKIN_ACTION_NR = from("tgherkin_action_nr", Integer.class);
   /**
   * Tgherkin tag for feature.
   */
   public static final Tag<String> TGHERKIN_FEATURE = from("tgherkin_feature", String.class);
   /**
   * Tgherkin tag for scenario.
   */
   public static final Tag<String> TGHERKIN_SCENARIO = from("tgherkin_scenario", String.class);
   /**
   * Tgherkin tag for type.
   */
   public static final Tag<String> TGHERKIN_TYPE = from("tgherkin_type", String.class);
   /**
   * Tgherkin tag for step.
   */
   public static final Tag<String> TGHERKIN_STEP = from("tgherkin_step", String.class);
   /**
   * Tgherkin tag for given.
   */
   public static final Tag<Boolean> TGHERKIN_GIVEN = from("tgherkin_given", Boolean.class);
   /**
   * Tgherkin tag for given mismatch.
   */
   public static final Tag<Boolean> TGHERKIN_GIVEN_MISMATCH = from("tgherkin_given_mismatch", Boolean.class);
   /**
   * Tgherkin tag for number of pre-generated derived actions.
   */
   public static final Tag<Integer> TGHERKIN_PRE_GENERATED_DERIVED_ACTIONS = from("tgherkin_pre_generated_derived_actions", Integer.class);
   /**
   * Tgherkin tag for number of derived actions.
   */
   public static final Tag<Integer> TGHERKIN_WHEN_DERIVED_ACTIONS = from("tgherkin_when_derived_actions", Integer.class);
   /**
   * Tgherkin tag for when mismatch.
   */
   public static final Tag<Boolean> TGHERKIN_WHEN_MISMATCH = from("tgherkin_when_mismatch", Boolean.class);
   /**
   * Tgherkin tag for selected action.
   */
   public static final Tag<String> TGHERKIN_SELECTED_ACTION = from("tgherkin_selected_action", String.class);
   /**
   * Tgherkin tag for selected action details.
   */
   public static final Tag<String> TGHERKIN_SELECTED_ACTION_DETAILS = from("tgherkin_selected_action_details", String.class);
   /**
   * Tgherkin tag for then.
   */
   public static final Tag<Boolean> TGHERKIN_THEN = from("tgherkin_then", Boolean.class);
   /**
   * Tgherkin tag for then mismatch.
   */
   public static final Tag<Boolean> TGHERKIN_THEN_MISMATCH = from("tgherkin_then_mismatch", Boolean.class);
   /**
   * Tgherkin tag for verdict.
   */
   public static final Tag<String> TGHERKIN_VERDICT = from("tgherkin_verdict", String.class);
}
