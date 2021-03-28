package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.a11y.reporting.HTMLReporter;

import org.apache.commons.math3.analysis.function.Abs;
import org.fruit.Util;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Action;
import org.fruit.alayer.Color;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.testar.OutputStructure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Set;

public class BorjaReward4 implements RewardFunction {
    
    private State previousState = null;
    AWTCanvas previousStateCanvas = null;

    /**
     *{@inheritDoc}
     */
    @Override
    public float getReward(State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final Action executedAction, final AbstractAction executedAbstractAction, final AbstractAction selectedAbstractAction, Set<Action> actions) {
    	System.out.println(". . . . . Enfoque 4 . . . . .");
		float reward = 0f;
		AWTCanvas currentStateCanvas = es.upv.staq.testar.ProtocolUtil.getStateshotBinary(state);

		if (previousState == null) {
	        previousState = state;
	        previousStateCanvas = currentStateCanvas;
			return reward;
		}

		float currentQValue = executedAbstractAction.getAttributes().get(RLTags.QBorja, 0f);
		float diffPxPercentage = getDiffPercentage(currentStateCanvas, previousStateCanvas);
		System.out.println("... diffPxPercentage: " + diffPxPercentage);
		reward = (float) - (1 - (currentQValue * diffPxPercentage));
		
		// Also decrement reward based on Widget Tree ZIndex

		// TODO: OriginWidget is not saved as Abstract Attribute
		// reward -= (0.01 *
		// selectedAbstractAction.getAttributes().get(Tags.OriginWidget).get(Tags.ZIndex));

		System.out.println(". . . . . Provisional Reward: " + reward);

		if (executedAction != null) {
			reward -= (0.01 * executedAction.get(Tags.OriginWidget).get(Tags.ZIndex));
		} else {
			System.out.println("WARNING: It was not possible to get the OriginWidget");
		}

        previousState = state;
        previousStateCanvas = currentStateCanvas;

        return reward;
    }
    public transient BufferedImage img;
    
    private float getDiffPercentage(AWTCanvas img1, AWTCanvas img2) {
    	float diff = 0f;
		BufferedImage bufImg1 = img1.image();
    	BufferedImage bufImg2 = img2.image();
		
		if (bufImg1.getHeight() == 0 || bufImg1.getWidth() == 0 || bufImg2.getHeight() == 0 || bufImg2.getWidth() == 0 ||
				bufImg1.getHeight() != bufImg2.getHeight() || bufImg1.getWidth() != bufImg2.getWidth()) {
			return 0f;
		}
		
		int[] pixels1Array = bufImg1.getRGB(0, 0, bufImg1.getWidth(), bufImg1.getHeight(), null, 0, bufImg1.getWidth());
		int[] pixels2Array = bufImg2.getRGB(0, 0, bufImg2.getWidth(), bufImg2.getHeight(), null, 0, bufImg2.getWidth());
				
		float differentPixels = 0,
			totalPixels = pixels1Array.length;
				
		for (int i = 0; i < totalPixels; i++) {
		    if (pixels1Array[i] != pixels2Array[i]) {
		    	differentPixels ++;
		    }
		}
		
		diff = differentPixels / totalPixels;
		
		System.out.println("*********");
		System.out.println("Totales actuales: " + totalPixels);
		System.out.println("Diferentes: " + differentPixels);
		System.out.println("Proporcion (0..1): " + diff);
		System.out.println("*********");
		
		return diff;
	}

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }
    
}