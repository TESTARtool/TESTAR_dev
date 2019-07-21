package nl.ou.testar.temporal.scratch;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.opencsv.bean.AbstractBeanField;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CSVConvertMultiLine extends AbstractBeanField {


    @Override
    protected Object convert( String value)
            throws IllegalArgumentException  {
        List<String> list=new ArrayList<>();
        Pattern pat = Pattern.compile(";+");
        list =Splitter.on(pat).splitToList(value);
        return list;
    }

    @Override
    public String convertToWrite(Object value) {
        List<String> list = (List<String>) value;
        String result = Joiner.on(";").join(list);
        return result;
    }



}