/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import org.testar.core.alayer.Rect;
import org.testar.core.tag.TaggableBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class UIAElement extends TaggableBase implements Serializable {
    private static final long serialVersionUID = -2561441199642411403L;
    List<UIAElement> children = Collections.emptyList();
    UIAElement parent;
    public UIARootElement root;
    UIAWidget backRef;
    boolean blocked, enabled, ignore, isModal,
        isContentElement, isControlElement,
        hasKeyboardFocus, isKeyboardFocusable,
        isTopmostWnd, isTopLevelContainer,
        scrollPattern, hScroll, vScroll; // by urueda
    long ctrlId, culture, orientation, windowHandle, wndInteractionState, wndVisualState;
    Rect rect;
    String name, helpText, automationId, className, providerDesc, frameworkId,
        acceleratorKey, accessKey;
    String valuePattern;

    double zindex,
        hScrollViewSize, vScrollViewSize, hScrollPercent, vScrollPercent; // by urueda

    public UIAElement(){ this(null); }

    public UIAElement(UIAElement parent){
        this.parent = parent;
        if(parent != null)
            root = parent.root;
        enabled = true;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
    }
}
