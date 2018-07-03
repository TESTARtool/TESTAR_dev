package nl.ou.testar.tgherkin.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.Utils;
import nl.ou.testar.tgherkin.model.ProtocolProxy;

/**
 * Singleton class responsible for the handling of XPath expressions.
 * This class supports the Tgherkin xpath, xpathBoolean, xpathNumber and xpathString functions.
 * No generics or sub-classing have been used, because these constructs do not quite fit with the singleton concept. 
 */
public class XPath {

	private static XPath xpath = new XPath();

	private State state;

	private Map<String,List<Widget>> xpathMap = new HashMap<String,List<Widget>>();
	private Map<String,Boolean> xpathBooleanMap = new HashMap<String,Boolean>();
	private Map<String,Double> xpathNumberMap = new HashMap<String,Double>();
	private Map<String,String> xpathStringMap = new HashMap<String,String>();
	
	// private Constructor prevents instantiation by other classes.
	private XPath() {
	}
	
	/**
	 * Retrieve singleton instance.
	 * @return XPath singleton instance
	 */
	public static XPath getInstance() {
		return xpath;
	}
	
	/**
	 * Determine whether a widget is in the xpath results.
	 * @param proxy document protocol proxy
	 * @param widget to be evaluated widget
	 * @param xpathExpr XPath expression
	 * @return true if widget is an element in the XPath node set, otherwise false
	 */
	public boolean isInXpathResult(ProtocolProxy proxy, Widget widget, String xpathExpr) {
		return getXpathResult(proxy, xpathExpr).contains(widget);
	}
	

	/**
	 * Get result of Xpath expression.
	 * The result is a list of widgets that were present as widget elements in the XPath result. 
	 * @param proxy document protocol proxy
	 * @param xpathExpr Xpath expression
	 * @return list of widgets, empty list if no widget is in the XPath result.  
	 */
	public List<Widget> getXpathResult(ProtocolProxy proxy, String xpathExpr) {
		if (state != proxy.getState()) {
			state = proxy.getState();
			clearMaps();
		}
		if (xpathMap.containsKey(xpathExpr)) {
			return xpathMap.get(xpathExpr);
		}
		List<String> imageFiles = updateImageRecognitionAndOCR(proxy, xpathExpr);
		List<Widget> widgetList = new ArrayList<Widget>();
		try {
			for (String concreteID : Utils.getXPathResult(Utils.getStateXML(proxy, state, imageFiles, xpathExpr.contains("ocr()")), xpathExpr)) {
				for (Widget widget : state) {
					if (widget.get(Tags.ConcreteID).equals(concreteID)) {
						widgetList.add(widget);
						break;
					}
				}
			}
		} catch(Exception e) {
			throw new TgherkinException("Xpath expression error");
		}
		xpathMap.putIfAbsent(xpathExpr, widgetList);
		return widgetList;
	}

	/**
	 * Retrieve results of XPath expression with return type Boolean.
	 * @param proxy document protocol proxy
	 * @param xpathExpr XPath expression
	 * @return results of XPath expression evaluation
	 */
	public boolean getXPathBooleanResult(ProtocolProxy proxy, String xpathExpr) {
		if (state != proxy.getState()) {
			state = proxy.getState();
			clearMaps();
		}
		if (xpathBooleanMap.containsKey(xpathExpr)) {
			return xpathBooleanMap.get(xpathExpr);
		}
		List<String> imageFiles = updateImageRecognitionAndOCR(proxy, xpathExpr);
		Boolean result = null;
		result = Utils.getXPathBooleanResult(Utils.getStateXML(proxy, state, imageFiles, xpathExpr.contains("ocr()")), xpathExpr);
		xpathBooleanMap.putIfAbsent(xpathExpr, result);
		return result;
	}
	
	/**
	 * Retrieve results of XPath expression with return type Number.
	 * @param proxy document protocol proxy
	 * @param xpathExpr XPath expression
	 * @return results of XPath expression evaluation
	 */
	public double getXPathNumberResult(ProtocolProxy proxy, String xpathExpr) {
		if (state != proxy.getState()) {
			state = proxy.getState();
			clearMaps();
		}
		if (xpathNumberMap.containsKey(xpathExpr)) {
			return xpathNumberMap.get(xpathExpr);
		}
		List<String> imageFiles = updateImageRecognitionAndOCR(proxy, xpathExpr);
		Double result = null;
		result = Utils.getXPathNumberResult(Utils.getStateXML(proxy, state, imageFiles, xpathExpr.contains("ocr()")), xpathExpr);
		xpathNumberMap.putIfAbsent(xpathExpr, result);
		return result;
	}
	
	/**
	 * Retrieve results of XPath expression with return type String.
	 * @param proxy document protocol proxy
	 * @param xpathExpr XPath expression
	 * @return results of XPath expression evaluation
	 */
	public String getXPathStringResult(ProtocolProxy proxy, String xpathExpr) {
		if (state != proxy.getState()) {
			state = proxy.getState();
			clearMaps();
		}
		if (xpathStringMap.containsKey(xpathExpr)) {
			return xpathStringMap.get(xpathExpr);
		}
		List<String> imageFiles = updateImageRecognitionAndOCR(proxy, xpathExpr);
		String result = null;
		result = Utils.getXPathStringResult(Utils.getStateXML(proxy, state, imageFiles, xpathExpr.contains("ocr()")), xpathExpr);
		xpathStringMap.putIfAbsent(xpathExpr, result);
		return result;
	}

	private List<String> updateImageRecognitionAndOCR(ProtocolProxy proxy, String xpathExpr) {
		if (xpathExpr.contains("ocr()")) {
			// if ocr function is used then collect ocr results 
			OCR.getInstance().updateAllWidgets(proxy);
		}
		List<String> imageFiles = Utils.getImageFilesFromTgherkinXpathExpression(xpathExpr);
		for (String imageFile : imageFiles) {
			// if image function used then collect image recognition results
			Image.getInstance().updateAllWidgets(proxy, imageFile);
		}		
		return imageFiles;
	}
	
	private void clearMaps() {
		xpathMap.clear();
		xpathBooleanMap.clear();
		xpathNumberMap.clear();
		xpathStringMap.clear();
	}
	 
}
