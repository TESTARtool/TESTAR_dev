import nl.ou.testar.resolver.MySQLSerialResolver;
import nl.ou.testar.resolver.OrientDBSerialResolver;
import org.testar.ActionSelector;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WidgetActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;
import org.testar.simplestategraph.QLearningActionSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_yoho extends WebdriverProtocol {
    // protected boolean isTestReportEnabled = true;
    // protected boolean isSequenceReportEnabled = true;
    // protected boolean isPlayableRecordEnabled = true;
    // protected boolean isLogSerializerEnabled = true;

    // protected MySQLSerialResolver databaseReplayResolver;
    // protected OrientDBSerialResolver orientDbSerialResolver;

    protected QLearningActionSelector qlActionSelector;

    @Override
    protected void initialize(Settings settings) {
        // isTestReportEnabled = true;
        // isSequenceReportEnabled = true;
        // isLogSerializerEnabled = true;
        // isPlayableRecordEnabled = true;

        super.initialize(settings);

        qlActionSelector = new QLearningActionSelector(settings.get(ConfigTags.MaxReward),
                settings.get(ConfigTags.Discount));

        // List of atributes to identify and close policy popups
        // Set to null to disable this feature
        policyAttributes = new HashMap<String, String>() {
            {
                put("id", "sncmp-banner-btn-agree");
            }
        };

        // DemoResolver demoResolver = new DemoResolver();
        // demoResolver.setNextResolver(this);
        // setActionResolver(demoResolver);
    }

    // Jam logging if disabled

    // @Override
    // protected String getAndStoreGeneratedSequence() {
    // if (!isLogSerializerEnabled) {
    // return null;
    // }
    // return super.getAndStoreGeneratedSequence();
    // }

    // @Override
    // protected File getAndStoreSequenceFile() {
    // if (!isLogSerializerEnabled) {
    // return null;
    // }
    // return super.getAndStoreSequenceFile();
    // }

    // @Override
    // protected void saveActionInfoInLogs(State state, Action action, String
    // actionMode) {
    // if (!isLogSerializerEnabled) {
    // return;
    // }
    // super.saveActionInfoInLogs(state, action, actionMode);
    // }

    // @Override
    // protected void classifyAndCopySequenceIntoAppropriateDirectory(Verdict
    // finalVerdict, String generatedSequence, File currentSeq) {
    // if (!isLogSerializerEnabled) {
    // return;
    // }
    // super.classifyAndCopySequenceIntoAppropriateDirectory(finalVerdict,
    // generatedSequence, currentSeq);
    // }

    // // Jam recording if disabled

    // @Override
    // protected void initFragmentForReplayableSequence(State state) {
    // if (!isPlayableRecordEnabled) {
    // return;
    // }
    // super.initFragmentForReplayableSequence(state);
    // }

    // @Override
    // protected void saveActionIntoFragmentForReplayableSequence(Action action,
    // State state, Set<Action> actions) {
    // if (!isPlayableRecordEnabled) {
    // return;
    // }
    // super.saveActionIntoFragmentForReplayableSequence(action, state, actions);
    // }

    // @Override
    // protected void saveStateIntoFragmentForReplayableSequence(State state) {
    // if (isPlayableRecordEnabled) {
    // return;
    // }
    // super.saveStateIntoFragmentForReplayableSequence(state);
    // }

    // @Override
    // protected void writeAndCloseFragmentForReplayableSequence() {
    // if (isPlayableRecordEnabled) {
    // return;
    // }
    // super.writeAndCloseFragmentForReplayableSequence();
    // }

    // @Override
    // protected void runReplayLoop() {
    // if (settings.get(ConfigTags.ReportType).equals(Settings.SUT_REPORT_DATABASE))
    // {
    // System.out.println(String.format("-= Trying to replay from a database %s =-",
    // sqlService));
    // isTestReportEnabled = true;
    // isSequenceReportEnabled = true;
    // isLogSerializerEnabled = true;
    // isPlayableRecordEnabled = true;

    // System.out.println(String.format("-= Service is now %s =-", sqlService));
    // databaseReplayResolver = new MySQLSerialResolver(sqlService, settings);
    // orientDbSerialResolver = new OrientDBSerialResolver(orientService, settings,
    // stateModelManager);

    // try {
    // orientDbSerialResolver.startReplay(settings.get(ConfigTags.SQLReporting));
    // assignActionResolver(orientDbSerialResolver);
    // // databaseReplayResolver.startReplay(settings.get(ConfigTags.SQLReporting));
    // // assignActionResolver(databaseReplayResolver);
    // runGenerateOuterLoop(null);
    // resignActionResolver();
    // }
    // catch (Exception e) {
    // System.err.println("Failed to replay from a database");
    // e.printStackTrace();
    // }
    // }
    // else {
    // System.out.println("-= No database available - falling back =-");
    // System.out.println(settings.get(ConfigTags.ReportType));
    // super.runReplayLoop();
    // }
    // }

    /**
     * This method is used by TESTAR to determine the set of currently available
     * actions.
     * You can use the SUT's current state, analyze the widgets and their properties
     * to create
     * a set of sensible actions, such as: "Click every Button which is enabled"
     * etc.
     * The return value is supposed to be non-null. If the returned set is empty,
     * TESTAR
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
            // The blackListed widgets are those that have been filtered during the SPY mode
            // with the
            // CAPS_LOCK + SHIFT + Click clickfilter functionality.
            if (blackListed(widget)) {
                if (isTypeable(widget)) {
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
                if (whiteListed(widget) || isUnfiltered(widget)) {
                    actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
                } else {
                    // filtered and not white listed:
                    filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
                }
            }

            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget)) {
                if (whiteListed(widget) || isUnfiltered(widget)) {
                    if (!isLinkDenied(widget)) {
                        actions.add(ac.leftClickAt(widget));
                    } else {
                        // link denied:
                        filteredActions.add(ac.leftClickAt(widget));
                    }
                } else {
                    // filtered and not white listed:
                    filteredActions.add(ac.leftClickAt(widget));
                }
            }
        }

        // if(actions.isEmpty()) {
        // return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
        // }

        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            System.out.println("--- Forced actions available ---");
            filteredActions = actions;
            actions = forcedActions;
        }

        // Showing the grey dots for filtered actions if visualization is on:
        if (visualizationOn || mode() == Modes.Spy)
            SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

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
        for (Widget widget : state) {
            if (!WdWidget.class.isInstance(widget)) {
                continue;
            }
            WdWidget wdWidget = (WdWidget) widget;
            // System.out.println(String.format("??? %s ???",
            // wdWidget.get(WdTags.WebItemType)));
            if ("ion-button".equals(wdWidget.element.tagName) &&
                    wdWidget.element.textContent.toLowerCase(Locale.ROOT).contains("sign in")) {
                System.out.println("!!! Custom button found !!!");
                return ac.leftClickAt(wdWidget);
            }
        }
        return null;
    }

    @Override
    protected boolean resolversSupported() {
        return true;
    }

    // @Override
    // protected ActionSelector createActionSelector() {
    // return new QLearningActionSelector(settings.get(ConfigTags.MaxReward), settings.get(ConfigTags.Discount));
    // }

    // @Override
    // protected ActionSelector getActionSelector() {
    //     return qlActionSelector;
    // }

    int forcedStep = 0;
    //private StdActionCompiler actionCompiler = new StdActionCompiler();



    @Override
    protected Set<Action> detectForcedActions(State state, WidgetActionCompiler actionCompiler) {
        System.out.println("<<< Selecting action >>>");
        String currentUrl = WdDriver.getCurrentUrl();
        if (currentUrl.startsWith("https://app.yohoapp.io/web/app/dashboard")) {
            System.out.println("<<< On a dashboard >>>");
            List<WdWidget> res;
            switch (forcedStep) {
                case 0:
                    List<WdWidget> debug = findByAttribute(state, "controlname");
                    for (WdWidget item : debug) {
                        System.out.println("!!! " + item.getAttribute("controlname") + " !!!");
                    }
                    res = findByAttribute(state, "controlname", "assignedTeam");
                    if (res.size() > 0) {
                        System.out.println("<<< Clicking work progress list >>>");
                        forcedStep++;
                        return Collections.singleton(actionCompiler.leftClickAt(res.get(0)));
                    }
                    break;
                case 1:
                    res = findByAttribute(state, "role", "menuitem");
                    for (WdWidget item : res) {
                        if (item.element.textContent.toLowerCase(Locale.ROOT).contains("planning")) {
                            System.out.println("<<< Setting to \"Planning\" >>>");
                            forcedStep++;
                            return Collections.singleton(actionCompiler.leftClickAt(item));
                        }
                    }
                    break;
                case 2:
                    res = findByAttribute(state, "controlname", "assignee");
                    if (res.size() > 0) {
                        System.out.println("<<< Clicking task category selector >>>");
                        forcedStep++;
                        return Collections.singleton(actionCompiler.leftClickAt(res.get(0)));
                    }
                    break;
                case 3:
                    res = findByAttribute(state, "id", "mat-option-19");
                    if (res.size() > 0) {
                        System.out.println("<<< Selecting \"Unassigned\" >>>");
                        forcedStep++;
                        return Collections.singleton(actionCompiler.leftClickAt(res.get(0)));
                    }
                    break;
            }
        }
        return super.detectForcedActions(state, actionCompiler);//super.selectAction(system, state, actions);
    }

    private List<WdWidget> findByAttribute(State state, String attr) {
        List<WdWidget> result = new ArrayList<>();
        for (Widget widget : state) {
            if (widget instanceof WdWidget) {
                WdWidget wdWidget = (WdWidget) widget;
                if (wdWidget.getAttribute(attr) != null) {
                    result.add(wdWidget);
                }
                addChildrenByAttribute(result, wdWidget, attr);
            }
        }
        return result;
    }

    private void addChildrenByAttribute(List<WdWidget> list, WdWidget parent, String attr) {
        for (int i = 0; i < parent.childCount(); i++) {
            WdWidget child = parent.child(i);
            if (child.getAttribute(attr) != null) {
                list.add(child);
                addChildrenByAttribute(list, child, attr);
            }
        }
    }

    private List<WdWidget> findByAttribute(State state, String attr, String value) {
        List<WdWidget> result = new ArrayList<>();
        for (Widget widget : state) {
            if (widget instanceof WdWidget) {
                WdWidget wdWidget = (WdWidget) widget;
                if (value.equals(wdWidget.getAttribute(attr))) {
                    result.add(wdWidget);
                }
                addChildrenByAttribute(result, wdWidget, attr, value);
            }
        }
        return result;
    }


    private void addChildrenByAttribute(List<WdWidget> list, WdWidget parent, String attr, String value) {
        for (int i = 0; i < parent.childCount(); i++) {
            WdWidget child = parent.child(i);
            if (value.equals(child.getAttribute(attr))) {
                list.add(child);
                addChildrenByAttribute(list, child, attr, value);
            }
        }
    }

    // private class RecursiveWidgets implements Iterator<WdWidget> {

    //     private Iterator Iterator;
    //     private WdWidget current;

    //     public RecursiveWidgets(State state) {
    //         originalIterator = state.iterator();
    //     }

    //     @Override
    //     public boolean hasNext() {
    //         if (currentWidget = )
    //     }

    //     @Override
    //     public WdWidget next() {
    //         // TODO Auto-generated method stub
    //         return null;
    //     }
    // }
}

