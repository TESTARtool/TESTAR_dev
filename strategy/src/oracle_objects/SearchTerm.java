package oracle_objects;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SearchTerm
{
    private enum Type { STRING, INTEGER, BOOLEAN }
    
    private final Type                  type;
    private final String                singleStrValue;
    private final Integer               singleIntValue;
    private final Boolean               singleBoolValue;
    private final Collection<String>    valueList;
    private final String                key;
    private final String                value;
    private final Integer               lowerBound;
    private final Integer               upperBound;
    
    private SearchTerm(Type type,
                       String singleStringValue,
                       Integer singleIntegerValue,
                       Boolean singleBooleanValue,
                       Collection<String> valueList,
                       String key, String value,
                       Integer lowerBound, Integer upperBound)
    {
        this.type            = type;
        this.singleStrValue  = singleStringValue;
        this.singleIntValue  = singleIntegerValue;
        this.singleBoolValue = singleBooleanValue;
        this.valueList       = valueList;
        this.key             = key;
        this.value           = value;
        this.lowerBound      = lowerBound;
        this.upperBound      = upperBound;
    }
    
    // setter and getter methods for different search types
    public static SearchTerm single(String value)
    {
        return new SearchTerm(Type.STRING, value, null, null, null, null, null, null, null);
    }
    
    public static SearchTerm singleInteger(String value)
    {
        Integer intValue = parseInteger(value);
        if (intValue == null)
            throw new IllegalArgumentException("Integer input must be a valid integer.");
        
        return new SearchTerm(Type.INTEGER,null, intValue, null, null, null, null, null, null);
    }
    
    public static SearchTerm singleBoolean(String value)
    {
        Boolean boolValue = parseBoolean(value);
        return new SearchTerm(Type.BOOLEAN, null, null, boolValue, null, null, null, null, null);
    }
    
    public static SearchTerm list(List<String> values)
    {
        return new SearchTerm(Type.STRING, null, null, null, values, null, null, null, null);
    }
    
    public static SearchTerm pair(String key, String value)
    {
        return new SearchTerm(Type.STRING, null, null, null, null, key, value, null, null);
    }
    
    public static SearchTerm range(String lowerBoundStr, String upperBoundStr)
    {
        Integer lowerBound = parseInteger(lowerBoundStr);
        Integer upperBound = parseInteger(upperBoundStr);
        
        if (lowerBound == null || upperBound == null)
            throw new IllegalArgumentException("Range bounds must be valid integers.");
        
        if (lowerBound > upperBound)
            throw new IllegalArgumentException("Lower bound cannot be greater than upper bound.");
        
        return new SearchTerm(Type.INTEGER, null, null, null, null, null, null, lowerBound, upperBound);
    }
    
    private static Boolean parseBoolean(String value)
    {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
            return Boolean.valueOf(value);
        else
            throw new IllegalArgumentException("No valid boolean 'true' or 'false' found.");
    }
    private static Integer parseInteger(String value)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
    
    public boolean isTypeString() {return this.type == Type.STRING;}
    public boolean isTypeInteger() {return this.type == Type.INTEGER;}
    public boolean isTypeBoolean() {return this.type == Type.BOOLEAN;}
    
    public boolean isSingle()
    {
        return (singleStrValue != null || singleIntValue != null || singleBoolValue != null);
    }
    
    public boolean isList()
    {
        return valueList != null;
    }
    
    public boolean isPair()
    {
        return key != null && value != null;
    }
    
    public boolean isRange()
    {
        return lowerBound != null && upperBound != null;
    }
    
    public String getSingleValueAsString()
    {
        switch(this.type)
        {
            case STRING: return singleStrValue;
            case INTEGER: return singleIntValue.toString();
            case BOOLEAN: return singleBoolValue.toString();
            default: return "";
        }
    }
    
    public Collection<String> getValueList()
    {
        return valueList;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public Integer getLowerBound()
    {
        return lowerBound;
    }
    
    public Integer getUpperBound()
    {
        return upperBound;
    }
    
    public boolean isInRange(String intString)
    {
        if(!isRange())
            return false;
        Integer valueToCheck = parseInteger(intString);
        if (valueToCheck == null)
            throw new IllegalArgumentException("Input must be an integer in string format.");
        return (lowerBound <= valueToCheck && valueToCheck < upperBound); // exclusive check
    }
    
    public boolean isInList(String string)
    {
        if(!isList())
            return false;
        return valueList.contains(string); // must be exact match
    }

    @Override
    public String toString()
    {
        if (isSingle()) return "Single: " + getSingleValueAsString();
        if (isList()) return "List: [" + String.join(", ", valueList) + "]";
        if (isPair()) return "Pair: (" + key + " -> " + value + ")";
        if (isRange()) return "Range: (" + lowerBound + " to " + upperBound + ")";
        return "Invalid SearchTerm";
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchTerm that = (SearchTerm) o;
        return type == that.type &&
               Objects.equals(singleStrValue, that.singleStrValue) &&
               Objects.equals(singleIntValue, that.singleIntValue) &&
               Objects.equals(singleBoolValue, that.singleBoolValue) &&
               Objects.equals(valueList, that.valueList) &&
               Objects.equals(key, that.key) &&
               Objects.equals(value, that.value) &&
               Objects.equals(lowerBound, that.lowerBound) &&
               Objects.equals(upperBound, that.upperBound);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(type, singleStrValue, singleIntValue, singleBoolValue, valueList, key, value, lowerBound, upperBound);
    }
}
