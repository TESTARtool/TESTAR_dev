/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.webstudio.api.dto.ResultFileSummaryDto;
import org.testar.webstudio.api.dto.SequenceVerdictDto;

// Verifies WS-FUNC-RUNTIME-EXECUTION-001: every report verdict is preserved for one Generate sequence.
public class ScriptlessSequenceVerdictTest {

    @Test
    public void preservesAllSequenceVerdictReports() {
        List<ResultFileSummaryDto> reportFiles = List.of(
            resultFile("sequence_1_V002_SUSPICIOUS_TAG.html", "failed"),
            resultFile("sequence_1_V001_WARNING_ACCESSIBILITY_FAULT.html", "failed"),
            resultFile("sequence_1_V003_WARNING_UI_VISUAL_OR_RENDERING_FAULT.html", "failed")
        );

        List<SequenceVerdictDto> verdicts = ScriptlessExecutionAdapter.buildSequenceVerdicts(reportFiles, 1);

        Assert.assertEquals(3, verdicts.size());
        Assert.assertEquals("sequence_1_V001_WARNING_ACCESSIBILITY_FAULT", verdicts.get(0).label());
        Assert.assertEquals("sequence_1_V002_SUSPICIOUS_TAG", verdicts.get(1).label());
        Assert.assertEquals("sequence_1_V003_WARNING_UI_VISUAL_OR_RENDERING_FAULT", verdicts.get(2).label());
        Assert.assertEquals("failed", verdicts.get(0).status());
        Assert.assertEquals("failed", verdicts.get(2).status());
    }

    @Test
    public void selectsOnlyReportsForTheCurrentSequenceWhenOutputPathIsUnavailable() {
        List<ResultFileSummaryDto> reportFiles = List.of(
            resultFile("sequence_1_V001_WARNING_ACCESSIBILITY_FAULT.html", "failed"),
            resultFile("sequence_2_V001_WARNING_ACCESSIBILITY_FAULT.html", "failed"),
            resultFile("sequence_1_V002_SUSPICIOUS_TAG.html", "failed")
        );

        List<ResultFileSummaryDto> sequenceFiles = ScriptlessExecutionAdapter.filterSequenceVerdictFiles(
            reportFiles,
            1
        );

        Assert.assertEquals(2, sequenceFiles.size());
        Assert.assertEquals("sequence_1_V001_WARNING_ACCESSIBILITY_FAULT.html", sequenceFiles.get(0).name());
        Assert.assertEquals("sequence_1_V002_SUSPICIOUS_TAG.html", sequenceFiles.get(1).name());
    }

    private ResultFileSummaryDto resultFile(String name, String status) {
        return new ResultFileSummaryDto(name, "reports/" + name, "text/html", status);
    }
}
