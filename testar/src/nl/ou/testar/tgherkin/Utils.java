package nl.ou.testar.tgherkin;

import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.Util;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang.StringEscapeUtils;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.model.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


/**
 * Tgherkin Utilities.
 *
 */
public class Utils {
	
	private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
	private static final double TOLERANCE = 5E-16;
	
	private Utils() {
		
	}
	
	/**
	 * Get document model.
	 * @param fileName name of Tgherkin text file
	 * @return document
	 */
	public static nl.ou.testar.tgherkin.model.Document getDocumentModel(String fileName) {
		nl.ou.testar.tgherkin.model.Document document = null;
		ANTLRInputStream inputStream;
		try {
			inputStream = new ANTLRInputStream(new FileInputStream(fileName));
		}catch (IOException e) {
			throw new TgherkinException("Unable to open character stream for Tgherkin file: " + fileName);
		}        
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
	    TgherkinParser parser = new TgherkinParser(tokens);
		TgherkinErrorListener errorListener = new TgherkinErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		document = new nl.ou.testar.tgherkin.DocumentBuilder().visitDocument(parser.document());
		List<String> errorList = errorListener.getErrorList();
		if (errorList.size() == 0) {
			// post-processing check
			errorList = document.check();
		}
		if (errorList.size() != 0) {
			for(String errorText : errorList) {
				LogSerialiser.log(errorText, LogSerialiser.LogLevel.Info);
			}
			throw new TgherkinException("Invalid Tgherkin document, see log for details");
		}
		return document;
	}
	
	/**
	 * Get Tgherkin parser.
	 * @param code Tgherkin code
	 * @return Tgherkin parser
	 */
	public static TgherkinParser getTgherkinParser(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TgherkinParser parser = new TgherkinParser(tokens);
		// remove error listener that generates output to the console
		parser.removeErrorListeners();
		return parser;
	}
	
	/**
	 * Get widget condition parser.
	 * @param code widget condition code
	 * @return widget condtion parser
	 */
	public static WidgetConditionParser getWidgetConditionParser(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		WidgetConditionParser parser = new WidgetConditionParser(new CommonTokenStream(lexer));
		// remove error listener that generates output to the console
		parser.removeErrorListeners();
		return parser;
	}

