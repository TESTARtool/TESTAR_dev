/***************************************************************************************************
 *
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.screenshot;

import org.testar.monkey.alayer.Action;

import java.util.Objects;
import java.util.Set;

import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.android.util.AndroidScreenshotUtil;
import org.testar.monkey.alayer.webdriver.util.WdScreenshotUtil;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.util.ScreenshotUtil;

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
