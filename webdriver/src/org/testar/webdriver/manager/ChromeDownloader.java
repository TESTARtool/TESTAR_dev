/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.manager;

import org.testar.webdriver.WebdriverPathResolver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChromeDownloader {
	protected static final Logger logger = LogManager.getLogger();

	private static final String CHROME_DOWNLOAD_URL = "https://googlechromelabs.github.io/chrome-for-testing/LATEST_RELEASE_STABLE";
	private static final String BASE_DOWNLOAD_URL = "https://storage.googleapis.com/chrome-for-testing-public/";
	private static final String DOWNLOAD_DIR = WebdriverPathResolver.resolveChromeDownloadDir().toString();

	public static String downloadChromeForTesting() {
		try {
			String platform = detectPlatform();
			String localBinary = findExistingChromeBinary(platform);
			if (localBinary != null) {
				logger.log(Level.INFO, "Using existing Chrome for Testing: " + localBinary);
				return localBinary;
			}

			String latestVersion = fetchLatestVersion();
			String versionDir = DOWNLOAD_DIR + File.separator + latestVersion;
			String extractPath = versionDir + File.separator + "chrome-" + platform;
			String chromeExecutable = platform.contains("win") ? "chrome.exe" : "chrome";
			String chromeBinaryPath = extractPath + File.separator + chromeExecutable;

			// Check if already extracted
			File binaryFile = new File(chromeBinaryPath);
			if (binaryFile.exists()) {
				logger.log(Level.INFO, "Chrome for Testing already exists: " + chromeBinaryPath);
				return chromeBinaryPath;
			}

			// If not, download and extract
			String downloadUrl = BASE_DOWNLOAD_URL + latestVersion + "/" + platform + "/chrome-" + platform + ".zip";
			String zipPath = versionDir + File.separator + "chrome.zip";

			// Ensure download directory
			new File(versionDir).mkdirs();

			// Download zip only if not exists
			File zipFile = new File(zipPath);
			if (!zipFile.exists()) {
				downloadFile(downloadUrl, zipPath);
				logger.log(Level.INFO, "Downloaded Chrome zip to: " + zipPath);
			} else {
				logger.log(Level.INFO, "Chrome zip already downloaded at: " + zipPath);
			}

			// Extract it
			unzip(zipPath, versionDir);
			logger.log(Level.INFO, "Extracted Chrome to: " + extractPath);

			if (!binaryFile.exists()) {
				throw new FileNotFoundException("Chrome binary not found after extraction: " + chromeBinaryPath);
			}

			// On Unix-like systems, set executable permissions on all potential binaries
			if (!System.getProperty("os.name").toLowerCase().contains("win")) {
				File[] files = new File(extractPath).listFiles();
				if (files != null) {
					for (File file : files) {
						String name = file.getName();
						if (file.isFile() && (name.equals("chrome") || name.equals("chrome_crashpad_handler"))) {
							if (!file.setExecutable(true)) {
								logger.log(Level.WARN, "Failed to set executable permission on: " + file.getAbsolutePath());
							} else {
								logger.log(Level.INFO, "Set executable permission on: " + file.getAbsolutePath());
							}
						}
					}
				} else {
					logger.log(Level.WARN, "No files found in: " + extractPath);
				}
			}

			return chromeBinaryPath;

		} catch (Exception e) {
			logger.log(Level.ERROR, "Failed to download or locate Chrome for Testing", e);
			return null;
		}
	}

	private static String findExistingChromeBinary(String platform) {
		File downloadDirectory = new File(DOWNLOAD_DIR);
		if (!downloadDirectory.isDirectory()) {
			return null;
		}

		String chromeExecutable = platform.contains("win") ? "chrome.exe" : "chrome";
		File[] versionDirectories = downloadDirectory.listFiles(File::isDirectory);
		if (versionDirectories == null || versionDirectories.length == 0) {
			return null;
		}

		Arrays.sort(versionDirectories, (left, right) -> right.getName().compareTo(left.getName()));
		for (File versionDirectory : versionDirectories) {
			File candidate = new File(versionDirectory, "chrome-" + platform + File.separator + chromeExecutable);
			if (candidate.isFile()) {
				return candidate.getAbsolutePath();
			}
		}

		return null;
	}

	private static String fetchLatestVersion() throws IOException {
		URL url = new URL(CHROME_DOWNLOAD_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			return in.readLine();
		}
	}

	private static void downloadFile(String fileURL, String savePath) throws IOException {
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

		File saveFile = new File(savePath);
		File parentDir = saveFile.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}

		try (InputStream inputStream = httpConn.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(saveFile)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
	}

	private static void unzip(String zipFilePath, String destDir) throws IOException {
		File dir = new File(destDir);
		if (!dir.exists()) dir.mkdirs();
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				File newFile = newFile(dir, entry);
				if (entry.isDirectory()) {
					newFile.mkdirs();
				} else {
					// Create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						byte[] buffer = new byte[4096];
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
			}
		}
	}

	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());
		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	private static String detectPlatform() {
		String os = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch");

		if (os.contains("win")) {
			return "win64";
		} else if (os.contains("mac")) {
			return arch.contains("aarch64") || arch.contains("arm") ? "mac-arm64" : "mac-x64";
		} else if (os.contains("nux") || os.contains("nix")) {
			return "linux64";
		} else {
			throw new RuntimeException("Unsupported operating system: " + os);
		}
	}
}
