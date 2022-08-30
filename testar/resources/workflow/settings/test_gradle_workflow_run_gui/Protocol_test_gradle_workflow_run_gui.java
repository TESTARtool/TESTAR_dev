/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

import java.awt.event.WindowEvent;
import java.io.IOException;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;
import org.testar.monkey.Util;
import org.testar.protocols.DesktopProtocol;
import org.testar.settingsdialog.SettingsDialog;

/**
 * This protocol is used to test TESTAR by executing a gradle CI workflow. 
 * 
 * Test that the GUI runs with different Java versions. 
 * 
 * ".github/workflows/gradle.yml"
 */
public class Protocol_test_gradle_workflow_run_gui extends DesktopProtocol {

	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
		String testSettingsFileName = Main.getTestSettingsFile();
		SettingsDialog dialog = null;
		try {
			dialog = new SettingsDialog();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// Create a thread to run TESTAR dialog
		MyThread guiThread = new MyThread("GUIThread", dialog, settings, testSettingsFileName);

		// Wait 10 second so the GUIThread will run the dialog, then close the dialog JFrame
		Util.pause(10);
		// Then force to close the dialog JFrame
		dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));

		try {
			guiThread.t.join();
		} catch (InterruptedException e) {
			System.out.println("Thread Interrupted : " + guiThread.name);
		}

		System.out.println("Thread Finished : " + guiThread.name);

		// Force TESTAR to finish the execution
		this.mode = Modes.Quit;
	}

	class MyThread implements Runnable {
		String name;
		Thread t;
		SettingsDialog dialog;
		Settings guiSettings;
		String testSettingsFileName;

		MyThread(String threadName, SettingsDialog dialog, Settings guiSettings, String testSettingsFileName) {
			name = threadName;
			t = new Thread(this, name);
			this.dialog = dialog;
			this.guiSettings = guiSettings;
			this.testSettingsFileName = testSettingsFileName;
			t.start();
		}

		public void run() {
			try {
				System.out.println("Thread: Run GUI dialog");
				dialog.run(this.guiSettings, this.testSettingsFileName);
				System.out.println("Thread: GUI dialog finished");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println(name + " interrupted.");
			}
			System.out.println(name + " exiting...");
		}
	}

}
