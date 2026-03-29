/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action;

import org.testar.core.Assert;
import org.testar.core.alayer.AbsolutePosition;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Position;
import org.testar.core.alayer.Role;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.exceptions.PositionException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;

public final class MouseMove extends TaggableBase implements Action {

    private static final long serialVersionUID = 3689287467588080030L;
    private final Position position;
    private final double minDuration;

    public MouseMove(Point point) {
        this(new AbsolutePosition(point), 0);
    }

    public MouseMove(double x, double y) {
        this(new AbsolutePosition(x, y), 0);
    }

    public MouseMove(Position position) {
        this(position, 0);
    }

    public MouseMove(Position position, double minDuration) {
        Assert.notNull(position);
        Assert.isTrue(minDuration >= 0);
        this.position = position;
        this.minDuration = minDuration;
    }

    public String toString() {
        return "Move mouse to " + position.toString() + ".";
    }

    @Override
    public String toString(Role... discardParameters) {
        for (Role r : discardParameters) {
            if (r.name().equals(ActionRoles.MouseMove.name())) {
                return "Mouse moved";
            }
        }
        return toString();
    }

    public void run(SUT system, State state, double duration) {
        try {
            Assert.notNull(system, state);
            Point p = position.apply(state);
            Util.moveCursor(system.get(Tags.StandardMouse), p.x(), p.y(), Math.max(duration, minDuration));
        } catch (NoSuchTagException tue) {
            throw new ActionFailedException(tue);
        } catch (PositionException pe) {
            throw new ActionFailedException(pe);
        }
    }

    @Override
    public String toShortString() {
        Role r = get(Tags.Role, null);
        if (r != null) {
            return r.toString() + toParametersString();
        } else {
            return toString();
        }
    }

    @Override
    public String toParametersString() {
        //return position.toString();
        return "";
    }
}
