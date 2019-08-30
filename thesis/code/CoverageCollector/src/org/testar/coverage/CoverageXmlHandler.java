package org.testar.coverage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CoverageXmlHandler extends DefaultHandler {

	private int level = 0;
	private CoverageData coverageData;

	public CoverageXmlHandler(int sequence, int actions) {
		super();
		this.coverageData = new CoverageData(sequence, actions);
	}

	/*
	 * Retrieve only the high level counters on report tag level 
	 * <report>
	 *   <...>
	 *   <counter type="INSTRUCTION" missed="69737" covered="59161"/>
	 *   <counter type="BRANCH" missed="4684" covered="3070"/>
	 *   <counter type="LINE" missed="13160" covered="11939"/>
	 *   <counter type="COMPLEXITY" missed="4775" covered="3009"/>
	 *   <counter type="METHOD" missed="1645" covered="2218"/>
	 *   <counter type="CLASS" missed="112" covered="386"/>
	 * </report>
	 */

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		level++;
		if (qName.equalsIgnoreCase("counter") && level == 2) { // Only counters on <report> level
			String type = attributes.getValue("type");
			int missed;
			try {
				missed = Integer.parseInt(attributes.getValue("missed"));
			} catch (NumberFormatException e) {
				missed = 0;
			}
			int covered;
			try {
				covered = Integer.parseInt(attributes.getValue("covered"));
			} catch (NumberFormatException e) {
				covered = 0;
			}
			coverageData.add(CoverageCounter.valueOf(type), missed, covered);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		level--;
	}

	public CoverageData getCoverageData() {
		return coverageData;
	}
}