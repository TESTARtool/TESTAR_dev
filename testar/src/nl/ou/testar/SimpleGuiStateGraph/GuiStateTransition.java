package nl.ou.testar.SimpleGuiStateGraph;

public class GuiStateTransition {
    private String sourceStateConcreteId;
    private String targetStateConcreteId;
    private String actionConcreteId;

    public GuiStateTransition(String sourceStateConcreteId, String targetStateConcreteId, String actionConcreteId) {
        this.sourceStateConcreteId = sourceStateConcreteId;
        this.targetStateConcreteId = targetStateConcreteId;
        this.actionConcreteId = actionConcreteId;
    }

    public String getSourceStateConcreteId() {
        return sourceStateConcreteId;
    }

    public String getTargetStateConcreteId() {
        return targetStateConcreteId;
    }

    public String getActionConcreteId() {
        return actionConcreteId;
    }
}
