package nl.ou.testar.HtmlReporting;

import java.util.LinkedList;
import java.util.List;

public class TestSuite {
    List<TestCase> testCases = new LinkedList<TestCase>();

    private TestRun testrun;
    public TestSuite() {
        
    }
     
    public void addTestCase(TestCase geval)
    {
        geval.setTestSuite(this);
        this.testCases.add(geval);
        testcaseCount = testCases.size();
        
    }

    void Update() {
        // Do counting of stats of different testcases
        testcaseCount= testCases.size();
        passed = testCases.size();
        testrun.Update();
    }

    public String id="1218";
    public int testcasecount=1;
    public String type = "Assembly";
    public String name="a";
    public String fullNamel="b";
    public String runState = "Runnable";
    public String result = "Passed";
    public String label="testLabel";
    public int passed=1;
    public int failed=0;
    public int skipped=0;


    private String getTestCases() {
        String res = "";
        if (testCases.size() >0)
        {
            for (int i=0; i< testCases.size(); i++)
            {
                TestCase tc= testCases.get(i);
                res += tc;
                switch(tc.result)
                {
                    case "Passed": passed +=1; break;
                    case "Failed": failed +=1; break;
                    case "Skipped": skipped+=1; break;

                }
                
            }
        }
        return res;
    }
    public String toString() {
        String testCasesStr = getTestCases();
        return String.format("<test-suite id=\"%s\" testcasecount=\"%d\" type=\"%s\" name=\"%s\" fullname=\"%s\" runstate=\"%s\" result=\"%s\" label=\"%s\" passed=\"%d\" skipped=\"%d\" failed=\"%d\">"+testCasesStr+"</test-suite>",
        id,
        testcaseCount,
        type,
        name,
        fullNamel,
        runState,
        result,
        label,
        passed,skipped,failed)
        ;
    }
    public int testcaseCount;
    
    void setTestRun(TestRun testRun) {
        this.testrun = testRun;
    }
}