package nl.ou.testar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JavaOutputParser extends ByteArrayOutputStream {
    private String lineSeparator;
    private String thingToLookFor;
    private boolean thingToLookFound = false;
    private java.io.PrintWriter stdOut;

    public JavaOutputParser(String thingToLookFor, String filename) {
        super();
        this.thingToLookFor=thingToLookFor;
        try {
            stdOut = new java.io.PrintWriter(new java.io.FileWriter(filename));
        } catch (Exception e) {
            //TODO handle exception
        }
        lineSeparator = System.getProperty("line.separator");
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
     */
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
            }
//            logger.logp(Level.ALL, "", "", record);
        }
    }

}
