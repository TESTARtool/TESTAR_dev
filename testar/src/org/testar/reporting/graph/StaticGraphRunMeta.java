package org.testar.reporting.graph;

public final class StaticGraphRunMeta {

    private final String verdictTitle;
    private final String modelIdentifier;
    private final String oracleVerdict;
    private final String llmGoalsText;
    private final boolean complete;

    public StaticGraphRunMeta(String verdictTitle, String modelIdentifier, String oracleVerdict, String llmGoalsText, boolean complete) {
        this.verdictTitle = verdictTitle;
        this.modelIdentifier = modelIdentifier;
        this.oracleVerdict = oracleVerdict;
        this.llmGoalsText = llmGoalsText;
        this.complete = complete;
    }

    public String getVerdictTitle() {
        return verdictTitle;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public String getOracleVerdict() {
        return oracleVerdict;
    }

    public String getLlmGoalsText() {
        return llmGoalsText;
    }

    public boolean isComplete() {
        return complete;
    }
}
