package nl.ou.testar.HtmlReporting;

import java.io.Console;
import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.testar.OutputStructure;



import nl.ou.testar.a11y.reporting.HTMLReporter;

public class XMLSequenceReport implements Reporting {

    class CommandLine {
        public String CommandLine;

        public CommandLine(String commandLine)
        {
            this.CommandLine = commandLine;
        }

        public String toString() {
            return "<command-line>" + CommandLine + "</command-line>";
        }
    }

    class Filter {
        public String toString() {
            return "<filter />";
        }
    }

    private PrintWriter out;

    private static final String REPORT_FILENAME_MID = "_sequence_";
    private static String REPORT_FILENAME_AFT = ".xml";

    public XMLSequenceReport() {
        try {
            // TODO put filename into settings, name with sequence number
            // creating a new file for the report
            String filename = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString
                    + "_" + OutputStructure.executedSUTname + REPORT_FILENAME_MID
                    + OutputStructure.sequenceInnerLoopCount + REPORT_FILENAME_AFT;

            out = new PrintWriter(filename, HTMLReporter.CHARSET);
            testRun.commandLine = new CommandLine("commando");
            testRun.filter = new Filter();
            System.out.println("XML Sequence report initialized filename = "+filename);
            /// for(String s:HEADER){
            // write(s);
            // }
            // write("<h1>TESTAR execution sequence report for sequence
            /// "+OutputStructure.sequenceInnerLoopCount+"</h1>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TestSuite activeSuite;
    @Override
    public void addSequenceStep(State state, String actionImagePath) {
        System.out.println("AddSequence Step "+actionImagePath);
        activeSuite = new TestSuite();
       TestCase tc = new TestCase();
       tc.addAttachment(actionImagePath, "Beschrijving");
       testRun.addTestSuite(activeSuite);
        activeSuite.addTestCase(tc);
        

        out.write(testRun.toString());
        out.flush();


    }

    @Override
    public void addState(State state) {
        System.out.println("addState");
        activeSuite = new TestSuite();
        testRun.addTestSuite(activeSuite);
        // TODO Auto-generated method stub

    }

    @Override
    public void addActions(Set<Action> actions) {
        System.out.println("addActions");
        if (activeSuite == null)
        {
            System.out.println("ActiveSuite is null");
            activeSuite = new TestSuite();
            testRun.addTestSuite(activeSuite);
        }
        
       

    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        System.out.println("addActionsAndUnivisitedActions");

    }

    @Override
    public void addSelectedAction(State state, Action action) {
        System.out.println("addSelectedAction");
        TestCase geval = new TestCase();
        geval.runstate="Running";
        activeSuite.addTestCase(geval);

    }

    @Override
    public void addTestVerdict(Verdict verdict) {
        System.out.println("addTestVerdict "+verdict);
        out.write(testRun.toString());
        out.flush();
    }

    private TestRun testRun = new TestRun();

    @Override
    public void close() {

        System.out.print("Afsluiten file");
        
        out.println(testRun.toString());
        out.flush();
        out.close();

    }

}
