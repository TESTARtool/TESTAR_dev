/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;

public class ConcreteStateHydrator implements EntityHydrator<VertexEntity>{

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
        for (Tag<?> tag :attributes.tags()) {
            // we simply add a property for each tag
            target.addPropertyValue(Validation.sanitizeAttributeName(tag.name()), new PropertyValue(TypeConvertor.getInstance().getOrientDBType(attributes.get(tag).getClass()), attributes.get(tag)));
        }

        // get the oracle verdict and transform it into a custom code
        Verdict verdict = attributes.get(Tags.OracleVerdict, null);
        int oracleVerdictCode = 4; // default value
        if (verdict != null) {
            if (verdict.severity() == Verdict.SEVERITY_MIN) {
                oracleVerdictCode = 1;
            }
            else if (verdict.severity() == Verdict.SEVERITY_MAX) {
                oracleVerdictCode = 2;
            }
            else if (verdict.severity() > Verdict.SEVERITY_MIN && verdict.severity() < Verdict.SEVERITY_MAX) {
                oracleVerdictCode = 3;
            }
        }
        target.addPropertyValue("oracleVerdictCode", new PropertyValue(OType.INTEGER, oracleVerdictCode));
    }
}
