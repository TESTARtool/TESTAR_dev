package nl.ou.testar.temporal.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class GraphML_DocEdge {
    @JacksonXmlProperty( isAttribute = true)
    private String id;
    @JacksonXmlProperty( isAttribute = true)
    private String source;
    @JacksonXmlProperty( isAttribute = true)
    private String target;



    @JacksonXmlProperty( isAttribute = false, localName = "data")
    @JacksonXmlElementWrapper(useWrapping = false)
        private List<GraphML_DocEleProperty> eleproperty;

    public GraphML_DocEdge(String id, String source, String target, List<GraphML_DocEleProperty> eleproperty) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.eleproperty = eleproperty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<GraphML_DocEleProperty> getEleproperty() {
        return eleproperty;
    }

    public void setEleproperty(List<GraphML_DocEleProperty> eleproperty) {
        this.eleproperty = eleproperty;
    }

}
