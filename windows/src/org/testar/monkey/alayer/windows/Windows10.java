/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.windows;

import org.testar.environment.IEnvironment;

/**
 * A Windows 10 specific environment implementation.
 */
public final class Windows10 implements IEnvironment {
	@Override
	public double getDisplayScale(long windowHandle) {
		double result = 1.0;
		try {
			long monitorHandle = Windows.MonitorFromWindow(windowHandle, Windows.MONITOR_DEFAULTTONULL);
			if (monitorHandle == 0){
				System.out.printf("WARNING: Could not find monitor handle for window handle:%d\n",windowHandle);
				return result;
			}
			long res[] = Windows.GetScaleFactorForMonitor(monitorHandle);
			if (res.length == 2 )
			{
				double scale = res[1] / 100.0;
				if (scale != 1.0) {
					System.out.printf("WARNING: DPI scaling is not 100%%. Detected scale: %.2f (on monitor %d)\n", scale, monitorHandle);
					System.out.println("WARNING: This might affect the coordinates to interact with GUI widgets coordinates");
				}
				return scale;
			}
			System.out.printf("WARNING Failed to requested scale factor for display:%d res:%d \n",monitorHandle,res[0]);
		} catch(NoClassDefFoundError nce) {
			System.out.println("WARNING: TESTAR was not able to obtain Windows10 environment displayScale value");
		} catch (Exception e) {
			System.out.println("WARNING: Unexpected error while getting the Windows displayScale value");
		}

		return result;
	}
}
