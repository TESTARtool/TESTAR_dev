// GIPHOUSE, file changed by team
/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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

 /* GIPHOUSE, file was changed
 */

package org.testar.settings.dialog.tagsvisualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.windows.UIATags;

public class DefaultTagFilter {

    private static final List<Tag<?>> allAvailableTags;
    private static final List<Tag<?>> uiaTags;
    private static final List<Tag<?>> wdTags;
    private static final List<Tag<?>> tags;

    static {
        // Fill the list with all the available tags.
        uiaTags = Collections.unmodifiableList(
            new ArrayList<>(UIATags.getUIATags())
        );

        wdTags = Collections.unmodifiableList(
            new ArrayList<>(WdTags.getWdTags())
        );

        tags = Collections.unmodifiableList(new ArrayList<>(Tags.getTags()));

        List<Tag<?>> tmpList = new ArrayList<>(uiaTags);
        tmpList.addAll(tags);
        tmpList.addAll(wdTags);
        allAvailableTags = Collections.unmodifiableList(tmpList);
    }

    public static List<Tag<?>> getSet() {
        return allAvailableTags;
    }

    public static List<Tag<?>> getUiatags() {
        return uiaTags;
    }

    public static List<Tag<?>> getWdtags() {
        return wdTags;
    }

    public static List<Tag<?>> getTags() {
        return tags;
    }

    private static Tag<?> findTagByName(String name, Set<Tag<?>> tagSet) {
        return tagSet
            .stream()
            .filter(tag -> tag.name().equals(name))
            .findFirst()
            .orElse(null);
    }

    public static Tag<?> findTagByName(String name) {
        Tag<?> tag = findTagByName(name, UIATags.getAllTags());
        if (tag == null) {
            tag = findTagByName(name, Tags.getAllTags());
        }
        if (tag == null) {
            tag = findTagByName(name, WdTags.getAllTags());
        }
        return tag;
    }
}
