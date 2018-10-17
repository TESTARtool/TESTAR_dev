package nl.ou.testar.tgherkin.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that defines the available Tgherkin parameter types.
 * 
 */
public class Parameters {

	/**
	 * Set of parameters. 
	 */
	protected static final Set<Parameter<?>> PARAMETER_SET = new HashSet<>();
	
	/**
	 * Define parameter by name and class.
	 * @param <T> the expected class of the value
	 * @param name the name of the parameter
	 * @param valueType the type of the values that are associated with this tag.
	 * @param list indicator whether the parameter is a list of values of a certain type
	 * @return parameter 
	 */
	protected static <T> Parameter<T> from(String name, Class<T> valueType, boolean list) {
		Parameter<T> ret = Parameter.from(name, valueType, list);
		PARAMETER_SET.add(ret);
		return ret;
	}
		
	/**
	 * Retrieve set of parameters.
	 * @return set of parameters
	 */
	public static Set<Parameter<?>> parameterSet() {
		return Collections.unmodifiableSet(PARAMETER_SET);
	}
	
	/**
	 * Unchecked parameter.
	 */
	public static final Parameter<Boolean> UNCHECKED = from("Unchecked", Boolean.class, false);
	/**
	 * Keyboard key parameter.
	 */
	public static final Parameter<String> KBKEYS = from("KBKEY", String.class, true);
	/**
	 * Text parameter.
	 */
	public static final Parameter<String> TEXT = from("Text", String.class, false);
	/**
	 * Widget tree condition parameter.
	 */
	public static final Parameter<WidgetCondition> WIDGET_CONITION = from("WidgetCondition", WidgetCondition.class, false);

	private Parameters() {
	}
}