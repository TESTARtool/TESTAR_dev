/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows;

import org.testar.core.environment.IEnvironment;

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
