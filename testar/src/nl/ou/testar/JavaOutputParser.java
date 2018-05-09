package nl.ou.testar;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class JavaOutputParser extends PrintStream{
    private String stringToLookFor, matchingOutput = "";
    private boolean stringToLookFound = false;
    private java.io.PrintWriter fileOut = null;

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

    /**
     * Constructor for output parser that intercepts OutputStream out, but does not change it's behavior
     *
     * This constructor writes the content of the OutputStream into a file specified with filename.
     * Note that it will overwrite the file with the next TESTAR run.
     *
     * The idea is to intercept StdOut / Error streams to look for Exceptions (or other String you want to look for)
     *
     * @param out Existing OutputStream that you want to intercept
     * @param stringToLookFor String that you want to look for in the OutputStream
     * @param filename The file where all content of the OutputStream will be written
     * @throws IOException If the filename is invalid, throws IOException
     */
    public JavaOutputParser(OutputStream out, String stringToLookFor, String filename) throws IOException {
        super(out,true);
        this.stringToLookFor=stringToLookFor;
        fileOut = new PrintWriter(filename);
        fileOut.println("Thread: name="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId()+", writing OutputStream to file "+filename+" started.");
        fileOut.flush();
    }

    @Override
    public void print(String s)
    {
        // Still writing to the StdOut / Error as implemented in super
        super.print(s);
        // checking if the StdOut / Error contains the String we are looking for:
        if (s.contains(stringToLookFor)) {
            matchingOutput = s;
            setStringToLookFound(true);
        }
        //if constructor with filename was used, writing all the content of the OutputStream into the file:
        if(fileOut!=null){
            fileOut.println(s);
            fileOut.flush();
        }
    }

    /**
     * Closes the output file, if constructor with filename has been used
     */
    public void close(){
        fileOut.close();
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
        System.out.println("DEBUG: set stringToLookFound="+stringToLookFound);
        this.stringToLookFound = stringToLookFound;
    }

    /**
     * Getter for matchingOutput, that is the String from the output stream that contained the stringToLookFor
     *
     * @return String from the output stream that contained the stringToLookFor
     */
    public String getMatchingOutput() {
        return matchingOutput;
    }
}
