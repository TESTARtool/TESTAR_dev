/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.tag;

import static org.testar.core.StateManagementTags.WidgetAutomationId;
import static org.testar.core.StateManagementTags.WidgetClassName;
import static org.testar.core.StateManagementTags.WidgetControlType;
import static org.testar.core.StateManagementTags.WidgetFrameworkId;
import static org.testar.core.StateManagementTags.WidgetHelpText;
import static org.testar.core.StateManagementTags.WidgetIsEnabled;
import static org.testar.core.StateManagementTags.WidgetPath;
import static org.testar.core.StateManagementTags.WidgetTitle;
import static org.testar.core.StateManagementTags.AndroidWidgetAccessibilityId;
import static org.testar.core.StateManagementTags.AndroidWidgetActivity;
import static org.testar.core.StateManagementTags.AndroidWidgetCheckable;
import static org.testar.core.StateManagementTags.AndroidWidgetChecked;
import static org.testar.core.StateManagementTags.AndroidWidgetClassName;
import static org.testar.core.StateManagementTags.AndroidWidgetClickable;
import static org.testar.core.StateManagementTags.AndroidWidgetDisplayed;
import static org.testar.core.StateManagementTags.AndroidWidgetEnabled;
import static org.testar.core.StateManagementTags.AndroidWidgetFocusable;
import static org.testar.core.StateManagementTags.AndroidWidgetFocused;
import static org.testar.core.StateManagementTags.AndroidWidgetHint;
import static org.testar.core.StateManagementTags.AndroidWidgetLongClickable;
import static org.testar.core.StateManagementTags.AndroidWidgetPackageName;
import static org.testar.core.StateManagementTags.AndroidWidgetPassword;
import static org.testar.core.StateManagementTags.AndroidWidgetResourceId;
import static org.testar.core.StateManagementTags.AndroidWidgetScrollable;
import static org.testar.core.StateManagementTags.AndroidWidgetSelected;
import static org.testar.core.StateManagementTags.AndroidWidgetText;
import static org.testar.core.StateManagementTags.AndroidWidgetXpath;

import org.testar.core.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public class AndroidMapping {
    // a mapping from the state management tags to Android tags
    private static Map<Tag<?>, Tag<?>> stateTagMappingAndroid =
            new HashMap<Tag<?>, Tag<?>>() {
                {
                    // Android
                    put(AndroidWidgetEnabled, AndroidTags.AndroidEnabled);
                    put(AndroidWidgetText, AndroidTags.AndroidText);
                    put(AndroidWidgetHint, AndroidTags.AndroidHint);
                    put(AndroidWidgetResourceId, AndroidTags.AndroidResourceId);
                    put(AndroidWidgetClassName, AndroidTags.AndroidClassName);
                    put(AndroidWidgetPackageName, AndroidTags.AndroidPackageName);
                    put(AndroidWidgetCheckable, AndroidTags.AndroidCheckable);
                    put(AndroidWidgetChecked, AndroidTags.AndroidChecked);
                    put(AndroidWidgetClickable, AndroidTags.AndroidClickable);
                    put(AndroidWidgetFocusable, AndroidTags.AndroidFocusable);
                    put(AndroidWidgetFocused, AndroidTags.AndroidFocused);
                    put(AndroidWidgetScrollable, AndroidTags.AndroidScrollable);
                    put(AndroidWidgetLongClickable, AndroidTags.AndroidLongClickable);
                    put(AndroidWidgetPassword, AndroidTags.AndroidPassword);
                    put(AndroidWidgetSelected, AndroidTags.AndroidSelected);
                    put(AndroidWidgetAccessibilityId, AndroidTags.AndroidAccessibilityId);
                    put(AndroidWidgetDisplayed, AndroidTags.AndroidDisplayed);
                    put(AndroidWidgetXpath, AndroidTags.AndroidXpath);
                    put(AndroidWidgetActivity, AndroidTags.AndroidActivity);

                    // Generic
                    put(WidgetTitle, AndroidTags.AndroidText);
                    put(WidgetHelpText, AndroidTags.AndroidHint);
                    put(WidgetControlType, AndroidTags.AndroidClassName);
                    put(WidgetClassName, AndroidTags.AndroidClassName);
                    put(WidgetAutomationId, AndroidTags.AndroidAccessibilityId);
                    put(WidgetIsEnabled, AndroidTags.AndroidEnabled);
                    put(WidgetFrameworkId, AndroidTags.AndroidPackageName);
                    put(WidgetPath, AndroidTags.AndroidXpath);
                }
            };

    /**
     * This method will return its equivalent, internal Android tag, if available.
     *
     * @param mappedTag
     * @return
     */
    public static <T> Tag<T> getMappedStateTag(Tag<T> mappedTag) {
        return (Tag<T>) stateTagMappingAndroid.getOrDefault(mappedTag, null);
    }
}
