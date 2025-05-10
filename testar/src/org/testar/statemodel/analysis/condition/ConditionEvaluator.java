/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
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

package org.testar.statemodel.analysis.condition;

import org.testar.statemodel.StateModelManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Condition evaluators are used to check if a given condition is true during one or more states.
 * This can for example, be used to check if certain text is present on a webpage.
 */
public abstract class ConditionEvaluator {
    private List<TestCondition> conditions;

    public ConditionEvaluator() {
        conditions = new ArrayList<>();
    }

    /**
     * Adds a new condition that will be evaluated when evaluateConditions is called.
     * @param condition New condition to add.
     */
    public void addCondition(TestCondition condition) {
        conditions.add(condition);
    }

    /**
     * Adds multiple conditions that will be evaluated when evaluateConditions is called.
     * @param newConditions New conditions to add.
     */
    public void addConditions(Collection<TestCondition> newConditions) {
        conditions.addAll(newConditions);
    }

    /**
     * Removes all conditions.
     */
    public void clear() {
        conditions.clear();
    }

    /**
     * Evaluates all conditions added to the ConditionEvaluator based on the state model.
     * @param modelIdentifier The modelIdentifier of the test sequence.
     * @param stateModelManager The stateModelManager of the test sequence.
     * @return True if all conditions evaluate to true.
     */
    public abstract boolean evaluateConditions(String modelIdentifier, StateModelManager stateModelManager);

    public List<TestCondition> getConditions() {
        return conditions;
    }
}
