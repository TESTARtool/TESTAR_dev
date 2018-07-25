package nl.ou.testar.tgherkin.functions;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;

import net.sourceforge.tess4j.*;
import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.model.ProtocolProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import es.upv.staq.testar.serialisation.LogSerialiser;

/**
 * Singleton class responsible for Optical Character Recognition (OCR).
 * This class supports the Tgherkin ocr function. 
 *
 */
public class OCR {

	/**
	 * Tesseract OCR data path.
	 */
	public static final String TESSERACT_DATA_PATH = "." + File.separator + "output" + File.separator + "temp";

	/**
	 * Tesseract OCR language.
	 */
	public static final String TESSERACT_LANGUAGE = "eng";
	
	/**
	 * Tesseract OCR language file suffix.
	 */
	public static final String TESSERACT_LANGUAGE_SUFFIX = ".traineddata";
	/**
	 * Tesseract OCR language file.
	 */
	public static final String TESSERACT_LANGUAGE_FILE = TESSERACT_DATA_PATH + File.separator + TESSERACT_LANGUAGE + TESSERACT_LANGUAGE_SUFFIX;

	/**
	 * Name of Tess4J jar.
	 * This jar contains the Tesseract data file for english.
	 */
	private static final String TESS4J_JAR = "tess4j-4.0.0.jar";

	/**
	 * Jar entry name.  
	 */
	public static final String JAR_ENTRY_NAME = "jar:file:../lib/" + TESS4J_JAR + "!/tessdata/" + TESSERACT_LANGUAGE + TESSERACT_LANGUAGE_SUFFIX;

	/**
	 * Target file for extraction of Jar entry. 
	 */
	public static final String TARGET_FILE = "output/Temp/" + TESSERACT_LANGUAGE + TESSERACT_LANGUAGE_SUFFIX;
	
	private static OCR ocr = new OCR();
	private State state;
	private Map<Widget,String> ocrMap = new HashMap<Widget,String>();
	
	// private Constructor prevents instantiation by other classes.
	private OCR() {
		File file = new File(TESSERACT_LANGUAGE_FILE); 
		if(!file.exists() || file.isDirectory()) {
			// if tesseract data not available then extract tesseract data file from jar
			try{
				extractTesseractDataFromJar();
			}catch(Exception e) {
				throw new TgherkinException("Extraction of Tesseract data failed with exception: " + e.getMessage().toString());
			}
		}
	}
	
	/**
	 * Retrieve singleton instance.
	 * @return singleton instance
	 */
	public static OCR getInstance( ) {
		return ocr;
	}
	
	/**
	 * Collect the OCR result for all top widgets of the state.
	 * @param proxy document protocol proxy
	 */
	public void updateAllWidgets(ProtocolProxy proxy) {
		// process all widgets 
		// any already retrieved ocr results for this state will be used 
		for (Widget widget : proxy.getTopWidgets(state)) {
			getOCR(proxy, widget);
		}
	}

	/**
	 * Retrieve OCR result of a widget image.
	 * @param proxy document protocol proxy
	 * @param widget to be analyzed widget
	 * @return OCR result, null if a problem occurred
	 */
	public String getOCR(ProtocolProxy proxy, Widget widget) {
		if (state != proxy.getState()) {
			state = proxy.getState();
			ocrMap.clear();
		}
		if (ocrMap.containsKey(widget)) {
			return ocrMap.get(widget);
		}
		String result = null;
		Rectangle actionArea = new Rectangle(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
		Shape shape = widget.get(Tags.Shape);
		Rectangle r = new Rectangle((int)shape.x(), (int)shape.y(), (int)shape.width(), (int)shape.height());
		actionArea = actionArea.union(r);
		if (!actionArea.isEmpty()) {
			AWTCanvas widgetShot = AWTCanvas.fromScreenshot(Rect.from(actionArea.x, actionArea.y, actionArea.width, actionArea.height),
					AWTCanvas.StorageFormat.PNG, 1);
			// convert to a grayscale image of the same size
			BufferedImage grayImage = new BufferedImage(widgetShot.image().getWidth(),widgetShot.image().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			ColorConvertOp op = new ColorConvertOp(
					widgetShot.image().getColorModel().getColorSpace(),
					grayImage.getColorModel().getColorSpace(),null);
			op.filter(widgetShot.image(),grayImage);
			try {
				result = getOCR(grayImage);
			}catch(Throwable t) {
				LogSerialiser.log(t.getMessage().toString() + "\n", LogSerialiser.LogLevel.Info);
			}
		}	
		if (result !=null) {
			result = result.trim();
		}
		ocrMap.putIfAbsent(widget, result);
		return result;
	}
	
	
	private static String getOCR(java.awt.image.BufferedImage bi) {
		String result = null;
		ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath(TESSERACT_DATA_PATH);
        instance.setLanguage(TESSERACT_LANGUAGE);
        try {
            result = instance.doOCR(bi);
        } catch (TesseractException e) {
        	LogSerialiser.log(e.getMessage().toString() + "\n", LogSerialiser.LogLevel.Info);
        }	
        return result;
	}
	
	private static void extractTesseractDataFromJar() throws Exception {
		InputStream in = null;
		OutputStream out = null;
		try {
			// to access a Jar content from the local file system
			URL url = new URL(JAR_ENTRY_NAME);
			JarURLConnection  conn = (JarURLConnection)url.openConnection();
			JarFile jarfile = conn.getJarFile();
			JarEntry jarEntry = conn.getJarEntry();
			in = new BufferedInputStream(jarfile.getInputStream(jarEntry));
			out = new BufferedOutputStream(new FileOutputStream(TARGET_FILE));
			final int bufferSize = 2048;
			byte[] buffer = new byte[bufferSize];
			for (;;)  {
				int nBytes = in.read(buffer);
				if (nBytes <= 0) { 
					break;
				}
				out.write(buffer, 0, nBytes);
			}
			LogSerialiser.log("Tesseract OCR data file has been extracted.\n", LogSerialiser.LogLevel.Info);
		}finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
	
}
