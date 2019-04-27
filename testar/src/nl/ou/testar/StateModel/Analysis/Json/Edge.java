package nl.ou.testar.StateModel.Analysis.Json;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;

public class Edge extends Document {

    private String sourceId;

    private String targetId;

    public Edge(String id, String sourceId, String targetId) {
        super(id);
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    @JsonGetter("source")
    public String getSourceId() {
        return sourceId;
    }

    @JsonGetter("target")
    public String getTargetId() {
        return targetId;
    }
}
