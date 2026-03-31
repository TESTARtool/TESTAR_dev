/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.util;

import java.util.ArrayList;

import org.testar.android.tag.AndroidTags;
import org.testar.core.Pair;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;

public class AndroidXpathUtil {

    private AndroidXpathUtil() {
    }


    // Method which constructs the hierarchy xpath (absolute path)
    // This method is needed as this is the only way to uniquely identify an GUI object if it has no accessibilityID.
    public static String constructXpath(Widget w) {
        StringBuilder sb =  new StringBuilder();
        Widget parentWidget = w;

        while (parentWidget != w.root()) {
            String classTag = parentWidget.get(Tags.Desc, "*");
            int indexNumber = parentWidget.get(AndroidTags.AndroidNodeIndex, -1);

            if (classTag.equals("Root")) {
                break;
            }

            parentWidget = parentWidget.parent();

            ArrayList<Pair<String, Integer>> childClasses = new ArrayList<Pair<String, Integer>>();

            for (int i = 0; i < parentWidget.childCount(); i++) {
                childClasses.add(new Pair<String, Integer>(
                    parentWidget.child(i).get(Tags.Desc, "*"), 
                    parentWidget.child(i).get(AndroidTags.AndroidNodeIndex, -1)
                ));
            }

            boolean checkDoubles = false;
            boolean incCounter = true;
            int counterOccur = 1;
            for (Pair childClass : childClasses) {
                String leftSide = (String) childClass.left();
                int rightSide = (int) childClass.right();
                if (leftSide.equals(classTag) && rightSide != indexNumber) {
                    checkDoubles = true;
                    if (incCounter) {
                        counterOccur++;
                    }
                }
                else if (leftSide.equals(classTag) && rightSide == indexNumber) {
                    incCounter = false;
                }
            }

            if (checkDoubles) {
                String xpathComponent = "/" + classTag + "[" + counterOccur + "]";
                sb.insert(0, xpathComponent);
            }
            else {
                String xpathComponent = "/" + classTag;
                sb.insert(0, xpathComponent);
            }

        }

        sb.insert(0, "/hierarchy");

        return sb.toString();
    }

}
