package nl.ou.testar.temporal.util;

import com.opencsv.bean.AbstractBeanField;
import nl.ou.testar.temporal.structure.TemporalBean;

public  class CSVConvertTemporalType<T,I> extends AbstractBeanField<T,I> {

    @Override
    protected Object convert(String value)  //reading from csv file
            throws IllegalArgumentException {
        if (value.equals("")) {
            return null;
        }
        return TemporalType.valueOf(value.trim());
    }
}

