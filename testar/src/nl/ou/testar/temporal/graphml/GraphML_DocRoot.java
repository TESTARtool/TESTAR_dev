package nl.ou.testar.temporal.graphml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Set;

@JacksonXmlRootElement( localName = "graphml")
public class GraphML_DocRoot {
    @JacksonXmlProperty( isAttribute = true)
    private String xmlns="http://graphml.graphdrawing.org/xmlns";
    @JacksonXmlProperty( isAttribute = true, localName = "xmlns:xsi")
    private String xmlns_xsi="http://www.w3.org/2001/XMLSchema-instance";
    @JacksonXmlProperty( isAttribute = true, localName = "xsi:schemaLocation")
    private String xsi_schemaLocation="http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.1/graphml.xsd";



    @JacksonXmlProperty()
    @JacksonXmlElementWrapper(useWrapping = false)
    private Set<GraphML_DocKey> key;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty()
    private GraphML_DocGraph graph;

    public GraphML_DocRoot(Set<GraphML_DocKey> keys, GraphML_DocGraph graph) {
        this.key = keys;
        this.graph = graph;
    }
}
