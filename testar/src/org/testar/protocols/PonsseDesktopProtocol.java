package org.testar.protocols;

import org.fruit.Util;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.windows.UIATags;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class PonsseDesktopProtocol extends DesktopProtocol {
    protected boolean widgetWithAutomationIdFound(String automationId, State state, SUT system, int maxNumberOfRetries){
        boolean uiElementFound = false;
        int numberOfRetries = 0;
        while(!uiElementFound&&numberOfRetries<maxNumberOfRetries){
            for(Widget widget:state){
                if(widget.get(UIATags.UIAAutomationId, "NoAutomationIdAvailable").equalsIgnoreCase(automationId)){
                    System.out.println("DEBUG: widget with automationId="+ automationId +" found!");
                    return true;
                }
            }
            if(!uiElementFound){
                Util.pause(1);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    protected boolean widgetWithAutomationIdFound(String automationId, State state){
        for(Widget widget:state){
            if(widget.get(UIATags.UIAAutomationId, "NoAutomationIdAvailable").equalsIgnoreCase(automationId)){
                //System.out.println("DEBUG: widget with automationId="+ automationId +" found!");
                return true;
            }
        }
        return false;
    }

    /**
     * This method waits until the widget with given title is found or retry limit is reached
     * If widget is found, it clicks left mouse button on it and returns true
     * Else returns false
     *
     * @param title
     * @param state
     * @param system
     * @param maxNumberOfRetries
     * @return
     */
    protected boolean waitAndClickButtonByTitle(String title, State state, SUT system, int maxNumberOfRetries){
        boolean uiElementFound = false;
        int numberOfRetries = 0;
        while(!uiElementFound&&numberOfRetries<maxNumberOfRetries){
            for(Widget widget:state){
                if(widget.get(Tags.Title, "NoTitleAvailable").equalsIgnoreCase(title)){
                    uiElementFound=true;
                    StdActionCompiler ac = new AnnotatingActionCompiler();
                    System.out.println("DEBUG: waitAndClickButtonByTitle: left mouse click on " + title);
                    executeAction(system,state,ac.leftClickAt(widget));
                    Util.pause(1);
                    return true;
                }
            }
            if(!uiElementFound){
                Util.pause(1);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    /**Ponsse: This method clicks a button in Opti5G UI*/
    protected boolean waitAndClickButtonByAutomationId(String automationId, State state, SUT system, int maxNumberOfRetries){
        boolean uiElementFound = false;
        int numberOfRetries = 0;
        while(!uiElementFound&&numberOfRetries<maxNumberOfRetries){
            for(Widget widget:state){
                if(widget.get(UIATags.UIAAutomationId, "NoAutomationIdAvailable").equalsIgnoreCase(automationId)){
                    uiElementFound=true;
                    StdActionCompiler ac = new AnnotatingActionCompiler();
                    System.out.println("DEBUG: waitAndClickButtonByAutomationId: left mouse click on " + automationId);
                    executeAction(system,state,ac.leftClickAt(widget));
                    Util.pause(1);
                    return true;
                }
            }
            if(!uiElementFound){
                Util.pause(1);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    /**Ponsse: This method types text to UI element */
    protected boolean waitAndTypeTextByAutomationId(String automationId, String text, State state, SUT system, int maxNumberOfRetries){
        boolean uiElementFound = false;
        int numberOfRetries = 0;
        while(!uiElementFound&&numberOfRetries<maxNumberOfRetries){
            for(Widget widget:state){
                if(widget.get(UIATags.UIAAutomationId, "NoAutomationIdAvailable").equalsIgnoreCase(automationId)){
                    uiElementFound=true;
                    StdActionCompiler ac = new AnnotatingActionCompiler();
                    System.out.println("DEBUG: waitAndTypeTextByAutomationId: left mouse click on " + automationId);
                    executeAction(system,state,ac.clickTypeInto(widget, text, true));
                    Util.pause(1);
                    return true;
                }
            }
            if(!uiElementFound){
                Util.pause(1);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    /**
     * Using SikuliX library to click on text on screen
     * @param textToFindOrImagePath
     */
    protected  static void executeClickOnTextOrImagePath(String textToFindOrImagePath){
        Screen sikuliScreen = new Screen();
        try {
            //System.out.println("DEBUG: sikuli clicking on text (or image path): "+textToFindOrImagePath);
            sikuliScreen.click(textToFindOrImagePath);
        } catch (FindFailed findFailed) {
            findFailed.printStackTrace();
        }
    }

    protected  static boolean textOrImageExists(String textOrImagePath){
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
    protected  static Region getRegionOfTextOrImage(String textOrImagePath){
        Screen sikuliScreen = new Screen();
        Pattern pattern = new Pattern(textOrImagePath).similar(new Float(0.90));
        Region region = sikuliScreen.exists(pattern);
        return region;
    }

}
