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

package org.testar.statemodel.analysis.metric;

/**
 * Extended metrics used to gauge effectiveness of llm powered testing.
 */
public class LlmMetrics extends BasicMetrics {
    public LlmMetrics() {
        super();
    }

    // Amount of time test goal was accomplished
    private int tgCompleteCount = -1;

    // For sequences that completed the test goal:

    // Minimum states to reach test goal
    private int minConcreteStatesComplete = -1;
    // Max states to reach test goal
    private int maxConcreteStatesComplete = -1;
    // Average amount of states needed to complete test goal
    private double averageConcreteStatesComplete = -1;

    // Minimum states to reach test goal
    private int minAbstractStatesComplete = -1;
    // Max states to reach test goal
    private int maxAbstractStatesComplete = -1;
    // Average amount of states needed to complete test goal
    private double averageAbstractStatesComplete = -1;

    // Min amount of invalid actions
    private int minTotalActionsComplete = -1;
    // Max amount of invalid actions
    private int maxTotalActionsComplete = -1;
    // Average amount of invalid actions;
    private double averageTotalActionsComplete = -1;

    // For all sequences:

    // Min amount of invalid actions
    private int minInvalidActions = -1;
    // Max amount of invalid actions
    private int maxInvalidActions = -1;
    // Average amount of invalid actions;
    private double averageInvalidActions = -1;

    // Min amount of repeat actions (total - unique)
    private int minRepeatActions = -1;
    // Max amount of repeat actions
    private int maxRepeatActions = -1;
    // Average amount of repeat actions;
    private double averageRepeatActions = -1;

    public int getTgCompleteCount() {
        return tgCompleteCount;
    }

    public void setTgCompleteCount(int tgCompleteCount) {
        this.tgCompleteCount = tgCompleteCount;
    }

    public int getMinConcreteStatesComplete() {
        return minConcreteStatesComplete;
    }

    public void setMinConcreteStatesComplete(int minConcreteStatesComplete) {
        this.minConcreteStatesComplete = minConcreteStatesComplete;
    }

    public int getMaxConcreteStatesComplete() {
        return maxConcreteStatesComplete;
    }

    public void setMaxConcreteStatesComplete(int maxConcreteStatesComplete) {
        this.maxConcreteStatesComplete = maxConcreteStatesComplete;
    }

    public double getAverageConcreteStatesComplete() {
        return averageConcreteStatesComplete;
    }

    public void setAverageConcreteStatesComplete(double averageConcreteStatesComplete) {
        this.averageConcreteStatesComplete = averageConcreteStatesComplete;
    }

    public int getMinAbstractStatesComplete() {
        return minAbstractStatesComplete;
    }

    public void setMinAbstractStatesComplete(int minAbstractStatesComplete) {
        this.minAbstractStatesComplete = minAbstractStatesComplete;
    }

    public int getMaxAbstractStatesComplete() {
        return maxAbstractStatesComplete;
    }

    public void setMaxAbstractStatesComplete(int maxAbstractStatesComplete) {
        this.maxAbstractStatesComplete = maxAbstractStatesComplete;
    }

    public double getAverageAbstractStatesComplete() {
        return averageAbstractStatesComplete;
    }

    public void setAverageAbstractStatesComplete(double averageAbstractStatesComplete) {
        this.averageAbstractStatesComplete = averageAbstractStatesComplete;
    }

    public int getMinInvalidActions() {
        return minInvalidActions;
    }

    public void setMinInvalidActions(int minInvalidActions) {
        this.minInvalidActions = minInvalidActions;
    }

    public int getMaxInvalidActions() {
        return maxInvalidActions;
    }

    public void setMaxInvalidActions(int maxInvalidActions) {
        this.maxInvalidActions = maxInvalidActions;
    }

    public double getAverageInvalidActions() {
        return averageInvalidActions;
    }

    public void setAverageInvalidActions(double averageInvalidActions) {
        this.averageInvalidActions = averageInvalidActions;
    }

    public int getMinTotalActionsComplete() {
        return minTotalActionsComplete;
    }

    public void setMinTotalActionsComplete(int minTotalActionsComplete) {
        this.minTotalActionsComplete = minTotalActionsComplete;
    }

    public int getMaxTotalActionsComplete() {
        return maxTotalActionsComplete;
    }

    public void setMaxTotalActionsComplete(int maxTotalActionsComplete) {
        this.maxTotalActionsComplete = maxTotalActionsComplete;
    }

    public double getAverageTotalActionsComplete() {
        return averageTotalActionsComplete;
    }

    public void setAverageTotalActionsComplete(double averageTotalActionsComplete) {
        this.averageTotalActionsComplete = averageTotalActionsComplete;
    }

    public int getMinRepeatActions() {
        return minRepeatActions;
    }

    public void setMinRepeatActions(int minRepeatActions) {
        this.minRepeatActions = minRepeatActions;
    }

    public int getMaxRepeatActions() {
        return maxRepeatActions;
    }

    public void setMaxRepeatActions(int maxRepeatActions) {
        this.maxRepeatActions = maxRepeatActions;
    }

    public double getAverageRepeatActions() {
        return averageRepeatActions;
    }

    public void setAverageRepeatActions(double averageRepeatActions) {
        this.averageRepeatActions = averageRepeatActions;
    }
}
