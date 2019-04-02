/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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

package org.fruit.alayer.windows;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.FruitException;
import org.fruit.Util;
import org.fruit.alayer.AutomationCache;
import org.fruit.alayer.SUT;
import org.fruit.alayer.SUTBase;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;

import es.upv.staq.testar.serialisation.LogSerialiser;

public final class WinProcess extends SUTBase {

	private static final String EMPTY_STRING = "";

	long hProcess;
	final boolean stopProcess;
	final Keyboard kbd = AWTKeyboard.build();
	final Mouse mouse = AWTMouse.build();
	final long mainPid;
	String pName = "";
	static List<Long> sutProcessesPid = Util.newArrayList();
	static List<Long> startOSPidProcesses = Util.newArrayList();
	static transient long pApplicationActivationManager;

	/**
	 * Constructor
	 * 
	 * Create a WinProcess with a process handle and boolean variable
	 * @param hProcess
	 * @param stopProcess
	 */
	private WinProcess(long hProcess, boolean stopProcess){
		this.hProcess = hProcess;
		this.stopProcess = stopProcess;
		mainPid = mainPid();
		pName = new WinProcHandle(mainPid).name();
	}

	/**
	 * Get the description of current WinProcess, or use the default value if not exist
	 */
	public String toString(){
		return this.get(Tags.Desc, "Windows Process");
	}

	/**
	 * From the current process handle use a native Windows call to obtain process pid
	 * 
	 * @return pid of current WinProcess
	 */
	public long mainPid(){
		if(!isRunning())
			throw new IllegalStateException();
		return Windows.GetProcessId(hProcess);
	}

	/**
	 * Return information about current WinProcess object
	 */
	public String getStatus(){
		return "PID[ " + this.mainPid + " ] & HANDLE[ " + this.hProcess + " ] & " +" NAME [ "+ pName+" ] ... "+ this.get(Tags.Desc,"");
	}

	/**
	 * From a introduced process pid obtain the handle of these process and check if still active
	 * 
	 * @param pid
	 * @return boolean condition if process still active
	 */
	public static boolean isRunning(long pid){
		long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION, false, pid);
		boolean ret = Windows.GetExitCodeProcess(hProcess) == Windows.STILL_ACTIVE;
		Windows.CloseHandle(hProcess);
		if(ret)
			return true;

