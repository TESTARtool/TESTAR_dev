package org.testar.action.priorization.llm;

public interface LlmResponse {
    public String getResponse();
    public int getUsageTokens();
}
