/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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


package org.testar.protocols;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.fruit.Environment;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.alayer.webdriver.WdElement;
import org.fruit.alayer.webdriver.WdMouse;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.alayer.windows.Windows;
import org.fruit.monkey.ConfigTags;
import org.testar.OutputStructure;

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.HtmlReporting.HtmlSequenceReport;

public class WebdriverProtocol extends ClickFilterLayerProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    protected HtmlSequenceReport htmlReport;
    protected State latestState;
    
    protected static Set<String> existingCssClasses = new HashSet<>();

    /**
     * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {
        //initializing the HTML sequence report:
        htmlReport = new HtmlSequenceReport();
    }
    
    /**
     * This method is called when TESTAR starts the System Under Test (SUT). The method should
     * take care of
     * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
     * out what executable to run)
     * 2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuratio files etc.)
     * 3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
     * seconds until they have finished loading)
     *
     * @return a started SUT, ready to be tested.
     */
    @Override
    protected SUT startSystem() throws SystemStartException {
    	SUT sut = super.startSystem();

    	// A workaround to obtain the browsers window handle, ideally this information is acquired when starting the
    	// webdriver in the constructor of WdDriver.
    	// A possible solution could be creating a snapshot of the running browser processes before and after
    	if(System.getProperty("os.name").contains("Windows")
    			&& sut.get(Tags.HWND, null) == null) {
    		// Note don't place a breakpoint here since the outcome of the function call will result in the IDE pid and
    		// window handle. The running browser needs to be in the foreground when we reach this part.
    		long hwnd = Windows.GetForegroundWindow();
    		long pid = Windows.GetWindowProcessId(Windows.GetForegroundWindow());
    		// Safe to set breakpoints again.
    		if (WinProcess.procName(pid).contains("chrome")) {
    			sut.set(Tags.HWND, hwnd);
    			sut.set(Tags.PID, pid);
    			System.out.printf("INFO System PID %d and window handle %d have been set\n", pid, hwnd);
    		}
    	}
    	
    	double displayScale = Environment.getInstance().getDisplayScale(sut.get(Tags.HWND, (long)0));

        // See remarks in WdMouse
        mouse = sut.get(Tags.StandardMouse);
        mouse.setCursorDisplayScale(displayScale);

    	return sut;
    }

    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
     * This can be used for example for bypassing a login screen by filling the username and password
     * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuration files etc.)
     */
    @Override
    protected void beginSequence(SUT system, State state) {
    	super.beginSequence(system, state);
    }
    
    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     * @return  the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
    	
    	WdDriver.waitDocumentReady();

    	State state = super.getState(system);

    	if(settings.get(ConfigTags.ForceForeground)
    			&& System.getProperty("os.name").contains("Windows")
    			&& system.get(Tags.PID, (long)-1) != (long)-1 
    			&& WinProcess.procName(system.get(Tags.PID)).contains("chrome") 
    			&& !WinProcess.isForeground(system.get(Tags.PID))){
    		WinProcess.politelyToForeground(system.get(Tags.HWND));
    		LogSerialiser.log("Trying to set Chrome Browser to Foreground... " 
    		+ WinProcess.procName(system.get(Tags.PID)) + "\n");
    	}

    	latestState = state;
    	
    	//Spy mode didn't use the html report
    	if(settings.get(ConfigTags.Mode) == Modes.Spy) {

    		for(Widget w : state) {
    			WdElement element = ((WdWidget) w).element;
    			for(String s : element.cssClasses) {
    				existingCssClasses.add(s);
    			}
    		}
    		
        	return state;
    	}
    	
        //adding state to the HTML sequence report:
        htmlReport.addState(latestState);
        return latestState;
    }

    /**
     * Overwriting to add HTML report writing into it
     *
     * @param state
     * @param actions
     * @return
     */
    @Override
    protected Action preSelectAction(State state, Set<Action> actions){
        // adding available actions into the HTML report:
        htmlReport.addActions(actions);
        return(super.preSelectAction(state, actions));
    }

    /**
     * Execute the selected action.
     * @param system the SUT
     * @param state the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action){
        // adding the action that is going to be executed into HTML report:
        htmlReport.addSelectedAction(state, action);
        return super.executeAction(system, state, action);
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void postSequenceProcessing() {
        htmlReport.addTestVerdict(getVerdict(latestState).join(processVerdict));
        
        String sequencesPath = getGeneratedSequenceName();
        try {
        	sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
        }catch (Exception e) {}
        		
        String status = (getVerdict(latestState).join(processVerdict)).verdictSeverityTitle();
		String statusInfo = (getVerdict(latestState).join(processVerdict)).info();
		
		statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");
		
		//Timestamp(generated by logback.xml) SUTname Mode SequenceFileObject Status "StatusInfo"
		INDEXLOG.info(OutputStructure.executedSUTname
				+ " " + settings.get(ConfigTags.Mode, mode())
				+ " " + sequencesPath
				+ " " + status + " \"" + statusInfo + "\"" );
		
		//Print into command line the result of the execution, useful to work with CI and timestamps
		System.out.println(OutputStructure.executedSUTname
				+ " " + settings.get(ConfigTags.Mode, mode())
				+ " " + sequencesPath
				+ " " + status + " \"" + statusInfo + "\"" );
    }
    
    @Override
	protected void finishSequence(){
    	//With webdriver version we don't use the call SystemProcessHandling.killTestLaunchedProcesses
	}

    @Override
    protected void stopSystem(SUT system) {
    	if(settings.get(ConfigTags.Mode) == Modes.Spy) {

    		try {
    			
    			File folder = new File(settings.getSettingsPath());
    			File file = new File(folder, "existingCssClasses.txt");
    			if(!file.exists())
    				file.createNewFile();

    			Stream<String> stream = Files.lines(Paths.get(file.getCanonicalPath()));
    			stream.forEach(line -> existingCssClasses.add(line));
    			stream.close();
    			
    			PrintWriter write = new PrintWriter(new FileWriter(file.getCanonicalPath()));
    			for(String s : existingCssClasses)
    			    write.println(s);
    			write.close();
    		
    		} catch (IOException e) {System.out.println(e.getMessage());}
    		
    		//System.out.println("* " + existingCssClasses.size()+ " * Existing Css Classes: " + existingCssClasses.toString());

    	}
    	super.stopSystem(system);
    }
    
    @Override
	protected void closeTestSession() {
    	super.closeTestSession();
    	NativeLinker.cleanWdDriverOS();
	}

}
