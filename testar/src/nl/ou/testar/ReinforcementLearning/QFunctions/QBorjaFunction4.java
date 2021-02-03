package nl.ou.testar.ReinforcementLearning.QFunctions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Collections;
import java.util.Set;

//

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.monkey.Settings;
import org.testar.action.priorization.ActionTags;
import org.testar.action.priorization.ActionTags.ActionGroupType;
import org.testar.protocols.DesktopProtocol;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import org.testar.OutputStructure;
import nl.ou.testar.a11y.reporting.HTMLReporter;

/**
 * Implements the default Q-function of QLearning
 */
public class QBorjaFunction4 implements QFunction {

    private final float defaultQValue;
    private State previousState;

    /**
     * Constructor
     * @param defaultQValue
     */
    public QBorjaFunction4(final float defaultQValue) {
        this.defaultQValue = defaultQValue;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public float getQValue(Tag<Float> rl_tag, final AbstractAction previousActionUnderExecution, final AbstractAction actionUnderExecution, final float reward, final AbstractState currentAbstractState, final Set<Action> actions) {
        float qValue = reward;

        private HTMLDifference htmlDifference;
        String differenceScreenshot;
        
        htmlDifference = new HTMLDifference();
        				
		if(previousState != null && previousState.get(Tags.ScreenshotPath, null) != null && currentAbstractState.get(Tags.ScreenshotPath, null) != null) {
			// Create and obtain the image-diff path
			differenceScreenshot = getDifferenceImage(
				previousState.get(Tags.ScreenshotPath), previousState.get(Tags.AbstractIDCustom, ""),
				currentAbstractState.get(Tags.ScreenshotPath), currentAbstractState.get(Tags.AbstractIDCustom, "")
				);
		}
				
        if(previousActionUnderExecution != null) {
			try {
				BufferedImage diffScreanshot = ImageIO.read(new File(differenceScreenshot));
				double diffPxPercentage = getDiffPxPercentage(diffScreanshot);
				qValue += diffPxPercentage;
			} catch (IOException e) {
		    	e.printStackTrace();
			}
		}
		
		qValue = qValue - (0.01 * previouslyExecutedAction.get(Tags.OriginWidget).get(Tags.ZIndex));
        
		previousActionUnderExecution.getAttributes().set(RLTags.QBorja, qValue);
        previousState = currentAbstractState;
        return qValue;
    }

    private double getDiffPxPercentage(BufferedImage diffScreanshot) {
		double diffPxPercentage = 0;
		double totalPixels = diffScreanshot.getWidth() * diffScreanshot.getHeight();
		double differentPixels = 0;
		int[] pixelsArray = diffScreanshot.getRGB(0, 0, diffScreanshot.getWidth(), diffScreanshot.getHeight(), null, 0, diffScreanshot.getWidth());
		
		for (int i = 0; i < totalPixels; i++) {
		    if (pixelsArray[i] != Color.Black.argb32()) {
		    	differentPixels ++;
		    }
		}
		
		diffPxPercentage = differentPixels / totalPixels;
		
		System.out.println("*********");
		System.out.println("Totales actuales: " + totalPixels);
		System.out.println("Diferentes: " + differentPixels);
		System.out.println("Proporcion (0..1): " + diffPxPercentage);
		System.out.println("*********");
		
		return diffPxPercentage;
	}
	
	/**
	 * State image difference 
	 * 
	 * https://stackoverflow.com/questions/25022578/highlight-differences-between-images
	 * 
	 * @param previousStateDisk
	 * @param namePreviousState
	 * @param stateDisk
	 * @param nameState
	 * @return
	 */
	private String getDifferenceImage(String previousStateDisk, String namePreviousState, String stateDisk, String nameState) {
		try {

			// State Images paths
			String previousStatePath = new File(previousStateDisk).getCanonicalFile().toString();
			String statePath = new File(stateDisk).getCanonicalFile().toString();
			
			System.out.println("Action: " + actionCount);
			System.out.println("PreviousState: " + previousStatePath);
			System.out.println("CurrentState: " + statePath);
			
			// TESTAR launches the process to create the image and move forward without wait if exists
			// thats why we need to check and wait to obtain the image-diff
			while(!new File(previousStatePath).exists() || !new File(statePath).exists()){
				System.out.println("Waiting for Screenshot creation");
				System.out.println("Waiting... PreviousState: " + previousStatePath);
				System.out.println("Waiting... CurrentState: " + statePath);
				Util.pause(2);
			}
			
			BufferedImage img1 = ImageIO.read(new File(previousStatePath));
			BufferedImage img2 = ImageIO.read(new File(statePath));

			int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
			int width2 = img2.getWidth(); // take no arguments
			int height1 = img1.getHeight();
			int height2 = img2.getHeight();
			if ((width1 != width2) || (height1 != height2)) {
				System.out.println("Error: Images dimensions mismatch");
				return "";
			}

			// NEW - Create output Buffered image of type RGB
			BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

			// Modified - Changed to int as pixels are ints
			int diff;
			int result; // Stores output pixel
			for (int i = 0; i < height1; i++) {
				for (int j = 0; j < width1; j++) {
					int rgb1 = img1.getRGB(j, i);
					int rgb2 = img2.getRGB(j, i);
					int r1 = (rgb1 >> 16) & 0xff;
					int g1 = (rgb1 >> 8) & 0xff;
					int b1 = (rgb1) & 0xff;
					int r2 = (rgb2 >> 16) & 0xff;
					int g2 = (rgb2 >> 8) & 0xff;
					int b2 = (rgb2) & 0xff;
					diff = Math.abs(r1 - r2); // Change
					diff += Math.abs(g1 - g2);
					diff += Math.abs(b1 - b2);
					diff /= 3; // Change - Ensure result is between 0 - 255
					// Make the difference image gray scale
					// The RGB components are all the same
					result = (diff << 16) | (diff << 8) | diff;
					outImg.setRGB(j, i, result); // Set result
				}
			}

			// Now save the image on disk
			// check if we have a directory for the screenshots yet
			File screenshotDir = new File(OutputStructure.htmlOutputDir + File.separator);

			// save the file to disk
			File screenshotFile = new File(screenshotDir, "diff_"+ namePreviousState + "_" + nameState + ".png");
			if (screenshotFile.exists()) {
				try {
					return screenshotFile.getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			FileOutputStream outputStream = new FileOutputStream(screenshotFile.getCanonicalPath());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(outImg, "png", baos);
			byte[] bytes = baos.toByteArray();

			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();

			return screenshotFile.getCanonicalPath();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}
}

/**
 * Helper Class to prepare HTML State report Difference
 */
class HTMLDifference {
	
	PrintWriter out = null;

	public HTMLDifference () { 
		String[] HEADER = new String[] {
				"<!DOCTYPE html>",
				"<html>",
				"<style>",
				".container {display: flex;}",
				".float {display:inline-block;}",
				"</style>",
				"<head>",
				"<title>TESTAR State Model difference report</title>",
				"</head>",
				"<body>"
		};
		
		String htmlReportName =  OutputStructure.htmlOutputDir + File.separator + "StateDifferenceReport.html";

		try {
			out = new PrintWriter(new File(htmlReportName).getCanonicalPath(), HTMLReporter.CHARSET);
		} catch (IOException e) { e.printStackTrace(); }

		for(String s:HEADER){
			out.println(s);
			out.flush();
		}
	}
	
	public void addStateDifferenceStep(int actionCount, String previousState, String state, String difference) {
		out.println("<h2> Specific State changes for action " + actionCount + " </h2>");
		try {
			out.println("<p><img src=\"" + new File(previousState).getCanonicalFile().toString() + "\">");
			out.println("<img src=\"" + new File(state).getCanonicalFile().toString() + "\">");
			out.println("<img src=\"" + new File(difference).getCanonicalFile().toString() + "\"></p>");
		} catch (IOException e) {
			out.println("<p> ERROR ADDING IMAGES </p>");;
		}
		out.flush();
	}
}