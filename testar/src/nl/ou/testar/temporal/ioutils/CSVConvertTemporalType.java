package nl.ou.testar.temporal.ioutils;

import com.opencsv.bean.AbstractBeanField;
import nl.ou.testar.temporal.oracle.TemporalFormalism;

public  class CSVConvertTemporalType<T,I> extends AbstractBeanField<T,I> {

    @Override
    protected Object convert(String value)  //reading from csv file
            throws IllegalArgumentException {
        if (value.equals("")) {
            return null;
        }
        return TemporalFormalism.valueOf(value.toUpperCase().trim());
    }
}

