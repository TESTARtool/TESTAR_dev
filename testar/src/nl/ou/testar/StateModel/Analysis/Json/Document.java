package nl.ou.testar.StateModel.Analysis.Json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;
import java.util.Map;

abstract class Document {

    // every document needs an id
    private String id;

    // a mapping of properties that the document contains
    private Map<String, String> properties;

    public Document(String id) {
        this.id = id;
        properties = new HashMap<>();
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonGetter("id")
    public String getId() {
        return id;
    }

}