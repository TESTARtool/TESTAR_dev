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

/**
 * HTML reporter for accessibility evaluation
 * @author Davy Kager
 *
 */
public final class HTMLReporter {
	
	// ----
	// HTML
	// ----
	
	private static final String[] HTML_HEADER = new String[] {
		"<!DOCTYPE html>",
		"<html>",
		"<head>",
		"<title>Accessibility Evaluation Report</title>", 
		"</head>",
		"<body>",
		"<h1>Accessibility Evaluation Report</h1>"
	};
	
	private static final String[] HTML_FOOTER = new String[] {
		"</body>",
		"</html>"
	};
	
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
		for (String el : HTML_HEADER)
			write(el);
		return this;
	}
	
	/**
	 * Writes the footer to the HTML report
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeFooter() {
		for (String el : HTML_FOOTER)
			write(el);
		return this;
	}
	
	/**
	 * Writes a heading
	 * @param level The heading level.
	 * @param text The heading text.
	 * @return This HTML reporter.
	 */
	public HTMLReporter writeHeading(int level, String text) {
		String h = "h" + level;
		write("<"+h+">" + text + "</"+h+">");
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
