package nl.ou.testar.temporal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

public class StreamConsumer implements Runnable
{
    private InputStream is;
    private String type;
    private Thread workerThread;
    private final AtomicBoolean running = new AtomicBoolean(false);


    public StreamConsumer(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    public void start() {
        workerThread = new Thread(this);
        workerThread.start();
    }

    public void stop() {
        running.set(false);
    }

    public void run()

    {
        try {
            running.set(true);
            while (running.get()) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                    System.out.println(type + ">" + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
