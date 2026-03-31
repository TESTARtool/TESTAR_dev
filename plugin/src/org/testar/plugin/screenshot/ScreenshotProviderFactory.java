/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin.screenshot;

import org.testar.android.util.AndroidScreenshotUtil;
import org.testar.core.action.Action;
import org.testar.core.alayer.AWTCanvas;
import org.testar.core.screenshot.ScreenshotProvider;
import org.testar.core.state.State;
import org.testar.core.util.ScreenshotUtil;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.webdriver.util.WdScreenshotUtil;

import java.util.Objects;
import java.util.Set;

public final class ScreenshotProviderFactory {

	private static final ScreenshotProvider DEFAULT_PROVIDER = new ScreenshotProvider() {
		@Override
		public AWTCanvas getStateshotBinary(State state) {
			Objects.requireNonNull(state, "State cannot be null");
			return ScreenshotUtil.getStateshotBinary(state);
		}

		@Override
		public String getActionshot(State state, Action action) {
			Objects.requireNonNull(state, "State cannot be null");
			Objects.requireNonNull(action, "Action cannot be null");
			return ScreenshotUtil.getActionshot(state, action);
		}
	};

	private static final ScreenshotProvider WEBDRIVER_PROVIDER = new ScreenshotProvider() {
		@Override
		public AWTCanvas getStateshotBinary(State state) {
			Objects.requireNonNull(state, "State cannot be null");
			return WdScreenshotUtil.getStateshotBinary(state);
		}

		@Override
		public String getActionshot(State state, Action action) {
			Objects.requireNonNull(state, "State cannot be null");
			Objects.requireNonNull(action, "Action cannot be null");
			return WdScreenshotUtil.getActionshot(state, action);
		}
	};

	private static final ScreenshotProvider ANDROID_PROVIDER = new ScreenshotProvider() {
		@Override
		public AWTCanvas getStateshotBinary(State state) {
			Objects.requireNonNull(state, "State cannot be null");
			return AndroidScreenshotUtil.getStateshotBinary(state);
		}

		@Override
		public String getActionshot(State state, Action action) {
			Objects.requireNonNull(state, "State cannot be null");
			Objects.requireNonNull(action, "Action cannot be null");
			return AndroidScreenshotUtil.getActionshot(state, action);
		}
	};

	private ScreenshotProviderFactory() {
	}

	public static ScreenshotProvider current() {
		Set<OperatingSystems> platform = NativeLinker.getPLATFORM_OS();
		if (platform == null) {
			return DEFAULT_PROVIDER;
		}
		if (platform.contains(OperatingSystems.WEBDRIVER)) {
			return WEBDRIVER_PROVIDER;
		}
		if (platform.contains(OperatingSystems.ANDROID)) {
			return ANDROID_PROVIDER;
		}
		return DEFAULT_PROVIDER;
	}
}
