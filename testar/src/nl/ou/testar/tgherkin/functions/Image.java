package nl.ou.testar.tgherkin.functions;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.model.ProtocolProxy;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.monkey.ConfigTags;
import org.sikuli.script.Finder;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;

/**
 * Singleton class responsible for image recognition.
 * This class supports the Tgherkin image function.
 *
 */
public class Image {

	private int teller = 0;
	private static final double TOLERANCE = 5E-16;
	private static Image image = new Image();
	private boolean invalidSikuliXInstallation;
	private boolean firstTime;
	private State state;
	private Map<String,Map<Widget, Double>> imagesMap = new HashMap<String,Map<Widget, Double>>();
	
	// private Constructor prevents instantiation by other classes.
	private Image()	{
		firstTime = true;
	}
	
	/**
	 * Retrieve singleton instance.
	 * @return singleton instance
	 */
	public static Image getInstance() {
		return image;
	}
	
	/**
	 * Collect image recognition results for all top widgets of the state.
	 * @param proxy document protocol proxy 
	 * @param imageFile name of the file with the reference image
	 */
	public void updateAllWidgets(ProtocolProxy proxy, String imageFile) {
		for (Widget widget : proxy.getTopWidgets(state)) {
			;
		}
	}
	
	/**
	 * Determine whether the widget image is recognized as the reference image.
	 * @param proxy document protocol proxy
	 * @param widget to be recognized widget
	 * @param imageFile name of the file with the reference image
	 * @return true if recognized, otherwise false
	 */
	public boolean isRecognized(ProtocolProxy proxy, Widget widget, String imageFile) {
		boolean istrue = (imageRecognition(proxy, widget, imageFile) - proxy.getSettings().get(ConfigTags.ConfidenceThreshold) >= - TOLERANCE);
		return istrue;
	}

	/**
	 * Retrieve recognition confidence.
	 * @param proxy document protocol proxy
	 * @param widget to be recognized widget
	 * @param imageFile name of the file with the reference image
	 * @return confidence value between 0 and 1 that indicates the level of confidence (1 is highest level)
	 */
	public Double getRecognitionConfidence(ProtocolProxy proxy, Widget widget, String imageFile) {
		return imageRecognition(proxy, widget, imageFile);
	}

	private Double imageRecognition(ProtocolProxy proxy, Widget widget, String imageFile) {
		if (state != proxy.getState()) {
			state = proxy.getState();
			imagesMap.clear();
		}
		double confidence = 0;
		if (imagesMap.containsKey(imageFile)) {
			if (imagesMap.get(imageFile).containsKey(widget)) {
				return imagesMap.get(imageFile).get(widget);
			}
		} else {
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
				if (!invalidSikuliXInstallation) {
					// SikuliX image recognition 
					confidence = imageRecognition(proxy, refShot.image(), widgetShot.image());
				}
				if (invalidSikuliXInstallation) {
					// Testar AWTCanvas compare
					confidence = widgetShot.compareImage(refShot);
				}
			}	
		} catch(Exception e) {
			throw new TgherkinException("Image recognition error");
		}
		imagesMap.get(imageFile).putIfAbsent(widget, confidence);
		return confidence;
	}

	// SikuliX image recognition
	private Double imageRecognition(ProtocolProxy proxy, BufferedImage refShot, BufferedImage widgetShot) {
	  /* create image files for buffered images
	    try {
            BufferedImage bi = widgetShot;
            File outputfile = new File("temp_image" + teller++ + ".png");
            ImageIO.write(bi, "png",outputfile);
        } 
        catch (IOException e2) {
        }
        */
		double confidence = 0;
		if (!invalidSikuliXInstallation) {
			try {
				Pattern pattern = new Pattern(refShot).similar(proxy.getSettings().get(ConfigTags.ConfidenceThreshold).floatValue());
				Finder finder = new Finder(widgetShot);
				finder.find(pattern);
				// finder iterator will only contain entries above the threshold
				// retrieve highest score
				while (finder.hasNext()) {
					Match match = finder.next();
					// check if found rectangle covers minimum percentage of entire screen shot
					if (((match.w * match.h) / (widgetShot.getWidth() * widgetShot.getHeight()) * 100) >= proxy.getSettings().get(ConfigTags.MinimumPercentageForImageRecognition)) {
						if (confidence < match.getScore()) {
							confidence = match.getScore(); 
						}
					}
				}
				if (firstTime) {
					LogSerialiser.log("Image recognition is based on SikuliX\n", LogSerialiser.LogLevel.Info);					
					firstTime = false;	
				}				
			} catch(Throwable t) {
				if (firstTime) {
					// dirty workaround: SikuliX installation might be invalid, in that case the SikuliX setup has to be executed manually
					// image recognition will continue with the standard Testar compareImage
					firstTime = false;
					invalidSikuliXInstallation = true;
					LogSerialiser.log("Invalid SikuliX installation, switching to AWTCanvas.compareImage()\n", LogSerialiser.LogLevel.Info);
				} else {
					throw(t); 
				}
			}
		}
		return confidence;
	}
}
