package nl.ou.testar.tgherkin.functions;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;

import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.model.ProtocolProxy;

/**
 * Singleton class responsible for image recognition.
 * This class supports the Tgherkin image function.
 *
 */
public class Image {

	private static final double TOLERANCE = 5E-16;
	private static Image image = new Image();
	private State state;
	private Map<String,Map<Widget, Double>> imagesMap = new HashMap<String,Map<Widget, Double>>();
	
	// private Constructor prevents instantiation by other classes.
	private Image() {
	}
	
	/**
	 * Retrieve singleton instance.
	 * @return OCR singleton instance
	 */
	public static Image getInstance( ) {
		return image;
	}
	


	/**
	 * Determine image recognition results for all top widgets of the state.
	 * @param proxy given protocol proxy 
	 * @param imageFile name of file with reference image
	 */
	public void updateAllWidgets(ProtocolProxy proxy, String imageFile){
		for (Widget widget : proxy.getTopWidgets(state)) {
			isRecognized(proxy, widget, imageFile);
		}
	}
	

	/**
	 * Determine whether the widget image is recognized as the reference image.
	 * @param proxy given protocol proxy
	 * @param widget given widget
	 * @param imageFile name of file with reference image
	 * @return true if recognized, otherwise false
	 */
	public boolean isRecognized(ProtocolProxy proxy, Widget widget, String imageFile){
		return (imageRecognition(proxy, widget, imageFile) - proxy.getSettings().get(ConfigTags.ConfidenceThreshold) >= - TOLERANCE);
	}

	/**
	 * Retrieve recognition confidence.
	 * @param proxy given protocol proxy
	 * @param widget given widget
	 * @param imageFile name of file with reference image
	 * @return confidence value between 0 and 1
	 */
	public Double getRecognitionConfidence(ProtocolProxy proxy, Widget widget, String imageFile){
		return imageRecognition(proxy, widget, imageFile);
	}

	private Double imageRecognition(ProtocolProxy proxy, Widget widget, String imageFile){
		if (state != proxy.getState()) {
			state = proxy.getState();
			imagesMap.clear();
		}
		double confidence = 0;
		if (imagesMap.containsKey(imageFile)){
			if (imagesMap.get(imageFile).containsKey(widget)) {
				return imagesMap.get(imageFile).get(widget);
			}
		}else {
			imagesMap.put(imageFile, new HashMap<Widget, Double>());
		}
		try {
			AWTCanvas refShot = AWTCanvas.fromFile(imageFile);
			Rectangle actionArea = new Rectangle(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
			Shape shape = widget.get(Tags.Shape);				
			Rectangle r = new Rectangle((int)shape.x(), (int)shape.y(), (int)shape.width(), (int)shape.height());
			actionArea = actionArea.union(r);
			if (!actionArea.isEmpty()) {
				AWTCanvas widgetShot = AWTCanvas.fromScreenshot(Rect.from(actionArea.x, actionArea.y, actionArea.width, actionArea.height),
						 AWTCanvas.StorageFormat.PNG, 1);
				confidence = widgetShot.compareImage(refShot);
			}	
		}catch(Exception e) {
			throw new TgherkinException("Image recognition error");
		}
		imagesMap.get(imageFile).putIfAbsent(widget, confidence);
		return confidence;
	}
	
}
