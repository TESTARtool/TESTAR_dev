package nl.ou.testar.tgherkin.protocol;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fruit.Util;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Widget;

import nl.ou.testar.tgherkin.model.Gesture;
import nl.ou.testar.utils.report.Reporter;

/**
 * Report utilities.
 *
 */
public class ReportUtils {

	/**
	 * Report separator.
	 */
	public static final String REPORT_SEPARATOR = "|";
	
	private ReportUtils () {		
	}
	
	/**
	 * Report derived gestures.
	 * @param map given widget-gesture list map
	 * @param sequenceCount current sequence number
	 * @param actionCount current action number
	 */
	public static void reportDerivedGestures(Map<Widget, List<Gesture>> map, int sequenceCount, int actionCount) {
		SortedSet<String> header = new TreeSet<String>();
		List<SortedMap<String,String>> reportLines = new ArrayList<SortedMap<String,String>>();
		String reportName = "output" + File.separator + "derivedGesturesReport_" + sequenceCount + "_" + actionCount + "_" + new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) + ".csv";
		Iterator<Map.Entry<Widget,List<Gesture>>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Widget,List<Gesture>> entrySet = iterator.next();
			Widget widget = entrySet.getKey();
			List<Gesture> gestureList = entrySet.getValue();
			SortedMap<String,String> reportLine = new TreeMap<String,String>();
	        for (Tag<?> tag : widget.tags()) {
	        	String tagValue = Util.toString((Object)widget.get(tag));
	        	if (tagValue != null && !"".equals(tagValue)) {
	        		// only include non-empty values
	        		if (!header.contains(tag.name())) {
	        			header.add(tag.name());	        			
	        		}
	        		reportLine.put(tag.name(), tagValue);
	        	}
	        }        
			for (Gesture gesture : gestureList) {
				String gestureName = "#" + gesture.getClass().getSimpleName();
        		if (!header.contains(gestureName)) {
        			header.add(gestureName);	        			
        		}
        		reportLine.put(gestureName, "Yes");
			}
			reportLines.add(reportLine);
		}
		outputDerivedGestures(header, reportLines, reportName);
	}

	private static void outputDerivedGestures(SortedSet<String> header, List<SortedMap<String,String>> reportLines, String reportName){
		StringBuilder reportContent = new StringBuilder();
		// header
		boolean notFirst = false;
		Iterator<String> headerIterator = header.iterator();
		while (headerIterator.hasNext()) {
			String columnName = headerIterator.next();
			if (notFirst) {
				reportContent.append(REPORT_SEPARATOR);
			}else {
				notFirst = true;
			}
			if (columnName.startsWith("#")) {
				// skip underscore (used to gestures in front of the widget tag columns)
				reportContent.append(columnName.substring(1));
			}else {
				reportContent.append(columnName);
			}
		}
		reportContent.append(System.getProperty("line.separator"));
		// Data Lines
		for (SortedMap<String,String> reportLine : reportLines) {
			notFirst = false;
			headerIterator = header.iterator();
			while (headerIterator.hasNext()) {
				String columnName = headerIterator.next();
				if (notFirst) {
					reportContent.append(REPORT_SEPARATOR);
				}else {
					notFirst = true;
				}
				String value = reportLine.get(columnName);
				if (value != null) {
					reportContent.append(value);	
				}				
			}
			reportContent.append(System.getProperty("line.separator"));
		}
		
		//
		Reporter.getInstance().report(reportName, reportContent.toString());
	}
	
	/**
	 * Report state.
	 * @param state GUI state
	 * @param sequenceCount current sequence number
	 * @param actionCount current action number
	 */
	public static void reportState(State state, int sequenceCount, int actionCount) {
		SortedSet<String> header = new TreeSet<String>();
		List<SortedMap<String,String>> reportLines = new ArrayList<SortedMap<String,String>>();
		String reportName = "output" + File.separator + "stateReport_" + sequenceCount + "_" + actionCount + "_" + new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) + ".csv";
		Iterator<Widget> iterator = state.iterator();
		while (iterator.hasNext()) {
			Widget widget = iterator.next();
			SortedMap<String,String> reportLine = new TreeMap<String,String>();
	        for (Tag<?> tag : widget.tags()) {
	        	String tagValue = Util.toString((Object)widget.get(tag));
	        	if (tagValue != null && !"".equals(tagValue)) {
	        		// only include non-empty values
	        		if (!header.contains(tag.name())) {
	        			header.add(tag.name());	        			
	        		}
	        		reportLine.put(tag.name(), tagValue);
	        	}
	        }        
			reportLines.add(reportLine);
		}
		outputState(header, reportLines, reportName);
	}

	private static void outputState(SortedSet<String> header, List<SortedMap<String,String>> reportLines, String reportName){
		StringBuilder reportContent = new StringBuilder();
		// header
		boolean notFirst = false;
		Iterator<String> headerIterator = header.iterator();
		while (headerIterator.hasNext()) {
			String columnName = headerIterator.next();
			if (notFirst) {
				reportContent.append(REPORT_SEPARATOR);
			}else {
				notFirst = true;
			}
			reportContent.append(columnName);
		}
		reportContent.append(System.getProperty("line.separator"));
		// Data Lines
		for (SortedMap<String,String> reportLine : reportLines) {
			notFirst = false;
			headerIterator = header.iterator();
			while (headerIterator.hasNext()) {
				String columnName = headerIterator.next();
				if (notFirst) {
					reportContent.append(REPORT_SEPARATOR);
				}else {
					notFirst = true;
				}
				String value = reportLine.get(columnName);
				if (value != null) {
					reportContent.append(value);	
				}				
			}
			reportContent.append(System.getProperty("line.separator"));
		}
		//
		Reporter.getInstance().report(reportName, reportContent.toString());
	}
	
	
}
