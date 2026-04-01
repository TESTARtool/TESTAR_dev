/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2015-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */


package org.testar.core.devices;

public interface IEventListener {

	public abstract void keyDown(KBKeys key);

	public abstract void keyUp(KBKeys key);

	public abstract void mouseDown(MouseButtons btn, double x, double y);

	public abstract void mouseUp(MouseButtons btn, double x, double y);

	public abstract void mouseMoved(double x, double y);
	
}
