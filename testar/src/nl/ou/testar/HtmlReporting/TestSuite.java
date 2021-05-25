/***************************************************************************************************
 *
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

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