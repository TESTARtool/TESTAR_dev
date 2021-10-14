package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.Helpers.CompareScreenshotsByPixelsHelper;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompareScreenshotsByPixelsRewardFunctionTest {
    @Spy
    private final CompareScreenshotsByPixelsRewardFunction rewardFunction = new CompareScreenshotsByPixelsRewardFunction(new CompareScreenshotsByPixelsHelper());

    @Mock
    State state;

    @Mock
    AWTCanvas previousCanvas;

    @Mock
    AWTCanvas awtCanvas;

    @Mock
    BufferedImage bufferedImage1;

    @Mock
    BufferedImage bufferedImage2;

    @Before
    public void setup () {
        rewardFunction.getStateshotBinary = (state) -> awtCanvas;
        when(awtCanvas.image()).thenReturn(bufferedImage1);
        when(previousCanvas.image()).thenReturn(bufferedImage2);
    }

    @Test
    public void getReward_forImagesOfEqualSizes_whichAreIdentical () {
        // given
        rewardFunction.previousStateCanvas = previousCanvas;
        when(bufferedImage1.getWidth()).thenReturn(1);
        when(bufferedImage1.getHeight()).thenReturn(1);
        when(bufferedImage2.getWidth()).thenReturn(1);
        when(bufferedImage2.getHeight()).thenReturn(1);
        int [] pixels1Array = new int[1];
        pixels1Array[0]= 1;
        when(bufferedImage1.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels1Array);
        when(bufferedImage2.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels1Array);

        // when
        float reward = rewardFunction.getReward(state, null, null, null, null, null, null);

        // then
        assertEquals(-1f, reward, 0.0001);
    }

    @Test
    public void getReward_forImagesOfEqualSizes_whichDiffer50Percent () {
        // given
        rewardFunction.previousStateCanvas = previousCanvas;
        when(bufferedImage1.getWidth()).thenReturn(1);
        when(bufferedImage1.getHeight()).thenReturn(2);
        when(bufferedImage2.getWidth()).thenReturn(1);
        when(bufferedImage2.getHeight()).thenReturn(2);
        int [] pixels1Array = new int[2];
        pixels1Array [0] = 1;
        pixels1Array [1] = 2;
        int [] pixels2Array = new int[2];
        pixels2Array [0] = 1;
        pixels2Array [1] = 3;
        when(bufferedImage1.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels1Array);
        when(bufferedImage2.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels2Array);

        // when
        float reward = rewardFunction.getReward(state, null, null, null, null, null, null);

        // then
        assertEquals(-0.5f, reward, 0.0001);
    }

    @Test
    public void getReward_forImagesOfEqualSizes_whichDiffer100Percent () {
        // given
        rewardFunction.previousStateCanvas = previousCanvas;
        when(bufferedImage1.getWidth()).thenReturn(1);
        when(bufferedImage1.getHeight()).thenReturn(1);
        when(bufferedImage2.getWidth()).thenReturn(1);
        when(bufferedImage2.getHeight()).thenReturn(1);
        int [] pixels1Array = new int[1];
        int [] pixels2Array = new int[1];
        pixels1Array[0]= 1;
        pixels2Array[0]= 2;
        when(bufferedImage1.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels1Array);
        when(bufferedImage2.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels2Array);

        // when
        float reward = rewardFunction.getReward(state, null, null, null, null, null, null);

        // then
        assertEquals(0.0f, reward, 0.0001);
    }

    @Test
    public void getReward_forImagesOfUnEqualSizes_exampleFromAnalysis () {
        // given
        rewardFunction.previousStateCanvas = previousCanvas;
        when(awtCanvas.x()).thenReturn(490d);
        when(awtCanvas.y()).thenReturn(189d);

        when(previousCanvas.x()).thenReturn(592d);
        when(previousCanvas.y()).thenReturn(189d);

        when(bufferedImage1.getWidth()).thenReturn(838);
        when(bufferedImage1.getHeight()).thenReturn(1812);
        when(bufferedImage2.getWidth()).thenReturn(736);
        when(bufferedImage2.getHeight()).thenReturn(702);
        int [] pixels1Array = new int[838*1812];
        int [] pixels2Array = new int[736*702];
        when(bufferedImage1.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels1Array);
        when(bufferedImage2.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels2Array);

        // when
        float reward = rewardFunction.getReward(state, null, null, null, null, null, null);

        // then
        assertEquals(-0.3402261, reward, 0.0001);
    }

    /**
     *  There are 10 pixels of which 2 overlap.
     *  Of the two pixels which overlap, one equals the one in the other image
     *  Thus 7 out of 8 differ
     */
    @Test
    public void getReward_forImagesOfUnEqualSizes () {
        // given
        rewardFunction.previousStateCanvas = previousCanvas;
        when(awtCanvas.x()).thenReturn(1d);
        when(awtCanvas.y()).thenReturn(1d);

        when(previousCanvas.x()).thenReturn(0d);
        when(previousCanvas.y()).thenReturn(0d);

        when(bufferedImage1.getWidth()).thenReturn(2);
        when(bufferedImage1.getHeight()).thenReturn(2);
        when(bufferedImage2.getWidth()).thenReturn(2);
        when(bufferedImage2.getHeight()).thenReturn(3);
        int [] pixels1Array = new int[4];
        int [] pixels2Array = new int[6];
        pixels1Array[0]= 1; // overlaps with pixels2Array[3]
        pixels1Array[1]= 3;
        pixels1Array[2]= 2; // overlaps with pixels2Array[5]
        pixels1Array[3]= 3;
        pixels2Array[0]= 3;
        pixels2Array[1]= 3;
        pixels2Array[2]= 3;
        pixels2Array[3]= 1;
        pixels2Array[4]= 3;
        pixels2Array[5]= 4;
        when(bufferedImage1.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels1Array);
        when(bufferedImage2.getRGB(anyInt(), anyInt(), anyInt(), anyInt(), eq(null), anyInt(), anyInt())).thenReturn(pixels2Array);

        // when
        float reward = rewardFunction.getReward(state, null, null, null, null, null, null);

        // then
        assertEquals(-0.125, reward, 0.0001);
    }
}
