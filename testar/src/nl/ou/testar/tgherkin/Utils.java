package nl.ou.testar.tgherkin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.fruit.Util;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.tgherkin.functions.Image;
import nl.ou.testar.tgherkin.functions.OCR;
import nl.ou.testar.tgherkin.gen.TgherkinLexer;
import nl.ou.testar.tgherkin.gen.TgherkinParser;
import nl.ou.testar.tgherkin.gen.WidgetConditionParser;
import nl.ou.testar.tgherkin.model.ProtocolProxy;

/**
 * Utilities for processing the Tgherkin language.
 *
 */
public class Utils {
	
	/**
	 * XML Header.
	 */
	public static final String XML_HEADER = "<?xml version=\"1.0\"?>";
	 
	/**
	 * Pattern for finding iamge files in a Tgherkin XPath expression.
	 * group 1: image function
	 * group 2: image file name 
	 */
	private static Pattern imageFilesPattern = Pattern.compile(".*?(image\\(\\s*\"(.+?)\"\\s*\\)).*?");
	
	private Utils() {
	}
	
	/**
	 * Get document model from file.
	 * @param fileName name of the Tgherkin text file
	 * @return document that contains the Tgherkin model
	 */
	public static nl.ou.testar.tgherkin.model.Document getDocumentFromFile(String fileName) {
		ANTLRInputStream inputStream;
		try {
			inputStream = new ANTLRInputStream(new FileInputStream(fileName));
		} catch (IOException e) {
			throw new TgherkinException("Unable to open character stream for Tgherkin file: " + fileName);
		}        
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		return getDocument(lexer);
	}
	
	private static nl.ou.testar.tgherkin.model.Document getDocument(TgherkinLexer lexer) {
		nl.ou.testar.tgherkin.model.Document document = null;
	    TgherkinParser parser = getTgherkinParser(lexer);
		TgherkinErrorListener errorListener = new TgherkinErrorListener();
		parser.addErrorListener(errorListener);
		document = new nl.ou.testar.tgherkin.DocumentBuilder().visitDocument(parser.document());
		List<String> errorList = errorListener.getErrorList();
		if (errorList.size() == 0) {
			// post-processing check
			errorList = document.check();
		}
		if (errorList.size() != 0) {
			for(String errorText : errorList) {
				LogSerialiser.log(errorText + "\n", LogSerialiser.LogLevel.Info);
			}
			throw new TgherkinException("Invalid Tgherkin document, see log for details");
		}
		return document;
	}
	
	/**
	 * Get document model.
	 * @param code Tgherkin source code
	 * @return document that contains the Tgherkin model
	 */
	public static nl.ou.testar.tgherkin.model.Document getDocument(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		return getDocument(lexer);
	}
	
	
	/**
	 * Get Tgherkin parser.
	 * @param code Tgherkin source code
	 * @return Tgherkin parser
	 */
	public static TgherkinParser getTgherkinParser(String code) {
		ANTLRInputStream inputStream = new ANTLRInputStream(code);
		TgherkinLexer lexer = new TgherkinLexer(inputStream);
		return getTgherkinParser(lexer);
	}
	
	private static TgherkinParser getTgherkinParser(TgherkinLexer lexer) {
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TgherkinParser parser = new TgherkinParser(tokens);
		// remove error listener that generates output to the console
		parser.removeErrorListeners();
		return parser;
	}

	
	/**
	 * Get widget condition parser.
	 * @param code widget condition code
	 * @return widget condition parser
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
	 * Retrieve Tgherkin source code from file.
	 * @param fileName name of the file with Tgherkin source code
	 * @return Tgherkin source code
	 */
	public static String readTgherkinSourceFile(String fileName) {
		String sourceCode = null;
	    try {
	        sourceCode = new String (Files.readAllBytes(Paths.get(fileName)));
	    } catch (IOException e) {
			throw new TgherkinException("Unable to read Tgherkin file: " + fileName);
	    }
	    return sourceCode;
	}	

