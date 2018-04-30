/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package nl.ou.testar.a11y.reporting;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.fruit.Assert;

/**
 * HTML reporter for accessibility evaluation.
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
	
	private static final String CHARSET = "UTF-8";
	
	// ----------
	// Attributes
	// ----------
	
	private final PrintWriter out;
	
	/**
	 * Opens a new HTML report.
	 * @param filename The filename to use.
	 * @throws FileNotFoundException On file I/O error.
	 * @throws UnsupportedEncodingException On charset error.
	 */
	public HTMLReporter(String filename) throws FileNotFoundException, UnsupportedEncodingException {
		out = new PrintWriter(filename, CHARSET);
	}
	
	/**
	 * Writes the header to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeader() {
		for (String el : HEADER) {
			write(el);
		}
		writeHeading(1, "Accessibility Evaluation Report");
		return this;
	}
	
	/**
	 * Writes the footer to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeFooter() {
		for (String el : FOOTER) {
			write(el);
		}
		return this;
	}
	
	/**
	 * Writes the start of a heading.
	 * @param level The heading level, 1 (highest) .. 6 (lowest).
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeadingStart(int level) {
		Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
		write(start(HEADING[level-1]));
		return this;
	}
	
	/**
	 * Writes the end of a heading.
	 * @param level The heading level, 1 (highest) .. 6 (lowest).
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeadingEnd(int level) {
		Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
		write(end(HEADING[level-1]));
		return this;
	}
	
	/**
	 * Writes a heading.
	 * @param level The heading level, 1 (highest) .. 6 (lowest).
	 * @param text The heading text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeading(int level, String text) {
		Assert.notNull(text);
		Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
		writeHeadingStart(level);
		write(text);
		writeHeadingEnd(level);
		return this;
	}
	
	/**
	 * Writes the start of a paragraph to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeParagraphStart() {
		write(start(PARAGRAPH));
		return this;
	}
	
	/**
	 * Writes the end of a paragraph to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeParagraphEnd() {
		write(end(PARAGRAPH));
		return this;
	}
	
	/**
	 * Writes a paragraph to the HTML report.
	 * @param text The paragraph text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeParagraph(String text) {
		Assert.notNull(text);
		writeParagraphStart();
		write(text);
		writeParagraphEnd();
		return this;
	}
	
	/**
	 * Writes the start of an unordered list to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeUListStart() {
		write(start(ULIST));
		return this;
	}
	
	/**
	 * Writes the end of an unordered list to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeUListEnd() {
		write(end(ULIST));
		return this;
	}
	
	/**
	 * Writes the start of an ordered list to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeOListStart() {
		write(start(OLIST));
		return this;
	}
	
	/**
	 * Writes the end of an ordered list to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeOListEnd() {
		write(end(OLIST));
		return this;
	}
	
	/**
	 * Writes the start of a list item to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeListItemStart() {
		write(start(LIST_ITEM));
		return this;
	}
	
	/**
	 * Writes the end of a list item to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeListItemEnd() {
		write(end(LIST_ITEM));
		return this;
	}
	
	/**
	 * Writes a list item to the HTML report.
	 * @param text The item text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeListItem(String text) {
		Assert.notNull(text);
		writeListItemStart();
		write(text);
		writeListItemEnd();
		return this;
	}
	
	/**
	 * Writes the start of a table to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableStart() {
		write(start(TABLE));
		return this;
	}
	
	/**
	 * Writes the end of a table to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableEnd() {
		write(end(TABLE));
		return this;
	}
	
	/**
	 * Writes the start of a table row to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableRowStart() {
		write(start(TABLE_ROW));
		return this;
	}
	
	/**
	 * Writes the end of a table row to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableRowEnd() {
		write(end(TABLE_ROW));
		return this;
	}
	
	/**
	 * Writes the start of a table heading to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableHeadingStart() {
		write(start(TABLE_HEADING));
		return this;
	}
	
	/**
	 * Writes the end of a table heading to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableHeadingEnd() {
		write(end(TABLE_HEADING));
		return this;
	}
	
	/**
	 * Writes a table heading to the HTML report.
	 * @param text The heading text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableHeading(String text) {
		Assert.notNull(text);
		writeTableHeadingStart();
		write(text);
		writeTableHeadingEnd();
		return this;
	}
	
	/**
	 * Writes the start of a table cell to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableCellStart() {
		write(start(TABLE_CELL));
		return this;
	}
	
	/**
	 * Writes the end of a table cell to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableCellEnd() {
		write(end(TABLE_CELL));
		return this;
	}
	
	/**
	 * Writes a table cell to the HTML report.
	 * @param text The cell text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableCell(String text) {
		Assert.notNull(text);
		writeTableCellStart();
		write(text);
		writeTableCellEnd();
		return this;
	}
	
	/**
	 * Writes a table headings row to the HTML report.
	 * @param headings The headings.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableHeadings(String... headings) {
		writeTableRowStart();
		for (String heading : headings) {
			writeTableHeading(heading);
		}
		writeTableRowEnd();
		return this;
	}
	
	/**
	 * Writes a regular table row to the HTML report.
	 * @param cells The cells.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableRow(String... cells) {
		writeTableRowStart();
		for (String cell : cells) {
			writeTableCell(cell);
		}
		writeTableRowEnd();
		return this;
	}
	
	/**
	 * Writes a regular table row to the HTML report.
	 * @param cells The cells.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableRow(Object... cells) {
		writeTableRowStart();
		for (Object cell : cells) {
			writeTableCell(cell.toString());
		}
		writeTableRowEnd();
		return this;
	}
	
	/**
	 * Writes a reference to an image to the HTML report.
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
	 * Writes the start of a link to the HTML report.
	 * @param dest The destination URL.
	 * @param newWindow If the link should open in a new window or tab.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeLinkStart(String dest, boolean newWindow) {
		Assert.notNull(dest);
		Map<String, String> attrs = new HashMap<>();
		attrs.put(LINK_DEST, dest);
		if (newWindow) {
			attrs.put(LINK_TARGET, LINK_TARGET_NEW);
		}
		write(start(LINK, attrs));
		return this;
	}
	
	/**
	 * Writes the end of a link to the HTML report.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeLinkEnd() {
		write(end(LINK));
		return this;
	}
	
	/**
	 * Writes a link to the HTML report.
	 * @param text The link text.
	 * @param dest The destination URL.
	 * @param newWindow If the link should open in a new window or tab.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeLink(String text, String dest, boolean newWindow) {
		Assert.notNull(text);
		writeLinkStart(dest, newWindow);
		write(text);
		writeLinkEnd();
		return this;
	}
	
	/**
	 * Closes the HTML report.
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
		for (Entry<String, String> attr : attrs.entrySet()) {
			ret += " " + attr.getKey() + "=\"" + attr.getValue() + "\"";
		}
		ret += ">";
		return ret;
	}
	
	private String end(String el) {
		return "</" + el + ">";
	}

}
