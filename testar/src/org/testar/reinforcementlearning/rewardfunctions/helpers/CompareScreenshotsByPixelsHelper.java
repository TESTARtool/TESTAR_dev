package org.testar.reinforcementlearning.rewardfunctions.helpers;

import org.testar.reinforcementlearning.rewardfunctions.CompareScreenshotsByPixelsRewardFunction;
import org.apache.logging.log4j.LogManager;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Rect;

import java.awt.image.BufferedImage;

public class CompareScreenshotsByPixelsHelper {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CompareScreenshotsByPixelsRewardFunction.class);
    public static final int OFFSET = 0;
    public static final int START_Y = 0;
    public static final int START_X = 0;

    /**
     * Compares two images of type {@link AWTCanvas}
     * Given is that both images are in the shape of a rectangle, see es.upv.staq.testar.ProtocolUtil::getStateshotBinary
     * The x and y coordinate of the AWTCanvas is the origin point and contains the coordinates on the screen
     * adding the width and height the rectangle area can be found on which the image is based
     * comparing these rectangles the overlapping area can be found, also the disjunct areas
     * For the overlapping areas on a pixel by pixel basis can be determined whether these differ
     * The disjunct areas of both images are considered to be different.
     * @param img1 An image in the shape of a rectangle
     * @param img2 An image in the shape of a rectangle
     * @param defaultReward The reward to return when the operation fails
     * @return the ratio of different pixels divided by the total number of pixels
     */
    public float getDiffPxRatio(AWTCanvas img1, AWTCanvas img2, float defaultReward) {
        BufferedImage bufImg1 = img1.image();
        BufferedImage bufImg2 = img2.image();

        Rect rectImg1 = Rect.from(img1.x(), img1.y(), bufImg1.getWidth(), bufImg1.getHeight());
        Rect rectImg2 = Rect.from(img2.x(), img2.y(), bufImg2.getWidth(), bufImg2.getHeight());

        if (rectImg1.height() == 0 || rectImg1.width() == 0 || rectImg2.height() == 0 || rectImg2.width() == 0) {
            return defaultReward;
        }
        int[] pixels1Array = bufImg1.getRGB(START_X, START_Y, (int) rectImg1.width(), (int) rectImg1.height(), null, OFFSET, (int) rectImg1.width());
        int[] pixels2Array = bufImg2.getRGB(START_X, START_Y, (int) rectImg2.width(), (int) rectImg2.height(), null, OFFSET, (int) rectImg2.width());

        // get the number of different pixels in overlapping area
        Rect intersectRect = Rect.intersection(rectImg1, rectImg2);
        float differentPixelsInOverlap = compareOverLappingAreasPixelByPixel(rectImg1, rectImg2, pixels1Array, pixels2Array, intersectRect);

        // get the number of pixels that do not overlap, notice the area does not overlap in both images differs from the other image by definition
        double sizeIntersectionRect = Rect.area(intersectRect);
        int sizeDisjunctAreaBufImg1 = getSizeDisjunctAreaBufImg(pixels1Array, sizeIntersectionRect);
        int sizeDisjunctAreaBufImg2 = getSizeDisjunctAreaBufImg(pixels2Array, sizeIntersectionRect);

        return getDiffRatioAndLog(differentPixelsInOverlap, sizeIntersectionRect, sizeDisjunctAreaBufImg1, sizeDisjunctAreaBufImg2);
    }

    private float getDiffRatioAndLog(float differentPixelsInOverlap, double sizeIntersectionRect, int sizeDisjunctAreaBufImg1, int sizeDisjunctAreaBufImg2) {
        float differentPixels = differentPixelsInOverlap + sizeDisjunctAreaBufImg1 + sizeDisjunctAreaBufImg2;
        double totalPixels = sizeIntersectionRect + sizeDisjunctAreaBufImg1 + sizeDisjunctAreaBufImg2;
        float diff = (float) (differentPixels / totalPixels);
        final float reward = diff;

        logger.info("*********");
        logger.info("Current differentPixelsInOverlap: " + differentPixelsInOverlap);
        logger.info("Current sizeDisjunctAreaBufImg1: " + sizeDisjunctAreaBufImg1);
        logger.info("Current sizeDisjunctAreaBufImg2: " + sizeDisjunctAreaBufImg2);
        logger.info("Different: " + differentPixels);
        logger.info("Current sizeIntersectionRect: " + sizeIntersectionRect);
        logger.info("Current Totals: " + totalPixels);
        logger.info("Different: " + differentPixels);
        logger.info("Ratio (0..1): " + diff);
        logger.info(". . . . . Reward: " + reward);
        logger.info("*********");

        return reward;
    }

    private int getSizeDisjunctAreaBufImg(int[] pixelsArray, double sizeIntersectionRect) {
        return (int) (pixelsArray.length - sizeIntersectionRect);
    }

    private float compareOverLappingAreasPixelByPixel(Rect rect1, Rect rect2, int[] pixels1Array, int[] pixels2Array, Rect intersectRect) {
        float differentPixels = 0f;
        // compare overlapping areas, pixel by pixel
        for (double y = intersectRect.y(); y < intersectRect.y() + intersectRect.height(); y++){
            for (double x = intersectRect.x(); x < intersectRect.x() + intersectRect.width(); x++){

                // translate image coordinates to bufferedImage coordinates
                double yBufImg1 = (y - rect1.y());
                double yBufImg2 = (y - rect2.y());

                double xBufImg1 = (x - rect1.x());
                double xBufImg2 = (x - rect2.x());

                // derived frm documentation BufferedImage::getRGB
                int pixel1Index = getPixelIndex(rect1.width() , yBufImg1, xBufImg1);
                int pixel2Index = getPixelIndex(rect2.width(), yBufImg2, xBufImg2);
                int pixel1 = pixels1Array[pixel1Index];
                int pixel2 = pixels2Array[pixel2Index];

                if (pixel1 != pixel2){
                    differentPixels++;
                }
            }
        }
        return differentPixels;
    }

    private int getPixelIndex(double width, double y, double x) {
        return (int) (OFFSET + (y - START_Y) * width + (x - START_X));
    }
}
