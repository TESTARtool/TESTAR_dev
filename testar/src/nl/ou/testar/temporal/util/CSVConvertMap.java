package nl.ou.testar.temporal.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.opencsv.bean.AbstractBeanField;

import java.util.Map;
import java.util.regex.Pattern;

public class CSVConvertMap extends AbstractBeanField {
public static final String csvsep = ";";
    public static final String kvsep = ":";

    @Override
    protected Object convert( String value)
            throws IllegalArgumentException  {
        Map<String,String> mappie;
        Pattern pat = Pattern.compile(csvsep+"+");
        mappie =Splitter.on(pat).withKeyValueSeparator(kvsep).split(value);
        return mappie;
    }

    @Override
    public String convertToWrite(Object value) {
        Map<String,String> mappie = (Map<String,String>) value;
        String result = Joiner.on(csvsep).withKeyValueSeparator(kvsep).join(mappie);
        return result;
    }



}