package oracle_objects;

public class LogicBlockSimple implements LogicBlock
{
    private final String propertyName;
    public LogicBlockSimple(String propertyName)
    {
        this.propertyName = propertyName;
    }
    
    public String getPropertyName()
    {
        return propertyName;
    }
}

enum LogicOperator
{
    HAS,
    IS,
    CONTAINS,
    IN,
    EQUALS
}
