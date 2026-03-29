/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.core.action.Action;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;

public class ConcreteActionFactory {

    public static ConcreteAction createConcreteAction(Action action, AbstractAction abstractAction) {
        ConcreteAction concreteAction =  new ConcreteAction(action.get(Tags.ConcreteID), abstractAction);

        // check if a widget is attached to this action.
        // if so, copy all the attributes to the action
        if (action.get(Tags.OriginWidget, null) != null) {
            setAttributes(concreteAction, action.get(Tags.OriginWidget));
        }

        // check if the action as attached a Description (More info than a Widget)
        // if so, set this Description to the current ConcreteAction
        if (action.get(Tags.Desc, null) != null) {
            setSpecificAttribute(concreteAction, Tags.Desc, action.get(Tags.Desc));
        }

        return concreteAction;
    }

    /**
     * Helper method to transfer attribute information from the testar entities to the model entities.
     * @param modelWidget
     * @param testarWidget
     */
    private static void setAttributes(ModelWidget modelWidget, Widget testarWidget) {
        for (Tag<?> t : testarWidget.tags()) {
            modelWidget.addAttribute(t, testarWidget.get(t, null));
        }
    }

    /**
     * Helper method to set a specific attribute information from the testar entities to the model entities.
     * @param modelWidget
     * @param tagAttribute
     * @param value
     */
    private static void setSpecificAttribute(ModelWidget modelWidget, Tag tagAttribute, Object value) {
        modelWidget.addAttribute(tagAttribute, value);
    }

}
