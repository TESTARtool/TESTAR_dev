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

    /**
     * Returns true if a widget with the given automationId is found.
     * Retries the given number of times, waits 1 second between retries.
     *
     * @param automationId
     * @param state
     * @param system
     * @param maxNumberOfRetries
     * @return
     */
    protected boolean widgetWithAutomationIdFound(String automationId, State state, SUT system, int maxNumberOfRetries){
        boolean uiElementFound = false;
        int numberOfRetries = 0;
        while(!uiElementFound&&numberOfRetries<maxNumberOfRetries){
            for(Widget widget:state){
                if(widget.get(UIATags.UIAAutomationId, "NoAutomationIdAvailable").equalsIgnoreCase(automationId)){
                    //System.out.println("DEBUG: widget with automationId="+ automationId +" found!");
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
     * Returns true if a widget with the given automationId is found.
     *
     * @param automationId
     * @param state
     * @return
     */
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
     * Executes a sequence on Ponsse 5G to login user 'testar'
     *
     * @param state
     * @param system
     */
    protected void executeSequenceLoginTestarUser(State state, SUT system){

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btntestar",state,system,20,1)){
            System.out.println("executeSequenceLoginTestarUser 1: btntestar found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 1: btntestar not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitLeftClickAndTypeIntoWidgetWithMatchingTag(UIATags.UIAAutomationId,"passwordBoxPinBoxCode","1111", state,system,20,1)){
            System.out.println("executeSequenceLoginTestarUser 2: passwordBoxPinBoxCode found, typing '1111'");
        }else{
            System.out.println("ERROR: executeSequenceLoginTestarUser 2: passwordBoxPinBoxCode not found!");
        }

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnLogOn",state,system,20,1)){
            System.out.println("executeSequenceLoginTestarUser 3: btnLogOn found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 3: btnLogOn not found!");
        }
    }

    /**
     * Executes a sequence on Ponsse 5G to create a new user 'testar'
     *
     * @param state
     * @param system
     */
    protected void executeSequenceToCreateTestarUser(State state, SUT system){

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnAddUser",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 1: btnAddUser found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 1: btnAddUser not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"NextButton",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 2: NextButton found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 2: NextButton not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"NextButton",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 3: NextButton found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 3: NextButton not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitLeftClickAndTypeIntoWidgetWithMatchingTag(UIATags.UIAAutomationId,"txtBoxFirstName","testar", state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 4: txtBoxFirstName found, typing 'testar'");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 4: txtBoxFirstName not found!");
        }

        if(waitLeftClickAndTypeIntoWidgetWithMatchingTag(UIATags.UIAAutomationId,"txtBoxLastName","test", state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 5: txtBoxLastName found, typing 'test'");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 5: txtBoxLastName not found!");
        }

        if(waitLeftClickAndTypeIntoWidgetWithMatchingTag(UIATags.UIAAutomationId,"txtBoxUserId","testar@ponsse.com", state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 6: txtBoxUserId found, typing 'testar@ponsse.com'");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 6: txtBoxUserId not found!");
        }

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"NextButton",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 7: NextButton found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 7: NextButton not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"NextButton",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 8: NextButton found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 8: NextButton not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"NextButton",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 9: NextButton found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 9: NextButton not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"NextButton",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 10: NextButton found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 10: NextButton not found!");
        }

        //updating TESTAR state:
        state=getState(system);

        if(waitLeftClickAndTypeIntoWidgetWithMatchingTag(UIATags.UIAAutomationId,"passwordBoxPinBox1","1111", state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 11: passwordBoxPinBox1 found, typing '1111'");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 11: passwordBoxPinBox1 not found!");
        }

        if(waitLeftClickAndTypeIntoWidgetWithMatchingTag(UIATags.UIAAutomationId,"passwordBoxPinBox2","1111", state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 12: passwordBoxPinBox2 found, typing '1111'");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 12: passwordBoxPinBox2 not found!");
        }

        if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"buttonFinish",state,system,20,1)){
            System.out.println("executeSequenceToCreateTestarUser 13: buttonFinish found and clicked");
        }else{
            System.out.println("ERROR: executeSequenceToCreateTestarUser 13: buttonFinish not found!");
        }
    }

    protected void executeSequenceCloseAndLogout(SUT system){
        // pushing back button until in logout screen:
        State state = getState(system);
        System.out.println("stopSystem: trying to gracefully close all the screens and logout");
        while(
                waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnTimeUsageQuery_UnutilizedTimeOtherunutilizedtime",state,system,1,0.5)
                        || widgetWithAutomationIdFound("specificDownTimeReasonWindow", state) && waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnCancelClose",state,system,1,0.5)
                        || widgetWithAutomationIdFound("MessageDialog", state) && waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnOk",state,system,1,0.5)
                        || waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnPopupCancel",state,system,1,0.5)
                        || waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"ExpanderButtonCancel",state,system,1,0.5)
                        || waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"ButtonBack",state,system,1,0.5)
                        || waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnCancelClose",state,system,1,0.5)
                        || waitAndLeftClickWidgetWithMatchingTag(Tags.Title,"Cancel",state,system,1,0.5)
                ){
            System.out.println("Pushing back/close/etc buttons until in logout screen");
            //update TESTAR state:
            state = getState(system);
        }
        while(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"ButtonLogOut",state,system,1,0.5)){
            System.out.println("Pushing logout button");
            //update TESTAR state:
            state = getState(system);
        }
        if(widgetWithAutomationIdFound("btnShutDown", state, system, 1)){
            System.out.println("Shutdown button found - graceful logout successful!");
        }else{
            System.out.println("ERROR: graceful logout failed!");
        }
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
    /* DEPRECATED
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
    */

    /**Ponsse: This method clicks a button in Opti5G UI*/
    /* DEPRECATED
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
    */

    /**Ponsse: This method types text to UI element */
    /* DEPRECATED
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
    */

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
