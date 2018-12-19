package nl.ou.testar.StateModel;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

import java.util.Set;

public class ConcreteState extends Widget{

    // a set of tags that was used in creating the concrete state id
    private Set<Tag<?>> tags;

    public ConcreteState(String id, Set<Tag<?>> tags) {
        super(id);
        this.tags = tags;
        this.setRootWidget(this);
    }

}
