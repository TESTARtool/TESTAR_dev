package org.testar.statemodel;

import org.testar.monkey.alayer.Tag;

import java.util.Set;

public class ConcreteState extends Widget implements IConcreteState {

    // a set of tags that was used in creating the concrete state id
//    private Set<Tag<?>> tags;

    /**
     * The abstract state that is abstracted from this concrete state.
     */
    private AbstractState abstractState;

    // a byte array holding the screenshot for this state
    private byte[] screenshot;

    public ConcreteState(String id, Set<Tag<?>> tags, AbstractState abstractState) {
        super(id);
        this.tags = tags;
        this.setRootWidget(this);
        this.abstractState = abstractState;
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

    /**
     * Returns the abstract state attached to this concrete state
     * @return
     */
    public AbstractState getAbstractState() {
        return abstractState;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }
}
