package nl.ou.testar.temporal.ioutils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.opencsv.bean.AbstractBeanField;

import java.util.Map;
import java.util.regex.Pattern;

public class CSVConvertMap<T,I> extends AbstractBeanField<T,I> {
public static final String csvsep = ";";
    public static final String kvsep = ":";

    @Override
    protected Object convert( String value)        throws IllegalArgumentException  {


        Map<String,String> mappie;
        Pattern pat = Pattern.compile(csvsep+"+");
        // https://stackoverflow.com/questions/51926704/why-is-guavas-eventbus-marked-unstable-in-intellij-2018-2
        // noinspection all
        mappie =Splitter.on(pat).withKeyValueSeparator(kvsep).split(value);
        return mappie;
    }

    @Override
    public String convertToWrite(Object value) {
        @SuppressWarnings("unchecked")
        Map<String,String> mappie = (Map<String,String>) value;
        return Joiner.on(csvsep).withKeyValueSeparator(kvsep).join(mappie);
    }



}