/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.TestarMode;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.environment.Environment;
import org.testar.core.exceptions.ActionBuildException;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.web.log.WebBrowserConsoleOracle;
import org.testar.plugin.NativeLinker;
import org.testar.scriptless.capability.ScriptlessCapabilities;
import org.testar.webdriver.state.WdDriver;
import org.testar.windows.Windows;
import org.testar.windows.exceptions.WinApiException;
import org.testar.windows.state.WinProcess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * WebDriver protocol implementation.
 */
public class ScriptlessWebdriverProtocol extends ComposedProtocol {

    private static final Logger LOGGER = LogManager.getLogger();

    private List<String> clickableClasses;
    private List<String> webDomainsAllowed;
    private WebBrowserConsoleOracle webBrowserConsoleOracle;

    public ScriptlessWebdriverProtocol() {
        super();
    }

    protected ScriptlessWebdriverProtocol(ScriptlessCapabilities scriptlessCapabilities) {
        super(scriptlessCapabilities);
    }

    @Override
    protected void initializeSettings(Settings settings) {
        super.initializeSettings(settings);
        NativeLinker.addWdDriverOS();
        clickableClasses = settings.get(ConfigTags.ClickableClasses);
        webDomainsAllowed = settings.get(ConfigTags.WebDomainsAllowed).contains("null")
                ? null
                : settings.get(ConfigTags.WebDomainsAllowed);
        webBrowserConsoleOracle = new WebBrowserConsoleOracle(settings);
    }

    @Override
    protected SUT startSystem() throws SystemStartException {
        SUT system = runtimeServices().systemService().startSystem();

        ensureWebDomainsAllowed();
        setWindowHandleForWebdriverBrowser(system);

        double displayScale = getDisplayScale(system);
        mouse = system.get(Tags.StandardMouse);
        mouse.setCursorDisplayScale(displayScale);

        return system;
    }

    @Override
    protected State getState(SUT system) throws StateBuildException {
        Assert.notNull(system);

        if (!WdDriver.waitDocumentReady()) {
            LogSerialiser.log("WEBDRIVER ERROR: Selenium Chromedriver seems not to respond!\n", LogSerialiser.LogLevel.Critical);
            System.out.println("******************************************************************");
            System.out.println("** WEBDRIVER ERROR: Selenium Chromedriver seems not to respond! **");
            System.out.println("******************************************************************");
            system.set(Tags.IsRunning, false);
        }

        updateCssClassesFromTestSettingsFile();

        return super.getState(system);
    }

