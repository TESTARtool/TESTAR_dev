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

import org.testar.statemodel.exceptions.HydrationException;

import java.util.HashMap;
import java.util.Map;

public abstract class HydratorFactory {

    public static final int HYDRATOR_ABSTRACT_STATE = 1;

    public static final int HYDRATOR_ABSTRACT_ACTION = 2;

    public static final int HYDRATOR_ABSTRACT_STATE_MODEL = 3;

    public static final int HYDRATOR_CONCRETE_STATE = 4;

    public static final int HYDRATOR_WIDGET = 5;

    public static final int HYDRATOR_WIDGET_RELATION = 6;

    public static final int HYDRATOR_ABSTRACTED_BY = 7;

    public static final int HYDRATOR_BLACKHOLE = 8;

    public static final int HYDRATOR_CONCRETE_ACTION = 9;

    public static final int HYDRATOR_SEQUENCE = 10;

    public static final int HYDRATOR_SEQUENCE_NODE = 11;

    public static final int HYDRATOR_ACCESSED = 12;

    public static final int HYDRATOR_SEQUENCE_STEP = 13;

    public static final int HYDRATOR_FIRST_NODE = 14;

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityHydrator> hydrators = new HashMap<>();

    public static EntityHydrator getHydrator(int hydratorType) throws HydrationException {
        if (hydrators.containsKey(hydratorType)) {
            return hydrators.get(hydratorType);
        }

        switch (hydratorType) {
            case HYDRATOR_ABSTRACT_STATE:
                return createAbstractStateHydrator();

            case HYDRATOR_ABSTRACT_ACTION:
                return createAbstractActionHydrator();

            case HYDRATOR_ABSTRACT_STATE_MODEL:
                return createAbstractStateModelHydrator();

            case HYDRATOR_CONCRETE_STATE:
                return createConcreteStateHydrator();

            case HYDRATOR_CONCRETE_ACTION:
                return createConcreteActionHydrator();

            case HYDRATOR_WIDGET:
                return createWidgetHydrator();

            case HYDRATOR_WIDGET_RELATION:
                return createWidgetRelationHydrator();

            case HYDRATOR_ABSTRACTED_BY:
                return createIsAbstractedByHydrator();

            case HYDRATOR_BLACKHOLE:
                return createBlackHoleHydrator();

            case HYDRATOR_SEQUENCE:
                return createSequenceHydrator();

            case HYDRATOR_SEQUENCE_NODE:
                return createSequenceNodeHydrator();

            case HYDRATOR_ACCESSED:
                return createAccessedHydrator();

            case HYDRATOR_SEQUENCE_STEP:
                return createSequenceStepHydrator();

            case HYDRATOR_FIRST_NODE:
                return createFirstNodeHydrator();

            default:
                throw new HydrationException("Invalid hydrator type provided to the hydrator factory");
        }
    }

    private static AbstractStateHydrator createAbstractStateHydrator() {
        AbstractStateHydrator abstractStateHydrator = new AbstractStateHydrator();
        hydrators.put(HYDRATOR_ABSTRACT_STATE, abstractStateHydrator);
        return abstractStateHydrator;
    }

    private static AbstractActionHydrator createAbstractActionHydrator() {
        AbstractActionHydrator abstractActionHydrator = new AbstractActionHydrator();
        hydrators.put(HYDRATOR_ABSTRACT_ACTION, abstractActionHydrator);
        return abstractActionHydrator;
    }

    private static AbstractStateModelHydrator createAbstractStateModelHydrator() {
        AbstractStateModelHydrator abstractStateModelHydrator  = new AbstractStateModelHydrator();
        hydrators.put(HYDRATOR_ABSTRACT_STATE_MODEL, abstractStateModelHydrator);
        return abstractStateModelHydrator;
    }

    private static ConcreteStateHydrator createConcreteStateHydrator() {
        ConcreteStateHydrator concreteStateHydrator = new ConcreteStateHydrator();
        hydrators.put(HYDRATOR_CONCRETE_STATE, concreteStateHydrator);
        return concreteStateHydrator;
    }

    private static WidgetHydrator createWidgetHydrator() {
        WidgetHydrator widgetHydrator = new WidgetHydrator();
        hydrators.put(HYDRATOR_WIDGET, widgetHydrator);
        return widgetHydrator;
    }

    private static WidgetRelationHydrator createWidgetRelationHydrator() {
        WidgetRelationHydrator widgetRelationHydrator = new WidgetRelationHydrator();
        hydrators.put(HYDRATOR_WIDGET_RELATION, widgetRelationHydrator);
        return widgetRelationHydrator;
    }

    private static IsAbstractedByHydrator createIsAbstractedByHydrator() {
        IsAbstractedByHydrator isAbstractedByHydrator = new IsAbstractedByHydrator();
        hydrators.put(HYDRATOR_ABSTRACTED_BY, isAbstractedByHydrator);
        return isAbstractedByHydrator;
    }

    private static BlackHoleHydrator createBlackHoleHydrator() {
        BlackHoleHydrator blackHoleHydrator = new BlackHoleHydrator();
        hydrators.put(HYDRATOR_BLACKHOLE, blackHoleHydrator);
        return blackHoleHydrator;
    }

    private static ConcreteActionHydrator createConcreteActionHydrator() {
        ConcreteActionHydrator concreteActionHydrator = new ConcreteActionHydrator();
        hydrators.put(HYDRATOR_CONCRETE_ACTION, concreteActionHydrator);
        return concreteActionHydrator;
    }

    private static SequenceHydrator createSequenceHydrator() {
        SequenceHydrator sequenceHydrator = new SequenceHydrator();
        hydrators.put(HYDRATOR_SEQUENCE, sequenceHydrator);
        return sequenceHydrator;
    }

    private static SequenceNodeHydrator createSequenceNodeHydrator() {
        SequenceNodeHydrator sequenceNodeHydrator = new SequenceNodeHydrator();
        hydrators.put(HYDRATOR_SEQUENCE_NODE, sequenceNodeHydrator);
        return sequenceNodeHydrator;
    }

    private static AccessedHydrator createAccessedHydrator() {
        AccessedHydrator accessedHydrator = new AccessedHydrator();
        hydrators.put(HYDRATOR_ACCESSED, accessedHydrator);
        return accessedHydrator;
    }

    private static SequenceStepHydrator createSequenceStepHydrator() {
        SequenceStepHydrator sequenceStepHydrator = new SequenceStepHydrator();
        hydrators.put(HYDRATOR_SEQUENCE_STEP, sequenceStepHydrator);
        return sequenceStepHydrator;
    }

    private static FirstNodeHydrator createFirstNodeHydrator() {
        FirstNodeHydrator firstNodeHydrator = new FirstNodeHydrator();
        hydrators.put(HYDRATOR_FIRST_NODE, firstNodeHydrator);
        return firstNodeHydrator;
    }

}

