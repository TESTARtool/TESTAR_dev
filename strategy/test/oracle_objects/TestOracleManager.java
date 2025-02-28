package oracle_objects;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.io.*;

public class TestOracleManager
{
    String grammarOracleFile = "test_oracle.txt";
    
    /**
     * Create a temporal test_oracle.log file for all the unit tests
     */
    @BeforeClass
    public static void createLogOracleFile()
    {
        try
        {
            File file = File.createTempFile("test_oracle", ".txt");
            file.deleteOnExit();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Clean the content of the temporal test_oracle.log file before each unit test
     */
    @Before
    public void cleanLogOracleFile()
    {
        try
        {
            new FileOutputStream(grammarOracleFile).close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void addTextToFile(String text) {
        try (FileOutputStream fos = new FileOutputStream(grammarOracleFile, true)) {
            // Convert the text to bytes
            byte[] bytes = text.getBytes();
            
            // Write the bytes to the file and add a new line
            fos.write(bytes);
            fos.write(System.lineSeparator().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String fileContent() {
        String content = "";
        try (BufferedReader br = new BufferedReader(new FileReader(grammarOracleFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                content = content + line;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return content;
    }
    
    @Test
    public void test_temp_file()
    {
        String instruction = "ORACLE 'basic oracle' { CHECK PROP KEY IS 'WebId' }";
        addTextToFile(instruction);
        String fileContent = fileContent();
        System.out.println(fileContent);
    }
    
    /**
     * ORACLE
     *     	PROP 'WebId' IS 'formButton',
     *     	PROP 'WebTitle' IS 'Send payment'
     */
    @Test
    public void test_oracle_manager()
    {
        StateStub  state  = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        
        widget.set(Tags.Role, Roles.Button);
        widget.set(WdTags.WebId, "formButton");
        widget.set(WdTags.WebTitle, "Send payment");
        
        String instruction = "ORACLE 'basic oracle' { CHECK PROP KEY IS 'WebId' }";
    
        addTextToFile(instruction);
        
        OracleManager manager = new OracleManager(grammarOracleFile);
        
        Assert.isFalse(manager.runOracles(state)); //todo: fix so it checks for true
    }
    
}