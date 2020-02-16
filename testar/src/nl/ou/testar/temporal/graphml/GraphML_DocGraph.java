package nl.ou.testar.temporal.graphml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class GraphML_DocGraph {
    @JacksonXmlProperty( isAttribute = true)
    private String id="G";
    @JacksonXmlProperty( isAttribute = true)
    private String edgedefault="directed";
    @JacksonXmlProperty( isAttribute = false, localName = "node")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<GraphML_DocNode> nodes ;
    @JacksonXmlProperty( isAttribute = false, localName = "edge")
    @JacksonXmlElementWrapper(useWrapping = false )
    private List<GraphML_DocEdge> edges ;

    public GraphML_DocGraph(String id, List<GraphML_DocNode> nodes, List<GraphML_DocEdge> edges) {
        this.id = id;
        this.nodes = nodes;
        this.edges = edges;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEdgedefault() {
        return edgedefault;
    }

    public void setEdgedefault(String edgedefault) {
        this.edgedefault = edgedefault;
    }

    public List<GraphML_DocNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<GraphML_DocNode> nodes) {
        this.nodes = nodes;
    }

    public List<GraphML_DocEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<GraphML_DocEdge> edges) {
        this.edges = edges;
    }



    //id="#72:1" source="#41:0" target="#41:0"
}