    @Override
    protected List<Verdict> getVerdicts(SUT system, State state) {
        List<Verdict> verdicts = super.getVerdicts(system, state);

        if (settings.get(ConfigTags.ForceForeground)
                && System.getProperty("os.name").contains("Windows")
                && state.get(Tags.IsRunning, false)
                && !state.get(Tags.NotResponding, false)
                && system.get(Tags.PID, -1L) != -1L
                && WinProcess.procName(system.get(Tags.PID)).contains("chrome")
                && !WinProcess.isForeground(system.get(Tags.PID))) {

            String message = "Trying to set the browser to Foreground... " + system.get(Tags.PID, -1L);
            LOGGER.log(Level.INFO, message);

            try {
                WinProcess.toForeground(system.get(Tags.PID), 0.3, 100);
            } catch (WinApiException exception) {
                LOGGER.log(Level.WARN, exception);
                Verdict notRespondingVerdict = new Verdict(
                        Verdict.Severity.NOT_RESPONDING,
                        "Unable to bring the browser to foreground!"
                );
                List<Verdict> browserNotRespondingVerdict = Arrays.asList(notRespondingVerdict);
                state.set(Tags.OracleVerdicts, browserNotRespondingVerdict);
                return browserNotRespondingVerdict;
            }
        }

        List<Verdict> browserConsoleVerdicts = webBrowserConsoleOracle.getVerdicts(state);

        for (Verdict browserVerdict : browserConsoleVerdicts) {
            if (browserVerdict.severity() > Verdict.OK.severity()) {
                verdicts.add(browserVerdict);
            }
        }

        return verdicts;
    }

    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        return super.deriveActions(system, state);
    }

    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        return super.executeAction(system, state, action);
    }

    @Override
    protected void stopSystem(SUT system) {
        runtimeServices().systemService().stopSystem(system);
    }

    private void setWindowHandleForWebdriverBrowser(SUT system) {
        try {
            if (System.getProperty("os.name").contains("Windows") && system.get(Tags.HWND, null) == null) {
                long hwnd = Windows.GetForegroundWindow();
                long pid = Windows.GetWindowProcessId(Windows.GetForegroundWindow());

                if (WinProcess.procName(pid).contains("chrome")) {
                    system.set(Tags.HWND, hwnd);
                    system.set(Tags.PID, pid);
                    System.out.printf("INFO System PID %d and window handle %d have been set\n", pid, hwnd);
                }
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError exception) {
            System.out.println("INFO: We can not obtain the Windows 10 windows handle of WebDriver browser instance");
        }
    }

    private double getDisplayScale(SUT system) {
        double displayScale = Environment.getInstance().getDisplayScale(system.get(Tags.HWND, 0L));
        String overrideDisplayScale = settings().get(ConfigTags.OverrideWebDriverDisplayScale, "");

        if (!overrideDisplayScale.isEmpty()) {
            try {
                double parsedOverrideDisplayScale = Double.parseDouble(overrideDisplayScale);
                if (parsedOverrideDisplayScale != 0) {
                    displayScale = parsedOverrideDisplayScale;
                }
            } catch (NumberFormatException exception) {
                System.out.printf(
                        "WARNING Unable to convert display scale override to double: %s, will use %f\n",
                        overrideDisplayScale,
                        displayScale
                );
            }
        }

        return displayScale;
    }

    private void updateCssClassesFromTestSettingsFile() {
        if (settings.get(ConfigTags.Mode) != TestarMode.Spy) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TestarDirectories.getTestSettingsFile()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains(ConfigTags.ClickableClasses.name())) {
                    List<String> fileClickableClasses = Arrays.asList(line.split("=")[1].trim().split(";"));

                    for (String webClass : fileClickableClasses) {
                        if (!webClass.isEmpty() && !clickableClasses.contains(webClass)) {
                            System.out.println("Adding new clickable class from settings file: " + webClass);
                            clickableClasses.add(webClass);
                            settings.set(ConfigTags.ClickableClasses, clickableClasses);
                        }
                    }

                    for (String clickableClass : clickableClasses) {
                        if (!clickableClass.isEmpty() && !fileClickableClasses.contains(clickableClass)) {
                            System.out.println("Removing the clickable class: " + clickableClass);
                            clickableClasses.remove(clickableClass);
                            settings.set(ConfigTags.ClickableClasses, clickableClasses);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            // Best-effort spy-mode refresh of CSS classes.
        }
    }

    private void ensureWebDomainsAllowed() {
        try {
            String[] parts = settings().get(ConfigTags.SUTConnectorValue, "").split(" ");
            String sutConnectorUrl = "";

            for (String raw : parts) {
                String part = raw.replace("\"", "");
                if (part.matches("^(?:[a-zA-Z][a-zA-Z0-9+.-]*):.*")) {
                    sutConnectorUrl = part;
                }
            }

            String sutDomain = getDomain(sutConnectorUrl);
            if (webDomainsAllowed != null && !webDomainsAllowed.contains(sutDomain)) {
                System.out.println(String.format("WEBDRIVER INFO: Automatically adding %s SUT Connector domain to webDomainsAllowed List", sutDomain));
                String[] newWebDomainsAllowed = webDomainsAllowed.stream().toArray(String[]::new);
                webDomainsAllowed = Arrays.asList(org.apache.commons.lang3.ArrayUtils.insert(newWebDomainsAllowed.length, newWebDomainsAllowed, sutDomain));
            }

            String initialUrl = WdDriver.getCurrentUrl();
            String initialDomain = getDomain(initialUrl);
            if (webDomainsAllowed != null && !webDomainsAllowed.contains(initialDomain)) {
                System.out.println(String.format("WEBDRIVER INFO: Automatically adding initial %s Web domain to webDomainsAllowed List", initialDomain));
                String[] newWebDomainsAllowed = webDomainsAllowed.stream().toArray(String[]::new);
                webDomainsAllowed = Arrays.asList(org.apache.commons.lang3.ArrayUtils.insert(newWebDomainsAllowed.length, newWebDomainsAllowed, initialDomain));
            }
        } catch (Exception exception) {
            System.out.println("WEBDRIVER ERROR: Trying to add the startup domain to webDomainsAllowed List");
            System.out.println("Please review webDomainsAllowed List inside Webdriver Java Protocol");
        }
    }

    private String getDomain(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        try {
            java.net.URL parsedUrl = new java.net.URL(url);
            return parsedUrl.getHost();
        } catch (Exception exception) {
            return "";
        }
    }

}
