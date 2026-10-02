package org.testar.engine.service;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testar.core.CodingManager;
import org.testar.core.alayer.Roles;
import org.testar.core.state.StateIdentity;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

public class DefaultStateIdentifierServiceTest {

    private Tag<?>[] previousAbstractTags;
    private Tag<?>[] previousConcreteTags;

    @Before
    public void preserveConfiguration() {
        previousAbstractTags = CodingManager.getCustomTagsForAbstractId();
        previousConcreteTags = CodingManager.getCustomTagsForConcreteId();
    }

    @After
    public void restoreConfiguration() {
        CodingManager.setCustomTagsForAbstractId(previousAbstractTags);
        CodingManager.setCustomTagsForConcreteId(previousConcreteTags);
    }

    @Test
    public void serviceKeepsItsConfigurationAfterGlobalReinitialization() {
        CodingManager.setCustomTagsForAbstractId(new Tag<?>[] {Tags.Title});
        CodingManager.setCustomTagsForConcreteId(new Tag<?>[] {Tags.Title});
        DefaultStateIdentifierService service = new DefaultStateIdentifierService();
        StateStub state = new StateStub();
        state.set(Tags.Title, "Submit");
        assertSame(state, service.identifyState(state));
        String abstractId = state.get(Tags.AbstractID);
        String concreteId = state.get(Tags.ConcreteID);

        CodingManager.initCodingManager(Collections.emptyList());
        service.identifyState(state);

        assertEquals(abstractId, state.get(Tags.AbstractID));
        assertEquals(concreteId, state.get(Tags.ConcreteID));
        state.set(Tags.Title, "Cancel");
        service.identifyState(state);
        assertNotEquals(abstractId, state.get(Tags.AbstractID));
    }

    @Test
    public void servicesCanInterleaveDifferentAbstractionConfigurations() {
        DefaultStateIdentifierService byTitle = new DefaultStateIdentifierService(
                new StateIdentity(new Tag<?>[] {Tags.Title}, new Tag<?>[] {Tags.Title}));
        DefaultStateIdentifierService byRole = new DefaultStateIdentifierService(
                new StateIdentity(new Tag<?>[] {Tags.Role}, new Tag<?>[] {Tags.Title}));
        StateStub state = new StateStub();
        state.set(Tags.Role, Roles.Widget);
        state.set(Tags.Title, "Submit");
        byTitle.identifyState(state);
        String titleId = state.get(Tags.AbstractID);
        byRole.identifyState(state);
        String roleId = state.get(Tags.AbstractID);
        assertNotEquals(titleId, roleId);

        state.set(Tags.Title, "Cancel");
        byTitle.identifyState(state);
        assertNotEquals(titleId, state.get(Tags.AbstractID));
        byRole.identifyState(state);
        assertEquals(roleId, state.get(Tags.AbstractID));
    }
}
