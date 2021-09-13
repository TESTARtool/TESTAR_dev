package nl.ou.testar.StateModel.Analysis.Json;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Element {

    public static final String GROUP_NODES = "nodes";

    public static final String GROUP_EDGES = "edges";

    private String group;

    private Document document;

    private ArrayList<String> classes;

    public Element(String group, Document document) {
        this(group, document, null);
    }

    public Element(String group, Document document, String className) {
        this.group = group;
        this.document = document;
        if (className != null) {
            classes = new ArrayList<>();
            classes.add(className);
        }
    }

    @JsonGetter("group")
    public String getGroup() {
        return group;
    }

    @JsonGetter("data")
    public Document getDocument() {
        return document;
    }

    @JsonGetter("classes")
    public ArrayList<String> getClasses() {
        return classes;
    }

    public void addClass(String className) {
        if (classes == null) {
            classes = new ArrayList<>();
        }
        classes.add(className);
    }
}