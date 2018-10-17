package nl.ou.testar.tgherkin.model;

import java.util.concurrent.ConcurrentHashMap;

import org.fruit.Assert;

/**
 * Element that can be attached to a parameter base.
 * An element has a name and a type and are associated with values who must be of that type. 
 * @param <T> the expected class of the value
 *
 */
public class Parameter<T> {

	private static final ConcurrentHashMap<Parameter<?>, Parameter<?>> EXISTING_PARAMETERS = new ConcurrentHashMap<Parameter<?>, Parameter<?>>();
	
	/**
	 * Returns a parameter object which is identified by name and value type. 
	 * @param <T> the expected class of the value
	 * @param name the name of the parameter
	 * @param valueType the type of the values that are associated with this tag.
	 * @param list indicator whether the parameter is a list of values of a certain type
	 * @return a parameter object
	 */
	@SuppressWarnings("unchecked")
	public static <T> Parameter<T> from(String name, Class<T> valueType, boolean list) {
		Assert.notNull(name, valueType);
		Parameter<T> ret = new Parameter<T>(name, valueType, list);
		Parameter<T> existing = (Parameter<T>)EXISTING_PARAMETERS.putIfAbsent(ret, ret);
		if (existing != null) {
			return existing;
		}	
		return ret;
	}

	private final Class<T> clazz;
	private final String name;
	private final boolean list;
	private int hashcode;

	/**
	 * Constructor.
	 * @param name parameter name
	 * @param clazz class of parameter value 
	 * @param list indicator whether parameter is a list of values of a certain type
	 */
	protected Parameter(String name, Class<T> clazz, boolean list) {
		this.clazz = clazz;
		this.name = name;
		this.list = list;
	}

	/**
	 * The name of the parameter.
	 * @return the name of the parameter
	 */
	public String name() { 
		return name; 
	}
	
	
	/**
	 * The type of the values associated with this parameter.
	 * @return value type
	 */
	public Class<T> type() { 
		return clazz; 
	}
	
	/**
	 * Determine whether the parameter is a list.
	 * @return true if parameter is a list, otherwise false
	 */
	public boolean isList() { 
		return list; 
	}

	@Override
	public String toString() { 
		return name; 
	}
	
	@Override
	public int hashCode() {
		int ret = hashcode;
		if (ret == 0) {
			ret = name.hashCode(); 
			hashcode = ret;
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}	
		if (other instanceof Parameter) {
			Parameter<?> ot = (Parameter<?>) other;
			return name.equals(ot.name) && clazz.equals(ot.clazz);
		}
		return false;
	}
	
}