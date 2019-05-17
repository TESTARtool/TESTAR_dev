/***************************************************************************************************
 *
 * Copyright (c) 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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


import java.util.Set;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.StateBuildException;
import nl.ou.testar.RandomActionSelector;
import es.upv.staq.testar.NativeLinker;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;

/**
 * This protocol is using the default Windows accessibility API (Windows UI Automation API) to test Web applications.
 *
 * It also extracts state models into graph database and uses state model to improve action selection
 *  so the graph model has to be enabled in the test settings.
 */
public class Protocol_web_orientdb extends DesktopProtocol {

	// This protocol expects Mozilla Firefox or Microsoft Internet Explorer on Windows10

	static Role webText; // browser dependent
	static double browser_toolbar_filter;

	/** 
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings   the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
		initBrowser();
	}

	// check browser
	private void initBrowser(){
		webText = NativeLinker.getNativeRole("UIAEdit"); // just init with some value
		String sutPath = settings().get(ConfigTags.SUTConnectorValue);
		if (sutPath.contains("iexplore.exe"))
			webText = NativeLinker.getNativeRole("UIAEdit");
		else if (sutPath.contains("firefox"))
			webText = NativeLinker.getNativeRole("UIAText");
	}

	/**
	 * This method is called when TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle 
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the 
	 * state is erroneous and if so why.
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException{

		State state = super.getState(system);

		for(Widget w : state){
			Role role = w.get(Tags.Role, Roles.Widget);
			if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAToolBar")}))
				browser_toolbar_filter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();
		}

		return state;

	}

	@Override
	protected boolean isClickable(Widget w){
		if (isAtBrowserCanvas(w))
			return super.isClickable(w);
		else
			return false;		
	} 

	@Override
	protected boolean isTypeable(Widget w){
		if (!isAtBrowserCanvas(w))
			return false;	

		Role role = w.get(Tags.Role, null);
		if (role != null && Role.isOneOf(role, webText))
			return isUnfiltered(w);

		return false;
	}

	private boolean isAtBrowserCanvas(Widget w){
		Shape shape = w.get(Tags.Shape,null);
		if (shape != null && shape.y() > browser_toolbar_filter)
			return true;
		else
			return false;		
	}

	/**
	 * Select one of the possible actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action a = preSelectAction(state, actions);
		if (a!= null) {
			return a;
		} else
			//if no preSelected actions are needed, then implement your own strategy
			System.out.println("Asking state model manager for action");
		Action modelAction = stateModelManager.getAbstractActionToExecute(actions);
		if (modelAction != null) return modelAction;
		System.out.println("StateModelManager did not return an action. Returning random");
		return RandomActionSelector.selectAction(actions);
	}

}
