package org.testar.statemodel.changedetection;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.changedetection.key.ActionPrimaryKeyProvider;
import org.testar.statemodel.exceptions.StateModelException;

public class ChangeDetectionEngineTest {

    @Test
    public void testDescriptionProviderConstructor() throws StateModelException {
        ActionPrimaryKeyProvider descriptionProvider = id -> "desc-" + id;
        ChangeDetectionEngine engine = new ChangeDetectionEngine(descriptionProvider);

        AbstractStateModel oldModel = new AbstractStateModel("oldModel", "app", "version", new HashSet<Tag<?>>(Collections.emptySet()));
        AbstractStateModel newModel = new AbstractStateModel("newModel", "app", "version", new HashSet<Tag<?>>(Collections.emptySet()));
        engine.compare(oldModel, newModel);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDescriptionProvider() throws StateModelException {
        new ChangeDetectionEngine(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOldModel() throws StateModelException {
        ActionPrimaryKeyProvider descriptionProvider = id -> "desc-" + id;
        ChangeDetectionEngine engine = new ChangeDetectionEngine(descriptionProvider);

        AbstractStateModel newModel = new AbstractStateModel("newModel", "app", "version", new HashSet<Tag<?>>(Collections.emptySet()));
        engine.compare(null, newModel);
    }

    @Test(expected = NullPointerException.class)
    public void testNullNewModel() throws StateModelException {
        ActionPrimaryKeyProvider descriptionProvider = id -> "desc-" + id;
        ChangeDetectionEngine engine = new ChangeDetectionEngine(descriptionProvider);

        AbstractStateModel oldModel = new AbstractStateModel("oldModel", "app", "version", new HashSet<Tag<?>>(Collections.emptySet()));
        engine.compare(oldModel, null);
    }

}
