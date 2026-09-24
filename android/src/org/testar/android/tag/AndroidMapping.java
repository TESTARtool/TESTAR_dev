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

import org.testar.core.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public class AndroidMapping {
    // a mapping from the state management tags to Android tags
    private static Map<Tag<?>, Tag<?>> stateTagMappingAndroid =
            new HashMap<Tag<?>, Tag<?>>() {
                {
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
