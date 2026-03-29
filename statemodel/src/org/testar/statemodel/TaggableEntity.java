/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.core.tag.Tag;
import org.testar.core.tag.TaggableBase;

public abstract class TaggableEntity {

    // a set of attributes and values
    private final TaggableBase attributes;

    /**
     * TaggableEntity Constructor
     */
    public TaggableEntity() {
        this.attributes = new TaggableBase();
    }

    /**
     * This method adds a custom attribute to the entity in the form of a tag and its value
     * @param attribute
     * @param value
     */
    public void addAttribute(Tag attribute, Object value) {
        try {
            attributes.set(attribute, value);
        } catch (Exception e) { //TODO check what kind of exceptions can happen
            System.out.println("Problem adding value for tag " + attribute.name() + " to abstract state");
        }
    }

    /**
     * This method returns the 'attributes' that have been added to this entity
     * @return
     */
    public TaggableBase getAttributes() {
        return attributes;
    }

}
