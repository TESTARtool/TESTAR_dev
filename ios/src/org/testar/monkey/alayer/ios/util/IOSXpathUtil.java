/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2026 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.ios.util;

import java.util.ArrayList;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.ios.IOSAppiumFramework;
import org.testar.monkey.alayer.ios.enums.IOSTags;

public class IOSXpathUtil {

    private IOSXpathUtil() {
    }

    // Method which constructs the hierachy xpath (absolute path)
    // This method is needed as this is the only way to uniquely identify an GUI object if it has no accessibilityID.
    //TODO: THIS METHOD IS VERY INEFFICIENT! Seems to work correctly though.
    public static String constructXpath(Widget w) {
        StringBuilder sb =  new StringBuilder();
        Widget parentWidget = w;

        while (parentWidget != w.root()) {
            String classTag = parentWidget.get(Tags.Desc, "*");
            int indexNumber = parentWidget.get(IOSTags.iosNodeIndex, -1);

            if (classTag.equals("Root")) {
                break;
            }

            parentWidget = parentWidget.parent();

            ArrayList<Pair<String, Integer>> childClasses = new ArrayList<>();

            for (int i = 0; i < parentWidget.childCount(); i++) {
                childClasses.add(new Pair<String, Integer>(
                    parentWidget.child(i).get(Tags.Desc, "*"), 
                    parentWidget.child(i).get(IOSTags.iosNodeIndex, -1)
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

        int indexBackSlash = sb.indexOf("/", sb.indexOf("/") + 1);
        if (indexBackSlash > 0) {
            String appName = IOSAppiumFramework.getAppName();
            String insertString = "[@name=\"" + appName + "\"]";
            sb.insert(indexBackSlash, insertString);
        }

        sb.insert(0, "/");

        return sb.toString();
    }

}
