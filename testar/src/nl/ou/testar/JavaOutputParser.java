package nl.ou.testar;

import java.io.OutputStream;
import java.io.PrintStream;

public class JavaOutputParser extends PrintStream{
    private String stringToLookFor;
    private boolean stringToLookFound = false;
    private java.io.PrintWriter stdOut;

    /**
     * Constructor for output parser that intercepts OutputStream out, but does not change it's behavior
     *
     * The idea is to intercept StdOut / Error streams to look for Exceptions (or other String you want to look for)
     *
     * @param out Existing OutputStream that you want to intercept
     * @param stringToLookFor String that you want to look for in the OutputStream
     */
    public JavaOutputParser(OutputStream out, String stringToLookFor) {
        super(out,true);
        this.stringToLookFor=stringToLookFor;
    }

    @Override
    public void print(String s)
    {
        // Still writing to the StdOut / Error as implemented in super
        super.print(s);
        // checking if the StdOut / Error contains the String we are looking for:
        if (s.contains(stringToLookFor)) {
            setStringToLookFound(true);
        }
    }

    /**
     * Getter for stringToLookFound boolean
     *
     * Allows checking if the String stringToLookFor specified in constructor has been found from StdOut / Error
     *
     * If it has been found, you have to reset the boolean by setStringToLookFound(false)
     *
     * @return true if the stringToLookFor specified in constructor has been found
     */
    public boolean isStringToLookFound() {
        return stringToLookFound;
    }

    /**
     * Setter for stringToLookFound boolean
     *
     * @param stringToLookFound
     */
    public void setStringToLookFound(boolean stringToLookFound) {
        this.stringToLookFound = stringToLookFound;
    }

}
