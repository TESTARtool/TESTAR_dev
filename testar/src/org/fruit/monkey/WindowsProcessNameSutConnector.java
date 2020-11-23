package org.fruit.monkey;

import es.upv.staq.testar.NativeLinker;
import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.SystemStartException;

import java.util.List;

public class WindowsProcessNameSutConnector implements SutConnector{

    private String processName;
    private double maxEngageTime;

    public WindowsProcessNameSutConnector(String processName, double maxEngageTime) {
        this.processName = processName;
        this.maxEngageTime = maxEngageTime;
    }

    @Override
    public SUT startOrConnectSut()  throws SystemStartException{
        Assert.hasText(processName);
        List<SUT> suts = null;
        long now = System.currentTimeMillis();
        //final double MAX_ENGAGE_TIME = Math.round(settings().get(ConfigTags.StartupTime) * 1000.0);
        do{
            Util.pauseMs(100);
            suts = NativeLinker.getNativeProcesses();
            if (suts != null){
                String desc;
                for (SUT theSUT : suts){
                    desc = theSUT.get(Tags.Desc, null);
                    if (desc != null && desc.contains(processName)){
                        System.out.println("SUT with Process Name -" + processName + "- DETECTED!");
                        return theSUT;
                    }
                }
            }
        } while (System.currentTimeMillis() - now < maxEngageTime);
        throw new SystemStartException("SUT Process Name not found!: -" + processName + "-");
    }

}
