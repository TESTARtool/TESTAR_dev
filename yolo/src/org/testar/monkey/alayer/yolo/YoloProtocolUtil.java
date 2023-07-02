/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.yolo;

import org.testar.ProtocolUtil;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.serialisation.ScreenshotSerialiser;

public class YoloProtocolUtil extends ProtocolUtil {

	public static String getStateshot(State state) {
		Shape viewPort = state.get(Tags.Shape, null);

		//If the state Shape is not properly obtained, or the State has an error, use full monitor screen
		if (viewPort == null || (state.get(Tags.OracleVerdict, Verdict.OK).severity() > Verdict.SEVERITY_OK))
			viewPort = state.get(Tags.Shape, null); // get the SUT process canvas (usually, full monitor screen)

		if (viewPort.width() <= 0 || viewPort.height() <= 0)
			return null;

		Rect stateRect = Rect.from(viewPort.x(), viewPort.y(), viewPort.width(), viewPort.height());

		AWTCanvas scrshot = AWTCanvas.fromScreenshot(stateRect, state.get(Tags.HWND, (long)0), AWTCanvas.StorageFormat.PNG, 1);

		return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), scrshot);
	}

}
