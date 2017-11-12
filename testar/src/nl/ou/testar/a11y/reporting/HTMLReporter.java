/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package nl.ou.testar.a11y.reporting;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.fruit.Assert;

/**
 * HTML reporter for accessibility evaluation
 * @author Davy Kager
 *
 */
public final class HTMLReporter {
	
	// ----
	// HTML
	// ----
	
	private static final String[] HEADER = new String[] {
		"<!DOCTYPE html>",
		"<html>",
		"<head>",
		"<title>Accessibility Evaluation Report</title>", 
		"</head>",
		"<body>"
	};
	
	private static final String[] FOOTER = new String[] {
		"</body>",
		"</html>"
	};
	
	private static final String[] HEADING = new String[] {
			"h1", "h2", "h3", "h4", "h5", "h6"
	};
	
	private static final String PARAGRAPH = "p",
			ULIST = "ul", OLIST = "ol", LIST_ITEM = "li",
			TABLE = "table", TABLE_ROW = "tr", TABLE_HEADING = "th", TABLE_CELL = "td",
			IMAGE = "img", IMAGE_SRC = "src", IMAGE_ALT = "alt",
			LINK = "a", LINK_DEST = "href", LINK_TARGET = "target", LINK_TARGET_NEW = "_blank";
	
	// ---------
	// Constants
	// ---------
	
	private final static String CHARSET = "UTF-8";
	
	// ----------
	// Attributes
	// ----------
	
	private final PrintWriter out;
	
	/**
	 * Opens a new HTML report
	 * @param filename The filename to use.
	 * @throws FileNotFoundException On file I/O error.
	 * @throws UnsupportedEncodingException On charset error.
	 */
	public HTMLReporter(String filename) throws FileNotFoundException, UnsupportedEncodingException {
		out = new PrintWriter(filename, CHARSET);
	}
	
	/**
	 * Writes the footer to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeader() {
		for (String el : HEADER)
			write(el);
		writeHeading(1, "Accessibility Evaluation Report");
		return this;
	}
	
	/**
	 * Writes the footer to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeFooter() {
		for (String el : FOOTER)
			write(el);
		return this;
	}
	
	/**
	 * Writes a heading
	 * @param level The heading level, 1 (highest) .. 6 (lowest).
	 * @param text The heading text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeading(int level, String text) {
		Assert.notNull(text);
		Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
		write(start(HEADING[level-1]) + text + end(HEADING[level-1]));
		return this;
	}
	
	/**
	 * Writes a paragraph to the HTML report
	 * @param text The paragraph text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeParagraph(String text) {
		Assert.notNull(text);
		write(start(PARAGRAPH) + text + end(PARAGRAPH));
		return this;
	}
	
	/**
	 * Writes the start of an unordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeUListStart() {
		write(start(ULIST));
		return this;
	}
	
	/**
	 * Writes the end of an unordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeUListEnd() {
		write(end(ULIST));
		return this;
	}
	
	/**
	 * Writes the start of an ordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeOListStart() {
		write(start(OLIST));
		return this;
	}
	
	/**
	 * Writes the end of an ordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeOListEnd() {
		write(end(OLIST));
		return this;
	}
	
	/**
	 * Writes a list item to the HTML report
	 * @param text The item text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeListItem(String text) {
		Assert.notNull(text);
		write(start(LIST_ITEM) + text + end(LIST_ITEM));
		return this;
	}
	
	/**
	 * Writes the start of a table to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableStart() {
		write(start(TABLE));
		return this;
	}
	
	/**
	 * Writes the end of a table to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableEnd() {
		write(end(TABLE));
		return this;
	}
	
	/**
	 * Writes a table headings row to the HTML report
	 * @param headings The headings.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableHeadings(String... headings) {
		write(start(TABLE_ROW));
		for (String heading : headings)
			write(start(TABLE_HEADING) + heading + end(TABLE_HEADING));
		write(end(TABLE_ROW));
		return this;
	}
	
	/**
	 * Writes a regular table row to the HTML report
	 * @param cells The cells.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableRow(String... cells) {
		write(start(TABLE_ROW));
		for (String cell : cells)
			write(start(TABLE_CELL) + cell + end(TABLE_CELL));
		write(end(TABLE_ROW));
		return this;
	}
	
	/**
	 * Writes a reference to an image to the HTML report
	 * @param src The image URL.
	 * @param alt The alternative text for the image.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeImage(String src, String alt) {
		Assert.notNull(src, alt);
		Map<String, String> attrs = new HashMap<>();
		attrs.put(IMAGE_SRC, src);
		attrs.put(IMAGE_ALT, alt);
		write(start(IMAGE, attrs)); // image tag is not closed
		return this;
	}
	
	/**
	 * Writes a reference to an image to the HTML report
	 * @param text The link text.
	 * @param dest The destination URL.
	 * @param newWindow If the link should open in a new window or tab.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeLink(String text, String dest, boolean newWindow) {
		Assert.notNull(text, dest);
		Map<String, String> attrs = new HashMap<>();
		attrs.put(LINK_DEST, dest);
		if (newWindow)
			attrs.put(LINK_TARGET, LINK_TARGET_NEW);
		write(start(LINK, attrs) + text + end(LINK));
		return this;
	}
	
	/**
	 * Closes the HTML report
	 */
	public void close() {
		out.close();
	}
	
	private void write(String s) {
		out.println(s);
	}
	
	private String start(String el) {
		return "<" + el + ">";
	}
	
	private String start(String el, Map<String, String> attrs) {
		String ret = "<" + el;
		for (Entry<String, String> attr : attrs.entrySet())
			ret += " " + attr.getKey() + "=\"" + attr.getValue() + "\"";
		ret += ">";
		return ret;
	}
	
	private String end(String el) {
		return "</" + el + ">";
	}

}
