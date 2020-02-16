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



    @JacksonXmlProperty( isAttribute = false)
    @JacksonXmlElementWrapper(useWrapping = false)
    private Set<GraphML_DocKey> key;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty( isAttribute = false)
    private GraphML_DocGraph graph;

    public GraphML_DocRoot(Set<GraphML_DocKey> keys, GraphML_DocGraph graph) {
        this.key = keys;
        this.graph = graph;
    }


    //id="#72:1" source="#41:0" target="#41:0"
    // public List<GraphML_DocKey> getKeys() {
    //        return key;
    //    }
    //
    //    public void setKeys(List<GraphML_DocKey> key) {
    //        this.key = key;
    //    }
    //
    //    public GraphML_DocGraph getGraph() {
    //        return graph;
    //    }
    //
    //    public void setGraph(GraphML_DocGraph graph) {
    //        this.graph = graph;
    //    }
}
