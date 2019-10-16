package nl.ou.testar.StateModel.Analysis.Representation;

public class ActionViz {

    private String screenshotSource;

    private String screenshotTarget;

    private String actionDescription;

    private int counterSource;

    private int counterTarget;

    public ActionViz(String screenshotSource, String screenshotTarget, String actionDescription, int counterSource, int counterTarget) {
        this.screenshotSource = screenshotSource;
        this.screenshotTarget = screenshotTarget;
        this.actionDescription = actionDescription;
        this.counterSource = counterSource;
        this.counterTarget = counterTarget;
    }

    public String getScreenshotSource() {
        return screenshotSource;
    }

    public String getScreenshotTarget() {
        return screenshotTarget;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public int getCounterSource() {
        return counterSource;
    }

    public int getCounterTarget() {
        return counterTarget;
    }
}
