/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.FruitException;
import org.fruit.Util;
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

	private static final String EMPTY_STRING = ""; // by wcoux

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
		List<WinProcHandle> processes = runningProcesses();;
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

	public static WinProcess fromExecutable(String path) throws SystemStartException{
		try{
			Assert.notNull(path);
			long handles[] = Windows.CreateProcess(null, path, false, 0, null, null, null, "unknown title", new long[14]);
			long hProcess = handles[0];
			long hThread = handles[1];
			Windows.CloseHandle(hThread);

			WinProcess ret = new WinProcess(hProcess, true);
			ret.set(Tags.Desc, path);
			return ret;
		}catch(FruitException fe){
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
		//System.out.println("foreground pid wanted: " + pid + "- hwnd: " + hwnd + " - wpid: " + wpid);
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
		}
		return Windows.GetProcessTimes(pid);
	}
	
	long hProcess;
	final boolean stopProcess;
	final Keyboard kbd = AWTKeyboard.build();
	final Mouse mouse = AWTMouse.build();
	final long pid;
	transient static long pApplicationActivationManager; // by wcoux

	private WinProcess(long hProcess, boolean stopProcess){
		this.hProcess = hProcess;
		this.stopProcess = stopProcess;
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
		return hProcess != 0 && 
				Windows.GetExitCodeProcess(hProcess) == Windows.STILL_ACTIVE;
	}

	public String toString(){
		return this.get(Tags.Desc, "Windows Process");
	}

	public long pid(){
		if(!isRunning())
			throw new IllegalStateException();
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
	
}