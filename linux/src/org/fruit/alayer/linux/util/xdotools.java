package org.fruit.alayer.linux.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Utility class that provides functionality to work with xprop.
 */
public class xdotools {

    private static final String PIDFromActiveWindow = "xdotool getactivewindow getwindowpid";
    private static final String NameFromActiveWindow = "xdotool getactivewindow getwindowname";


    /**
     * Tries to retrieve the PID from the currently active window (application).
     * @return If successful the PID of the currently active window (application); -1 otherwise.
     */
    public static int getPIDFromActiveWindow() {


        try {


            // Run the command.
            Process p = Runtime.getRuntime().exec(PIDFromActiveWindow);


            // Get the stream to read the command output from.
            BufferedReader commandOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));


            // Read the output as lines - should only be one.
            String outputLine = commandOutputReader.readLine();

            if (!JavaHelper.isNullOrWhitespace(outputLine) && JavaHelper.tryParseInt(outputLine)) {
                return Integer.parseInt(outputLine);
            }


            System.out.println("Could not parse '" + outputLine + "' to a PID.");


            return -1;


        } catch (IOException e) {
            return -1;
        }


    }


    /**
     * Tries to retrieve the name of the currently active window (application).
     * @return If successful the name of the currently active window (applicaiton); null otherwise.
     */
    public static String getNameFromActiveWindow() {


        try {


            // Run the command.
            Process p = Runtime.getRuntime().exec(NameFromActiveWindow);


            // Get the stream to read the command output from.
            BufferedReader commandOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));


            // Read the output as lines - should only be one.
            return commandOutputReader.readLine();



        } catch (IOException e) {
            return null;
        }


    }


}