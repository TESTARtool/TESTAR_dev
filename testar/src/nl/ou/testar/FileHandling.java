package nl.ou.testar;

import es.upv.staq.testar.serialisation.LogSerialiser;
import org.fruit.Util;
import org.fruit.alayer.State;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.io.*;

import static org.fruit.alayer.Tags.SystemState;

public class FileHandling {

    //TODO move away from abstract, to helper class, call from Default protocol with a setting to turn on/off
    /**
     * Creates a file out of the given state.
     * could be more interesting as XML instead of Java serialization
     * @param state the SUT's current state
     * @param file  File containing snapshot
     */
    public static void saveStateSnapshot(final State state, File file) {
        try {
                //System.out.println(Utils.treeDesc(state, 2, Tags.Role, Tags.Desc, Tags.Shape, Tags.Blocked));
                Taggable taggable = new TaggableBase();
                taggable.set(SystemState, state);
                LogSerialiser.log("Saving state snapshot...\n", LogSerialiser.LogLevel.Debug);
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                oos.writeObject(taggable);
                oos.close();
                LogSerialiser.log("Saved state snapshot to " + file.getAbsolutePath() + "\n", LogSerialiser.LogLevel.Info);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Making a log file of a sequence.
     * @param page  the page that will be written to the output file
     * @param outputFile the output file
     * @param logLevel level of logging
     */
    public static void saveReportPage(String page, File outputFile, int logLevel) {
        try {
            LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile))),logLevel);
        } catch (NoSuchTagException e3) {
            e3.printStackTrace();
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        }
        LogSerialiser.log(page, LogSerialiser.LogLevel.Critical);
        LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();
    }

    public static void saveReport(String[] reportPages, String generatedSequence, String outputDir, int logLevel) {
        saveReportPage(reportPages[0], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "clusters" + ".log"), logLevel);
        saveReportPage(reportPages[1], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "testable" + ".log"), logLevel);
        saveReportPage(reportPages[2], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "curve" + ".log"), logLevel);
        saveReportPage(reportPages[3], new File(outputDir + File.separator + "logs" + File.separator + generatedSequence + "_" + "stats" + ".log"), logLevel);
    }

    public static void copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict, String outputDir) {
        String targetFolder = "";
        final double sev = verdict.severity();
        if (sev == Verdict.SEVERITY_OK) {
            targetFolder = "sequences_ok";
        } else if (sev == Verdict.SEVERITY_WARNING) {
            targetFolder = "sequences_warning";
        } else if (sev == Verdict.SEVERITY_SUSPICIOUS_TITLE) {
            targetFolder = "sequences_suspicioustitle";
        } else if (sev == Verdict.SEVERITY_NOT_RESPONDING) {
            targetFolder = "sequences_unresponsive";
        } else if (sev == Verdict.SEVERITY_NOT_RUNNING) {
            targetFolder = "sequences_unexpectedclose";
        } else if (sev == Verdict.SEVERITY_FAIL) {
            targetFolder = "sequencces_fail";
        } else {
            targetFolder = "sequences_other";
        }
        LogSerialiser.log("Copying classified sequence (\"" + generatedSequence + "\") to " + targetFolder + " folder...\n", LogSerialiser.LogLevel.Info);
        try {
            Util.copyToDirectory(currentSeq.getAbsolutePath(),
                    outputDir + File.separator + targetFolder,
                    generatedSequence,
                    true);
        } catch (NoSuchTagException e) {
            LogSerialiser.log("No such tag exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
        } catch (IOException e) {
            LogSerialiser.log("I/O exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
        }
        LogSerialiser.log("Copied classified sequence to output <" + targetFolder + "> directory!\n", LogSerialiser.LogLevel.Debug);
    }
}