	/**
	 * @param state given state
	 * @param imageFile name of file with reference image
	 * @param confidenceThreshold given confidence threshold
	 * @return list of recognized widgets
	 * @throws RuntimeException if a problem occurs
	 */
	public static List<Widget> getImageRecognitionResult(State state, String imageFile, double confidenceThreshold){
		List<Widget> widgetList = new ArrayList<Widget>();
		try {
			AWTCanvas refShot = AWTCanvas.fromFile(imageFile);
			for (Widget widget : state) {
				Rectangle actionArea = new Rectangle(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
				Shape shape = widget.get(Tags.Shape);				
				Rectangle r = new Rectangle((int)shape.x(), (int)shape.y(), (int)shape.width(), (int)shape.height());
				actionArea = actionArea.union(r);
				if (!actionArea.isEmpty()) {
					AWTCanvas widgetShot = AWTCanvas.fromScreenshot(Rect.from(actionArea.x, actionArea.y, actionArea.width, actionArea.height),
							 AWTCanvas.StorageFormat.PNG, 1);
					double confidence = widgetShot.compareImage(refShot);
					if (confidence - confidenceThreshold >= - TOLERANCE) {
						widgetList.add(widget);
					}
				}	
			}
		}catch(Exception e) {
			throw new TgherkinException("Image recognition error");
		}
		return widgetList;
	}

	/**
	 * Get result of Xpath expression.
	 * The result is a list of widgets that were present as widget elements in the Xpath result. 
	 * @param state given state
	 * @param xpathExpr given Xpath expression
	 * @return list of widgets  
	 */
	public static List<Widget> getXpathResult(State state, String xpathExpr){
		List<Widget> widgetList = new ArrayList<Widget>();
		try {
			for (String concreteID : getXpathResult(getStateXML(state), xpathExpr)) {
				for (Widget widget : state){
					if (widget.get(Tags.ConcreteID).equals(concreteID)){
						widgetList.add(widget);
						break;
					}
				}
			}
		}catch(Exception e) {
			throw new TgherkinException("Xpath expression error");
		}
		return widgetList;
	}
	
	/**
	 * Get result of Xpath expression. 
	 * The result is a list of concrete ID's of all the widget elements that were in the Xpath result. 
	 * @param xmlData given XML data
	 * @param xpathExpr given Xpath expression
	 * @return list of concrete ID's
	 */
	private static List<String> getXpathResult(String xmlData, String xpathExpr) throws Exception{
	    List<String> concreteIDList = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xmlData));
	    org.w3c.dom.Document document = builder.parse(is);
        // Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();
        // Create XPath object
        XPath xpath = xpathFactory.newXPath();
        //create XPathExpression object
        XPathExpression expr = xpath.compile(xpathExpr);
        //evaluate expression result on XML document
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
        	// only select widget elements from the Xpath result
        	// collect concrete ID's of those widget elements
        	if (nodes.item(i).getNodeName().equals("widget")) {
        		if (nodes.item(i) instanceof Element) {
        			Element element = (Element)nodes.item(i);
        			Element childElement =  (Element) element.getElementsByTagName("ConcreteID").item(0);
        			concreteIDList.add(childElement.getFirstChild().getTextContent());
       			}
        	}
        }   
        return concreteIDList; 
	}
	
	
    /**
     * Transform state to XML.
     * @param widget given state
     * @return XML representation of the state
     */
	private static String getStateXML(State state) {
    	StringBuilder stringBuilder = new StringBuilder();    	
    	stringBuilder.append(XML_HEADER);
        stringBuilder.append(getWidgetTreeXML(state));
        return stringBuilder.toString();
    }

    /**
     * Transform widget tree to XML elements.
     * @param widget given root widget
     * @return XML representation of the widget tree
     */
    private static String getWidgetTreeXML(Widget widget) {
    	StringBuilder stringBuilder = new StringBuilder(); 
    	stringBuilder.append("<widget>");
        // write tags
        for (Tag<?> tag : widget.tags()) {
            stringBuilder.append(toXMLElement(tag.name(), Util.toString((Object)widget.get(tag))));        	
        }        
        // write children
        for (int i = 0; i < widget.childCount(); ++i) {
            Widget child = widget.child(i);
            stringBuilder.append(getWidgetTreeXML(child).toString());
        }
        stringBuilder.append("</widget>");
        return stringBuilder.toString();
    }
	
	/**
	 * Transform name - value pair to an XML element.
	 * @param name given element name
	 * @param value given element value
	 * @return
	 */
	private static String toXMLElement(String name, String value){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<");
		stringBuilder.append(getXMLName(name));
		stringBuilder.append(">");
		stringBuilder.append(StringEscapeUtils.escapeXml(toXMLValue(value)));
		stringBuilder.append("</" + getXMLName(name) + ">");
		return stringBuilder.toString();		
	}
    
	/**
	  * This method ensures that the output String has only valid XML unicode
	  * characters as specified by the XML 1.0 standard.
	  * Invalid characters are removed.
	  * Special XML control characters like < are escaped.    
	  * 
	  * @param in The String we want to correct.
	  * @return The corrected in String.
	  */
	private static String toXMLValue(String in) {
		return StringEscapeUtils.escapeXml(stripInvalidXMLCharacters(in));
	}
	
	/**
	  * This method removes unicode characters that do not adhere to the XML 1.0 standard.  
	  * 
	  * @param in The String whose non-valid characters we want to remove.
	  * @return The in String, stripped of non-valid characters.
	  */
	private static String stripInvalidXMLCharacters(String in) {
	     // XML 1.0
	     // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
	     String xml10pattern = 
	             "[^" + 
	             "\u0009\r\n" + 
	             "\u0020-\uD7FF" + 
	             "\uE000-\uFFFD" + 
	             "\ud800\udc00-\udbff\udfff" + 
	             "]";
	     return in.replaceAll(xml10pattern, "").trim();
	}

	/**
	  * This method removes unicode characters that do not adhere to the XML 1.0 standard for element names.  
	  * 
	  * @param in The String whose non-valid characters we want to remove.
	  * @return The in String, stripped of non-valid characters.
	  */
	private static String getXMLName(String in) {
	     String xml10pattern = 
	             "[^" + 
	             "\u0041-\u005A" +  // upper case letters
	             "\u0061-\u007A" +  // lower case letters 
	             "\u002D\r\n" +     // hyphen
	             "\u005F\r\n" +     // underscore
	             "\u002E\r\n" +     // period
	             "]";
	     return in.replaceAll(xml10pattern, "").trim();
	}

}	
