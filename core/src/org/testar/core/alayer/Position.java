/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.io.Serializable;

import org.testar.core.state.State;
import org.testar.core.exceptions.PositionException;

/**
 * A position is a function that operates on a state and calculates a concrete point.
 * For example: A position could calculate "The center of the Button with the title 'OK'".
 * In order to do that, the position first needs to find the Button within the widget tree
 * (i.e. the state). Then it will get the button's <code>Shape</code> tag and calculate
 * the center position.
 */
public interface Position extends Serializable {

    Point apply(State state) throws PositionException;

    public void obscuredByChildFeature(boolean enable);
}
