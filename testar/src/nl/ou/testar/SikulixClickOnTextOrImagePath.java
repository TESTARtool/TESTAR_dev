package nl.ou.testar;
import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.sikuli.script.Screen;
import org.sikuli.script.FindFailed;

public class SikulixClickOnTextOrImagePath extends TaggableBase implements Action {

    private String textOrImagePath;

    public SikulixClickOnTextOrImagePath(String textOrImagePath){
        Assert.notNull(textOrImagePath);
        this.textOrImagePath = textOrImagePath;
    }

    public String toString() { return "SikuliX Click on Text (or image path): " + textOrImagePath; }

    public void run(SUT system, State state, double duration){
        try{
            Util.pause(duration);
            Screen sikuliScreen = new Screen();
            try {
                System.out.println("DEBUG: sikuli clicking on text (or image path): "+textOrImagePath);
                sikuliScreen.click(textOrImagePath);
            } catch (FindFailed findFailed) {
                findFailed.printStackTrace();
            }

        }catch(NoSuchTagException tue){
            throw new ActionFailedException(tue);
        }
    }

    @Override
    public String toShortString() {
        return "SikulixClickOnTextOrImage";
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
