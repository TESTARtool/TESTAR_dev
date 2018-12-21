package nl.ou.testar.StateModel;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

import java.util.Set;

public class ConcreteState extends Widget{

    // a set of tags that was used in creating the concrete state id
    private Set<Tag<?>> tags;

    // a byte array holding the screenshot for this state
    private byte[] screenshot;

    public ConcreteState(String id, Set<Tag<?>> tags) {
        super(id);
        this.tags = tags;
        this.setRootWidget(this);
    }

    /**
     * Retrieves the screenshot data for this state.
     * @return
     */
    public byte[] getScreenshot() {
        return screenshot;
    }

    /**
     * Sets the screenshot data for this state.
     * @param screenshot
     */
    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot;
    }
}
