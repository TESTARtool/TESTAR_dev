/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class CliDaemonClient {

    private static final int MAX_REQUEST_ATTEMPTS = 5;

    CliResponse send(CliRequest request) {
        ensureDaemonRunning();
        IOException lastException = null;
        for (int attempt = 0; attempt < MAX_REQUEST_ATTEMPTS; attempt++) {
            try {
                return sendOnce(request);
            } catch (IOException exception) {
                lastException = exception;
                sleepQuietly(200L);
            }
        }
        throw new IllegalStateException("Unable to communicate with CLI daemon", lastException);
    }

    private void ensureDaemonRunning() {
        if (isDaemonReachable()) {
            return;
        }
        startDaemonProcess();
        long deadline = System.currentTimeMillis() + CliDaemonConfig.START_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isDaemonReachable()) {
                return;
            }
            sleepQuietly(200L);
        }
        throw new IllegalStateException("CLI daemon did not start in time");
    }

    private boolean isDaemonReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(CliDaemonConfig.HOST, CliDaemonConfig.PORT),
                    200
            );
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private void startDaemonProcess() {
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java.exe").toString();
        String classPath = System.getProperty("java.class.path");
        Path testarHome = resolveTestarHome();
        Path logFile = Path.of(System.getProperty("java.io.tmpdir"), "testar-cli-daemon.log");
        ProcessBuilder builder = new ProcessBuilder(
                javaExecutable,
                "-cp",
                classPath,
                CliMain.class.getName(),
                "daemon"
        );
        builder.directory(testarHome.toFile());
        builder.redirectErrorStream(true);
        builder.redirectOutput(logFile.toFile());
        try {
            Files.createDirectories(logFile.getParent());
            builder.start();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start CLI daemon", exception);
        }
    }

    private Path resolveTestarHome() {
        return Path.of(".").toAbsolutePath().normalize();
    }

    private CliResponse sendOnce(CliRequest request) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(CliDaemonConfig.HOST, CliDaemonConfig.PORT),
                    CliDaemonConfig.CONNECT_TIMEOUT_MS
            );
            try (DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                 DataInputStream input = new DataInputStream(socket.getInputStream())) {
                output.writeUTF(request.getCommand().name());
                output.writeInt(request.getArguments().size());
                for (String argument : request.getArguments()) {
                    output.writeUTF(argument);
                }
                output.flush();

                int exitCode = input.readInt();
                int lineCount = input.readInt();
                List<String> lines = new ArrayList<>(lineCount);
                for (int index = 0; index < lineCount; index++) {
                    lines.add(input.readUTF());
                }
                return new CliResponse(exitCode, lines);
            }
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
