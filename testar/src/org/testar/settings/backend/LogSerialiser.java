package org.testar.settings.backend;

import java.io.PrintStream;

public class LogSerialiser {

    private LogSerialiser() {}

    public static final class LogLevel {

        private LogLevel() {}

        public static final org.testar.serialisation.LogSerialiser.LogLevel Critical =
            org.testar.serialisation.LogSerialiser.LogLevel.Critical;
        public static final org.testar.serialisation.LogSerialiser.LogLevel Info =
            org.testar.serialisation.LogSerialiser.LogLevel.Info;
        public static final org.testar.serialisation.LogSerialiser.LogLevel Debug =
            org.testar.serialisation.LogSerialiser.LogLevel.Debug;
    }

    public static void start(PrintStream log, int logLevel) {
        org.testar.serialisation.LogSerialiser.start(log, logLevel);
    }

    public static void log(String logS) {
        org.testar.serialisation.LogSerialiser.log(logS);
        WebSocket.broadcast(WebSocket.buildLogMessage(logS));
    }

    public static void log(
        String logS,
        org.testar.serialisation.LogSerialiser.LogLevel logLevel
    ) {
        org.testar.serialisation.LogSerialiser.log(logS, logLevel);
        WebSocket.broadcast(WebSocket.buildLogMessage(logS));
    }

   	public static PrintStream getLogStream(){
        return org.testar.serialisation.LogSerialiser.getLogStream();
    }

    public static void flush() {
        org.testar.serialisation.LogSerialiser.flush();
    }

    public static void finish() {
        org.testar.serialisation.LogSerialiser.finish();
    }

    public static void exit() {
        org.testar.serialisation.LogSerialiser.exit();
    }
}
