package nl.ou.testar.temporal.util;

import com.opencsv.bean.AbstractBeanField;

public  class CSVConvertTemporalType<T> extends AbstractBeanField<T> {

    @Override
    protected Object convert( String value)
            throws IllegalArgumentException  {
        if (value.equals("")) {
            return null;
        }
        return TemporalType.valueOf(value.trim());
    }
}
