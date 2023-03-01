package org.testar.reinforcementlearning.rewardfunctions;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.sikuli.basics.Settings;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This reward function uses image recognition to compare two states.
 * It makes extensively use of Sikulix, @see <a href="https://sikulix-2014.readthedocs.io/en/latest/basicinfo.html">link</a>
 */
public class ImageRecognitionBasedRewardFunction implements RewardFunction {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ImageRecognitionBasedRewardFunction.class);

    public static Consumer<WriterExperimentsParams> WRITER_EXPERIMENTS_CONSUMER = WriterExperiments::writeMetrics;

    private final float defaultReward;

    protected BufferedImage screenImagePreviouslyExecutedAction = null;

    public ImageRecognitionBasedRewardFunction(final float defaultReward) {
        this.defaultReward = defaultReward;
    }

    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions) {
        final String id = executedAbstractAction==null ? null : executedAbstractAction.getId();
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
            final float reward = (float) (1 - match.getScore());
            logger.info("ID={} reward={}", id, reward);

            // Write metrics information inside rlRewardMetrics.txt file to be stored in the centralized file server
            String information = String.format("ID | %s | reward | %s ",
                    id, reward);
//            WRITER_EXPERIMENTS_CONSUMER.accept(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
//                    .setFilename("rlRewardMetrics")
//                    .setInformation(information)
//                    .setNewLine(true)
//                    .build());

            return reward;
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
            screenImagePreviouslyExecutedAction = takeScreenshot();
            logger.info("ID={} reward={}", id, defaultReward);

            // Write metrics information inside rlRewardMetrics.txt file to be stored in the centralized file server
            String information = String.format("ID | %s | reward | %s ",
                    id, defaultReward);
//            WRITER_EXPERIMENTS_CONSUMER.accept(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
//                    .setFilename("rlRewardMetrics")
//                    .setInformation(information)
//                    .setNewLine(true)
//                    .build());

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
