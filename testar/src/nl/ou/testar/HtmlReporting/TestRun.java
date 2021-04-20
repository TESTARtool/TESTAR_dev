package nl.ou.testar.HtmlReporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jdk.jfr.Timespan;
import nl.ou.testar.HtmlReporting.XMLSequenceReport.CommandLine;
import nl.ou.testar.HtmlReporting.XMLSequenceReport.Filter;

public class TestRun {
    public int randomseed;
    public int asserts = 0;
    public int warnings = 0;
    public int skipped = 0;
    public int inconclusive = 0;
    public int failed = 0;
    public int passed = 0;
    public int total = 0;
    public long duration;
    public Date startTime;
    public Date endTime;
    public String label = "testLabel";
    public String result = "Passed";
    public int testcaseCount = 0;
    public String fullName = "full.name";
    public String Name = "name";
    public int Id = 1;

    public CommandLine commandLine;
    public Filter filter;
    public List<TestSuite> testCase = new LinkedList<TestSuite>();

    public void addTestSuite(TestSuite suite) {
        total += suite.testcaseCount;
        testcaseCount += suite.testcaseCount;
        suite.setTestRun(this);

        testCase.add(suite);
    }

    public TestRun() {
        randomseed = Math.abs(new Random().nextInt());
        startTime = new Date();
        endTime = new Date();
    }

    private String testSuite() {
        String res = "";
        if (testCase.size() > 0)
        {
            
           for (int i=0; i<testCase.size(); i++)
           {
               res += testCase.get(i);
           }
           
        }
        return res;
    }

    public String toString() {
        String base = "<test-run id=\"%d\" name=\"%s\" fullname=\"%s\" testcasecount=\"%d\" result=\"%s\" label=\"%s\" start-time=\"%s\" end-time=\"%s\" duration=\"%d\" total=\"%d\" passed=\"%d\" failed=\"%d\" inconclusive=\"%d\" skipped=\"%d\" warnings=\"%d\" asserts=\"%d\" random-seed=\"%d\">"
                + commandLine + "\n" + filter +  testSuite()+"</test-run>";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
        String res = String.format(base, Id, Name, fullName, testcaseCount, result, label, dateFormat.format(startTime), dateFormat.format(endTime),
                duration, total, passed, failed, inconclusive, skipped, warnings, asserts, randomseed);
        return res;
    }

    void Update() {
        endTime = new Date();
        duration = endTime.getTime()-startTime.getTime();
        total = 0;
        failed = 0;
        skipped = 0;
        warnings=0;
        asserts=0;
        for (int i=0; i<testCase.size(); i++)
        {
            TestSuite tc = testCase.get(i);
            failed += tc.failed;
            total += tc.testcaseCount;
            skipped += tc.skipped;
            

        }
    }
}