	/**
	 * Get XML XPath expression.
	 * @param xpathExpr Tgherkin XPath expression
	 * @return XML XPath expression
	 */
	public static String getXMLXpathExpression(String xpathExpr) {
		// replace Tgherkin functions 
		String result = xpathExpr.replace("ocr()", "OCR");
		Matcher matcher = imageFilesPattern.matcher(result);
		while (matcher.find()) {
			// replace image(<file name>) by Image_<file name>
			String found = matcher.group(1);
			String replacement = getXMLName("Image_" + matcher.group(2));
			result = result.replace(found, replacement);
		}
		return result;
	}

	/**
	 * Get image files referenced in a Tgherkin XPath expression.
	 * @param xpathExpr Tgherkin XPath expression
	 * @return list of image file names, empty list if no image files are referenced
	 */
	public static List<String> getImageFilesFromTgherkinXpathExpression(String xpathExpr) {
		List<String> imageFiles = new ArrayList<String>();
		Matcher matcher = imageFilesPattern.matcher(xpathExpr);
		while (matcher.find()) {
			imageFiles.add(matcher.group(2));
		}
		return imageFiles;
	}
	
	/**
	 * Get result of XPath expression. 
	 * The result is a list of concrete ID's of all the widget elements that were in the Xpath result. 
	 * @param xmlData XML data
	 * @param xpathExpr XPath expression
	 * @return list of concrete ID's, empty list if no widget elements were returned
	 */
	public static List<String> getXPathResult(String xmlData, String xpathExpr) {
        NodeList nodes = (NodeList) evaluateXPathExpresion(xmlData, xpathExpr,XPathConstants.NODESET);
		List<String> concreteIDList = new ArrayList<String>();
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
	 * Get result of XPath expression with result type Boolean. 
	 * @param xmlData XML data
	 * @param xpathExpr XPath expression
	 * @return XPath expression result
	 */
	public static Boolean getXPathBooleanResult(String xmlData, String xpathExpr) {
		return (Boolean) evaluateXPathExpresion(xmlData, xpathExpr,XPathConstants.BOOLEAN);
	}
	
	/**
	 * Get result of XPath expression with result type Number. 
	 * @param xmlData XML data
	 * @param xpathExpr XPath expression
	 * @return XPath expression result
	 */
	public static Double getXPathNumberResult(String xmlData, String xpathExpr) {
		return (Double) evaluateXPathExpresion(xmlData, xpathExpr,XPathConstants.NUMBER);
	}

	/**
	 * Get result of XPath expression with result type String. 
	 * @param xmlData XML data
	 * @param xpathExpr XPath expression
	 * @return XPath expression result
	 */
	public static String getXPathStringResult(String xmlData, String xpathExpr) {
		return (String) evaluateXPathExpresion(xmlData, xpathExpr,XPathConstants.STRING);
	}

	/**
	 * Evaluate XPath expression.
	 * @param xmlData XML data
	 * @param xpathExpr XPath expression
	 * @param resultType result type of the XPath expression
	 * @return result of the XPath expression
	 */
	public static Object evaluateXPathExpresion(String xmlData, String xpathExpr, QName resultType) {
		Object result = null;
		String xpathExprXML = Utils.getXMLXpathExpression(xpathExpr);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
			javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xmlData));
		    org.w3c.dom.Document document = builder.parse(is);
	        // Create XPathFactory object
	        XPathFactory xpathFactory = XPathFactory.newInstance();
	        // Create XPath object
	        javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
	        //create XPathExpression object
	        XPathExpression expr = xpath.compile(xpathExprXML);
	        //evaluate expression result on XML document
			result = expr.evaluate(document, resultType);
		} catch(Exception e) {
			throw new TgherkinException("XPath expression " + xpathExpr + " is invalid: " + e.getMessage());
		}
        return result;
	}
	
    /**
     * Transform state to XML.
	 * @param proxy document protocol proxy
     * @param state the SUT's current state
     * @param images list of file names for the images used in image recognition
     * @param ocr indicator of OCR usage
     * @return XML representation of the state
     */
	public static String getStateXML(ProtocolProxy proxy, State state, List<String> images, boolean ocr) {
    	StringBuilder stringBuilder = new StringBuilder();    	
    	stringBuilder.append(XML_HEADER);
        stringBuilder.append(getWidgetTreeXML(proxy, state, images, ocr));
        return stringBuilder.toString();
    }

    private static String getWidgetTreeXML(ProtocolProxy proxy, Widget widget, List<String> imageFiles, boolean ocr) {
    	StringBuilder stringBuilder = new StringBuilder(); 
    	stringBuilder.append("<widget>");
        // write tags
        for (Tag<?> tag : widget.tags()) {
    		if (tag.type() == Shape.class) {
    			Shape shape = widget.get(Tags.Shape);
    			stringBuilder.append(toXMLElement(tag.name(), Util.toString(shape)));
    			// insert at same level, not as children of Shape element
       			stringBuilder.append(toXMLElement("Shape.x", "" + shape.x()));
       			stringBuilder.append(toXMLElement("Shape.y", "" + shape.y()));
       			stringBuilder.append(toXMLElement("Shape.width", "" + shape.width()));
       			stringBuilder.append(toXMLElement("Shape.height", "" + shape.height()));
    		} else {
                stringBuilder.append(toXMLElement(tag.name(), Util.toString(widget.get(tag))));
    		}
    		if (ocr) {
    			// add OCR element
    			String ocrResult = OCR.getInstance().getOCR(proxy, widget);
    			if (ocrResult == null) {
        			ocrResult = "";
    			}
    			stringBuilder.append(toXMLElement("OCR", Util.toString(ocrResult)));
    		}
    		for (String imageFile : imageFiles) {
    			// add image recognition result
    			String recognitionResult;
    			if (Image.getInstance().isRecognized(proxy, widget, imageFile)) {
    				recognitionResult = "true";
    			} else {
    				recognitionResult = "false";
    			}
   				stringBuilder.append(toXMLElement("Image_" + imageFile, recognitionResult));
    		}
        }        
        // write children
        for (int i = 0; i < widget.childCount(); ++i) {
            Widget child = widget.child(i);
            stringBuilder.append(getWidgetTreeXML(proxy, child, imageFiles, ocr).toString());
        }
        stringBuilder.append("</widget>");
        return stringBuilder.toString();
    }
	
	/**
	 * Get XML element code.
	 * @param name element name
	 * @param value element value
	 * @return XML element code
	 */
	public static String toXMLElement(String name, String value) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<");
		stringBuilder.append(getXMLName(name));
		stringBuilder.append(">");
		stringBuilder.append(StringEscapeUtils.escapeXml(toXMLValue(value)));
		stringBuilder.append("</" + getXMLName(name) + ">");
		return stringBuilder.toString();		
	}
    
	private static String toXMLValue(String in) {
		return StringEscapeUtils.escapeXml(stripInvalidXMLCharacters(in));
	}
	
	private static String stripInvalidXMLCharacters(String in) {
	     // XML 1.0
	     // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
	     String xml10pattern = "[^"
	             + "\u0009\r\n"
	             + "\u0020-\uD7FF"
	             + "\uE000-\uFFFD"
	             + "\ud800\udc00-\udbff\udfff"
	             + "]";
	     return in.replaceAll(xml10pattern, "").trim();
	}

	private static String getXMLName(String in) {
	     String xml10pattern = "[^" 
	             + "\u0041-\u005A"   // upper case letters
	             + "\u0061-\u007A"   // lower case letters 
	             + "\u002D\r\n"      // hyphen
	             + "\u005F\r\n"      // underscore
	             + "\u002E\r\n"      // period
	             + "]";
	     return in.replaceAll(xml10pattern, "").trim();
	}
	 
}	
