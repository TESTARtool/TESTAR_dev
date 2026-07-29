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
import org.testar.rascal.DslOracleMetadata;
import org.testar.webstudio.testoracle.TestOracleService;

public final class TestOracleController {

    private final TestOracleService testOracleService;

    public TestOracleController(TestOracleService testOracleService) {
        this.testOracleService = testOracleService;
    }

    // Implements WS-FUNC-TEST-ORACLES-001: exposes workspace oracle inventory and Java/DSL file operations.
    public TestOracleInventoryDto inventory(String workspaceName) {
        return testOracleService.inventory(workspaceName);
    }

    // Implements WS-FUNC-ORACLE-DSL-EDITOR-001: exposes backend-generated DSL editor metadata.
    public DslOracleMetadata dslMetadata() {
        return testOracleService.dslMetadata();
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

    // Implements WS-FUNC-ORACLE-DSL-EDITOR-001: exposes authoritative Rascal DSL validation.
    public TestOracleDslResultDto validateDslFile(String workspaceName, String relativePath, String content) {
        return testOracleService.validateDslFile(workspaceName, relativePath, content);
    }

    // Implements WS-FUNC-ORACLE-DSL-EDITOR-001: saves DSL content and generates workspace Java oracle source.
    public TestOracleDslResultDto generateJavaFromDslFile(String workspaceName, String relativePath, String content) {
        return testOracleService.generateJavaFromDslFile(workspaceName, relativePath, content);
    }

    // Implements WS-FUNC-ORACLE-JAVA-ENABLEMENT-001: reads workspace Java oracle source files.
    public WorkspaceFileDto readJavaFile(String workspaceName, String relativePath) {
        return testOracleService.readJavaFile(workspaceName, relativePath);
    }

    // Implements WS-FUNC-ORACLE-JAVA-ENABLEMENT-001: persists workspace Java oracle source files.
    public WorkspaceFileDto saveJavaFile(String workspaceName, String relativePath, String content) {
        return testOracleService.saveJavaFile(workspaceName, relativePath, content);
    }

    // Implements WS-FUNC-ORACLE-JAVA-ENABLEMENT-001: creates workspace Java oracle files and enables new classes.
    public WorkspaceFileDto createJavaFile(String workspaceName, String relativePath) {
        return testOracleService.createJavaFile(workspaceName, relativePath);
    }

    // Implements WS-FUNC-ORACLE-JAVA-ENABLEMENT-001: deletes workspace Java oracle files and refreshes inventory.
    public TestOracleInventoryDto deleteJavaFile(String workspaceName, String relativePath) {
        return testOracleService.deleteJavaFile(workspaceName, relativePath);
    }

    // Implements WS-FUNC-ORACLE-JAVA-ENABLEMENT-001: compiles workspace Java oracle files and returns diagnostics.
    public WorkspaceJavaCompileResultDto compileJavaFile(String workspaceName, String relativePath, String content) {
        return testOracleService.compileJavaFile(workspaceName, relativePath, content);
    }
}
