package org.testar;

import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import nl.ou.testar.FileHandling;
import nl.ou.testar.SutVisualization;
import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.exceptions.*;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.DefaultProtocol;
import org.fruit.monkey.RuntimeControlsProtocol;
import org.fruit.monkey.Settings;

import java.io.*;
import java.util.List;
import java.util.Set;

import static org.fruit.alayer.Tags.*;
import static org.fruit.alayer.Tags.OracleVerdict;
import static org.fruit.monkey.ConfigTags.LogLevel;

public class RecordSequenceProtocol extends RuntimeControlsProtocol {


    private TaggableBase fragment; // Fragment is used for saving a replayable sequence:

    private int sequenceCount;
    protected void setSequenceCount(int sequenceCount){
        this.sequenceCount = sequenceCount;
    }
    protected int getSequenceCount() {
        return sequenceCount;
    }
    protected void incrementSequenceCount(){
        sequenceCount++;
    }

    private String generatedSequence;
    protected String getGeneratedSequenceName() {
        return generatedSequence;
    }

    private File currentSeq;
    public File getCurrentSeq() {
        return currentSeq;
    }
    public void setCurrentSeq(File currentSeq) {
        this.currentSeq = currentSeq;
    }

    private Action lastExecutedAction = null;

    private int actionCount;
    protected final int getActionCount() {
        return actionCount;
    }
    protected void incrementActionCount() {
         actionCount++;
    }
    protected void resetActionCount(){
        actionCount=0;
    }

    /**
     * Method to run TESTAR on Record User Actions Mode.
     * @param system
     */
    protected void runRecordLoop(SUT system) {
        boolean startedRecordMode = false;

        //If system is null, it means that we have started TESTAR from the Record User Actions Mode
        //We need to invoke the SUT & the canvas representation
        if(system == null) {

            synchronized(this){
                OutputStructure.calculateInnerLoopDateString();
                OutputStructure.sequenceInnerLoopCount++;
            }

            preSequencePreparations();


            system = startSystem();
            startedRecordMode = true;
            // this.cv = buildCanvas();
            resetActionCount();
            incrementActionCount();

            //Reset LogSerialiser
            LogSerialiser.finish();
            LogSerialiser.exit();


            //Activate process Listeners if enabled in the test.settings
            /*
            // Is process listener needed for record mode? manual tester should know if it fails or not?
            if(enabledProcessListener)
                processListener.startListeners(system, settings);
             */

            // notify the statemodelmanager
            //TODO capture state model in record mode
            // stateModelManager.notifyTestSequencedStarted();
        }
        //else, SUT & canvas exists (startSystem() & buildCanvas() created from Generate mode)
        else{
            debugPrint("RecordMode: SUT was already started");
        }

        /**
         * Start Record User Action Loop
         */
        while(mode() == Modes.Record && system.isRunning()) {
            debugPrint("RecordMode: first time getState and actions (Why actions too?)");
            State state = getState(system);

            // TODO: check if state has changed or not, otherwise do nothing

            // clearing the visualization:
            //cv.begin(); Util.clear(cv);

            Set<Action> actions = deriveActions(system,state);
            CodingManager.buildIDs(state, actions);

            //notify the state model manager of the new state
            //TODO capture state model in record mode
            //stateModelManager.notifyNewStateReached(state, actions);

			/*
			// Why do we generate actions in record mode???

			if(actions.isEmpty()){
				if (escAttempts >= MAX_ESC_ATTEMPTS){
					LogSerialiser.log("No available actions to execute! Tried ESC <" + MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
				}
				//----------------------------------
				// THERE MUST ALMOST BE ONE ACTION!
				//----------------------------------
				// if we did not find any actions, then we just hit escape, maybe that works ;-)
				Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
				CodingManager.buildEnvironmentActionIDs(state, escAction);
				actions.add(escAction);
				escAttempts++;
			} else
				escAttempts = 0;
			 */

            ActionStatus actionStatus = new ActionStatus();

            //Start Wait User Action Loop to obtain the Action did by the User
            waitUserActionLoop(system, state, actionStatus);

            //Save the user action information into the logs
            if (actionStatus.isUserEventAction()) {

                CodingManager.buildIDs(state, actionStatus.getAction());

                //notify the state model manager of the executed action
                // TODO capture state model in record mode
                //stateModelManager.notifyActionExecution(actionStatus.getAction());

                saveActionInfoInLogs(state, actionStatus.getAction(), "RecordedAction");
                lastExecutedAction = actionStatus.getAction();
                actionCount++;
            }

            /**
             * When we close TESTAR with Shift+down arrow, last actions is detected like null
             */
            if(actionStatus.getAction()!=null) {
                //System.out.println("DEBUG: User action is not null");
                saveActionIntoFragmentForReplayableSequence(actionStatus.getAction(), state, actions);
            }else {
                //System.out.println("DEBUG: User action ----- null");
            }


            //Util.clear(cv);
            //cv.end();
        }

        //If user change to Generate mode & we start TESTAR on Record mode, invoke Generate mode with the created SUT
        /*
        //TODO allow changing from record mode to generate mode
        if(mode() == Modes.Generate && startedRecordMode){
            //Util.clear(cv);
            //cv.end();

            runGenerateOuterLoop(system);
        }
         */

        //If user closes the SUT while in Record-mode, TESTAR will close (or go back to SettingsDialog):
        if(!system.isRunning()){
            this.mode = Modes.Quit;
        }

        if(startedRecordMode && mode() == Modes.Quit){
            // notify the statemodelmanager
            // TODO capture state model in record mode
            // stateModelManager.notifyTestSequenceStopped();
            // notify the state model manager of the sequence end
            //stateModelManager.notifyTestingEnded();

            //Closing fragment for recording replayable test sequence:
            writeAndCloseFragmentForReplayableSequence();

            //Copy sequence file into proper directory:
            classifyAndCopySequenceIntoAppropriateDirectory(Verdict.OK);

            postSequenceProcessing();

            //If we want to Quit the current execution we stop the system
            stopSystem(system);
        }
    }

