/***************************************************************************************************
 *
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
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

package org.testar;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestarDirectories {

	public static final String SETTINGS_FILE = "test.settings";
	public static final String SUT_SETTINGS_EXT = ".sse";

	private static final Path BASE_DIR = Paths.get(".").toAbsolutePath().normalize();

	private static String testarDir = BASE_DIR.toString() + File.separator;
	private static String settingsDir = BASE_DIR.resolve("settings").toString() + File.separator;
	private static String outputDir = BASE_DIR.resolve("output").toString() + File.separator;
	private static String tempDir = BASE_DIR.resolve("output").resolve("temp").toString() + File.separator;
	private static String selectedSse;

	private TestarDirectories() {}

	public static String getTestarDir() {
		return testarDir;
	}

	public static void setTestarDir(String directory) {
		testarDir = directory;
	}

	public static String getSettingsDir() {
		return settingsDir;
	}

	public static void setSettingsDir(String directory) {
		settingsDir = directory;
	}

	public static String getOutputDir() {
		return outputDir;
	}

	public static void setOutputDir(String directory) {
		outputDir = directory;
	}

	public static String getTempDir() {
		return tempDir;
	}

	public static void setTempDir(String directory) {
		tempDir = directory;
	}

	public static String getSelectedSse() {
		return selectedSse;
	}

	public static void setSelectedSse(String sse) {
		selectedSse = sse;
	}

	public static String[] getSseFiles() {
		return new File(settingsDir).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(SUT_SETTINGS_EXT);
			}
		});
	}

	public static String getTestSettingsFile() {
		return settingsDir + selectedSse + File.separator + SETTINGS_FILE;
	}
}
