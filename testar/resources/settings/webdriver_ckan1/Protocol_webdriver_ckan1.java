import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONTokener;

import org.testar.CompoundTextActionSelector;
import org.testar.RandomActionSelector;
import org.testar.SutVisualization;
import org.testar.action.priorization.ActionTags;
import org.testar.managers.InterestingStringsDataManager;
import org.testar.managers.InterestingStringsFilteringManager;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.KeyDown;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.actions.WdHistoryBackAction;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;
import org.testar.statemodel.sequence.Sequence;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.protocols.CodeAnalysisWebdriverProtocol;

/** Protocol for code analysis with CKAN SUT */

public class Protocol_webdriver_ckan1 extends CodeAnalysisWebdriverProtocol {

    protected String applicationUsername, applicationPassword;
    protected boolean loggedIn=false;
	protected CompoundTextActionSelector selector;

    	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
        System.out.println("CKAN protocol calls super.init");
 		super.initialize(settings);

        this.applicationUsername = settings.get(ConfigTags.ApplicationUsername);
		this.applicationPassword = settings.get(ConfigTags.ApplicationPassword);

		if ( settings.get(ConfigTags.CompoundTextActionLogicEnabled) ) {
			selector = new CompoundTextActionSelector(
				settings.get(ConfigTags.CompoundTextActionInitialProbability),
				settings.get(ConfigTags.CompoundTextActionResetProbability),
				settings.get(ConfigTags.CompoundTextActionGrowthRate),
				settings.get(ConfigTags.CompoundTextActionLowPriorityInitialFactor),
				settings.get(ConfigTags.CompoundTextActionLowPriorityResetFactor),
				settings.get(ConfigTags.CompoundTextActionLowPriorityGrowthRate),
				settings.get(ConfigTags.CompoundTextActionHighPriorityInitialFactor),
				settings.get(ConfigTags.CompoundTextActionHighPriorityResetFactor),
				settings.get(ConfigTags.CompoundTextActionHighPriorityShrinkRate)
			);
		}
	}

    protected void initializeDataManager() {
        dataManager = new InterestingStringsDataManager(this.fullStringRate, this.maxInputStrings, this.typeMatchRate);
        dataManager.loadInputValues();
    }

    @Override
    protected void initializeFilteringManager() {
        filteringManager = new InterestingStringsFilteringManager((InterestingStringsDataManager)this.dataManager);
        filteringManager.loadFilters();
    }

    @Override
	protected SUT startSystem() throws SystemStartException {
        this.loggedIn=false;
        return super.startSystem();
    }

    @Override
	protected void beginSequence(SUT system, State state) {
        super.beginSequence(system,state);
        if ( ! this.loggedIn ) {
            loginSUT(system,state);
            this.loggedIn=true;
        }
    }

    protected void loginSUT(SUT system, State state) {
		waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","login", this.applicationUsername, state, system, 1, 0.5);
        Map<String, String> passwordFieldMap  = new HashMap<String, String>() {{
            put("WebId", "field-password");
            put("WebType", "password");
        }};
        waitLeftClickAndTypeIntoWidgetWithMatchingTags(passwordFieldMap, this.applicationPassword, state, system, 1, 0.5);
        Map<String, String> loginButtonMap  = new HashMap<String, String>() {{
            put("WebTagName", "button");
            put("WebName","Login");
        }};
        waitAndLeftClickWidgetWithMatchingTags(loginButtonMap, state, system, 1, 0.5);

    }


    @Override
    protected void processSUTDataAfterAction(JSONTokener tokener) {
        JSONArray root = new JSONArray(tokener);

        Vector<Map<String,String>> output = new Vector<>();

        for (int i = 0; i < root.length(); i++) {
            JSONArray inner = root.getJSONArray(i);
            String type = inner.getString(0);
            String value = inner.getString(1);
            System.out.println("Extracted string " + type + " / " + value);
            Map<String, String> innerMap = new HashMap<>();
            innerMap.put("type", type);
            innerMap.put("value", value);
            output.add(innerMap);
        }

        if ( output.size() > 0 ) {
            ((InterestingStringsDataManager)(dataManager)).loadInput(output);
        }

        // TODO: store extracted string data in the state model
    }

    /**
     *
     * This method has been overridden for the CKAN protocol to filter out clicks on links
     * to API endpoints, as well as logout actions.
     *
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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground

        // CKAN Customization: start with empty actions HashSet, so that we only rely
        // on this method definition for deriving actions.
		Set<Action> actions = new HashSet<>();
		Set<Action> filteredActions = new HashSet<>();

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// This variable is used when for building a compound text action
		List<Action> textActions = new ArrayList<>();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);

		// iterate through all widgets
		for (Widget widget : state) {

            /* CKAN Customization to skip various elements:
             * - Don't click on links to API endpoints. They aren't in HTML and don't have any actions on them.
             *   so TESTAR gets stuck.
             * - Don't click on the Logout button, because there isn't much functionality we can test if we're
             *   not logged in anymore.
             * - Don't switch languages. This would change some of the properties of the widgets, so it would
             *   become more difficult to avoid actions we don't want.
             * - Don't upload stuff, because of high risk that TESTAR gets stuck performing actions in the upload
             *   dialog for a long time.
             */


            if( widget.get(WdTags.WebHref,"").contains("/api/")
                || widget.get(WdTags.WebHref,"").endsWith("/_logout")
                || widget.get(WdTags.WebName,"").equals("Upload")
                || widget.get(WdTags.WebName,"").equals("English")
                || widget.get(WdTags.WebTextContent,"").equals("English")
                || widget.get(WdTags.WebCssClasses,"").contains("fa-sign-out") ) {
                //System.out.println("DeriveActions ignored href = " + widget.get(WdTags.WebHref,"") + " / CSS class = " + widget.get(WdTags.WebCssClasses,"") );
                continue;
            }

			/** CKAN customization to tag widgets that should be assigned
			 *  low probability after entering text datta.
			 *  This is currently only applied to clickable widgets.
			 */
			boolean isLowPriorityWidget = false;
			if ( widget.get(WdTags.WebName,"").equals("Datasets")
				|| widget.get(WdTags.WebName,"").equals("Organizations")
				|| widget.get(WdTags.WebName,"").equals("Groups")
				|| widget.get(WdTags.WebName,"").equals("Showcases")
				|| widget.get(WdTags.WebName,"").equals("About")
				|| widget.get(WdTags.WebName,"").equals("CKAN")
				|| widget.get(WdTags.WebName,"").equals("About CKAN")
				|| widget.get(WdTags.WebAlt,"").equals("Gravatar")
				|| widget.get(WdTags.WebCssClasses,"").contains("fa-gavel")
				|| widget.get(WdTags.WebCssClasses,"").contains("fa-tachometer")
				|| widget.get(WdTags.WebCssClasses,"").contains("fa-cog") ) {
				isLowPriorityWidget = true;
			}

			/** CKAN customization to tag widgets that should be assigned
			 *  high probability after entering text data.
			 *  This is currently only applied to clickable widgets.
			 */
			boolean isHighPriorityWidget = false;
			if ((! isLowPriorityWidget ) &&
				( widget.get(WdTags.WebName,"").equals("save")
				|| widget.get(WdTags.WebType,"").equals("submit")
				|| widget.get(WdTags.WebCssClasses,"").contains("fa-search") ) ) {
				System.out.println("Found high priority!");
				isHighPriorityWidget = true;
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) {
				continue;
			}
			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if(blackListed(widget)){
				if(isTypeable(widget)){
					filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes ( either as a single action, or as a compound action, depending on settings)
			if ( isAtBrowserCanvas(widget) && isTypeable(widget) ) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					if ( settings.get(ConfigTags.CompoundTextActionLogicEnabled ) ) {
						textActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
					}
					else {
						actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
					}

				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				}
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget)) {
				Action clickAction = ac.leftClickAt(widget);
				if ( isLowPriorityWidget ) {
					clickAction.set(ActionTags.CompoundTextLowPriorityWidget, true);
				}
				else if ( isHighPriorityWidget ) {
					clickAction.set(ActionTags.CompoundTextHighPriorityWidget, true);
					System.out.println("Set high priority!");
				}

				if(whiteListed(widget) || isUnfiltered(widget)){
					if (!isLinkDenied(widget)) {
						actions.add(clickAction);
					}else{
						// link denied:
						filteredActions.add(clickAction);
					}
				}else{
					// filtered and not white listed:
					filteredActions.add(clickAction);
				}
			}
		}

		if ( settings.get(ConfigTags.CompoundTextActionLogicEnabled ) && textActions.size() > 0 ) {
			Action textAction = new CompoundAction(textActions);
			textAction.set(ActionTags.CompoundTextAction, true);
			textAction.set(Tags.Role, Roles.Text);
			textAction.set(Tags.Desc, "Compound text action to enter text into all text widgets");
			actions.add(textAction);
		}

		if(actions.isEmpty()) {
			return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}

		// If we have forced actions, prioritize and filter the other ones
		if (forcedActions != null && forcedActions.size() > 0) {
			filteredActions = actions;
			actions = forcedActions;
		}

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		return actions;
	}

	protected Action selectAction(State state, Set<Action> actions){
		Assert.isTrue(actions != null && !actions.isEmpty());
		if ( settings.get(ConfigTags.CompoundTextActionLogicEnabled) ) {
			return selector.selectAction(actions);
		}
		else {
			return RandomActionSelector.selectAction(actions);
		}

}


}