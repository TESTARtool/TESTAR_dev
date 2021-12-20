/*******************************************************************************
 * Copyright (c) 2009, 2021 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *    Fernando Pastor Ricos - adapt original code for TESTAR purposes
 *
 *******************************************************************************/
package org.testar.jacoco;

import java.io.File;
import java.io.IOException;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.html.HTMLFormatter;

/**
 * This example creates a HTML report for eclipse like projects based on a
 * single execution data store called jacoco.exec. The report contains no
 * grouping information.
 *
 * The class files under test must be compiled with debug information, otherwise
 * source highlighting will not work.
 */
public class ReportGenerator {

    private final File executionDataFile;
    private final File classesDirectory;
    private final File sourceDirectory;

    private ExecFileLoader execFileLoader;
    
    /**
     * Create a new generator based for the given project.
     * https://www.jacoco.org/jacoco/trunk/coverage/org.jacoco.examples/org.jacoco.examples/ReportGenerator.java.html
     * 
     * @param projectDirectory
     * @param executionDataFile
     * @param classesDirectory
     * @param sourceDirectory
     */
    public ReportGenerator(final File executionDataFile, final File classesDirectory, final File sourceDirectory) {
    	this.executionDataFile = executionDataFile;
    	this.classesDirectory = classesDirectory;
    	this.sourceDirectory = sourceDirectory;
    }

    /**
     * Create the report.
     *
     * @throws IOException
     */
    public String create() throws IOException {

        // Read the jacoco.exec file. Multiple data files could be merged
        // at this point
        loadExecutionData();

        // Run the structure analyzer on a single class folder to build up
        // the coverage model. The process would be similar if your classes
        // were in a jar file. Typically you would create a bundle for each
        // class folder and each jar you want in your report. If you have
        // more than one bundle you will need to add a grouping node to your
        // report
        final IBundleCoverage bundleCoverage = analyzeStructure();

        return createReport(bundleCoverage);
    }

    /**
     *  https://github.com/jacoco/jacoco/blob/b68fe1a0a7fb86f12cda689ec473fd6633699b55/org.jacoco.report/src/org/jacoco/report/html/HTMLFormatter.java
     *  https://github.com/jacoco/jacoco/blob/b68fe1a0a7fb86f12cda689ec473fd6633699b55/org.jacoco.report/src/org/jacoco/report/csv/CSVGroupHandler.java
     * 
     * @param bundleCoverage
     * @throws IOException
     */
    private String createReport(final IBundleCoverage bundleCoverage) throws IOException {
        final IReportGroupVisitor visitor = new CodeReader();

        visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(sourceDirectory, "utf-8", 4));
        return ((CodeReader) visitor).getCodeInfo();
    }

    private void loadExecutionData() throws IOException {
        execFileLoader = new ExecFileLoader();
        execFileLoader.load(executionDataFile);
    }

    private IBundleCoverage analyzeStructure() throws IOException {
        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(
                execFileLoader.getExecutionDataStore(), coverageBuilder);

        analyzer.analyzeAll(classesDirectory);

        return coverageBuilder.getBundle(executionDataFile.getName());
    }

}