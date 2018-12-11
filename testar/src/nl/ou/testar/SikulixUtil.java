package nl.ou.testar;

import org.sikuli.script.*;

import java.util.Iterator;

public class SikulixUtil {

    /**
     * Using SikuliX library to click on text on screen
     * @param textToFindOrImagePath
     */
    public static void executeClickOnTextOrImagePath(String textToFindOrImagePath){
        Screen sikuliScreen = new Screen();
        try {
            //System.out.println("DEBUG: sikuli clicking on text (or image path): "+textToFindOrImagePath);
            sikuliScreen.click(textToFindOrImagePath);
        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }
    }

    public static boolean textOrImageExists(String textOrImagePath){
        if(getRegionOfTextOrImage(textOrImagePath)==null){
            // text or image not found
            return false;
        }
        return true;
    }

    /**
     *
     * @param textOrImagePath
     * @return null if not found
     */
    public static Region getRegionOfTextOrImage(String textOrImagePath){
        Screen sikuliScreen = new Screen();
        Pattern pattern = new Pattern(textOrImagePath).similar(new Float(0.95));
        Region region = sikuliScreen.exists(pattern);
        return region;
    }


    public static boolean textExistsOnScreen(String textToFind){
        Screen sikuliScreen = new Screen();
        try {
            System.out.println("DEBUG: sikuli trying to find text: "+textToFind);
            sikuliScreen.findText(textToFind);
            return true;
        } catch (FindFailed findFailed) {
            return false;
        }
    }

    /**
     * Trying to use SikuliX library to check whether the given text is only once on the screen
     *
     * Does not seem to work, freezes with heavy computing...
     *
     * @param textToFind
     * @return
     */
    public static boolean textExistsExactlyOnceOnScreen(String textToFind){
        Screen sikuliScreen = new Screen();
        try {
            System.out.println("DEBUG: sikuli trying to find text: "+textToFind);
            int numberOfMatches = 0;
            Iterator<Match> it = sikuliScreen.findAllText(textToFind);
            while(((Iterator) it).hasNext()){
                numberOfMatches++;
            }
            System.out.println("Text "+textToFind+" was found "+numberOfMatches+" times on the screen.");
            if(numberOfMatches==1)
                return true;
        } catch (FindFailed findFailed) {
            return false;
        }
        return false;
    }
}
