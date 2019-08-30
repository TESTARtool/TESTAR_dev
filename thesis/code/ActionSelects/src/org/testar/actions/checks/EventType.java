package org.testar.actions.checks;

/**
 * EventType
 * 
 * <li>RESTRICTED - open modal windows
 * <li>UNRESTRICTED - open modeless windows
 * <li>TERMINATION - close modal windows
 * <li>MENU_OPEN - open a menu
 * <li>SYSTEM_INTERACTION - interaction with the underlying software to perform some actions
 * 
 */
public enum EventType {
	RESTRICTED, UNRESTRICTED, TERMINATION, MENU_OPEN, SYSTEM_INTERACTION
}
