/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.manager;

import java.awt.GraphicsEnvironment;
import java.util.logging.Level;

import org.testar.engine.devices.EventHandler;
import org.testar.core.serialisation.LogSerialiser;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

public class NativeHookManager {

	public static void registerNativeHook(EventHandler eventHandler) {
		if (!GraphicsEnvironment.isHeadless()) {
			try {
				LogSerialiser.log("Registering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
				java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
				logger.setLevel(Level.OFF);
				logger.setUseParentHandlers(false);

				if (!GlobalScreen.isNativeHookRegistered()) {
					GlobalScreen.registerNativeHook();
				}

				GlobalScreen.addNativeKeyListener(eventHandler);
				GlobalScreen.addNativeMouseListener(eventHandler);
				GlobalScreen.addNativeMouseMotionListener(eventHandler);
				LogSerialiser.log("Successfully registered keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Debug);

			} catch (NativeHookException e) {
				e.printStackTrace();
			}
		}
	}

	public static void unregisterNativeListener(EventHandler eventHandler) {
		if (!GraphicsEnvironment.isHeadless() && GlobalScreen.isNativeHookRegistered()) {
			LogSerialiser.log("Unregistering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
			GlobalScreen.removeNativeMouseMotionListener(eventHandler);
			GlobalScreen.removeNativeMouseListener(eventHandler);
			GlobalScreen.removeNativeKeyListener(eventHandler);
		}
	}

	public static void unregisterNativeHook() {
		if (!GraphicsEnvironment.isHeadless() && GlobalScreen.isNativeHookRegistered()) {
			try {
				LogSerialiser.log("Finishing JNativeHook\n", LogSerialiser.LogLevel.Debug);
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				e.printStackTrace();
			}
		}
	}
}
