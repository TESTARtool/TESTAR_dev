/***************************************************************************************************
 *
 * Copyright (c) 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 Open Universiteit - www.ou.nl
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

package es.upv.staq.testar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Role;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ActionManagementTags {

    public enum Group {GeneralAction}

    public static final Tag<String> ActionOriginStateAbstractId = Tag.from("Action Origin State AbstractId", String.class);
    public static final Tag<String> ActionOriginWidgetAbstractId = Tag.from("Action Origin Widget AbstractId", String.class);
    public static final Tag<String> ActionOriginWidgetPath = Tag.from("Action Origin Widget Path", String.class);
    public static final Tag<Role> ActionOriginWidgetRole = Tag.from("Action Origin Widget Role", Role.class);
    public static final Tag<String> ActionOriginWidgetTitle = Tag.from("Action Origin Widget Title", String.class);
    public static final Tag<String> ActionOriginWidgetValuePattern = Tag.from("Action Origin Widget ValuePattern", String.class);

    // a set containing the tags that are available for action management
    @SuppressWarnings("serial")
    private static Set<Tag<?>> actionManagementTags = new HashSet<Tag<?>>() {
        {
            add(ActionOriginStateAbstractId);
            add(ActionOriginWidgetAbstractId);
            add(ActionOriginWidgetPath);
            add(ActionOriginWidgetRole);
            add(ActionOriginWidgetTitle);
            add(ActionOriginWidgetValuePattern);
        }
    };

    /**
     * Method will return true if a given tag is an available action management tag.
     * @param tag
     * @return
     */
    public static boolean isActionManagementTag(Tag<?> tag) {
        return actionManagementTags.contains(tag);
    }

    // a bi-directional mapping from the action management tags to a string equivalent for use in the settings file
    private static BiMap<Tag<?>, String> settingsMap = HashBiMap.create(actionManagementTags.size());
    static { 
        settingsMap.put(ActionOriginStateAbstractId, "ActionOriginStateAbstractId");
        settingsMap.put(ActionOriginWidgetAbstractId, "ActionOriginWidgetAbstractId");
        settingsMap.put(ActionOriginWidgetPath, "ActionOriginWidgetPath");
        settingsMap.put(ActionOriginWidgetRole, "ActionOriginWidgetRole");
        settingsMap.put(ActionOriginWidgetTitle, "ActionOriginWidgetTitle");
        settingsMap.put(ActionOriginWidgetValuePattern, "ActionOriginWidgetValuePattern");
    }

    // a mapping of a tag to its group
    @SuppressWarnings("serial")
    private static Map<Tag<?>, Group> tagGroupMap = new HashMap<Tag<?>, Group>() {
        {
            put(ActionOriginStateAbstractId, Group.GeneralAction);
            put(ActionOriginWidgetAbstractId, Group.GeneralAction);
            put(ActionOriginWidgetPath, Group.GeneralAction);
            put(ActionOriginWidgetRole, Group.GeneralAction);
            put(ActionOriginWidgetTitle, Group.GeneralAction);
            put(ActionOriginWidgetValuePattern, Group.GeneralAction);
        }
    };

    /**
     * This method will return the tag group for a given action management tag
     * @param tag action management tag
     * @return
     */
    public static Group getTagGroup(Tag<?> tag) {
        return tagGroupMap.getOrDefault(tag, Group.GeneralAction);
    }

    /**
     * This method will return all the tags that are available for use in action management.
     * @return
     */
    public static Set<Tag<?>> getAllTags() {
        return actionManagementTags;
    }

    /**
     * This method returns the action management tag belonging to a given settings string.
     * @param settingsString
     * @return
     */
    public static Tag<?> getTagFromSettingsString(String settingsString) {
        return settingsMap.inverse().getOrDefault(settingsString, null);
    }

    /**
     * This method returns the settings string for a given action management tag.
     * @param tag
     * @return
     */
    public static String getSettingsStringFromTag(Tag<?> tag) {
        return settingsMap.getOrDefault(tag, null);
    }

    // a mapping from the action management tags
    @SuppressWarnings("serial")
    private static Map<Tag<?>, Tag<?>> actionTagMapping = new HashMap<Tag<?>, Tag<?>>()
    {
        {
            put(ActionManagementTags.ActionOriginStateAbstractId, Tags.OriginStateAbstractId);
            put(ActionManagementTags.ActionOriginWidgetAbstractId, Tags.OriginWidgetAbstractId);
            put(ActionManagementTags.ActionOriginWidgetPath, Tags.OriginWidgetPath);
            put(ActionManagementTags.ActionOriginWidgetRole, Tags.OriginWidgetRole);
            put(ActionManagementTags.ActionOriginWidgetTitle, Tags.OriginWidgetTitle);
            put(ActionManagementTags.ActionOriginWidgetValuePattern, Tags.OriginWidgetValuePattern);
        }
    };

    public static Map<Tag<?>, Tag<?>> getActionTagMap() {
        return actionTagMapping;
    }

}
