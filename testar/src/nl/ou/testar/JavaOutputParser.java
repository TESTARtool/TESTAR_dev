package nl.ou.testar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class JavaOutputParser extends PrintStream{
        //ByteArrayOutputStream {
    private String thingToLookFor;
    private boolean thingToLookFound = false;
    private java.io.PrintWriter stdOut;

    public JavaOutputParser(OutputStream out, String thingToLookFor) {
        super(out,true);
        this.thingToLookFor=thingToLookFor;
//        try {
//            stdOut = new java.io.PrintWriter(new java.io.FileWriter(filename));
//        } catch (Exception e) {
//            //TODO handle exception
//        }
    }

    @Override
    public void print(String s)
    {//do what ever you like
        super.print(s);
        if (s.contains(thingToLookFor)) {
            setThingToLookFound(true);
        }
    }

    public boolean isThingToLookFound() {
        return thingToLookFound;
    }

    public void setThingToLookFound(boolean thingToLookFound) {
        this.thingToLookFound = thingToLookFound;
    }

    /**
     * Writes the existing contents of the OutputStream to the
     * logger as a log record.
     *
     * @throws java.io.IOException in case of error

    public void flush() throws IOException {
        String record;
        synchronized (this) {
            super.flush();
            record = this.toString();
            super.reset();

            if (record.length() == 0 || record.equals(lineSeparator)) {
                // avoid empty records
                return;
            }
            stdOut.println(record);
            stdOut.flush();
            if (record.contains(thingToLookFor)) {
                setThingToLookFound(true);
                stdOut.println("DEBUG: Found "+thingToLookFor+" in the output!");
                stdOut.flush();
            }
//            logger.logp(Level.ALL, "", "", record);
        }
    }*/

}
