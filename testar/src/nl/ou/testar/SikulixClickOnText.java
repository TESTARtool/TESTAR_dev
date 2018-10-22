package nl.ou.testar;
import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.sikuli.script.Screen;
import org.sikuli.script.FindFailed;

public class SikulixClickOnText extends TaggableBase implements Action {

    private String text;

    public SikulixClickOnText(String text){
        Assert.notNull(text);
        this.text = text;
    }

    public String toString() { return "SikuliX Click on Text: " + text; }

    public void run(SUT system, State state, double duration){
        try{
            Util.pause(duration);
            Screen sikuliScreen = new Screen();
            try {
                System.out.println("DEBUG: sikuli clicking on text: "+text);
                sikuliScreen.click(text);
            } catch (FindFailed findFailed) {
                findFailed.printStackTrace();
            }

        }catch(NoSuchTagException tue){
            throw new ActionFailedException(tue);
        }
    }

    @Override
    public String toShortString() {
        return "SikulixClickOnText";
    }


    @Override
    public String toString(Role... discardParameters) {
        return toString();
    }

    @Override
    public String toParametersString() {
        //return "(" + btn.toString() + ")";
        return "";
    }

}
