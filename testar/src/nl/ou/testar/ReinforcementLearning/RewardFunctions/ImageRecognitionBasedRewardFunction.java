package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.commons.lang.Validate;
import org.fruit.alayer.State;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class ImageRecognitionBasedRewardFunction implements RewardFunction {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final float defaultReward;

    protected BufferedImage screenImagePreviouslyExecutedAction = null;

    public ImageRecognitionBasedRewardFunction(final float defaultReward) {
        this.defaultReward = defaultReward;
    }

    /**
    *{@inheritDoc}
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction) {
        try {
            Settings.MinSimilarity = 0.01; //override default of 0.3
            Validate.notNull(screenImagePreviouslyExecutedAction, "ScreenImagePreviouslyExecutedAction has the value null");
            final Pattern patternOfPreviousSceenshot = new Pattern(screenImagePreviouslyExecutedAction);
            Validate.isTrue(patternOfPreviousSceenshot.isValid(), "Sceenshot of previously executed action could not be retrieved");

            // compare screenshots
            final BufferedImage screenshot = takeScreenshot();
            final Finder finder = new Finder(screenshot);
            finder.find(patternOfPreviousSceenshot);
            Validate.isTrue(finder.hasNext(), "Screenshots could not be compared");
            final Match match = finder.next();
            logger.debug("Match found between screenshots with " + (1 - match.getScore()));
            screenImagePreviouslyExecutedAction = screenshot;
            finder.destroy();
            return (float) (1d - match.getScore());
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
            screenImagePreviouslyExecutedAction = takeScreenshot();
            return defaultReward;
        }
    }

    protected BufferedImage takeScreenshot() {
        return new Screen().capture().getImage();
    }

}
