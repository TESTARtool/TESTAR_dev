/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

package org.testar.managers;

import java.awt.GraphicsEnvironment;
import java.util.logging.Level;

import org.testar.EventHandler;
import org.testar.serialisation.LogSerialiser;

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
