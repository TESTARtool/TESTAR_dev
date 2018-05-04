package nl.ou.testar.tgherkin.functions;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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

import nl.ou.testar.tgherkin.TgherkinException;
import nl.ou.testar.tgherkin.model.ProtocolProxy;

/**
 * Singleton class responsible for the handling of XPath expressions.
 * This class supports the Tgherkin XPath function.
 *
 */
public class XPath {

	private static final String XML_HEADER = "<?xml version=\"1.0\"?>";

	private static XPath xpath = new XPath();

	private State state;

	private Map<String,List<Widget>> xpathMap = new HashMap<String,List<Widget>>();
	
	// private Constructor prevents instantiation by other classes.
	private XPath() {
	}
	
	/**
	 * Retrieve singleton instance.
	 * @return OCR singleton instance
	 */
	public static XPath getInstance( ) {
		return xpath;
	}
	
	/**
	 * Determine whether a widget is in the xpath results.
	 * @param proxy given protocol proxy
	 * @param widget given widget
	 * @param xpathExpr xpath expression
	 * @return true if match, otherwise false
	 */
	public boolean isXpathResult(ProtocolProxy proxy, Widget widget, String xpathExpr) {
		return getXpathResult(proxy, xpathExpr).contains(widget);
	}
	
	/**
	 * Get result of Xpath expression.
	 * The result is a list of widgets that were present as widget elements in the Xpath result. 
	 * @param proxy given protocol proxy
	 * @param xpathExpr given Xpath expression
	 * @return list of widgets  
	 */
	public List<Widget> getXpathResult(ProtocolProxy proxy, String xpathExpr){
		if (state != proxy.getState()) {
			state = proxy.getState();
			xpathMap.clear();
		}
		if (xpathMap.containsKey(xpathExpr)){
			return xpathMap.get(xpathExpr);
		}		
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
		xpathMap.putIfAbsent(xpathExpr, widgetList);
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
        javax.xml.xpath.XPath xpath = xpathFactory.newXPath();
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
    		if (tag.type() == Shape.class) {
    			Shape shape = widget.get(Tags.Shape);
    			stringBuilder.append(toXMLElement(tag.name(), Util.toString(shape)));
    			// insert at same level, not as children of Shape element
       			stringBuilder.append(toXMLElement("Shape.x", "" + shape.x()));
       			stringBuilder.append(toXMLElement("Shape.y", "" + shape.y()));
       			stringBuilder.append(toXMLElement("Shape.width", "" + shape.width()));
       			stringBuilder.append(toXMLElement("Shape.height", "" + shape.height()));
    		}else {
                stringBuilder.append(toXMLElement(tag.name(), Util.toString(widget.get(tag))));
    		}
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
