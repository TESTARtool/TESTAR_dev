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

package org.testar.jacoco;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    /**
     * Path to the output file for execution data.
     */
    private File destFile;

    /**
     * This mojo accepts any number of execution data file sets.
     */
    private List<String> jacocoFiles;

    /**
     * Indicate the list of existing jacoco.exec files and the desired output merged file
     * 
     * @param jacocoFiles
     * @param destFile
     */
    public void testarExecuteMojo(List<String> jacocoFiles, File destFile) {
        this.jacocoFiles = jacocoFiles;
        this.destFile = destFile;
        if (!canMergeReports()) {
            return;
        }
        executeMerge();
    }

    private boolean canMergeReports() {
        if (jacocoFiles == null || jacocoFiles.isEmpty()) {
            System.out.println("ERROR: jacocoFiles are null or empty");
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
                System.out.println("Loading execution data file " + inputFile.getAbsolutePath());
                loader.load(inputFile);
            } catch (final IOException e) {
                System.out.println("Unable to read " + inputFile.getAbsolutePath());
            }
        }
    }

    private void save(final ExecFileLoader loader) {
        if (loader.getExecutionDataStore().getContents().isEmpty()) {
            System.out.println("MergeJacocoFiles save : getExecutionDataStore().getContents().isEmpty()");
            return;
        }
        System.out.println("Writing merged execution data to " + destFile.getAbsolutePath());
        try {
            loader.save(destFile, false);
        } catch (final IOException e) {
            System.out.println("Unable to write merged file " + destFile.getAbsolutePath());
        }
    }

    @Override
    protected void executeMojo() throws MojoExecutionException, MojoFailureException {
        // Nothing, customized behavior testarExecuteMojo()
    }
}