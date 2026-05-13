/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.writer;

import java.io.IOException;
import java.nio.file.Path;

import org.testar.reporting.sequence.SequenceTrace;

public interface SequenceTraceWriter {

    void write(SequenceTrace trace, Path outputPath) throws IOException;
}
