/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.state.State;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.web.log.WebBrowserConsoleOracle;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.windows.exceptions.WinApiException;
import org.testar.windows.state.WinProcess;

import java.util.Arrays;
import java.util.List;

public final class ScriptlessWebdriverOracleComposer extends ScriptlessOracleComposer {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ScriptlessOracleComposer delegate;
    private final WebBrowserConsoleOracle webBrowserConsoleOracle;

    public ScriptlessWebdriverOracleComposer(ScriptlessOracleComposer delegate, Settings settings) {
        this.delegate = Assert.notNull(delegate);
        this.webBrowserConsoleOracle = new WebBrowserConsoleOracle(Assert.notNull(settings));
    }

    @Override
    public List<Verdict> composeVerdicts(RuntimeContext runtimeContext, SUT system, State state, List<Verdict> verdicts) {
        List<Verdict> composedVerdicts = delegate.composeVerdicts(runtimeContext, system, state, verdicts);

        if (system != null
                && runtimeContext.settings().get(ConfigTags.ForceForeground)
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
                composedVerdicts.add(browserVerdict);
            }
        }

        if (composedVerdicts.size() > 1) {
            composedVerdicts.removeIf(verdict -> verdict.severity() == Verdict.OK.severity());
        }

        state.set(Tags.OracleVerdicts, composedVerdicts);
        return composedVerdicts;
    }
}
