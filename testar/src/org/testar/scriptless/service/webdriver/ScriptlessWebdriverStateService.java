/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.TestarMode;
import org.testar.core.Assert;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.webdriver.state.WdDriver;
import org.testar.core.tag.Tags;
import org.testar.scriptless.RuntimeContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public final class ScriptlessWebdriverStateService implements StateService {

    private final StateService delegate;
    private final RuntimeContext runtimeContext;

    public ScriptlessWebdriverStateService(StateService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        this.runtimeContext = Assert.notNull(runtimeContext);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        Assert.notNull(system);

        if (!WdDriver.waitDocumentReady()) {
            System.out.println("******************************************************************");
            System.out.println("** WEBDRIVER ERROR: Selenium Chromedriver seems not to respond! **");
            System.out.println("******************************************************************");
            system.set(Tags.IsRunning, false);
        }

        updateCssClassesFromTestSettingsFile();
        return delegate.getState(system);
    }

    private void updateCssClassesFromTestSettingsFile() {
        if (runtimeContext.settings().get(ConfigTags.Mode) != TestarMode.Spy) {
            return;
        }

        List<String> clickableClasses = runtimeContext.settings().get(ConfigTags.ClickableClasses);
        try (BufferedReader reader = new BufferedReader(new FileReader(TestarDirectories.getTestSettingsFile()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                if (line.contains(ConfigTags.ClickableClasses.name())) {
                    List<String> fileClickableClasses = Arrays.asList(line.split("=")[1].trim().split(";"));

                    for (String webClass : fileClickableClasses) {
                        if (!webClass.isEmpty() && !clickableClasses.contains(webClass)) {
                            System.out.println("Adding new clickable class from settings file: " + webClass);
                            clickableClasses.add(webClass);
                            runtimeContext.settings().set(ConfigTags.ClickableClasses, clickableClasses);
                        }
                    }

                    for (String clickableClass : clickableClasses.toArray(new String[0])) {
                        if (!clickableClass.isEmpty() && !fileClickableClasses.contains(clickableClass)) {
                            System.out.println("Removing the clickable class: " + clickableClass);
                            clickableClasses.remove(clickableClass);
                            runtimeContext.settings().set(ConfigTags.ClickableClasses, clickableClasses);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            // If this fails just ignore this optional feature
        }
    }
}
