/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import org.testar.webstudio.api.dto.TestOracleInventoryDto;
import org.testar.webstudio.api.dto.TestOracleDslResultDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileResultDto;
import org.testar.webstudio.testoracle.TestOracleService;

public final class TestOracleController {

    private final TestOracleService testOracleService;

    public TestOracleController(TestOracleService testOracleService) {
        this.testOracleService = testOracleService;
    }

    public TestOracleInventoryDto inventory(String workspaceName) {
        return testOracleService.inventory(workspaceName);
    }

    public WorkspaceFileDto readDslFile(String workspaceName, String relativePath) {
        return testOracleService.readDslFile(workspaceName, relativePath);
    }

    public WorkspaceFileDto saveDslFile(String workspaceName, String relativePath, String content) {
        return testOracleService.saveDslFile(workspaceName, relativePath, content);
    }

    public WorkspaceFileDto createDslFile(String workspaceName, String relativePath) {
        return testOracleService.createDslFile(workspaceName, relativePath);
    }

    public TestOracleInventoryDto deleteDslFile(String workspaceName, String relativePath) {
        return testOracleService.deleteDslFile(workspaceName, relativePath);
    }

    public TestOracleDslResultDto validateDslFile(String workspaceName, String relativePath, String content) {
        return testOracleService.validateDslFile(workspaceName, relativePath, content);
    }

    public TestOracleDslResultDto generateJavaFromDslFile(String workspaceName, String relativePath, String content) {
        return testOracleService.generateJavaFromDslFile(workspaceName, relativePath, content);
    }

    public WorkspaceFileDto readJavaFile(String workspaceName, String relativePath) {
        return testOracleService.readJavaFile(workspaceName, relativePath);
    }

    public WorkspaceFileDto saveJavaFile(String workspaceName, String relativePath, String content) {
        return testOracleService.saveJavaFile(workspaceName, relativePath, content);
    }

    public WorkspaceFileDto createJavaFile(String workspaceName, String relativePath) {
        return testOracleService.createJavaFile(workspaceName, relativePath);
    }

    public TestOracleInventoryDto deleteJavaFile(String workspaceName, String relativePath) {
        return testOracleService.deleteJavaFile(workspaceName, relativePath);
    }

    public WorkspaceJavaCompileResultDto compileJavaFile(String workspaceName, String relativePath, String content) {
        return testOracleService.compileJavaFile(workspaceName, relativePath, content);
    }
}
