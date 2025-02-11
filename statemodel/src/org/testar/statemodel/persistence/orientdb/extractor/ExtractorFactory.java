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

package org.testar.statemodel.persistence.orientdb.extractor;

import org.testar.statemodel.exceptions.ExtractionException;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtractorFactory {

    public static final int EXTRACTOR_ABSTRACT_STATE = 1;

    public static final int EXTRACTOR_ABSTRACT_ACTION = 2;

    public static final int EXTRACTOR_ABSTRACT_STATE_TRANSITION = 3;

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<Integer, EntityExtractor> extractors = new HashMap<>();

    /**
     * This method returns an extractor for a given extractor type.
     * @param extractorType
     * @return
     * @throws ExtractionException
     */
    public static EntityExtractor getExtractor(int extractorType) throws ExtractionException {
        if (extractors.containsKey(extractorType)) {
            return extractors.get(extractorType);
        }

        switch (extractorType) {
            case EXTRACTOR_ABSTRACT_STATE:
                return createAbstractStateExtractor();

            case EXTRACTOR_ABSTRACT_ACTION:
                return createAbstractActionExtractor();

            case EXTRACTOR_ABSTRACT_STATE_TRANSITION:
                return createAbstractStateTransitionExtractor();

            default: throw new ExtractionException("Illegal extractor type specified");
        }
    }

    private static AbstractStateExtractor createAbstractStateExtractor() {
        AbstractStateExtractor abstractStateExtractor = null;
        try {
            abstractStateExtractor = new AbstractStateExtractor((AbstractActionExtractor) ExtractorFactory.getExtractor(EXTRACTOR_ABSTRACT_ACTION));
        } catch (ExtractionException e) {
            return null;
        }
        extractors.put(EXTRACTOR_ABSTRACT_STATE, abstractStateExtractor);
        return abstractStateExtractor;
    }

    private static AbstractActionExtractor createAbstractActionExtractor() {
        AbstractActionExtractor abstractActionExtractor = new AbstractActionExtractor();
        extractors.put(EXTRACTOR_ABSTRACT_ACTION, abstractActionExtractor);
        return abstractActionExtractor;
    }

    private static AbstractStateTransitionExtractor createAbstractStateTransitionExtractor() {
        try {
            AbstractStateExtractor abstractStateExtractor = (AbstractStateExtractor) ExtractorFactory.getExtractor(EXTRACTOR_ABSTRACT_STATE);
            AbstractActionExtractor abstractActionExtractor = (AbstractActionExtractor) ExtractorFactory.getExtractor(EXTRACTOR_ABSTRACT_ACTION);
            AbstractStateTransitionExtractor abstractStateTransitionExtractor = new AbstractStateTransitionExtractor(abstractStateExtractor, abstractActionExtractor);
            extractors.put(EXTRACTOR_ABSTRACT_STATE_TRANSITION, abstractStateTransitionExtractor);
            return abstractStateTransitionExtractor;
        }
        catch (ExtractionException ex) {
            return null;
        }
    }

}
