package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class GraphML_DocNode {
    @JacksonXmlProperty( isAttribute = true)
    private String id;
    //       @JacksonXmlProperty( isAttribute = false, localName = "data")
    @JacksonXmlProperty( isAttribute = false, localName = "data")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GraphML_DocEleProperty> eleProperty;

    public GraphML_DocNode(String id, List<GraphML_DocEleProperty> eleproperty) {
        this.id = id;
        this.eleProperty = eleproperty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GraphML_DocEleProperty> getEleProperty() {
        return eleProperty;
    }

    public void setEleProperty(List<GraphML_DocEleProperty> eleProperty) {
        this.eleProperty = eleProperty;
    }


    }
