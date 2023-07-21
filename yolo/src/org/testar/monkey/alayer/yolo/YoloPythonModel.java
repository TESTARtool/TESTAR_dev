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

package org.testar.monkey.alayer.yolo;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class YoloPythonModel {

	private final String yoloPythonCommand;
	private final String yoloProjectAbsolutePath;
	private final String yoloPythonServiceRelativePath;
	private final String yoloModelAbsolutePath;
	private final String yoloInputImagesDirectory;
	private final String yoloModelOutputDirectory;

	public YoloPythonModel(String yoloPythonCommand, 
			String yoloProjectAbsolutePath, 
			String yoloPythonServiceRelativePath, 
			String yoloModelAbsolutePath, 
			String yoloInputImagesDirectory,
			String yoloModelOutputDirectory) {

		this.yoloPythonCommand = yoloPythonCommand;
		this.yoloProjectAbsolutePath = yoloProjectAbsolutePath;
		this.yoloPythonServiceRelativePath = yoloPythonServiceRelativePath;
		this.yoloModelAbsolutePath = yoloModelAbsolutePath;
		this.yoloInputImagesDirectory = yoloInputImagesDirectory;
		this.yoloModelOutputDirectory = yoloModelOutputDirectory;

		try {
			deleteExistingImageFile();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		initializeYoloPythonService();
	}

	private void initializeYoloPythonService() {
		// Invoke the python service that loads the Yolo model
		// and waits for input images
		try {
			String pyApiService = yoloProjectAbsolutePath + File.separator + yoloPythonServiceRelativePath;

			ProcessBuilder processBuilder = new ProcessBuilder(
					yoloPythonCommand, pyApiService, 
					"--weights", yoloModelAbsolutePath, 
					"--input_img_dir", yoloInputImagesDirectory, 
					"--output_txt_dir", yoloModelOutputDirectory);

			Process process = processBuilder.start();

			// Create a separate thread to capture and print the output and error streams
			Thread outputThread = new Thread(() -> printStream(process.getInputStream()));
			Thread errorThread = new Thread(() -> printStream(process.getErrorStream()));

			// Start the output and error threads
			outputThread.start();
			errorThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Helper method to print the contents of a stream
	private void printStream(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> processImageWithYolo(BufferedImage image) throws IOException, AWTException, InterruptedException {
		// Prepare the screenshot that Yolo reads
		saveScreenshotToYolo(image);

		// Extract widgets boundaries using the response of the Yolo invokation
		String outputCoordinates = obtainYoloOutput();
		String[] lines = outputCoordinates.split("\n");
		List<String> stringList = new ArrayList<>();
		for (String line : lines) {
			if (!line.trim().isEmpty() && !line.trim().matches(".*[a-zA-Z].*")) {
				stringList.add(line);
			}
		}

		return stringList;
	}

	private void saveScreenshotToYolo(BufferedImage image) throws IOException, AWTException, InterruptedException {
		// Waits until no image exists in the directory
		// This is done to do not interfere with the python service
		Path scrPath = Paths.get(yoloInputImagesDirectory, "state.png");
		while (Files.exists(scrPath)) {
			Thread.sleep(50);
		}

		// Save the screenshot to the specified location
		File output = new File(yoloInputImagesDirectory + File.separator + "state.png");
		ImageIO.write(image, "png", output);
		
	}

	private String obtainYoloOutput() throws IOException, InterruptedException {
		// Wait until Yolo has created the output txt result file
		Path filePathOut = Paths.get(yoloModelOutputDirectory, "widgets.txt");
		Path filePathIn = Paths.get(yoloInputImagesDirectory, "state.png");
		while (!Files.exists(filePathOut) || Files.exists(filePathIn)) {
			Thread.sleep(100);
		}

		// Yolo output file exists, read it
		byte[] fileBytes = Files.readAllBytes(filePathOut);
		String outputResult = new String(fileBytes);

		// Before returning the content, delete the file to clear the output folder for next iteration
		Files.delete(filePathOut);

		// Wait until the file deletion is complete
		while (Files.exists(filePathOut)) {
			Thread.sleep(50);
		}

		return outputResult;
	}

	private void deleteExistingImageFile() throws IOException, InterruptedException{
		Path filePath = Paths.get(yoloInputImagesDirectory, "state.png");

		if (Files.exists(filePath)) {
			Files.delete(filePath);

			// Wait until the file deletion is complete
			while (Files.exists(filePath)) {
				Thread.sleep(50);
			}
		}
	}
}
