package org.fruit.monkey;

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.ProtocolUtil;
import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;

import java.util.List;

public class WindowsWindowTitleSutConnector implements SutConnector{

    private String windowTitle;
    private double maxEngangeTime;
    private StateBuilder builder;

    public WindowsWindowTitleSutConnector(String windowTitle, double maxEngangeTime, StateBuilder builder) {
        this.windowTitle = windowTitle;
        this.maxEngangeTime = maxEngangeTime;
        this.builder = builder;
    }

    @Override
    public SUT startOrConnectSut() throws SystemStartException {
        Assert.hasText(windowTitle);
        List<SUT> suts = null;
        State state; Role role; String title;
        long now = System.currentTimeMillis();
        //final double MAX_ENGAGE_TIME = Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0);
        do{
            Util.pauseMs(100);
            suts = NativeLinker.getNativeProcesses();
            if (suts != null){
                for (SUT theSUT : suts){
                    state = getStateByWindowTitle(theSUT);
                    if (state.get(Tags.Foreground)){
                        for (Widget w : state){
                            role = w.get(Tags.Role, null);
                            if (role != null && Role.isOneOf(role, NativeLinker.getNativeRole_Window())){
                                title = w.get(Tags.Title, null);
                                if (title != null && title.contains(windowTitle)){
                                    System.out.println("SUT with Window Title -" + windowTitle + "- DETECTED!");
                                    return theSUT;
                                }
                            }
                        }
                    }
                }
            }
        } while (System.currentTimeMillis() - now < maxEngangeTime);
        throw new SystemStartException("SUT Window Title not found!: -" + windowTitle + "-");
    }


    protected State getStateByWindowTitle(SUT system) throws StateBuildException {
        Assert.notNull(system);
        State state = builder.apply(system);

        CodingManager.buildIDs(state);

        Shape viewPort = state.get(Tags.Shape, null);
        if(viewPort != null){
            state.set(Tags.ScreenshotPath, ProtocolUtil.getStateshot(state));
        }

        state = ProtocolUtil.calculateZIndices(state);

        return state;
    }

}
