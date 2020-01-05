package nl.ou.testar.temporal.util;

import com.opencsv.bean.AbstractBeanField;

public class CSVConvertValStatus<T,I> extends AbstractBeanField<T,I> {

    @Override
    protected Object convert( String value)
            throws IllegalArgumentException {
        if (value.equals("")) {
            return null;
        }
        return ValStatus.valueOf(value.trim());
    }
}