    protected void initSequenceFile(){
        //Generating the sequence file that can be replayed:
        generatedSequence = getAndStoreGeneratedSequence();
        currentSeq = getAndStoreSequenceFile();
    }


    //	/**
    //	 * Waits for an user UI action.
    //	 * Requirement: Mode must be GenerateManual.
    //	 */
    private void waitUserActionLoop(SUT system, State state, ActionStatus actionStatus){

        debugPrint("RecordMode: waitUserActionLoop started");
        while (mode() == RuntimeControlsProtocol.Modes.Record && !actionStatus.isUserEventAction()){
            if (userEvent != null){
                actionStatus.setAction(mapUserEvent(state));
                actionStatus.setUserEventAction((actionStatus.getAction() != null));
                userEvent = null;
            }
            synchronized(this){
                try {
                    this.wait(100);
                } catch (InterruptedException e) {}
            }
            state = getState(system);
            //cv.begin(); Util.clear(cv);

            //In Record-mode, we activate the visualization with Shift+ArrowUP:
            //if(visualizationOn) SutVisualization.visualizeState(false, markParentWidget, mouse, lastPrintParentsOf, cv,state);

            Set<Action> actions = deriveActions(system,state);
            CodingManager.buildIDs(state, actions);

            //In Record-mode, we activate the visualization with Shift+ArrowUP:
            // why do we need to visualize in record mode???
            // if(visualizationOn) visualizeActions(cv, state, actions);

            //cv.end();
        }
    }

