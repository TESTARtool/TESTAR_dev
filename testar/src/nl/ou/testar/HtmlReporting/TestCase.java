package nl.ou.testar.HtmlReporting;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class TestCase {

    private Map<String, String> attachment =new HashMap<String,String>();

    public String id="12";
    public String Name="name2";
    public String fullName="full1.name2";
    public String methodName="method";
    public String className="class";
    public String runState="Runnable";
    public int seed;
    public String result = "Passed";
    public Date startTime;
    public Date endTime;
    public long duration;
    public int asserts=0;

    public TestCase () {
        seed = Math.abs( new Random().nextInt());
        startTime = new Date();
        endTime = new Date();
        duration = 0;
        //suite.Update();
    }

    public void addAttachment(String fn, String desc) {
        attachment.put(fn, desc);
    }
    private String attachmentsToString() {
        String result ="";
        if (attachment.size() > 0)
        {
            result = "><attachments>";
            for (Entry<String, String> v :attachment.entrySet()){
                result += String.format("<attachment><filePath>{0}</filePath><description>{1}</description></attachment>",v.getKey(), v.getValue());
            }
            result += "</attachments>\n</test-case>";
        } else {
            result = "/>";
        }
        return result;
    }
    public String runstate = "Runnable";
    public String toString(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String res = String.format("<test-case id=\"%s\" name=\"%s\" fullname=\"%s\" methodname=\"%s\" classname=\"%s\" runstate=\"%s\" seed=\"%d\" result=\"%s\" start-time=\"%s\" end-time=\"%s\" duration=\"%d\" asserts=\"%d\"%s",
        id,
        Name,
        fullName,
        methodName,
        className,
        runState,
        seed,
        result,
        dateFormat.format(startTime),
        dateFormat.format(endTime),
        duration,
        asserts,
        attachmentsToString()
        );
        return res;
    }
    private TestSuite suite;

    void setTestSuite(TestSuite testSuite) {
        this.suite = testSuite;
        suite.Update();
    }
}