		return oneSUTprocessIsRunning();
	}

	private static boolean oneSUTprocessIsRunning() {
		for(long pidSUT : sutProcessesPid) {
			long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION, false, pidSUT);
			boolean ret = Windows.GetExitCodeProcess(hProcess) == Windows.STILL_ACTIVE;
			Windows.CloseHandle(hProcess);
			if(ret)
				return true;
		}
		return false;
	}

	/**
	 * Use the process handle of current WinProcess to check if his process still active
	 */
	public boolean isRunning() {
		if(hProcess != 0 && Windows.GetExitCodeProcess(hProcess) == Windows.STILL_ACTIVE)
			return true;

		return oneSUTprocessIsRunning();
	}

	/**
	 * From a introduced process pid obtain the handle of these process and make him a Terminate call
	 * 
	 * @param pid
	 * @throws SystemStopException
	 */
	public static void killProcess(long pid) throws SystemStopException{
		try{
			long hProcess = Windows.OpenProcess(Windows.PROCESS_TERMINATE, false, pid);
			Windows.TerminateProcess(hProcess, -1);
			Windows.CloseHandle(hProcess);	
		}catch(WinApiException wae){
			throw new SystemStopException(wae);
		}
	}

	/**
	 * Stop and release current WinProcess
	 */
	public void finalize(){
		stop();
		release();
	}

	//TODO: pApplicationActivationManager, what is this?
	public void release(){
		if(pApplicationActivationManager != 0){
			Windows.IUnknown_Release(pApplicationActivationManager);
			Windows.CoUninitialize();
			pApplicationActivationManager = 0;
		}
		if (this.getNativeAutomationCache() != null)
			this.getNativeAutomationCache().releaseCachedAutomationElements();
	}

	/**
	 * Terminate the process of current WinProcess, using a Windows native call
	 */
	public void stop() throws SystemStopException {

		//Windows.ExitProcess(0)
		//Windows.CloseMainWindows
		//Runtime rt = Runtime.getRuntime();
		//String closeGracefully = "taskkill /pid ";
		//Process pr = rt.exec(closeGracefully+mainPid);

		try{
			if(hProcess != 0){
				if(stopProcess) 
					Windows.TerminateProcess(hProcess, 0);
				Windows.CloseHandle(hProcess);
				hProcess = 0;

				for(Long pidSUT : sutProcessesPid) {
					System.out.println("Process pid to kill: "+pidSUT);
					long hProcess = Windows.OpenProcess(Windows.PROCESS_TERMINATE, false, pidSUT);
					Windows.TerminateProcess(hProcess, -1);
					Windows.CloseHandle(hProcess);
					sutProcessesPid.remove(pidSUT);

				}
			}
		}catch(WinApiException wae){
			throw new SystemStopException(wae);
		}
	}

	/**
	 * From the pid of a process make a Windows native call to GetModuleBaseName and obtain the name of desired process
	 * 
	 * @param pid
	 * @return process name
	 * @throws WinApiException
	 */
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

	/**
	 * Compare the process identifier of foreground windows with the introduced pid
	 * 
	 * @param pid
	 * @return boolean foreground condition
	 */
	public static boolean isForeground(long pid){
		long hwnd = Windows.GetForegroundWindow();
		long wpid = Windows.GetWindowProcessId(hwnd);
		if(!Windows.IsIconic(hwnd) && (wpid == pid))
			return true;

		return oneSUTprocessIsForeground();
	}

	private static boolean oneSUTprocessIsForeground() {
		long hwnd = Windows.GetForegroundWindow();
		long wpid = Windows.GetWindowProcessId(hwnd);
		for(long pidSUT : sutProcessesPid)
			if(!Windows.IsIconic(hwnd) && (wpid == pidSUT))
				return true;
		return false;
	}

	/**
	 * Call the current WinProcess object to check if is on foreground
	 * @return boolean foreground condition
	 */
	public boolean isForeground(){

		if (isForeground(mainPid()))
			return true;

		return oneSUTprocessIsForeground();
	}

	/**
	 * Call toForeground method with harcoded settings (TODO: remove this)
	 * 
	 * @param pid
	 * @throws WinApiException
	 */
	public static void toForeground(long pid) throws WinApiException{
		toForeground(pid, 0.3, 100);
	}

	public void toForeground(){ toForeground(mainPid()); }

	/**
	 * Use ALT + TAB combination until the pid of our SUT will be on foreground or we try the maxTries count
	 * 
	 * @param pid
	 * @param foregroundEstablishTime
	 * @param maxTries
	 * @throws WinApiException
	 */
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

	/**
	 * From a process pid create a WinProcess object and return it
	 * 
	 * @param pid
	 * @return WinProcess
	 * @throws SystemStartException
	 */
	public static WinProcess fromPID(long pid) throws SystemStartException{
		try{
			long hProcess = Windows.OpenProcess(Windows.PROCESS_QUERY_INFORMATION, false, pid);
			WinProcess ret = new WinProcess(hProcess, false);
			ret.set(Tags.Desc, procName(pid));
			return ret;
		}catch(FruitException fe){
			throw new SystemStartException(fe);
		}
	}

	/**
	 * Read all Windows running processes, create a List of WinProcHandle objects that represent this processes
	 * and return this List
	 * 
	 * @return List<WinProcHandle>
	 */
	public static List<WinProcHandle> runningProcesses(){
		List<WinProcHandle> ret = Util.newArrayList();
		for(long pid : Windows.EnumProcesses()){
			if(pid != 0) {
				ret.add(new WinProcHandle(pid));
				startOSPidProcesses.add(pid);
			}
		}
		return ret;
	}

	/**
	 * From a String that represents a process name, search into existing running processes if exists.
	 * If we found the WinProcHandle with the input process name,
	 * 		create a WinProcess object from the founded process pid and return it.
	 * If doesn't exist throw an error
	 * 
	 * @param processName
	 * @return WinProcess
	 * @throws SystemStartException
	 */
	public static WinProcess fromProcName(String processName) throws SystemStartException{
		Assert.notNull(processName);
		for(WinProcHandle wph : runningProcesses()){
			if(processName.equals(wph.name()))
				return fromPID(wph.pid());
		}
		throw new SystemStartException("Process '" + processName + "' not found!");
	}

	/**
	 * Read all running processesReturn a list of all running Applications as if they were SUTs
	 * 
	 * @return List<SUT>
	 */
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

	public static WinProcess fromExecutable(String path, boolean processListenerEnabled) throws SystemStartException{
		try{
			Assert.notNull(path);

			//Disabled with browsers, only allow it with desktop applications executed with command_line
			if(!processListenerEnabled) {

				startOSPidProcesses = Util.newArrayList();
				sutProcessesPid = Util.newArrayList();

				//PID of running processes before the execution of the SUT
				List<WinProcHandle> beforeProcesses = runningProcesses();
				List<Long> beforePID = Util.newArrayList();
				for(WinProcHandle winp : beforeProcesses)
					beforePID.add(winp.pid());

				//Create the SUT process, with the executable app of the selected path
				long handles[] = Windows.CreateProcess(null, path, false, 0, null, null, null, "unknown title", new long[14]);
				long hProcess = handles[0];
				long hThread = handles[1];
				Windows.CloseHandle(hThread);

				//Wait for the SUT process until it is ready
				if(path.contains("java -jar"))
					Util.pause(2);
				else
					Windows.WaitForInputIdle(hProcess);

				WinProcess ret = new WinProcess(hProcess, true);

				//System.out.println("WinProcess Status: "+ret.getStatus());

				//Read the running processes after the execution of the SUT
				//This allow us to obtain the potential PID of SUT running processes
				List<WinProcHandle> runningProcesses = runningProcesses();
				for(WinProcHandle winp : runningProcesses) {
					if(!beforePID.contains(winp.pid()))
						sutProcessesPid.add(winp.pid());
				}

				//TODO: Think about create extra conditions to make sure that we are working with SUT process
				if(sutProcessesPid!=null) {
					for(Long info : sutProcessesPid)
						System.out.println("Potential SUT PID: "+info);
				}

				ret.set(Tags.Desc, path);
				return ret;
			}

			//Associate Output / Error from SUT

			final Process p = Runtime.getRuntime().exec(path);
			Field f = p.getClass().getDeclaredField("handle");
			f.setAccessible(true);

			long procHandle = f.getLong(p);

			//TODO: WaitForInputIdle is not working with java app, investigate this issue.
			//TODO: Read Util.pause with new "Tags.SUTwaitInput" (think Tag name) from settings file
			if(path.contains("java -jar"))
				Util.pause(5);
			else
				Windows.WaitForInputIdle(procHandle);

			long pid = Windows.GetProcessId(procHandle);

			WinProcess ret = fromPID(pid);

			ret.set(Tags.StdErr,p.getErrorStream());
			ret.set(Tags.StdOut, p.getInputStream());
			ret.set(Tags.StdIn, p.getOutputStream());

			//Investigate why this cause issue with cpu
			//Windows.CloseHandle(procHandle);

			ret.set(Tags.Path, path);
			ret.set(Tags.Desc, path);
			return ret;
		}catch(FruitException | IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException  fe){
			throw new SystemStartException(fe);
		}
	}

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

		}catch(FruitException fe){
			LogSerialiser.log(appUserModelId + " - " + ActivateOptions.AO_NOERRORUI.getValue());
			System.out.println(appUserModelId + " - " + ActivateOptions.AO_NOERRORUI.getValue());
			throw new SystemStartException(fe);
		}
	}

	/**
	 * Obtain the memory usage info of the WinProcess introduced, using a Windows native call
	 * 
	 * @param wp
	 * @return MemoryInfo
	 */
	public static long getMemUsage(WinProcess wp){
		long pid = -1;
		try{
			pid = wp.mainPid();
		} catch(IllegalStateException e){
			System.out.println("SUT is not running - cannot retrieve RAM usage");
			return -1;
		}
		return Windows.GetProcessMemoryInfo(pid);
	}

	/**
	 * Obtain the CPU usage info of the WinProcess introduced, using a Windows native call
	 * 
	 * @param wp
	 * @return CPU Info
	 */
	public static long[] getCPUsage(WinProcess wp){
		long pid = -1;
		try{
			pid = wp.mainPid();
		} catch(IllegalStateException e){
			System.out.println("SUT is not running - cannot retrieve CPU usage");
			return new long[]{-1,-1};
		}
		return Windows.GetProcessTimes(pid);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> T fetch(Tag<T> tag){		
		if(tag.equals(Tags.StandardKeyboard))
			return (T)kbd;
		else if(tag.equals(Tags.StandardMouse))
			return (T)mouse;
		else if(tag.equals(Tags.PID))
			return (T)(Long)mainPid;
		else if (tag.equals(Tags.HANDLE))
			return (T)(Long)hProcess;
		else if(tag.equals(Tags.ProcessHandles))
			return (T)runningProcesses().iterator();
		else if(tag.equals(Tags.SystemActivator))
			return (T) new WinProcessActivator(mainPid);
		return null;
	}

	@Override
	protected Set<Tag<?>> tagDomain(){
		Set<Tag<?>> ret = Util.newHashSet();
		ret.add(Tags.StandardKeyboard);
		ret.add(Tags.StandardMouse);
		ret.add(Tags.ProcessHandles);
		ret.add(Tags.PID);
		ret.add(Tags.SystemActivator);
		return ret;
	}

	@Override
	public void setNativeAutomationCache() {
		this.nativeAutomationCache = new AutomationCache(){
			@Override
			public void nativeReleaseAutomationElement(long elementPtr){
				Windows.IUnknown_Release(elementPtr);
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