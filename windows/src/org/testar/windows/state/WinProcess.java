/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testar.core.Assert;
import org.testar.core.util.Util;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.state.SUT;
import org.testar.core.state.SUTBase;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.devices.AWTKeyboard;
import org.testar.core.devices.AWTMouse;
import org.testar.core.devices.KBKeys;
import org.testar.core.devices.Keyboard;
import org.testar.core.devices.Mouse;
import org.testar.core.exceptions.FruitException;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;

import org.testar.core.serialisation.LogSerialiser;
import org.testar.windows.ActivateOptions;
import org.testar.windows.WinProcHandle;
import org.testar.windows.WinProcessActivator;
import org.testar.windows.Windows;
import org.testar.windows.exceptions.WinApiException;

public final class WinProcess extends SUTBase {

    private static final String EMPTY_STRING = ""; // by wcoux

    public static boolean politelyToForeground(long hwnd) throws WinApiException{
        return Windows.SetForegroundWindow(hwnd);
    }
    
    public static void toForeground(long pid) throws WinApiException{
        toForeground(pid, 0.3, 100);
    }

    public static void toForeground(long pid, double foregroundEstablishTime, int maxTries) throws WinApiException{
        Keyboard kb = AWTKeyboard.build();

        int cnt = 0;
        while(!isForeground(pid) && cnt < maxTries && isRunning(pid)){
            cnt++;
            kb.press(KBKeys.VK_ALT);

            for(int i = 0; i < cnt && isRunning(pid); i++){
                kb.press(KBKeys.VK_TAB);
                kb.release(KBKeys.VK_TAB);
            }
            kb.release(KBKeys.VK_ALT);
            Util.pause(foregroundEstablishTime);
        }    

        if(!isForeground(pid) && isRunning(pid))
            throw new WinApiException("Unable to bring process to foreground!");
    }

