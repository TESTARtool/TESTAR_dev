/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This software is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar;

import static org.fruit.alayer.windows.UIARoles.UIAButton;
import static org.fruit.alayer.windows.UIARoles.UIACheckBox;
import static org.fruit.alayer.windows.UIARoles.UIAComboBox;
import static org.fruit.alayer.windows.UIARoles.UIACustomControl;
import static org.fruit.alayer.windows.UIARoles.UIADataItem;
import static org.fruit.alayer.windows.UIARoles.UIAEdit;
import static org.fruit.alayer.windows.UIARoles.UIAHyperlink;
import static org.fruit.alayer.windows.UIARoles.UIAList;
import static org.fruit.alayer.windows.UIARoles.UIAListItem;
import static org.fruit.alayer.windows.UIARoles.UIAMenuItem;
import static org.fruit.alayer.windows.UIARoles.UIARadioButton;
import static org.fruit.alayer.windows.UIARoles.UIAScrollBar;
import static org.fruit.alayer.windows.UIARoles.UIASlider;
import static org.fruit.alayer.windows.UIARoles.UIASpinner;
import static org.fruit.alayer.windows.UIARoles.UIASplitButton;
import static org.fruit.alayer.windows.UIARoles.UIATabItem;
import static org.fruit.alayer.windows.UIARoles.UIAText;
import static org.fruit.alayer.windows.UIARoles.UIATree;
import static org.fruit.alayer.windows.UIARoles.UIATreeItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Canvas;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.StateBuilder;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.linux.AtSpiRolesWrapper;
import org.fruit.alayer.linux.AtSpiStateBuilder;
import org.fruit.alayer.linux.AtSpiTags;
import org.fruit.alayer.linux.GdkScreenCanvas;
import org.fruit.alayer.linux.LinuxProcess;
import org.fruit.alayer.linux.LinuxProcessHandle;
import org.fruit.alayer.windows.GDIScreenCanvas;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.alayer.windows.UIAStateBuilder;
import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.windows.WinProcHandle;
import org.fruit.alayer.windows.WinProcess;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import static org.fruit.alayer.linux.AtSpiRolesWrapper.*; // by wcoux

