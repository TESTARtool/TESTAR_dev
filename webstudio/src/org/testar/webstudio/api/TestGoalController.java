/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import org.testar.webstudio.api.dto.TestGoalFileDto;
import org.testar.webstudio.api.dto.TestGoalNodeDto;
import org.testar.webstudio.testgoal.TestGoalService;

public final class TestGoalController {

    private final TestGoalService testGoalService;

    public TestGoalController(TestGoalService testGoalService) {
        this.testGoalService = testGoalService;
    }

    public TestGoalNodeDto tree(String workspaceName) {
        return testGoalService.tree(workspaceName);
    }

    public TestGoalFileDto readFile(String workspaceName, String path) {
        return testGoalService.readFile(workspaceName, path);
    }

    public TestGoalFileDto saveFile(String workspaceName, String path, String content) {
        return testGoalService.saveFile(workspaceName, path, content);
    }

    public TestGoalFileDto createFile(String workspaceName, String path) {
        return testGoalService.createFile(workspaceName, path);
    }

    public TestGoalNodeDto createFolder(String workspaceName, String path) {
        return testGoalService.createFolder(workspaceName, path);
    }

    public TestGoalNodeDto delete(String workspaceName, String path) {
        return testGoalService.delete(workspaceName, path);
    }
}
