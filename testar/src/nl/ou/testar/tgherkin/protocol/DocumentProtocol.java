package nl.ou.testar.tgherkin.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.DocumentBuilder;
import nl.ou.testar.tgherkin.TgherkinErrorListener;
import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.model.ActionWidgetProxy;
import nl.ou.testar.tgherkin.model.Document;
import nl.ou.testar.utils.report.Reporter;

/**
 * DocumentProtocol for Tgherkin documents.
 *
 */
public class DocumentProtocol extends ClickFilterLayerProtocol implements ActionWidgetProxy{
	
	private Document document;
	private boolean documentActionExecuted;
	private boolean actionSwitchesOn;
	
	/**
     * Constructor.
     */
	public DocumentProtocol(){
		super();
	}
	
	/** 
	 * Initialize document protocol.
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	protected void initialize(Settings settings){
		super.initialize(settings);
		if (documentExecutionMode()) {
			String fileName = settings.get(ConfigTags.TgherkinDocument);
			ANTLRInputStream inputStream;
			try {
				inputStream = new ANTLRInputStream(new FileInputStream(fileName));
			}catch (IOException e) {
				throw new TgherkinException("Unable to open character stream for Tgherkin file: " + fileName);
			}        
			TgherkinLexer lexer = new TgherkinLexer(inputStream);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
		    TgherkinParser parser = new TgherkinParser(tokens);
			TgherkinErrorListener errorListener = new TgherkinErrorListener();
			parser.removeErrorListeners();
			parser.addErrorListener(errorListener);
			document = new DocumentBuilder().visitDocument(parser.document());
			List<String> errorList = errorListener.getErrorList();
			if (errorList.size() == 0) {
				// post-processing check
				errorList = document.check();
			}
			if (errorList.size() != 0) {
				for(String errorText : errorList) {
					LogSerialiser.log(errorText, LogSerialiser.LogLevel.Info);
				}
				throw new TgherkinException("Invalid Tgherkin document, see log for details");
			}
			if (settings().get(ConfigTags.GenerateTgherkinReport)){
				// report header
				Report.report();			
			}
		}
	}
	
	/**
	 * Begin sequence.
	 * This method is invoked each time TESTAR starts to generate a new sequence.
	 */
	protected void beginSequence(){
		super.beginSequence();
		if (documentExecutionMode()) {
			document.beginSequence();
		}
	}
	