    public static WinProcess fromPID(long pid) throws SystemStartException{
        try{
            long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION, false, pid);
            WinProcess ret = new WinProcess(hProcess, false);
            ret.set(Tags.Desc, procName(pid)); // + " (pid: " + pid + ")");
            return ret;
        }catch(FruitException fe){
            throw new SystemStartException(fe);
        }
    }

    public static WinProcess fromProcName(String processName) throws SystemStartException{
        Assert.notNull(processName);
        for(WinProcHandle wph : runningProcesses()){
            if(processName.equals(wph.name()))
                return fromPID(wph.pid());
        }
        throw new SystemStartException("Process '" + processName + "' not found!");
    }
    
    // by urueda
    public static List<SUT> fromAll(){
        List<WinProcHandle> processes = runningProcesses();
        if (processes == null || processes.isEmpty())
            return null;
        List<SUT> suts = new ArrayList<SUT>();
        for(WinProcHandle wph : processes){
            try{
                suts.add(fromPID(wph.pid()));
            } catch(Exception e){} // non interesting process
        }
        return suts;
    }

    /**
     * Execute the indicated SUT path and create a handle to the SUT process. 
     * Optionally connect with the SUT output and error buffer if ProcessListenerEnabled is enabled. 
     * Optionally indicate a regex expression for multi SUTProcesses. 
     * 
     * @param path
     * @param ProcessListenerEnabled
     * @param SUTProcesses
     * @return
     * @throws SystemStartException
     */
    public static WinProcess fromExecutable(String path, boolean ProcessListenerEnabled, String SUTProcesses) throws SystemStartException{
        try{
            Assert.notNull(path);

            //Disabled with browsers, only allow it with desktop applications executed with command_line
            if(!ProcessListenerEnabled) {

                long handles[] = Windows.CreateProcess(null, path, false, 0, null, null, null, "unknown title", new long[14]);
                long processHandle = handles[0];
                long threadHandle = handles[1];
                Windows.CloseHandle(threadHandle);

                WinProcess ret = new WinProcess(processHandle, true, SUTProcesses);
                ret.set(Tags.Desc, path);
                return ret;
            }
            
            //Associate Output / Error from SUT

            final Process process = Runtime.getRuntime().exec(path);
            long pid = process.pid();
            long processHandle = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION, false, pid);
            
            //TODO: WaitForInputIdle is not working with java app, investigate this issue.
            //TODO: Read Util.pause with new "Tags.SUTwaitInput" (think Tag name) from settings file
            if(path.contains("java -jar"))
                Util.pause(5);
            else
                Windows.WaitForInputIdle(processHandle);
            
            WinProcess returnProcess = new WinProcess(processHandle, false, SUTProcesses);
            
            returnProcess.set(Tags.StdErr,process.getErrorStream());
            returnProcess.set(Tags.StdOut, process.getInputStream());
            returnProcess.set(Tags.StdIn, process.getOutputStream());
            
            //Investigate why this cause issue with cpu
            //Windows.CloseHandle(procHandle);
            
            returnProcess.set(Tags.Path, path);
            returnProcess.set(Tags.Desc, path);
            return returnProcess;
        }catch(FruitException | IOException fe){
            throw new SystemStartException(fe);
        }
    }

    // begin by wcoux
    
    public static WinProcess fromExecutableUwp(String appUserModelId) throws SystemStartException{
        try{

            Assert.notNull(appUserModelId);

            // Initialize COM library; fails if already initialized but ignore it.
            Windows.CoInitializeEx(0, Windows.COINIT_MULTITHREADED);


            // Create COM object to launch UWP applications.
            if (pApplicationActivationManager == 0) {
                pApplicationActivationManager = Windows.CoCreateInstance(Windows.Get_CLSID_ApplicationActivationManager_Ptr(),
                        0, Windows.CLSCTX_INPROC_SERVER, Windows.Get_IID_IApplicationActivationManager_Ptr());
            }


            // Launch the application: it will return its PID.
            long pid = Windows.IApplicationActivationManager_ActivateApplication(pApplicationActivationManager,
                    appUserModelId, EMPTY_STRING, ActivateOptions.AO_NOERRORUI.getValue());


            // Process handle to Core.
            long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION + Windows.PROCESS_TERMINATE, false, pid);


            // SUT startup time.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // After launch the application should be the foreground.
            long fHwnd = Windows.GetForegroundWindow();
            long framePid = Windows.GetWindowProcessId(fHwnd);

            long fhProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION + Windows.PROCESS_TERMINATE, false, framePid);


            // Create a process wrapper, set the description for the widget to the appUserModelId.
            WinProcess ret = new WinProcess(fhProcess, true);
            ret.set(Tags.Desc, appUserModelId);

            return ret;

        }catch(WinApiException we){
            throw new WinApiException(we.getMessage());
        }catch(FruitException fe){
            LogSerialiser.log(appUserModelId + " - " + ActivateOptions.AO_NOERRORUI.getValue());
            System.out.println(appUserModelId + " - " + ActivateOptions.AO_NOERRORUI.getValue());
            throw new SystemStartException(fe);
        }
    }

    // end by wcoux

    public static boolean isForeground(long pid){
        long hwnd = Windows.GetForegroundWindow();
        long wpid = Windows.GetWindowProcessId(hwnd);
        //System.out.println("foreground pid wanted: " + pid + "- windowHandle: " + windowHandle + " - wpid: " + wpid);
        return !Windows.IsIconic(hwnd) && (wpid == pid);
    }

    public static boolean isRunning(long pid){
        long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION, false, pid);
        boolean ret = Windows.GetExitCodeProcess(hProcess) == Windows.STILL_ACTIVE;
        Windows.CloseHandle(hProcess);
        return ret;
    }

    public static void killProcess(long pid) throws SystemStopException{
        try{
            long hProcess = Windows.OpenProcess(Windows.PROCESS_TERMINATE, false, pid);
            Windows.TerminateProcess(hProcess, -1);
            Windows.CloseHandle(hProcess);
        }catch(WinApiException wae){
            throw new SystemStopException(wae);
        }
    }

    public static String procName(long pid) throws WinApiException{
        long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION | Windows.PROCESS_VM_READ, false, pid);
        long[] hm = Windows.EnumProcessModules(hProcess);
        String ret = null;
        if(hm.length == 0)
            throw new WinApiException("Unable to retrieve process name!");
        ret = Windows.GetModuleBaseName(hProcess, hm[0]);
        Windows.CloseHandle(hProcess);
        return ret;
    }

    public static List<WinProcHandle> runningProcesses(){
        List<WinProcHandle> ret = Util.newArrayList();
        for(long pid : Windows.EnumProcesses()){
            if(pid != 0)
                ret.add(new WinProcHandle(pid));
        }
        return ret;
    }

    /**
     * by urueda
     */
    public static long getMemUsage(WinProcess wp){
        long pid = -1;
        try{
            pid = wp.pid();
        } catch(IllegalStateException e){
            System.out.println("SUT is not running - cannot retrieve RAM usage");
            return -1;
        }
        return Windows.GetProcessMemoryInfo(pid);
    }
    
    /**
     * by urueda
     */
    public static long[] getCPUsage(WinProcess wp){
        long pid = -1;
        try{
            pid = wp.pid();
        } catch(IllegalStateException e){
            System.out.println("SUT is not running - cannot retrieve CPU usage");
            return new long[]{-1,-1};
        }
        return Windows.GetProcessTimes(pid);
    }
    
    long hProcess;
    final boolean stopProcess;
    final Keyboard kbd = AWTKeyboard.build();
    final Mouse mouse = AWTMouse.build();
    final long pid;
    transient static long pApplicationActivationManager; // by wcoux
    String SUTProcesses;

    private WinProcess(long hProcess, boolean stopProcess){
        this.hProcess = hProcess;
        this.stopProcess = stopProcess;
        pid = pid();
    }

    // Method for multi-processes SUTs
    // The hProcess corresponds to the main process
    // And SUTProcesses corresponds to the multi-processes' names
    private WinProcess(long hProcess, boolean stopProcess, String SUTProcesses){
        this.hProcess = hProcess;
        this.stopProcess = stopProcess;
        this.SUTProcesses = SUTProcesses;
        pid = pid();
    }

    public void finalize(){
        stop();
        release(); // by wcoux
    }


    /**
     * @author: wcoux
     */
    public void release(){
        if(pApplicationActivationManager != 0){
            Windows.IUnknown_Release(pApplicationActivationManager);
            Windows.CoUninitialize();
            pApplicationActivationManager = 0;
        }
        // begin by urueda
        if (this.getNativeAutomationCache() != null)
            this.getNativeAutomationCache().releaseCachedAutomationElements();
        // end by urueda
    }


    public void stop() throws SystemStopException {
        try{
            if(hProcess != 0){
                if(stopProcess)
                    Windows.TerminateProcess(hProcess, 0);
                Windows.CloseHandle(hProcess);
                hProcess = 0;
            }
        }catch(WinApiException wae){
            throw new SystemStopException(wae);
        }
    }

    public boolean isRunning() {
        return (hProcess != 0 && Windows.GetExitCodeProcess(hProcess) == Windows.STILL_ACTIVE) 
                || multiProcessRunning();
    }

    private boolean multiProcessRunning() {
        if(SUTProcesses == null || SUTProcesses.isEmpty()) return false;

        for(WinProcHandle wph : runningProcesses()){
            if(wph.name() != null && !wph.name().isEmpty()) {
                Pattern pattern = Pattern.compile(SUTProcesses, Pattern.UNICODE_CHARACTER_CLASS);
                Matcher matcher = pattern.matcher(wph.name());

                // If the process name matches the regex, return true
                if (matcher.matches()) {
                    return true;
                }
            }
        }

        // If none of the process names matches the regex, return false
        return false;
    }

    public String toString(){
        return this.get(Tags.Desc, "Windows Process");
    }

    public long pid(){
        if(!isRunning())
            throw new IllegalStateException("Error trying to get the pid of the SUT windows process!");
        return Windows.GetProcessId(hProcess);
    }

    public boolean isForeground(){ return isForeground(pid()); }
    public void toForeground(){ toForeground(pid()); }
    
    @SuppressWarnings("unchecked")
    protected <T> T fetch(Tag<T> tag){        
        if(tag.equals(Tags.StandardKeyboard))
            return (T)kbd;
        else if(tag.equals(Tags.StandardMouse))
            return (T)mouse;
        else if(tag.equals(Tags.PID))
            return (T)(Long)pid;
        // begin by urueda
        else if (tag.equals(Tags.HANDLE))
            return (T)(Long)hProcess;
        // end by urueda
        else if(tag.equals(Tags.ProcessHandles))
            return (T)runningProcesses().iterator();
        else if(tag.equals(Tags.SystemActivator))
            return (T) new WinProcessActivator(pid);
        return null;
    }
    
    protected Set<Tag<?>> tagDomain(){
        Set<Tag<?>> ret = Util.newHashSet();
        ret.add(Tags.StandardKeyboard);
        ret.add(Tags.StandardMouse);
        ret.add(Tags.ProcessHandles);
        ret.add(Tags.PID);
        ret.add(Tags.SystemActivator);
        return ret;
    }
    
    public String getStatus(){
        return "PID[ " + this.pid + " ] & HANDLE[ " + this.hProcess + " ] ... " + this.get(Tags.Desc,"");
    }

    @Override
    public void setNativeAutomationCache() {
        this.nativeAutomationCache = new AutomationCache(){
            @Override
            public void nativeReleaseAutomationElement(long elementPtr){
                /*long refCount =*/ Windows.IUnknown_Release(elementPtr);
                //System.out.println("Released automation uiaElement <" + elementPtr + " > reference count: " + refCount);
            }
            @Override
            public long nativeGetAutomationElementFromHandle(long automationPtr, long hwndPtr){
                return Windows.IUIAutomation_ElementFromHandle(automationPtr, hwndPtr);
            }
            @Override
            public long[] nativeGetAutomationElementBoundingRectangl(long cachedAutomationElementPtr, boolean fromCache){
                return  Windows.IUIAutomationElement_get_BoundingRectangle(cachedAutomationElementPtr, fromCache);
            }
            @Override
            public long nativeGetAutomationElementFromHandleBuildCache(long automationPtr, long hwndPtr, long cacheRequestPtr){
                return Windows.IUIAutomation_ElementFromHandleBuildCache(automationPtr, hwndPtr, cacheRequestPtr);
            }
        };
    }

}
