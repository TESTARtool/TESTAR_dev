package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphML_DocEleProperty {
    static Pattern p = Pattern.compile("[\\p{Graph}\\p{Space}]");// do once
    @JacksonXmlProperty( isAttribute = true )
        private String key;
    @JacksonXmlText
    private String value;

    public GraphML_DocEleProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        // remove non printable low ascii codes. observed during excel test at the Ideasbutton: Ideas.
        // and escape XMl meta tags in the content as well
        //negative matches were problematic [^\p{Graph}\p{Space}] or (".*(?![\\p{Graph}\\p{Space}]).*");
        String val = StringEscapeUtils.escapeXml(value);
        Matcher m;
        StringBuilder result = new StringBuilder();
        //Pattern p = Pattern.compile("[\\p{Graph}\\p{Space}]");//
        String[] ary = val.split("");
        for (String ch : ary) {
            m = p.matcher(ch);
            result.append(m.matches() ? ch : "");
        }
        return result.toString();
    }

    public void setValue(String value) {
        this.value = value;
    }


    }
