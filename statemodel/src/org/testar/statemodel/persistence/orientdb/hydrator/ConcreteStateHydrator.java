/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.hydrator;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.TypeConvertor;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.persistence.orientdb.util.Validation;

import java.util.Collections;
import java.util.List;

import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;

public class ConcreteStateHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity target, Object source) throws HydrationException {
        if (!(source instanceof ConcreteState)) {
            throw new HydrationException();
        }

        // fetch the connected abstract state
        AbstractState abstractState = ((ConcreteState) source).getAbstractState();
        if (abstractState == null) {
            throw new HydrationException("No abstract state is connected to the concrete state with id " + ((ConcreteState) source).getId());
        }

        // first make sure the identity property is set
        Property identifier = target.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("No identifying properties were provided for entity class " + target.getEntityClass().getClassName());
        }

        // for the unique id, we add in the model identifier for the abstract state. Otherwise, we could get the same concrete states shared across models
        String uniqueId = abstractState.getModelIdentifier() + "-" + ((ConcreteState) source).getId();
        target.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), uniqueId));

        // of course we also have to add the state id for use in our in-memory model
        target.addPropertyValue("stateId", new PropertyValue(OType.STRING, ((ConcreteState) source).getId()));

        // we need to add a widgetId property, as this is inherited from the orientdb base class
        //@todo this is hardcoded in now to fix an inheritance issue. Ideally, this needs to be solved in the entity classes
        String widgetId = ((ConcreteState) source).getId() + "-" + ((ConcreteState) source).getId();
        target.addPropertyValue("widgetId", new PropertyValue(OType.STRING, uniqueId));

        // add the screenshot
        if (((ConcreteState) source).getScreenshot() != null) {
            target.addPropertyValue("screenshot", new PropertyValue(OType.BINARY, ((ConcreteState) source).getScreenshot()));
        }

        // loop through the tagged attributes for this state and add them
        TaggableBase attributes = ((ConcreteState) source).getAttributes();
        for (Tag<?> tag : attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(Validation.sanitizeAttributeName(tag.name()), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }

        // get the oracle verdict and transform it into a custom code
        List<Verdict> verdicts = attributes.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
        int oracleVerdictCode = 4; // default value
        if (Verdict.helperAreAllVerdictsOK(verdicts)) {
            oracleVerdictCode = 1;
        } else {
            oracleVerdictCode = 2;
        }
        target.addPropertyValue("oracleVerdictCode", new PropertyValue(OType.INTEGER, oracleVerdictCode));
    }
}
