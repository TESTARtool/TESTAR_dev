package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class GraphML_DocEleProperty {
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
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    }