	/**
	 * Get verdict.
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 * @param state the SUT's current state
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	protected Verdict getVerdict(State state){
		Verdict verdict = super.getVerdict(state);
		if (documentExecutionMode()) {
			if (documentActionExecuted) {
				if (verdict.severity() < settings().get(ConfigTags.FaultThreshold)) {
					// no fault yet: determine document verdict
					verdict = document.getVerdict(state, settings());
				}
			}
			if (settings().get(ConfigTags.GenerateTgherkinReport)){
				Report.appendReportDetail(Report.Column.VERDICT, verdict.toString());
			}
		}
		return verdict;				
	}

	/**
	 * Derive actions.
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return a set of actions
	 * @throws ActionBuildException 
	 */
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		ReportUtils.reportState(state, getSequenceCount(), getActionCount());
		// unwanted processes, force SUT to foreground, ... actions automatically derived!
		Set<Action> actions = super.deriveActions(system,state);
		if (documentExecutionMode()) {		
			if (settings().get(ConfigTags.GenerateTgherkinReport)){
				Report.appendReportDetail(Report.Column.PRE_GENERATED_DERIVED_ACTIONS,"" + actions.size());
			}
			// if an action switch is on then do not process document step
			if (!checkActionSwitches()) {
				actions.addAll(document.deriveActions(state, settings(), this));
			}
		}
		return actions;		
	}

	/**
	 * Select one of the possible actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of available actions
	 * @return the selected action (non-null!)
	 */
	protected Action selectAction(State state, Set<Action> actions){ 
		Action action = super.selectAction(state, actions);
		if (documentExecutionMode() && action != null && settings().get(ConfigTags.GenerateTgherkinReport)) {		
			String data = Util.toString((Object)action.get(Tags.Desc, null));
			Report.appendReportDetail(Report.Column.SELECTED_ACTION,data);			
			data = action.toString();
			data = data.replaceAll("(\\r|\\n|\\t)", "");
			Report.appendReportDetail(Report.Column.SELECTED_ACTION_DETAILS,data);
		}
		return action;
	}

	/**
	 * Execute the selected action.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	protected boolean executeAction(SUT system, State state, Action action){
		if (documentExecutionMode() && !actionSwitchesOn) {
			documentActionExecuted = true;
		}
		return super.executeAction(system, state, action);
	}
	
	/**
	 * Determine whether more actions should be executed.
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You could stop the sequence's generation after a given amount of executed
	 * actions or after a specific time etc.
	 * @param state the SUT's current state
	 * @return  if <code>true</code> continue generation, else stop
	 */
	protected boolean moreActions(State state) {
		if (documentExecutionMode()) {
			documentActionExecuted = false;
			if (settings().get(ConfigTags.GenerateTgherkinReport)){
				Report.appendReportDetail(Report.Column.SEQUENCE_NR,"" + sequenceCount());
				Report.appendReportDetail(Report.Column.ACTION_NR,"" + (actionCount() - 1));
				Report.report();			
			}
			return super.moreActions(state) && document.moreActions(settings());
		}
		return super.moreActions(state);
	}


	/** 
	 * Finish sequence.
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 * @param recordedSequence the recorded sequence
	 */
	protected void finishSequence(File recordedSequence){
		super.finishSequence(recordedSequence);
		if (documentExecutionMode()) {				
			// reset document if scenarios should be repeated until number of sequences has been reached
			if (settings().get(ConfigTags.RepeatTgherkinScenarios) && super.moreSequences() && !document.moreSequences()) {
				document.reset();
			}
		}
	}


	/**
	 * Determine whether more sequences should be executed.
	 * TESTAR uses this method to determine when to stop the entire test.
	 * You could stop the test after a given amount of generated sequences or
	 * after a specific time etc.
	 * @return  if <code>true</code> continue test, else stop	 
	 */
	protected boolean moreSequences() {
		if (documentExecutionMode()) {
			boolean result = super.moreSequences() && document.moreSequences();
			if (!result && settings().get(ConfigTags.GenerateTgherkinReport)) {
				Reporter.getInstance().finish();			
			}
			return result;
		}
		return super.moreSequences();
	}
	
	@Override
	// change visibility from protected to public
	public boolean isUnfiltered(Widget w){
		return super.isUnfiltered(w);
	}

	@Override
	// change visibility from protected to public
	public boolean isClickable(Widget w){
		return super.isClickable(w);
	}
	
	@Override
	// change visibility from protected to public
	public boolean isTypeable(Widget w){
		return super.isTypeable(w);
	}
	
    @Override
	// change visibility from protected to public
    public String getRandomText(Widget w){
    	return super.getRandomText(w);
    }
    
    @Override
	// change visibility from protected to public
	public List<Widget> getTopWidgets(State state){
		return super.getTopWidgets(state);
	}
    
    // change visibility from protected to public    
    public int getSequenceCount(){
    	return super.sequenceCount();
    }

    // change visibility from protected to public    
    public int getActionCount(){
    	return super.actionCount();
    }
	
    private boolean documentExecutionMode() {
		return mode() == Modes.Generate || mode() == Modes.GenerateDebug;
	}

    private boolean checkActionSwitches() {
    	if (forceKillProcess != null || forceToForeground || forceNextActionESC) {
    		actionSwitchesOn = true;
    	}else {
    		actionSwitchesOn = false;
    	}
    	return actionSwitchesOn;		
	}

}