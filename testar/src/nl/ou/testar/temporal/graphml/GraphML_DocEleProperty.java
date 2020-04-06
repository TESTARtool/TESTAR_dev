package nl.ou.testar.temporal.graphml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphML_DocEleProperty {
    // remove non printable low ascii codes. observed during excel test at the Ideasbutton: Ideas.
    //negative matches were problematic [^\p{Graph}\p{Space}] or (".*(?![\\p{Graph}\\p{Space}]).*");
    static Pattern p = Pattern.compile("[\\p{Graph}\\p{Space}]");

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
        // and escape XMl meta tags in the content as well
        //String val = StringEscapeUtils.escapeXml(value);
        String val= value;
        Matcher m;
        StringBuilder result = new StringBuilder();
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
