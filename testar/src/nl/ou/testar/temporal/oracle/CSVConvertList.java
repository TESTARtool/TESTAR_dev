package nl.ou.testar.temporal.oracle;

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
        // https://stackoverflow.com/questions/51926704/why-is-guavas-eventbus-marked-unstable-in-intellij-2018-2
        // noinspection all
        listie =Splitter.on(pat).splitToList(value);
        return listie;
    }

    @Override
    public String convertToWrite(Object value) {
        @SuppressWarnings("unchecked")
        List<String> listie = (List<String>) value;
        return Joiner.on(csvsep).join(listie);
    }



}