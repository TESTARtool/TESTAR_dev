/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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


package es.upv.staq.testar;

import org.fruit.alayer.*;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.linux.*;
import org.fruit.alayer.webdriver.WdCanvas;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.alayer.webdriver.WdStateBuilder;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.alayer.windows.*;

import java.util.*;

import static org.fruit.alayer.linux.AtSpiRolesWrapper.*;
import static org.fruit.alayer.windows.UIARoles.*;

/**
 * A native connector.
 */
public class NativeLinker {
	
	private NativeLinker() {}
	
	private static long lastCPUquery = 0;

	private static EnumSet<OperatingSystems> PLATFORM_OS = determinePlatform();
	private static String osName;
	
	/**
	 * Determines the platform this executable is currently running on.
	 * @return The Operating system the executable is currently running on.
	 * @author: wcoux
	 */
	private static EnumSet<OperatingSystems> determinePlatform() {
		osName = System.getProperty("os.name");
		switch (osName) {
		case "Windows 10":
			return EnumSet.of(OperatingSystems.WINDOWS, OperatingSystems.WINDOWS_10);
		case "Windows 7":
			return EnumSet.of(OperatingSystems.WINDOWS, OperatingSystems.WINDOWS_7);
		default:
			if (osName.contains("Windows"))
				return EnumSet.of(OperatingSystems.WINDOWS);
			else if(osName.contains("Linux"))
				return EnumSet.of(OperatingSystems.UNIX);
			else
				return EnumSet.of(OperatingSystems.UNKNOWN);
		}
	}

	public static void addWdDriverOS() {
		PLATFORM_OS.add(OperatingSystems.WEBDRIVER);
	}
	
	public static void cleanWdDriverOS() {
		PLATFORM_OS.remove(OperatingSystems.WEBDRIVER);
	}
	
	public static Set<OperatingSystems> getPLATFORM_OS() {
		return PLATFORM_OS;
	}