    /**
     * This method creates the name for generated sequence that can be replayed
     * and starts the LogSerialiser for outputting the test sequence
     *
     * @return name of the generated sequence file
     */
    private String getAndStoreGeneratedSequence() {
        //TODO refactor replayable sequences with something better (model perhaps?)

        String sequenceCountDir = "_sequence_" + OutputStructure.sequenceInnerLoopCount;

        String generatedSequenceName = OutputStructure.sequencesOutputDir
                + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir + ".testar";

        String logFileName = OutputStructure.logsOutputDir
                + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir + ".log";

        String screenshotsDirectory = OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir;

        try {
            LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
                            logFileName), true))),
                    settings.get(LogLevel));
        }catch (NoSuchTagException | FileNotFoundException e3) {
            e3.printStackTrace();
        }

        ScreenshotSerialiser.start(OutputStructure.screenshotsOutputDir, screenshotsDirectory);

        return generatedSequenceName;
    }


    /**
     * Saving the action information into the logs
     *
     * @param state
     * @param action
     * @param actionMode
     */
    protected void saveActionInfoInLogs(State state, Action action, String actionMode) {

        //Obtain action information
        String[] actionRepresentation = Action.getActionRepresentation(state,action,"\t");

        //Output/logs folder
        LogSerialiser.log(String.format(actionMode+" [%d]: %s\n%s",
                actionCount,
                "\n @Action ConcreteID = " + action.get(Tags.ConcreteID,"ConcreteID not available") +
                        " AbstractID = " + action.get(Tags.AbstractID,"AbstractID not available") +"\n"+
                        " ConcreteID CUSTOM = " +  action.get(Tags.ConcreteIDCustom,"ConcreteID CUSTOM not available")+
                        " AbstractID CUSTOM = " +  action.get(Tags.AbstractIDCustom,"AbstractID CUSTOM not available")+"\n"+

                        " @State ConcreteID = " + state.get(Tags.ConcreteID,"ConcreteID not available") +
                        " AbstractID = " + state.get(Tags.Abstract_R_ID,"Abstract_R_ID not available") +"\n"+
                        " ConcreteID CUSTOM = "+ state.get(Tags.ConcreteIDCustom,"ConcreteID CUSTOM not available")+
                        " AbstractID CUSTOM = "+state.get(Tags.AbstractIDCustom,"AbstractID CUSTOM not available")+"\n",
                actionRepresentation[0]) + "\n",
                LogSerialiser.LogLevel.Info);
    }



    /**
     * This method creates a temporary file for saving the test sequence (that can be replayed)
     * The name of the temporary file is changed in the end of the test sequence (not in this function)
     *
     * @return temporary file for saving the test sequence
     */
    protected File getAndStoreSequenceFile() {
        LogSerialiser.log("Creating new sequence file...\n", LogSerialiser.LogLevel.Debug);

        String sequenceObject = settings.get(ConfigTags.TempDir)
                + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname
                + "_sequence_" + OutputStructure.sequenceInnerLoopCount + ".testar";

        final File currentSeqObject = new File(sequenceObject);

        try {
            TestSerialiser.start(new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeqObject, true))));
            LogSerialiser.log("Created new sequence file!\n", LogSerialiser.LogLevel.Debug);
        } catch (IOException e) {
            LogSerialiser.log("I/O exception creating new sequence file\n", LogSerialiser.LogLevel.Critical);
        }

        return currentSeqObject;
    }
    //TODO move to ManualRecording helper class??
    //	/**
    //	 * Records user action (for example for Generate-Manual)
    //	 *
    //	 * @param state
    //	 * @return
    //	 */
    protected Action mapUserEvent(State state){
        Assert.notNull(userEvent);
        if (userEvent[0] instanceof MouseButtons){ // mouse events
            double x = ((Double)userEvent[1]).doubleValue();
            double y = ((Double)userEvent[2]).doubleValue();
            Widget w = null;
            try {
                w = Util.widgetFromPoint(state, x, y);
                x = 0.5; y = 0.5;
                if (userEvent[0] == MouseButtons.BUTTON1) // left click
                    return (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
                else if (userEvent[0] == MouseButtons.BUTTON3) // right click
                    return (new AnnotatingActionCompiler()).rightClickAt(w,x,y);
            } catch (WidgetNotFoundException we){
                System.out.println("Mapping user event ... widget not found @(" + x + "," + y + ")");
                return null;
            }
        } else if (userEvent[0] instanceof KBKeys) // key events
            return (new AnnotatingActionCompiler()).hitKey((KBKeys)userEvent[0]);
        else if (userEvent[0] instanceof String){ // type events
            if (lastExecutedAction == null)
                return null;
            List<Finder> targets = lastExecutedAction.get(Tags.Targets,null);
            if (targets == null || targets.size() != 1)
                return null;
            try {
                Widget w = targets.get(0).apply(state);
                return (new AnnotatingActionCompiler()).clickTypeInto(w,(String)userEvent[0], true);
            } catch (WidgetNotFoundException we){
                return null;
            }
        }
        return null;
    }

    /**
     * Saving the action into the fragment for replayable sequence
     *
     * @param action
     */
    protected void saveActionIntoFragmentForReplayableSequence(Action action, State state, Set<Action> actions) {
        // create fragment
        fragment = new TaggableBase();
        fragment.set(ExecutedAction, action);
        fragment.set(ActionSet, actions);
        fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
        fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
        fragment.set(SystemState, state);
        fragment.set(OracleVerdict, getVerdict(state));

        //Find the target widget of the current action, and save the title into the fragment
        if (state != null && action.get(Tags.OriginWidget, null) != null){
            fragment.set(Tags.Title, action.get(Tags.OriginWidget).get(Tags.Title, ""));
        }

        LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
        TestSerialiser.write(fragment);
    }



    /**
     * Wait until fragments have been written then close the test serialiser
     */
    protected void writeAndCloseFragmentForReplayableSequence() {
        //Wait since TestSerialiser and ScreenshotSerialiser write all fragments/src on sequence File
        while(!TestSerialiser.isSavingQueueEmpty() || !ScreenshotSerialiser.isSavingQueueEmpty()) {
            synchronized (this) {
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //closing ScreenshotSerialiser and TestSerialiser
        ScreenshotSerialiser.finish();
        ScreenshotSerialiser.exit();
        TestSerialiser.finish();
        TestSerialiser.exit();

        LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
        LogSerialiser.log("Sequence " + sequenceCount + " finished.\n", LogSerialiser.LogLevel.Info);
    }


    protected void classifyAndCopySequenceIntoAppropriateDirectory(Verdict finalVerdict){
        if (!settings().get(ConfigTags.OnlySaveFaultySequences) ||
                finalVerdict.severity() >= settings().get(ConfigTags.FaultThreshold)) {

            LogSerialiser.log("Saved generated sequence (\"" + generatedSequence + "\")\n", LogSerialiser.LogLevel.Info);

            FileHandling.copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
        }
    }

    protected void deleteTempSequenceFile(){
        //Delete the temporally testar file
        try {
            Util.delete(currentSeq);
        } catch (IOException e2) {
            LogSerialiser.log("I/O exception deleting <" + currentSeq + ">\n", LogSerialiser.LogLevel.Critical);
        }
    }

    @Override
    public void mouseMoved(double x, double y) {

    }

    @Override
    protected void initialize(Settings settings) {

    }

    @Override
    protected void initTestSession() {

    }

    @Override
    protected void preSequencePreparations() {

    }

    @Override
    protected SUT startSystem() throws SystemStartException {
        return null;
    }

    @Override
    protected void beginSequence(SUT system, State state) {

    }

    @Override
    protected State getState(SUT system) throws StateBuildException {
        return null;
    }

    @Override
    protected Verdict getVerdict(State state) {
        return null;
    }

    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        return null;
    }

    @Override
    protected Action selectAction(State state, Set<Action> actions) {
        return null;
    }

    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        return false;
    }

    @Override
    protected boolean moreActions(State state) {
        return false;
    }

    @Override
    protected boolean moreSequences() {
        return false;
    }

    @Override
    protected void finishSequence() {

    }

    @Override
    protected void stopSystem(SUT system) {

    }

    @Override
    protected void postSequenceProcessing() {

    }

    @Override
    protected void closeTestSession() {

    }

    @Override
    public void run(Settings argument) {

    }
}
