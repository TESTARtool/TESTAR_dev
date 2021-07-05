package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.fruit.alayer.State;
import org.sikuli.basics.Settings;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import java.awt.image.BufferedImage;

/**
 * This reward function uses image recognition to compare two states.
 * It makes extensively use of Sikulix, @see <a href="https://sikulix-2014.readthedocs.io/en/latest/basicinfo.html">link</a>
 */
public class ImageRecognitionBasedRewardFunction implements RewardFunction {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ImageRecognitionBasedRewardFunction.class);

    private final float defaultReward;

    protected BufferedImage screenImagePreviouslyExecutedAction = null;

    public ImageRecognitionBasedRewardFunction(final float defaultReward) {
        this.defaultReward = defaultReward;
    }

    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        try {
            Settings.MinSimilarity = 0.01; //override default of 0.3
            Validate.notNull(screenImagePreviouslyExecutedAction, "ScreenImagePreviouslyExecutedAction has the value null");
            final Pattern patternOfPreviousSceenshot = new Pattern(screenImagePreviouslyExecutedAction);
            Validate.isTrue(patternOfPreviousSceenshot.isValid(), "Screenshot of previously executed action could not be retrieved");

            // compare screenshots
            final BufferedImage screenshot = takeScreenshot();
            final Finder finder = new Finder(screenshot);
            finder.find(patternOfPreviousSceenshot);
            Validate.isTrue(finder.hasNext(), "Screenshots could not be compared");
            final Match match = finder.next();
            logger.debug("Match found between screenshots with " + (1 - match.getScore()));
            screenImagePreviouslyExecutedAction = screenshot;
            finder.destroy();
            final float reward = (float) (1d - match.getScore());
            logger.info("ID={} reward={}", executedAction.getId(), reward);
            return reward;
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
            screenImagePreviouslyExecutedAction = takeScreenshot();
            logger.info("ID={} reward={}", executedAction.getId(), defaultReward);
            return defaultReward;
        }
    }

    protected BufferedImage takeScreenshot() {
        return new Screen().capture().getImage();
    }

    @Override
    public void reset() {
        screenImagePreviouslyExecutedAction = null;
        logger.info("ImageRecognitionBasedRewardFunction was reset");
    }

}
