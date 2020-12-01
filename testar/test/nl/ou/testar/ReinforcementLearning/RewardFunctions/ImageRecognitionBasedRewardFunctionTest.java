package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Test class for the {@link ImageRecognitionBasedRewardFunction}
 */
public class ImageRecognitionBasedRewardFunctionTest {

    @Spy private final ImageRecognitionBasedRewardFunction rewardFunction = new ImageRecognitionBasedRewardFunction(1f);

    @Mock private AbstractState currentAbstractState;

    @Mock private AbstractAction executedAction;

    public static final String PATH_SCREENSHOT_ORIGINAL = "test//resources//images//screenshotOriginal.png";

    public static final String PATH_SCREENSHOT_WITH_MINOR_CHANGE = "test//resources//images//screenshotWithMinorChange.png";

    public static final String PATH_SCREENSHOT_WITH_GREATER_CHANGE = "test//resources//images//screenshotWithGreaterChange.png";

    public static final String PATH_SCREENSHOT_WITH_MAJOR_CHANGE = "test//resources//images//screenshotWithMajorChange.png";

    public static final String PATH_SCREENSHOT_WITH_COMPLETE_CHANGE = "test//resources//images//screenshotWithCompleteChange.png";

    @Before
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getReward_whenTheNewStateHasNoChanges() throws IOException {
        // given
        final BufferedImage mockedScreenshotPreviouslyExecutedAction = ImageIO.read(new File(PATH_SCREENSHOT_ORIGINAL));
        assertNotNull(mockedScreenshotPreviouslyExecutedAction);

        rewardFunction.screenImagePreviouslyExecutedAction = mockedScreenshotPreviouslyExecutedAction;
        when(rewardFunction.takeScreenshot()).thenReturn(mockedScreenshotPreviouslyExecutedAction);

        // when
        final float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(0f, reward, 0);
    }

    @Test
    public void getReward_whenTheStateHasMinorChanges() throws IOException {
        // given
        final BufferedImage mockedScreenshotPreviouslyExecutedAction = ImageIO.read(new File(PATH_SCREENSHOT_ORIGINAL));
        final BufferedImage mockedScreenshot = ImageIO.read(new File(PATH_SCREENSHOT_WITH_MINOR_CHANGE));

        assertNotNull(mockedScreenshotPreviouslyExecutedAction);
        assertNotNull(mockedScreenshot);

        rewardFunction.screenImagePreviouslyExecutedAction = mockedScreenshotPreviouslyExecutedAction;
        when(rewardFunction.takeScreenshot()).thenReturn(mockedScreenshot);

        // when
        final float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(0.25f, reward, 0.05f);
    }

    @Test
    public void getReward_whenTheStateHasGreaterChanges() throws IOException {
        // given
        final BufferedImage mockedScreenshotPreviouslyExecutedAction = ImageIO.read(new File(PATH_SCREENSHOT_ORIGINAL));
        final BufferedImage mockedScreenshot = ImageIO.read(new File(PATH_SCREENSHOT_WITH_GREATER_CHANGE));

        assertNotNull(mockedScreenshotPreviouslyExecutedAction);
        assertNotNull(mockedScreenshot);

        rewardFunction.screenImagePreviouslyExecutedAction = mockedScreenshotPreviouslyExecutedAction;
        when(rewardFunction.takeScreenshot()).thenReturn(mockedScreenshot);

        // when
        final float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(0.50f, reward, 0.05f);
    }

    @Test
    public void getReward_whenTheStateHasMajorChanges() throws IOException {
        // given
        final BufferedImage mockedScreenshotPreviouslyExecutedAction = ImageIO.read(new File(PATH_SCREENSHOT_ORIGINAL));
        final BufferedImage mockedScreenshot = ImageIO.read(new File(PATH_SCREENSHOT_WITH_MAJOR_CHANGE));

        assertNotNull(mockedScreenshotPreviouslyExecutedAction);
        assertNotNull(mockedScreenshot);

        rewardFunction.screenImagePreviouslyExecutedAction = mockedScreenshotPreviouslyExecutedAction;
        when(rewardFunction.takeScreenshot()).thenReturn(mockedScreenshot);

        // when
        final float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(0.75f, reward, 0.05f);
    }

    @Test
    public void getReward_whenTheStateHasCompletelyChanged() throws IOException {
        // given
        final BufferedImage mockedScreenshotPreviouslyExecutedAction = ImageIO.read(new File(PATH_SCREENSHOT_ORIGINAL));
        final BufferedImage mockedScreenshot = ImageIO.read(new File(PATH_SCREENSHOT_WITH_COMPLETE_CHANGE));

        assertNotNull(mockedScreenshotPreviouslyExecutedAction);
        assertNotNull(mockedScreenshot);

        rewardFunction.screenImagePreviouslyExecutedAction = mockedScreenshotPreviouslyExecutedAction;
        when(rewardFunction.takeScreenshot()).thenReturn(mockedScreenshot);

        // when
        final float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(1.00f, reward, 0.05f);
    }
}