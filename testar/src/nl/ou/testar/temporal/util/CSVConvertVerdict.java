package nl.ou.testar.temporal.util;

import com.opencsv.bean.AbstractBeanField;

public  class CSVConvertVerdict<T,I> extends AbstractBeanField<T,I> {

    @Override
    protected Object convert( String value)
            throws IllegalArgumentException {
        if (value.equals("")) {
            return null;
        }
        return Verdict.valueOf(value.trim());

    }
}
