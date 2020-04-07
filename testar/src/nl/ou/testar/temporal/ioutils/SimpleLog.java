package nl.ou.testar.temporal.ioutils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleLog {
    BufferedWriter out;
    boolean alsoToConsole;


    public SimpleLog(String fileName) {
        this(fileName,false);
    }
    public SimpleLog(String fileName,boolean alsoToConsole) {

        this.alsoToConsole=alsoToConsole;
        try {
            out = new BufferedWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void append(String str) {
        if (alsoToConsole) {System.out.println(str);}
        try {
            if (str.endsWith("\n")){ out.write(str);
            }
            else{ out.write(str+"\n");}
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}