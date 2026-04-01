/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.testar.android.AndroidAppiumFramework;
import org.testar.android.alayer.AndroidCanvas;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.state.AndroidStateBuilder;
import org.testar.android.tag.AndroidTags;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Roles;
import org.testar.core.exceptions.NoSuchTagException;
import org.testar.core.process.ProcessHandle;
import org.testar.core.state.SUT;
import org.testar.core.state.StateBuilder;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.plugin.exceptions.UnsupportedPlatformException;
import org.testar.webdriver.alayer.WdCanvas;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.state.WdStateBuilder;
import org.testar.webdriver.tag.WdTags;
import org.testar.windows.alayer.GDIScreenCanvas;
import org.testar.windows.alayer.UIARoles;
import org.testar.windows.state.UIAStateBuilder;
import org.testar.windows.state.WinProcess;
import org.testar.windows.tag.UIATags;

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
				return EnumSet.of(OperatingSystems.LINUX);
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

	public static void addAndroidOS() {
		PLATFORM_OS.add(OperatingSystems.ANDROID);
	}

	public static void cleanAndroidOS() {
		PLATFORM_OS.remove(OperatingSystems.ANDROID);
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
	public static StateBuilder getNativeStateBuilder(Double timeToFreeze, boolean accessBridgeEnabled, String SUTProcesses) {
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER)) {
			return new WdStateBuilder(timeToFreeze);
		}
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			return new AndroidStateBuilder(timeToFreeze);
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_7)) {
				return new UIAStateBuilder(timeToFreeze, accessBridgeEnabled, SUTProcesses);
			}
			else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_10)) {
				// TODO: a win10 state builder might make use of the new CUI8 Automation object.
				return new UIAStateBuilder(timeToFreeze, accessBridgeEnabled, SUTProcesses);
			}
			else {
				System.out.println("TESTAR detected OS: " + osName + " and this is not yet full supported. If the detected OS is wrong, please contact the TESTAR team at info@testar.org.");
				return new UIAStateBuilder(timeToFreeze, accessBridgeEnabled, SUTProcesses);
			}
		}

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
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			return new AndroidCanvas(pen);
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			return GDIScreenCanvas.fromPrimaryMonitor(pen);
		}

		throw new UnsupportedPlatformException();
	}


	/**
	 * Opens a SUT (runs a process) on the machine and returns a handle to the process in a SUT object.
	 * @param executableCommand The application/ process/ command that will be run.
	 * @return A handle to the process in a SUT object.
	 */
	public static SUT getNativeSUT(String executableCommand, boolean ProcessListenerEnabled, String SUTProcesses) {
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER)) {
			return WdDriver.fromExecutable(executableCommand);
		}
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			return AndroidAppiumFramework.fromCapabilities(executableCommand);
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_7)) {
				return WinProcess.fromExecutable(executableCommand, ProcessListenerEnabled, SUTProcesses);
			}
			else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_10)) {
				if (executableCommand.toLowerCase().contains(".exe") || executableCommand.contains(".jar")) {
					return WinProcess.fromExecutable(executableCommand, ProcessListenerEnabled, SUTProcesses);
				}
				else {
					return WinProcess.fromExecutableUwp(executableCommand);
				}
			}
			else {
				System.out.println("TESTAR detected OS: " + osName + " and this is not yet full supported.");
				return WinProcess.fromExecutable(executableCommand, ProcessListenerEnabled, SUTProcesses);
			}
		}

		throw new UnsupportedPlatformException();
	}


	/**
	 * Gets a list of running processes wrapped in a SUT class.
	 * @return A list of running processes wrapped in a SUT class.
	 */
	public static List<SUT> getNativeProcesses(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdDriver.fromAll();
		else if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			return AndroidAppiumFramework.fromAll();
		}
		else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return WinProcess.fromAll();

		throw new UnsupportedPlatformException();
	}

	public static SUT getNativeProcess(String processName){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return WinProcess.fromProcName(processName);

		throw new UnsupportedPlatformException();
	}

	public static ProcessHandle getNativeProcessHandle(long processPID){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return new org.testar.windows.process.WinProcHandle(processPID);

		throw new UnsupportedPlatformException();
	}

	/** Gets the memory usage for a SUT.
	 * @param nativeSUT The SUT to get memory usage for.
	 * @return Memory usage in KB.
	 */
	public static int getMemUsage(SUT nativeSUT){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return 0;
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			//TODO: Implement Emulator + internal android usage
			return 0;
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return (int)(WinProcess.getMemUsage((WinProcess)nativeSUT) / 1024); // byte -> KB

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
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			//TODO: Implement Emulator + internal android usage
			return new long[]{0, 0, 0};
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			long now = System.currentTimeMillis();
			long cpuFrame = now - lastCPUquery;
			lastCPUquery = now;
			long[] cpums = WinProcess.getCPUsage((WinProcess)nativeSUT);
			return new long[]{ cpums[0], cpums[1], cpuFrame };
		}

		throw new UnsupportedPlatformException();
	}

	public static Collection<Role> getNativeRoles(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER)) {
			return WdRoles.rolesSet();
		}
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID)) {
			return AndroidRoles.rolesSet();
		}
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.rolesSet();

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

		throw new UnsupportedPlatformException();
	}

	/**
	 * Get native tags.
	 * @return Native tags.
	 */
	public static Set<Tag<?>> getNativeTags(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdTags.tagSet();
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID))
			return AndroidTags.tagSet();
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIATags.tagSet();

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
	 * Gets all roles that correspond to elements that can be edited.
	 * @return All roles that correspond to elements that can be edited.
	 */
	public static Role[] getNativeTypeableRoles(){
		if (PLATFORM_OS.contains(OperatingSystems.WEBDRIVER))
			return WdRoles.nativeTypeableRoles();
		if (PLATFORM_OS.contains(OperatingSystems.ANDROID))
			return AndroidRoles.nativeTypeableRoles();
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return new Role[]{UIARoles.UIADocument, UIARoles.UIAEdit, UIARoles.UIAText};

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

		throw new UnsupportedPlatformException();
	}
}
