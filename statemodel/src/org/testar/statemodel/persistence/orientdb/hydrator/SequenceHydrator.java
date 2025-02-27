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
import org.testar.statemodel.exceptions.HydrationException;
import org.testar.statemodel.persistence.orientdb.entity.Property;
import org.testar.statemodel.persistence.orientdb.entity.PropertyValue;
import org.testar.statemodel.persistence.orientdb.entity.VertexEntity;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceVerdict;

import java.util.Date;

public class SequenceHydrator implements EntityHydrator<VertexEntity> {

    @Override
    public void hydrate(VertexEntity entity, Object source) throws HydrationException {
        if (!(source instanceof Sequence)) {
            throw new HydrationException("Instance of Sequence expected. Class " +source.getClass() + " was given.");
        }

        // add the sequence identifier
        Property identifier = entity.getEntityClass().getIdentifier();
        if (identifier == null) {
            throw new HydrationException("Entityclass did not have a valid identifier property");
        }

        entity.addPropertyValue(identifier.getPropertyName(), new PropertyValue(identifier.getPropertyType(), ((Sequence) source).getCurrentSequenceId()));

        // add the starting date and time
        // use of Date is not recommended, but OrientDB expects it
        Date date = new Date(((Sequence) source).getStartDateTime().toEpochMilli());
        entity.addPropertyValue("startDateTime", new PropertyValue(OType.DATETIME, date));

        // fetch the abstract level identifier for the current state model
        String modelIdentifier = ((Sequence) source).getModelIdentifier();
        entity.addPropertyValue("modelIdentifier", new PropertyValue(OType.STRING, modelIdentifier));

        // fetch the sequence's verdict and if needed, a termination message
        SequenceVerdict verdict = ((Sequence) source).getSequenceVerdict();
        entity.addPropertyValue("verdict", new PropertyValue(OType.STRING, verdict.toString()));

        if (verdict == SequenceVerdict.INTERRUPTED_BY_ERROR) {
            entity.addPropertyValue("terminationMessage", new PropertyValue(OType.STRING, ((Sequence) source).getTerminationMessage()));
        }
    }
}
