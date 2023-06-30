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

import org.testar.monkey.Assert;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.StateBuildException;

import java.util.concurrent.*;

public class YoloStateBuilder implements StateBuilder {
	private static final long serialVersionUID = 8199967877414181450L;

	private static final int defaultThreadPoolCount = 1;
	private final double timeOut;
	private transient ExecutorService executor;
	//private final YoloDnnModel yoloModel;
	private final YoloPythonModel yoloPyModel;

	public YoloStateBuilder(double timeOut, 
			String yoloProjectAbsolutePath, 
			String yoloPythonServiceRelativePath, 
			String yoloModelAbsolutePath, 
			String yoloInputImagesDirectory,
			String yoloModelOutputDirectory) {

		Assert.isTrue(timeOut > 0);
		this.timeOut = timeOut;

		// In this YoloStateBuilder class we load and start the Yolo model only once
		this.yoloPyModel = new YoloPythonModel(
				yoloProjectAbsolutePath, 
				yoloPythonServiceRelativePath, 
				yoloModelAbsolutePath, 
				yoloInputImagesDirectory, 
				yoloModelOutputDirectory);

		// Needed to be able to schedule asynchronous tasks conveniently.
		executor = Executors.newFixedThreadPool(defaultThreadPoolCount);
	}

	@Override
	public State apply(SUT system) throws StateBuildException {
		try {
			// Future YoloStateFetcher calls send the images to the loaded Yolo model
			Future<YoloState> future = executor.submit(new YoloStateFetcher(system, yoloPyModel));
			YoloState state = future.get((long) (timeOut), TimeUnit.SECONDS);
			return state;
		}
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new StateBuildException(e.getMessage());
		}
		catch (TimeoutException e) {
			YoloRootElement rootElement = YoloStateFetcher.buildRoot(system);
			YoloState YoloState = new YoloState(rootElement);
			YoloState.set(Tags.Role, Roles.Process);
			YoloState.set(Tags.NotResponding, true);

			return YoloState;
		}
	}
}
