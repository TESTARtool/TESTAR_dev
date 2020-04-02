package nl.ou.testar.temporal.ioutils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.opencsv.bean.AbstractBeanField;

import java.util.List;
import java.util.regex.Pattern;

public class CSVConvertList<T,I> extends AbstractBeanField<T,I> {
public  static String csvsep;

    public CSVConvertList() {
        super();
        csvsep = ";=+;";      // ;=========; is accepted and also ;=; , but not ;;
    }
    public CSVConvertList(String csvsep) {
        super();
        CSVConvertList.csvsep = csvsep;
    }

    @Override
    protected Object convert( String value)
            throws IllegalArgumentException  {
        List<String> listie;
        Pattern pat = Pattern.compile(csvsep);
        listie =Splitter.on(pat).splitToList(value);
        return listie;
    }

    @Override
    public String convertToWrite(Object value) {
        List<String> listie = (List<String>) value;
        return Joiner.on(csvsep).join(listie);
    }



}