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

import org.testar.statemodel.StateModelManager;

/**
 * Class used to collect and manage metrics using the strategy pattern.
 */
public class MetricsManager {
    private IMetricsCollector metricsCollector;

    /**
     * Creates a new MetricsManager with the selected metrics collector.
     * @param metricsCollector The IMetricsCollector implementation to use.
     */
    public MetricsManager(IMetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    public void setMetricsCollector(IMetricsCollector collector) {
        this.metricsCollector = collector;
    }

    public void finish() {
        this.metricsCollector.finalizeMetrics();
        this.metricsCollector.printMetrics();
    }

    // TODO: Find better way to handle invalidActions (we can't easily retrieve these from the state model)
    public void collect(String modelIdentifier, StateModelManager stateModelManager, int invalidActions) {
        metricsCollector.collectMetrics(modelIdentifier, stateModelManager, invalidActions);
    }
}
