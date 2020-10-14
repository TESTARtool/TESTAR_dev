package nl.ou.testar.temporal.oracle;

import com.opencsv.bean.AbstractBeanField;
import nl.ou.testar.temporal.util.foundation.Verdict;

public  class CSVConvertVerdict<T,I> extends AbstractBeanField<T,I> {

    @Override
    protected Object convert( String value)
            throws IllegalArgumentException {
        if (value.equals("")) {
            return null;
        }
        return Verdict.valueOf(value.toUpperCase().trim());

    }
}
