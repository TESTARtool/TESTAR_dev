import nl.ou.testar.resolver.MySQLSerialResolver;
import nl.ou.testar.resolver.OrientDBSerialResolver;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WidgetActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_para extends WebdriverProtocol {
    // protected boolean isTestReportEnabled = true;
    // protected boolean isSequenceReportEnabled = true;
    // protected boolean isPlayableRecordEnabled = true;
    // protected boolean isLogSerializerEnabled = true;

    // protected MySQLSerialResolver databaseReplayResolver;
    // protected OrientDBSerialResolver orientDbSerialResolver;

    @Override
    protected void initialize(Settings settings) {
        // isTestReportEnabled = true;
        // isSequenceReportEnabled = true;
        // isLogSerializerEnabled = true;
        // isPlayableRecordEnabled = true;

        super.initialize(settings);

        // List of atributes to identify and close policy popups
        // Set to null to disable this feature
        policyAttributes = new HashMap<String, String>() {{
            put("id", "sncmp-banner-btn-agree");
        }};
    }

    // Jam logging if disabled

//     @Override
//     protected String getAndStoreGeneratedSequence() {
//         if (!isLogSerializerEnabled) {
//             return null;
//         }
//         return super.getAndStoreGeneratedSequence();
//     }

//     @Override
//     protected File getAndStoreSequenceFile() {
//         if (!isLogSerializerEnabled) {
//             return null;
//         }
//         return super.getAndStoreSequenceFile();
//     }

//     @Override
//     protected void saveActionInfoInLogs(State state, Action action, String actionMode) {
//         if (!isLogSerializerEnabled) {
//             return;
//         }
//         super.saveActionInfoInLogs(state, action, actionMode);
//     }

//     @Override
//     protected void classifyAndCopySequenceIntoAppropriateDirectory(Verdict finalVerdict, String generatedSequence, File currentSeq) {
//         if (!isLogSerializerEnabled) {
//             return;
//         }
//         super.classifyAndCopySequenceIntoAppropriateDirectory(finalVerdict, generatedSequence, currentSeq);
//     }

//     // Jam recording if disabled

//     @Override
//     protected void initFragmentForReplayableSequence(State state) {
//         if (!isPlayableRecordEnabled) {
//             return;
//         }
//         super.initFragmentForReplayableSequence(state);
//     }

//     @Override
//     protected void saveActionIntoFragmentForReplayableSequence(Action action, State state, Set<Action> actions) {
//         if (!isPlayableRecordEnabled) {
//             return;
//         }
//         super.saveActionIntoFragmentForReplayableSequence(action, state, actions);
//     }

//     @Override
//     protected void saveStateIntoFragmentForReplayableSequence(State state) {
//         if (isPlayableRecordEnabled) {
//             return;
//         }
//         super.saveStateIntoFragmentForReplayableSequence(state);
//     }

//     @Override
//     protected void writeAndCloseFragmentForReplayableSequence() {
//         if (isPlayableRecordEnabled) {
//             return;
//         }
//         super.writeAndCloseFragmentForReplayableSequence();
//     }

//     @Override
//     protected void runReplayLoop() {
//         if (settings.get(ConfigTags.ReportType).equals(Settings.SUT_REPORT_DATABASE)) {
//             System.out.println(String.format("-= Trying to replay from a database %s =-", sqlService));
//             isTestReportEnabled = true;
//             isSequenceReportEnabled = true;
//             isLogSerializerEnabled = true;
//             isPlayableRecordEnabled = true;

//             System.out.println(String.format("-= Service is now %s =-", sqlService));
//             databaseReplayResolver = new MySQLSerialResolver(sqlService, settings);
//             orientDbSerialResolver = new OrientDBSerialResolver(orientService, settings, stateModelManager);

//             try {
//                 orientDbSerialResolver.startReplay(settings.get(ConfigTags.SQLReporting));
//                 assignActionResolver(orientDbSerialResolver);
// //                databaseReplayResolver.startReplay(settings.get(ConfigTags.SQLReporting));
// //                assignActionResolver(databaseReplayResolver);
//                 runGenerateOuterLoop(null);
//                 resignActionResolver();
//             }
//             catch (Exception e) {
//                 System.err.println("Failed to replay from a database");
//                 e.printStackTrace();
//             }
//         }
//         else {
//             System.out.println("-= No database available - falling back =-");
//             System.out.println(settings.get(ConfigTags.ReportType));
//             super.runReplayLoop();
//         }
//     }


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
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        // Kill unwanted processes, force SUT to foreground
        Set<Action> actions = super.deriveActions(system, state);
        Set<Action> filteredActions = new HashSet<>();

        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // Check if forced actions are needed to stay within allowed domains
        Set<Action> forcedActions = detectForcedActions(state, ac);

        // iterate through all widgets
        for (Widget widget : state) {
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

            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                if(whiteListed(widget) || isUnfiltered(widget)){
                    actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
                }
            }

            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget)) {
                if(whiteListed(widget) || isUnfiltered(widget)){
                    if (!isLinkDenied(widget)) {
                        actions.add(ac.leftClickAt(widget));
                    }else{
                        // link denied:
                        filteredActions.add(ac.leftClickAt(widget));
                    }
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.leftClickAt(widget));
                }
            }
        }

        //if(actions.isEmpty()) {
        //	return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
        //}

        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            System.out.println("--- Forced actions available ---");
            filteredActions = actions;
            actions = forcedActions;
        }

        //Showing the grey dots for filtered actions if visualization is on:
        if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

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

        Set<String> clickSet = new HashSet<>(clickableClasses);
        clickSet.retainAll(element.cssClasses);
        return clickSet.size() > 0;
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

    @Override
    /*
     * Locate and press Ionic button with "Sign in" caption
     */
    protected Action generateCustomLoginAction(State state, WidgetActionCompiler ac) {
        for (Widget widget: state) {
            if (!WdWidget.class.isInstance(widget)) {
                continue;
            }
            WdWidget wdWidget = (WdWidget) widget;
//            System.out.println(String.format("??? %s ???", wdWidget.get(WdTags.WebItemType)));
            if("ion-button".equals(wdWidget.element.tagName) &&
                    wdWidget.element.textContent.toLowerCase(Locale.ROOT).contains("sign in")) {
                System.out.println("!!! Custom button found !!!");
                return ac.leftClickAt(wdWidget);
            }
        }
        return null;
    }

    protected boolean resolversSupported() {
        return true;
   }
}