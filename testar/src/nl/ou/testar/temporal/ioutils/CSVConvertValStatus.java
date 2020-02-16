package nl.ou.testar.temporal.ioutils;

import com.opencsv.bean.AbstractBeanField;
import nl.ou.testar.temporal.foundation.ValStatus;

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
