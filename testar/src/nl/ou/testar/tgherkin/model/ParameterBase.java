package nl.ou.testar.tgherkin.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.Util;

import nl.ou.testar.tgherkin.TgherkinException;

/**
 * ParameterBase defines the parameters of a method.
 *
 */
public class ParameterBase {

  private Map<Parameter<?>, List<Pair<String,Object>>> parameterValues = Util.newHashMap();

  /**
   * Retrieve parameter value.
   * @param <T> the expected class of the value
   * @param parameter parameter whose value should be retrieved
   * @param dataTable data table contained in the examples section of a scenario outline
   * @return value, null if no parameter entry found
   */
  @SuppressWarnings("unchecked")
  public final <T> T get(Parameter<T> parameter, DataTable dataTable) {
    Assert.notNull(parameter);
    Assert.isTrue(!parameter.isList());
    List<Pair<String, Object>> list = parameterValues.get(parameter);
    if (list == null || list.isEmpty()) {
      return null;
    }
    // all values are in lists: get first element of list
    Pair<String, Object> pair = list.get(0);
    if (pair == null) {
      return null;
    }
    if (pair.left() == null) {
      // no placeholder construction used
      return (T) pair.right();
    }
    return getPlaceholderValue(parameter, pair.left(), dataTable);
  }

  /**
   * Retrieve parameter value list.
   * @param <T> the expected class of the value
   * @param parameter parameter whose value should be retrieved
   * @param dataTable data table contained in the examples section of a scenario outline
   * @return list of values, null if no parameter entry found
   */
  @SuppressWarnings("unchecked")
  public final <T> List<T> getList(Parameter<T> parameter, DataTable dataTable) {
    Assert.notNull(parameter);
    Assert.isTrue(parameter.isList());
    List<Pair<String, Object>> list = parameterValues.get(parameter);
    if (list == null || list.isEmpty()) {
      return null;
    }
    List<T> resultList = new ArrayList<T>();
    for (Pair<String, Object> pair: list) {
      if (pair != null) {
        if (pair.left() == null) {
          // no placeholder construction used
          resultList.add((T)pair.right());
        } else {
          if (parameter.isList()) {
            // referenced data table cell could contain multiple list elements separated by spaces
            resultList.addAll(getPlaceholderValues(parameter, pair.left(), dataTable));
          } else {
            resultList.add(getPlaceholderValue(parameter, pair.left(), dataTable));
          }
        }
      }
    }
    return resultList;
  }

  @SuppressWarnings("unchecked")
  private <T> T getPlaceholderValue(Parameter<T> parameter, String columnName, DataTable dataTable) {
    String tableValue = dataTable.getPlaceholderValue(columnName);
    if (String.class.isAssignableFrom(parameter.type())) {
      return (T) tableValue;
    }
    if (Boolean.class.isAssignableFrom(parameter.type())) {
      return (T) Boolean.valueOf(tableValue);
    }
    if (WidgetCondition.class.isAssignableFrom(parameter.type())) {
      return (T) new WidgetCondition(tableValue);
    }
    throw new TgherkinException("Incompatible placeholder value");
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> getPlaceholderValues(Parameter<T> parameter, String columnName, DataTable dataTable) {
    List<T> resultList = new ArrayList<T>();
    String tableValue = dataTable.getPlaceholderValue(columnName);
    // elements are separated by spaces
    String[] elements = tableValue.split("\\s+");
    for (String element: elements) {
      if (String.class.isAssignableFrom(parameter.type())) {
        resultList.add((T)element);
      } else {
        if (Boolean.class.isAssignableFrom(parameter.type())) {
          resultList.add((T) Boolean.valueOf(element));
        } else {
          throw new TgherkinException("Incompatible placeholder list value");
        }

      }
    }
    return resultList;
  }

  /**
   * Set parameter value.
   * @param <T> the expected class of the value
   * @param parameter parameter whose value should be set
   * @param value to be set value
   */
  public <T> void setValue(Parameter<T> parameter, T value) {
    set(parameter, value, null);
  }

  /**
   * Set parameter value.
   * @param <T> the expected class of the value
   * @param parameter parameter whose value should be set
   * @param placeholder name of placeholder that refers to a data table column
   */
  public <T> void setPlaceholder(Parameter<T> parameter, String placeholder) {
    set(parameter, null, placeholder);
  }

  private <T> void set(Parameter<T> parameter, T value, String placeholder) {
    Assert.notNull(parameter);
    if (value != null) {
      Assert.isTrue(parameter.type().isInstance(value), "Value not of type required by this parameter!");
    } else {
      Assert.notNull(placeholder);
    }
    List<Pair<String, Object>> list;
    if (parameterValues.containsKey(parameter)) {
      Assert.isTrue(parameter.isList());
      list = parameterValues.get(parameter);
    } else {
      list = new ArrayList<Pair<String, Object>>();
    }
    list.add(new Pair<String, Object>(placeholder, value));
    parameterValues.put(parameter, list);
  }

  /**
   * Retrieve all placeholders in the parameter base.
   * @return list of placeholder name, empty list if no placeholders exist
   */
  public List<String> getPlaceholders() {
    List<String> result = new ArrayList<String>();
    for (List<Pair<String, Object>> list: parameterValues.values()) {
      for (Pair<String, Object> element: list) {
          if (element.left() != null) {
            result.add(element.left());
          }
      }
    }
    return result;
  }

  /**
   * Retrieve toString representation of all non-placeholder values in the parameter base.
   * @return list of toString representation of all non-placeholder values, empty list if no non-placeholder values exist
   */
  public List<String> getNonPlaceholderValuesToString() {
    List<String> result = new ArrayList<String>();
    for (List<Pair<String, Object>> list: parameterValues.values()) {
      for (Pair<String, Object> element: list) {
          if (element.left() == null) {
            result.add(element.right().toString());
          }
      }
    }
    return result;
  }

  /**
   * Retrieve size.
   * @return number of parameters
   */
  public int size() {
    return parameterValues.size();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
      result.append("(");
    boolean first = true;
    Iterator<Map.Entry<Parameter<?>, List<Pair<String,Object>>>> iterator = parameterValues.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Parameter<?>, List<Pair<String,Object>>> entrySet = iterator.next();
      Parameter<?> parameter = entrySet.getKey();
      List<Pair<String,Object>> list = entrySet.getValue();
      for (Pair<String, Object> element: list) {
          if (!first) {
            result.append(",");
          }
          if (element.left() != null) {
            // placeholder
            result.append("<" + element.left() + ">");
          } else {
            if (parameter.equals(Parameters.KBKEYS)) {
              // do not add enclosing quotes for key board key constants
              result.append(element.right());
            } else {
              result.append("\"" + element.right() + "\"");
            }
          }
      }
    }
    result.append(")");
    result.append(System.getProperty("line.separator"));
    return result.toString();
  }

}