/**
 * A native connector.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class NativeLinker {
	
	private static long lastCPUquery = 0;

    private static EnumSet<OperatingSystems> PLATFORM_OS = determinePlatform(); // by urueda
	
	/**
	 * Determines the platform this executable is currently running on.
	 * @return The Operating system the executable is currently running on.
	 * @author: wcoux
	 */
	private static EnumSet<OperatingSystems> determinePlatform() {
		String osName = System.getProperty("os.name");
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
	
	/**
	 * Retrieves a StateBuilder instance which will be used to determine the state of the application.
	 * @param timeToFreeze The time after which requesting the state of an application will time out.
	 * @return A StateBuilder instance.
	 */
	public static StateBuilder getNativeStateBuilder(Double timeToFreeze){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_7))
				return new UIAStateBuilder(timeToFreeze);
			else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_10)) {
				// TODO: a win10 state builder might make use of the new CUI8 Automation object.
				return new UIAStateBuilder(timeToFreeze);
			}
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return new AtSpiStateBuilder(timeToFreeze);
		throw new NotImplementedException();
	}


	/**
	 * Retrieves a canvas for the primary monitor so Testar can paint elements on screen in Spy mode.
	 * @param pen A pen with which to paint on the screen.
	 * @return A Canvas on which Testar can paint elements in Spy mode.
	 */
	public static Canvas getNativeCanvas(Pen pen){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			return GDIScreenCanvas.fromPrimaryMonitor(pen);
			//return JavaScreenCanvas.fromPrimaryMonitor(pen);
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX)) {
			// TODO: spy mode on linux still needs to be implemented - The Canvas is also being used by Test mode.
			return new GdkScreenCanvas();
			//return JavaScreenCanvas.fromPrimaryMonitor(pen);
		}
		throw new NotImplementedException();
	}


	/**
	 * Opens a SUT (runs a process) on the machine and returns a handle to the process in a SUT object.
	 * @param executableCommand The application/ process/ command that will be run.
	 * @return A handle to the process in a SUT object.
	 */
	public static SUT getNativeSUT(String executableCommand){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_7))
				return WinProcess.fromExecutable(executableCommand);
			else if (PLATFORM_OS.contains(OperatingSystems.WINDOWS_10)) {
				if (executableCommand.contains(".exe") || executableCommand.contains(".jar"))
					return WinProcess.fromExecutable(executableCommand);
				else
					return WinProcess.fromExecutableUwp(executableCommand);
			}
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return LinuxProcess.fromExecutable(executableCommand);
		throw new NotImplementedException();
	}


	/**
	 * Gets a list of running processes wrapped in a SUT class.
	 * @return A list of running processes wrapped in a SUT class.
	 */
	public static List<SUT> getNativeProcesses(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return WinProcess.fromAll();
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return LinuxProcess.fromAll();
		throw new NotImplementedException();
	}
	
	public static SUT getNativeProcess(String processName){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return WinProcess.fromProcName(processName);		
		//else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			// TODO
		throw new NotImplementedException();
	}
	
	public static ProcessHandle getNativeProcessHandle(long processPID){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return new WinProcHandle(processPID);
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return new LinuxProcessHandle(processPID);
		throw new NotImplementedException();
	}
	
	/** Gets the memory usage for a SUT.
	 * @param nativeSUT The SUT to get memory usage for.
	 * @return Memory usage in KB.
	 */
	public static int getMemUsage(SUT nativeSUT){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return (int)(WinProcess.getMemUsage((WinProcess)nativeSUT) / 1024); // byte -> KB
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return (int)(LinuxProcess.getMemUsage((LinuxProcess)nativeSUT) / 1024);
		throw new NotImplementedException();
	}
	
	
	/** Gets the CPU usage for a SUT.
	 * @param nativeSUT The SUT to get CPU usage for.
	 * @return CPU usage in ms: user x system x frame.
	 */
	public static long[] getCPUsage(SUT nativeSUT){
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
		throw new NotImplementedException();
	}

	public static Collection<Role> getNativeRoles(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.rolesSet();
		// else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			// TODO
		throw new NotImplementedException();
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
		throw new NotImplementedException();
	}

	/**
	 * Gets the native Role wrapper for a button.
	 * @return The native Role wrapper for a button.
	 */
	public static Role getNativeRole_Button(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.UIAButton;
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiRolesWrapper.AtSpiPushButton;
		throw new NotImplementedException();
	}

	/**
	 * Gets the native Role wrapper for a menu item.
	 * @return The native Role wrapper for a menu item.
	 */
	public static Role getNativeRole_Menuitem(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIARoles.UIAMenuItem;
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiMenuItem;
		throw new NotImplementedException();
	}

	/**
	 * Get native tags.
	 * @return Native tags.
	 */
	public static Set<Tag<?>> getNativeTags(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return UIATags.tagSet();
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return AtSpiTags.tagSet();
		throw new NotImplementedException();		
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
	public static Role[] getNativeClickable(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS)) {
			return new Role[]{UIAMenuItem, UIAButton, UIACheckBox, UIARadioButton,
							  UIAComboBox, UIAList, UIAListItem,
							  UIATabItem, UIAHyperlink, UIADataItem, UIATree, UIATreeItem,
							  UIASlider, UIASpinner, UIAScrollBar, UIASplitButton,
							  UIACustomControl}; // be careful on custom control (we do not know what they are)
		} else if (PLATFORM_OS.contains(OperatingSystems.UNIX)) {
			return new Role[]{AtSpiCheckBox, AtSpiCheckMenuItem, AtSpiComboBox, AtSpiMenuItem,
			AtSpiListItem, AtSpiSpinButton, AtSpiToggleButton, AtSpiTreeItem, AtSpiListBox,
			AtSpiPushButton, AtSpiLink, AtSpiScrollBar};
		}
		throw new NotImplementedException();	
	}


	/**
	 * Gets all roles that correspond to elements that can be edited.
	 * @return All roles that correspond to elements that can be edited.
	 */
	public static Role[] getNativeTypeable(){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return new Role[]{UIAEdit, UIAText};
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return new Role[]{AtSpiPasswordText, AtSpiText, AtSpiDocumentText, AtSpiDocumentWeb,
							  AtSpiDocumentEmail};
		throw new NotImplementedException();
	}


	/**
	 * Determines whether a widget supports typing.
	 * @param w The widget for which to determine if it supports typing.
	 * @return True if the widget supports typing; False otherwise.
	 */
	public static boolean isNativeTypeable(Widget w){
		if (PLATFORM_OS.contains(OperatingSystems.WINDOWS))
			return w.get(UIATags.UIAIsKeyboardFocusable);
		else if (PLATFORM_OS.contains(OperatingSystems.UNIX))
			return w.get(AtSpiTags.AtSpiIsFocusable);
		throw new NotImplementedException();
	}

}