package org.testar.monkey.alayer.yolo;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
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

	private final String yoloProjectAbsolutePath;
	private final String yoloPythonServiceRelativePath;
	private final String yoloModelAbsolutePath;
	private final String yoloInputImagesDirectory;
	private final String yoloModelOutputDirectory;

	public YoloPythonModel(String yoloProjectAbsolutePath, 
			String yoloPythonServiceRelativePath, 
			String yoloModelAbsolutePath, 
			String yoloInputImagesDirectory,
			String yoloModelOutputDirectory) {

		this.yoloProjectAbsolutePath = yoloProjectAbsolutePath;
		this.yoloPythonServiceRelativePath = yoloPythonServiceRelativePath;
		this.yoloModelAbsolutePath = yoloModelAbsolutePath;
		this.yoloInputImagesDirectory = yoloInputImagesDirectory;
		this.yoloModelOutputDirectory = yoloModelOutputDirectory;

		initializeYoloPythonService();
	}

	private void initializeYoloPythonService() {
		// Invoke the python service that loads the Yolo model
		// and waits for input images
		try {
			String pyApiService = yoloProjectAbsolutePath + File.separator + yoloPythonServiceRelativePath;

			ProcessBuilder processBuilder = new ProcessBuilder(
					"python", pyApiService, 
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

	public List<String> processImageWithYolo() throws IOException, AWTException, InterruptedException {
		// Prepare the screenshot that Yolo reads
		saveScreenshotToYolo();

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

	private void saveScreenshotToYolo() throws IOException, AWTException, InterruptedException {
		// Waits until no image exists in the directory
		// This is done to do not interfere with the python service
		Path scrPath = Paths.get(yoloInputImagesDirectory, "state.png");
		while (Files.exists(scrPath)) {
			Thread.sleep(500);
		}

		// Create a Robot object
		Robot robot = new Robot();

		// Capture the screen using the Robot's createScreenCapture method
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenshot = robot.createScreenCapture(screenRect);

		// Save the screenshot to the specified location
		File output = new File(yoloInputImagesDirectory + File.separator + "state.png");
		ImageIO.write(screenshot, "png", output);
	}

	private String obtainYoloOutput() throws IOException, InterruptedException {
		// Wait until Yolo has created the output txt result file
		Path filePath = Paths.get(yoloModelOutputDirectory, "widgets.txt");
		while (!Files.exists(filePath)) {
			Thread.sleep(500);
		}

		// Yolo output file exists, read it
		byte[] fileBytes = Files.readAllBytes(filePath);
		String outputResult = new String(fileBytes);

		// Before returning the content, delete the file to clear the output folder for next iteration
		Files.delete(filePath);

		// Wait until the file deletion is complete
		while (Files.exists(filePath)) {
			Thread.sleep(100);
		}

		return outputResult;
	}

}
