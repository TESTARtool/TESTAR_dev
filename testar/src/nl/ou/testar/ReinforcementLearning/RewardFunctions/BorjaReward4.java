package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.commons.lang.Validate;
import org.fruit.Util;
import org.fruit.alayer.Color;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testar.OutputStructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class BorjaReward4 implements RewardFunction {


//    private final float defaultReward;

    private State latestState;

//    protected BufferedImage screenImagePreviouslyExecutedAction = null;

//    public BorjaReward4(final float defaultReward) {
//        this.defaultReward = defaultReward;
//    }

    /**
     *{@inheritDoc}
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {

        State previousState = latestState;
        latestState = state;

        if(previousState != null && previousState.get(Tags.ScreenshotPath, null) != null && state.get(Tags.ScreenshotPath, null) != null) {

            // Create and obtain the image-diff path
            String differenceScreenshot = getDifferenceImage(
                    previousState.get(Tags.ScreenshotPath), previousState.get(Tags.ConcreteIDCustom, ""),
                    state.get(Tags.ScreenshotPath), state.get(Tags.ConcreteIDCustom, "")
            );

            try {
                BufferedImage diffScreanshot = ImageIO.read(new File(differenceScreenshot));

                double totalPixels = diffScreanshot.getWidth() * diffScreanshot.getHeight();
                float differentPixels = 0;
                int[] pixelsArray = diffScreanshot.getRGB(0, 0, diffScreanshot.getWidth(), diffScreanshot.getHeight(), null, 0, diffScreanshot.getWidth());
                for (int i = 0; i < totalPixels; i++) {
                    if (pixelsArray[i] != Color.Black.argb32()) {
                        differentPixels ++;
                    }
                }
                double diffPxPercentage = differentPixels / totalPixels;

                System.out.println("*********");
                System.out.println("Totales actuales: " + totalPixels);
                System.out.println("Diferentes: " + differentPixels);
                System.out.println("Proporcion (0..1): " + diffPxPercentage);
                System.out.println("*********");

                return differentPixels;

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update: output\timestamp_app_version\HTMLreports\StateDifferenceReport.html
            //htmlDifference.addStateDifferenceStep(actionCount, previousState.get(Tags.ScreenshotPath), state.get(Tags.ScreenshotPath), differenceScreenshot);
        }
        return 0;
    }

    protected BufferedImage takeScreenshot() {
        return new Screen().capture().getImage();
    }

    private String getDifferenceImage(String previousStateDisk, String namePreviousState, String stateDisk, String nameState) {
        try {

            // State Images paths
            String previousStatePath = new File(previousStateDisk).getCanonicalFile().toString();
            String statePath = new File(stateDisk).getCanonicalFile().toString();

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
