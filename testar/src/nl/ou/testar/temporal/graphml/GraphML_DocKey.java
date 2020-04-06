package nl.ou.testar.temporal.graphml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.Objects;

public class GraphML_DocKey {


    @JacksonXmlProperty( isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty( isAttribute = true, localName = "for")
    private String forElementType;
    @JacksonXmlProperty( isAttribute = true,localName = "attr.name")
    private String attributeName;
    @JacksonXmlProperty( isAttribute = true, localName = "attr.type")
    private String attributeType;
    //@JacksonXmlText
    //private String dummyvalue="";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphML_DocKey that = (GraphML_DocKey) o;
        return id.equals(that.id) &&
                forElementType.equals(that.forElementType) &&
                attributeName.equals(that.attributeName) &&
                attributeType.equals(that.attributeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, forElementType, attributeName, attributeType);
    }

    public GraphML_DocKey(String id, String forType, String attributeName, String attributeType) {
        this.id = id;
        this.forElementType = forType;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
    }

    //id="UIAIsWindowModal" for="edge" attr.name="UIAIsWindowModal" attr.type="boolean">
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForElementType() {
        return forElementType;
    }

    public void setForElementType(String forElementType) {
        this.forElementType = forElementType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
