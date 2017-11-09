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
	
	private static final String[]
			HEADING_START = new String[] { "<h1>", "<h2>", "<h3>", "<h4>", "<h5>", "<h6>" },
			HEADING_END = new String[] { "</h1>", "</h2>", "</h3>", "</h4>", "</h5>", "</h6>" };
	
	private static final String PARAGRAPH_START = "<p>",
			PARAGRAPH_END = "</p>";
	
	private static final String ULIST_START = "<ul>",
			ULIST_END = "</ul>";
	
	private static final String OLIST_START = "<ol>",
			OLIST_END = "</ol>";
	
	private static final String LIST_ITEM_START = "<li>",
			LIST_ITEM_END = "</li>";
	
	private static final String TABLE_START = "<table>",
			TABLE_END = "</table>";
	private static final String TABLE_ROW_START = "<tr>",
			TABLE_ROW_END = "</tr>";
	private static final String TABLE_HEADING_START = "<th>",
			TABLE_HEADING_END = "</th>";
	private static final String TABLE_CELL_START = "<td>",
			TABLE_CELL_END = "</td>";
	
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
		Assert.isTrue(level >= 1 && level <= 6, "Invalid HTML heading level");
		write(HEADING_START[level-1] + text + HEADING_END[level-1]);
		return this;
	}
	
	/**
	 * Writes a paragraph to the HTML report
	 * @param text The paragraph text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeParagraph(String text) {
		write(PARAGRAPH_START + text + PARAGRAPH_END);
		return this;
	}
	
	/**
	 * Writes the start of an unordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeUListStart() {
		write(ULIST_START);
		return this;
	}
	
	/**
	 * Writes the end of an unordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeUListEnd() {
		write(ULIST_END);
		return this;
	}
	
	/**
	 * Writes the start of an ordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeOListStart() {
		write(OLIST_START);
		return this;
	}
	
	/**
	 * Writes the end of an ordered list to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeOListEnd() {
		write(OLIST_END);
		return this;
	}
	
	/**
	 * Writes a list item to the HTML report
	 * @param text The item text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeListItem(String text) {
		write(LIST_ITEM_START + text + LIST_ITEM_END);
		return this;
	}
	
	/**
	 * Writes the start of a table to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableStart() {
		write(TABLE_START);
		return this;
	}
	
	/**
	 * Writes the end of a table to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableEnd() {
		write(TABLE_END);
		return this;
	}
	
	/**
	 * Writes a table headings row to the HTML report
	 * @param headings The headings.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableHeadings(String... headings) {
		write(TABLE_ROW_START);
		for (String heading : headings)
			write(TABLE_HEADING_START + heading + TABLE_HEADING_END);
		write(TABLE_ROW_END);
		return this;
	}
	
	/**
	 * Writes a regular table row to the HTML report
	 * @param cells The cells.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeTableRow(String... cells) {
		write(TABLE_ROW_START);
		for (String cell : cells)
			write(TABLE_CELL_START + cell + TABLE_CELL_END);
		write(TABLE_ROW_END);
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

}
