/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.agent.codex;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CodexAgentRunnerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void runningRunIsNotAliveWhenResultFileExists() throws Exception {
        Path runDirectory = temporaryFolder.newFolder("codex-run").toPath();
        Path resultFile = runDirectory.resolve("result.json");
        CodexAgentRunner runner = new CodexAgentRunner(temporaryFolder.getRoot().toPath());
        CodexAgentRunner.RunningCodexRun runningRun = runner.new RunningCodexRun(
                new AliveProcess(),
                runDirectory,
                runDirectory.resolve("stdout.log"),
                runDirectory.resolve("stderr.log"),
                resultFile,
                null
        );

        Assert.assertTrue(runningRun.isAlive());

        Files.writeString(resultFile, "{\"status\":\"completed\"}");

        Assert.assertTrue(runningRun.hasResult());
        Assert.assertFalse(runningRun.isAlive());
    }

    private static final class AliveProcess extends Process {

        @Override
        public OutputStream getOutputStream() {
            return OutputStream.nullOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
        }

        @Override
        public boolean isAlive() {
            return true;
        }
    }
}
