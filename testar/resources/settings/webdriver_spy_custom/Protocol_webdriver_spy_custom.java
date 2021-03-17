/**
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.protocols.WebdriverProtocol;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;


public class Protocol_webdriver_spy_custom extends WebdriverProtocol {
	/**
	 * Customize the clickable css classes using TESTAR Spy mode + Control button over widget
	 * The file "settings/webdriver_spy_custom/customizedCssClasses.txt"
	 * Will act as input/reader of clickable css classes
	 */
	protected Set<String> customizedCssClasses = new HashSet<>();
	protected File fileCustomizedCssClasses;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

		// List of atributes to identify and close policy popups
		// Set to null to disable this feature
		policyAttributes = new HashMap<String, String>() {{
			put("class", "iAgreeButton");
		}};

		try {
			File folder = new File(settings.getSettingsPath());
			fileCustomizedCssClasses = new File(folder, "customizedCssClasses.txt");
			if(!fileCustomizedCssClasses.exists())
				fileCustomizedCssClasses.createNewFile();

			Stream<String> stream = Files.lines(Paths.get(fileCustomizedCssClasses.getCanonicalPath()));
			stream.forEach(line -> customizedCssClasses.add(line));
			stream.close();

			customizedCssClasses.removeIf(Strings::isNullOrEmpty);
		} catch (IOException e) {
			System.out.println("ERROR reading customizedCssClasses.txt file");
		}

	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @return a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state)
			throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all widgets
		for (Widget widget : state) {
			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			// addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget,
			//     state);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				if (!isLinkDenied(widget)) {
					actions.add(ac.leftClickAt(widget));
				}
			}
		}

		return actions;
	}

	@Override
	protected boolean isClickable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}

		WdElement element = ((WdWidget) widget).element;
		if (element.isClickable) {
			return true;
		}

		for(String s : element.cssClasses)
			if (customizedCssClasses.contains(s))
				return true;

		return false;
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type);
			}
			return true;
		}

		return false;
	}

	/**
	 * Add additional TESTAR keyboard shortcuts in SPY mode to enable the filtering of actions by clicking on them
	 * @param key
	 */
	@Override
	public void keyDown(KBKeys key) {    	
		super.keyDown(key);        
		if (mode() == Modes.Spy){ 
			if (key == KBKeys.VK_RIGHT) {
				try {

					Widget w = Util.widgetFromPoint(latestState, mouse.cursor().x(), mouse.cursor().y());

					WdElement element = ((WdWidget) w).element;
					for(String s : element.cssClasses)
						if(s!=null && !s.isEmpty())
							customizedCssClasses.add(s);

				}catch(Exception e) {
					System.out.println("ERROR reading and adding the widget from point: "
							+ "x(" + mouse.cursor().x() + "), y("+ mouse.cursor().y() +")");
				}
			}

			if (key == KBKeys.VK_LEFT) {
				try {
					Widget w = Util.widgetFromPoint(latestState, mouse.cursor().x(), mouse.cursor().y());

					WdElement element = ((WdWidget) w).element;
					for(String s : element.cssClasses)
						if(s!=null && !s.isEmpty())
							customizedCssClasses.remove(s);
				}catch(Exception e) {
					System.out.println("ERROR reading and removing the widget from point: "
							+ "x(" + mouse.cursor().x() + "), y("+ mouse.cursor().y() +")");
				}
			}
		}
	}

	@Override
	protected void stopSystem(SUT system) {
		if(settings.get(ConfigTags.Mode) == Modes.Spy) {
			try (PrintWriter write = new PrintWriter(new FileWriter(fileCustomizedCssClasses.getCanonicalPath()))){
				for(String s : customizedCssClasses)
					if(s!=null && !s.isEmpty())
						write.println(s);
			}catch(Exception e) {
				System.out.println("ERROR writing customizedCssClasses.txt file");
			}
		}
		super.stopSystem(system);
	}
}