	/**
	 * Retrieves a StateBuilder instance which will be used to determine the state of the application.
	 * @param timeToFreeze The time after which requesting the state of an application will time out.
	 * @param accessBridgeEnabled Whether to activate the AccessBridge (Java/Swing SUTs).
	 * @param SUTProcesses A regex of the set of processes that conform the SUT.
	 * @return A StateBuilder instance.
	 */
	public static StateBuilder getNativeStateBuilder(Double timeToFreeze,
			boolean accessBridgeEnabled,
			String SUTProcesses){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return new WdStateBuilder(timeToFreeze);
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_7))
				return new UIAStateBuilder(timeToFreeze, accessBridgeEnabled, SUTProcesses);
			else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_10)) {
				// TODO: a win10 state builder might make use of the new CUI8 Automation object.
				return new UIAStateBuilder(timeToFreeze, accessBridgeEnabled, SUTProcesses);
			}
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return new AtSpiStateBuilder(timeToFreeze);
		System.out.println("TESTAR detected OS: " + osName + " and this is not yet supported. If the detected OS is wrong, please contact the TESTAR team at info@testar.org. Exiting with Exception.");
		throw new UnsupportedPlatformException();
	}


	/**
	 * Retrieves a canvas for the primary monitor so Testar can paint elements on screen in Spy mode.
	 * @param pen A pen with which to paint on the screen.
	 * @return A Canvas on which Testar can paint elements in Spy mode.
	 */
	public static Canvas getNativeCanvas(Pen pen){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return new WdCanvas(pen);
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			return GDIScreenCanvas.fromPrimaryMonitor(pen);
			//return JavaScreenCanvas.fromPrimaryMonitor(pen);
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX)) {
			// TODO: spy mode on linux still needs to be implemented - The Canvas is also being used by Test mode.
			return new GdkScreenCanvas();
			//return JavaScreenCanvas.fromPrimaryMonitor(pen);
		}
		throw new UnsupportedPlatformException();
	}


	/**
	 * Opens a SUT (runs a process) on the machine and returns a handle to the process in a SUT object.
	 * @param executableCommand The application/ process/ command that will be run.
	 * @return A handle to the process in a SUT object.
	 */
	public static SUT getNativeSUT(String executableCommand, boolean ProcessListenerEnabled){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdDriver.fromExecutable(executableCommand);
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_7))
				return WinProcess.fromExecutable(executableCommand, ProcessListenerEnabled);
			else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_10)) {
				if (executableCommand.toLowerCase().contains(".exe") || executableCommand.contains(".jar"))
					return WinProcess.fromExecutable(executableCommand, ProcessListenerEnabled);
				else
					return WinProcess.fromExecutableUwp(executableCommand);
			}
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return LinuxProcess.fromExecutable(executableCommand);
		throw new UnsupportedPlatformException();
	}


	/**
	 * Gets a list of running processes wrapped in a SUT class.
	 * @return A list of running processes wrapped in a SUT class.
	 */
	public static List<SUT> getNativeProcesses(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdDriver.fromAll();
		else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return WinProcess.fromAll();
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return LinuxProcess.fromAll();
		throw new UnsupportedPlatformException();
	}

	public static SUT getNativeProcess(String processName){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return WinProcess.fromProcName(processName);
		//else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
		// TODO
		throw new UnsupportedPlatformException();
	}

	public static ProcessHandle getNativeProcessHandle(long processPID){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return new WinProcHandle(processPID);
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return new LinuxProcessHandle(processPID);
		throw new UnsupportedPlatformException();
	}

	/** Gets the memory usage for a SUT.
	 * @param nativeSUT The SUT to get memory usage for.
	 * @return Memory usage in KB.
	 */
	public static int getMemUsage(SUT nativeSUT){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return 0;
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return (int)(WinProcess.getMemUsage((WinProcess)nativeSUT) / 1024); // byte -> KB
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return (int)(LinuxProcess.getMemUsage((LinuxProcess)nativeSUT) / 1024);
		throw new UnsupportedPlatformException();
	}


	/** Gets the CPU usage for a SUT.
	 * @param nativeSUT The SUT to get CPU usage for.
	 * @return CPU usage in ms: user x system x frame.
	 */
	public static long[] getCPUsage(SUT nativeSUT){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER)) {
			// TODO Make sure 'runTest' doesn't need this anymore
			return new long[]{0, 0, 0};
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			long now = System.currentTimeMillis();
			long cpuFrame = now - lastCPUquery;
			lastCPUquery = now;
			long[] cpums = WinProcess.getCPUsage((WinProcess)nativeSUT);
			return new long[]{ cpums[0], cpums[1], cpuFrame };
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX)) {
			// TODO: this probably needs to change somehow...
			return new long[] { (long)(LinuxProcess.getCpuUsage((LinuxProcess)nativeSUT)),
					(long)(LinuxProcess.getCpuUsage((LinuxProcess)nativeSUT)),
					(long)(LinuxProcess.getCpuUsage((LinuxProcess)nativeSUT))};
		}
		throw new UnsupportedPlatformException();
	}

	public static Collection<Role> getNativeRoles(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER)) {
			return WdRoles.rolesSet();
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.rolesSet();
		// else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
		// TODO
		throw new UnsupportedPlatformException();
	}

	public static Role getNativeRole(String roleName){
		Collection<Role> roles = getNativeRoles();
		Role role = null, r;
		Iterator<Role> it = roles.iterator();
		while (role == null && it.hasNext()){
			r = (Role) it.next();
			if (r.name().equals(roleName))
				return r;
		}
		return null; // not found
	}

	public static Collection<Role> getNativeRoles(String... roleNames){
		Collection<Role> roles = new ArrayList<Role>(roleNames.length);
		for (String roleName : roleNames){
			roles.add(getNativeRole(roleName));
		}
		return roles;
	}

	/**
	 * Gets the native Role wrapper for a window.
	 * @return The native Role wrapper for a window.
	 */
	public static Role getNativeRole_Window(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.UIAWindow;
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiRolesWrapper.AtSpiWindow;
		throw new UnsupportedPlatformException();
	}

	/**
	 * Gets the native Role wrapper for a button.
	 * @return The native Role wrapper for a button.
	 */
	public static Role getNativeRole_Button(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdRoles.WdBUTTON;
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.UIAButton;
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiRolesWrapper.AtSpiPushButton;
		throw new UnsupportedPlatformException();
	}

	/**
	 * Gets the native Role wrapper for a menu item.
	 * @return The native Role wrapper for a menu item.
	 */
	public static Role getNativeRole_Menuitem(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdRoles.WdUnknown;
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.UIAMenuItem;
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiMenuItem;
		throw new UnsupportedPlatformException();
	}

	/**
	 * Get native tags.
	 * @return Native tags.
	 */
	public static Set<Tag<?>> getNativeTags(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdTags.tagSet();
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIATags.tagSet();
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiTags.tagSet();
		throw new UnsupportedPlatformException();
	}

	/**
	 * Gets a native tag by its name.
	 * @param tagName A native tag name.
	 * @return The native tag.
	 */
	public static Tag<?> getNativeTag(String tagName){
		for (Tag<?> tag : getNativeTags()){
			if (tag.name().equals(tagName))
				return tag;
		}
		return null; // not found
	}

	/**
	 * Gets the value of the native boolean property of a widget.
	 * @param widget The widget.
	 * @param booleanPropertyName A native boolean property name.
	 * @return true/false value for the widget native boolean property.
	 * @throws NoSuchTagException If the property is not available for the widget.
	 */
	@SuppressWarnings("unchecked")
	public static boolean getNativeBooleanProperty(Widget widget, String booleanPropertyName) throws NoSuchTagException {
		Tag<Boolean> tag = null;
		try {
			tag = (Tag<Boolean>) getNativeTag(booleanPropertyName);
			return widget.get(tag).booleanValue();
		} catch (Exception e) {
			throw new NoSuchTagException(tag);
		}
	}

	/**
	 * Gets all roles that correspond to elements that can be clicked.
	 * @return All roles that correspond to elements that can be clicked.
	 */
	public static Role[] getNativeClickableRoles(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdRoles.nativeClickableRoles();
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			return new Role[]{UIAMenu, UIAMenuItem, UIAButton, UIACheckBox, UIARadioButton,
					UIAComboBox, UIAList, UIAListItem,
					UIATabItem, UIAHyperlink, UIADataItem, UIATree, UIATreeItem,
					UIASlider, UIASpinner, UIAScrollBar, UIASplitButton,
					UIACustomControl}; // be careful on custom control (we do not know what they are)
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX)) {
			return new Role[]{AtSpiCheckBox, AtSpiCheckMenuItem, AtSpiComboBox, AtSpiMenuItem,
					AtSpiListItem, AtSpiSpinButton, AtSpiToggleButton, AtSpiTreeItem, AtSpiListBox,
					AtSpiPushButton, AtSpiLink, AtSpiScrollBar};
		}
		throw new UnsupportedPlatformException();
	}

	/**
	 * Gets all roles that correspond to elements that can be edited.
	 * @return All roles that correspond to elements that can be edited.
	 */
	public static Role[] getNativeTypeableRoles(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdRoles.nativeTypeableRoles();
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return new Role[]{UIADocument, UIAEdit, UIAText};
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return new Role[]{AtSpiPasswordText, AtSpiText, AtSpiDocumentText, AtSpiDocumentWeb,
					AtSpiDocumentEmail};
		throw new UnsupportedPlatformException();
	}

	/**
	 * Determines whether a widget supports typing.
	 * @param w The widget for which to determine if it supports typing.
	 * @return True if the widget supports typing; False otherwise.
	 */
	public static boolean isNativeTypeable(Widget w){
		if (!Role.isOneOf(w.get(Tags.Role, Roles.Widget), getNativeTypeableRoles()))
			return false;
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return w.get(UIATags.UIAIsKeyboardFocusable);
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return w.get(AtSpiTags.AtSpiIsFocusable);
		throw new UnsupportedPlatformException();
	}
}
