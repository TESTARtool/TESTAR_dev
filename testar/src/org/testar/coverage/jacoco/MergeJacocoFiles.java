/*******************************************************************************
 * Copyright (c) 2009, 2020 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Mads Mohr Christensen - implementation of MergeMojo
 *    Fernando Pastor Ricos - adapt original code for TESTAR purposes
 *
 *******************************************************************************/

package org.testar.coverage.jacoco;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.maven.AbstractJacocoMojo;

/**
 * Mojo for merging a set of execution data files (*.exec) into a single file.
 * Add TESTAR testarExecuteMojo method for tool coverage purposes.
 *
 * @since 0.6.4
 */
public class MergeJacocoFiles extends AbstractJacocoMojo {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Path to the output file for execution data.
	 */
	private File destMergedFile;

	/**
	 * Deque to track exactly two exec jacocoFiles.
	 */
	private Deque<String> jacocoFiles = new ArrayDeque<>(2);

	/**
	 * Indicate the list of existing jacoco.exec files and the desired output merged file
	 * 
	 * @param jacocoFiles
	 * @param destFile
	 */
	public void testarExecuteMojo(JacocoReportCSV jacocoReportCSV, String jacocoFile) {
		// Add the new file to the deque and manage size
		addJacocoFile(jacocoFile);

		if (!canMergeReports()) {
			return;
		}

		// Merge the exec files and prepare the CSV results
		String jacocoExecMerged = jacocoFile.replace(".exec", "_merged.exec");
		String jacocoCsvMerged = jacocoFile.replace(".exec", "_merged.csv");
		this.destMergedFile = new File(jacocoExecMerged);
		executeMerge();
		jacocoReportCSV.generateCSVresults(jacocoExecMerged, jacocoCsvMerged);

		// Finally, add the merged file to the deque and manage size
		// This is because the last merged file is the one that tracks the accumulative coverage
		addJacocoFile(jacocoExecMerged);
	}

	/**
	 * Adds a new jacoco file to the deque, replacing the oldest if it already contains two elements.
	 * @param newJacocoFile
	 */
	private void addJacocoFile(String newJacocoFile) {
		// If deque already contains 2 elements, remove the oldest (from the front)
		if (jacocoFiles.size() == 2) {
			jacocoFiles.removeFirst();
		}

		// Add the new file to the back of the deque
		jacocoFiles.addLast(newJacocoFile);

		logger.trace("Added Jacoco file: " + newJacocoFile + ", updated files: " + jacocoFiles);
	}

	private boolean canMergeReports() {
		if (jacocoFiles.size() != 2) {
			logger.trace("Jacoco merge is not possible, we need two jacoco files");
			return false;
		}
		return true;
	}

	private void executeMerge() {
		final ExecFileLoader loader = new ExecFileLoader();

		load(loader);
		save(loader);
	}

	private void load(final ExecFileLoader loader) {
		for(final String file : jacocoFiles) {
			final File inputFile = new File(file);
			try {
				logger.trace("Loading execution data file " + inputFile.getAbsolutePath());
				loader.load(inputFile);
			} catch (final IOException e) {
				logger.error("Unable to read " + inputFile.getAbsolutePath());
			}
		}
	}

	private void save(final ExecFileLoader loader) {
		if (loader.getExecutionDataStore().getContents().isEmpty()) {
			logger.error("MergeJacocoFiles save : getExecutionDataStore().getContents().isEmpty()");
			return;
		}
		logger.trace("Writing merged execution data to " + destMergedFile.getAbsolutePath());
		try {
			loader.save(destMergedFile, false);
		} catch (final IOException e) {
			logger.error("Unable to write merged file " + destMergedFile.getAbsolutePath());
		}
	}

	@Override
	protected void executeMojo() throws MojoExecutionException, MojoFailureException {
		// Nothing, customized behavior is in testarExecuteMojo()
	}

}