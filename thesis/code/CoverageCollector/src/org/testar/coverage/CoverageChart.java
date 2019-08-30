package org.testar.coverage;

import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bridj.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

public class CoverageChart {

	private static final Logger logger = LoggerFactory.getLogger(CoverageChart.class);
	private String templateDir = "resource";

	/**
	 * Instantiates a new coverage chart with default template directory
	 */
	public CoverageChart() {
	}

	/**
	 * Instantiates a new coverage chart.
	 *
	 * @param templateDir the template directory to use
	 */
	public CoverageChart(String templateDir) {
	    this.templateDir = templateDir;
	}

	/**
	 * Creates the chart of the provided data.
	 *
	 * @param title the title of the chart
	 * @param subTitle the sub title of the chart
	 * @param coverageDataMap the coverage data
	 * @param chartFilename the chart filename
	 */
    public void createChart(String title, String subTitle, Map<Integer, CoverageData> coverageDataMap, String chartFilename) {
		try {
			STGroup group = new STGroupDir(templateDir, '$', '$');
			ST st = group.getInstanceOf("coverageChart");

			List<String> minutes = new ArrayList<>();
			List<Pair<Integer,Integer>> sequences = new ArrayList<>();
            List<String> actions = new ArrayList<>();
            List<String> lines = new ArrayList<>();
            List<String> branches = new ArrayList<>();
			
			Map<Integer, CoverageData> sortedCoverageDataMap = new TreeMap<Integer, CoverageData>(coverageDataMap);
			int currentSequence = -1;
			for (Entry<Integer, CoverageData> coverageDataEntry: sortedCoverageDataMap.entrySet()) {
			    int minute = coverageDataEntry.getKey();
			    minutes.add(String.valueOf(minute));
			    CoverageData coverageData = coverageDataEntry.getValue();
			    actions.add(String.valueOf(coverageData.getActions()));
			    if (currentSequence != coverageData.getSequence()) {
			        sequences.add(new Pair<Integer, Integer>(minute, coverageData.getSequence()));
			        currentSequence = coverageData.getSequence();
			    }
			    lines.add(String.valueOf(coverageData.getCoveragePercentage(CoverageCounter.LINE)));
                branches.add(String.valueOf(coverageData.getCoveragePercentage(CoverageCounter.BRANCH)));
			}
			// Add data ranges
			st.add("title", title);
            st.add("subTitle", subTitle);
			st.add("minutes", minutes);
            st.add("actions", actions);
            st.add("sequences", sequences);
            st.add("lines", lines);
            st.add("branches", branches);

			String chart = st.render();
			if (chart == null || chart.isEmpty()) {
				logger.error("Failed to create chart: nothing returned from template");
				return;
			}

			// Write chart to disk
			Path chartFile = Paths.get(chartFilename);
	    	try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(chartFile, CREATE))) {
	    	    out.write(chart.getBytes(Charset.defaultCharset()));
			} catch (IOException e) {
				logger.error("Failed to save chart file: " + e.getMessage(), e);
				// Can't do nothing here
			}

		} catch (Exception e) {
			logger.error("Failed to create chart: " + e.getMessage(), e);
		}
	}

	public static void main(String args[]) {
	    CoverageChart chart = new CoverageChart();
	    Map<Integer, CoverageData> coverageDataMap = new HashMap<>();
	    CoverageData coverageData1 = new CoverageData(1, 2);
	    coverageData1.add(CoverageCounter.LINE, 100, 25);
        coverageData1.add(CoverageCounter.BRANCH, 125, 5);
        coverageDataMap.put(0, coverageData1);

        CoverageData coverageData2 = new CoverageData(1, 50);
        coverageData2.add(CoverageCounter.LINE, 80, 45);
        coverageData2.add(CoverageCounter.BRANCH, 100, 30);
        coverageDataMap.put(5, coverageData2);

        CoverageData coverageData3 = new CoverageData(2, 67);
        coverageData3.add(CoverageCounter.LINE, 60, 65);
        coverageData3.add(CoverageCounter.BRANCH, 90, 40);
        coverageDataMap.put(10, coverageData3);

        CoverageData coverageData4 = new CoverageData(3, 99);
        coverageData4.add(CoverageCounter.LINE, 50, 75);
        coverageData4.add(CoverageCounter.BRANCH, 75, 55);
        coverageDataMap.put(15, coverageData4);
	    
        CoverageData coverageData5 = new CoverageData(3, 117);
        coverageData5.add(CoverageCounter.LINE, 47, 78);
        coverageData5.add(CoverageCounter.BRANCH, 72, 58);
        coverageDataMap.put(20, coverageData5);

        chart.createChart("Main title", "Sub title", coverageDataMap, "test_chart_test_1.html");
	}
}
