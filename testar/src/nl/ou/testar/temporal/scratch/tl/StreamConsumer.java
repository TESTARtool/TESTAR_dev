package nl.ou.testar.temporal.scratch.tl;

import java.io.*;
class StreamConsumer extends Thread
{
    InputStream is;
    String type;

    StreamConsumer(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    public void run()

    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                System.out.println(type + ">" + line);
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
