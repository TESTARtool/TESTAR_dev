package org.fruit.alayer.windows;

import org.fruit.IEnvironment;
import static org.fruit.alayer.windows.Windows.MONITOR_DEFAULTTONULL;

/**
 * A Windows 10 specific environment implementation.
 */
public final class Windows10 implements IEnvironment {
    @Override
    public double getDisplayScale(long windowHandle) {
        double result = 1.0;
        long monitorHandle = Windows.MonitorFromWindow(windowHandle, MONITOR_DEFAULTTONULL);
        if (monitorHandle == 0){
            System.out.printf("WARNING: Could not find monitor handle for window handle:%d\n",windowHandle);
            return result;
        }
        long res[] = Windows.GetScaleFactorForMonitor(monitorHandle);
        if (res.length == 2 )
        {
            return res[1] / 100.0;
        }
        System.out.printf("WARNING Failed to requested scale factor for display:%d res:%d \n",monitorHandle,res[0]);
        return result;
    }
}
